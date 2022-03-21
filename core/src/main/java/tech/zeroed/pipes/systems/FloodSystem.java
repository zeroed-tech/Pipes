package tech.zeroed.pipes.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.annotations.EntityId;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import tech.zeroed.pipes.components.*;
import tech.zeroed.pipes.utilities.Grid;

import java.util.Arrays;

import static tech.zeroed.pipes.Globals.CELL_SIZE;

@All({FillProgress.class, Position.class, Direction.class, Filling.class, PipeType.class})
public class FloodSystem extends IteratingSystem {
    public Grid<FloodNode> floodGrid;

    public ComponentMapper<FillProgress> mFillProgress;
    public ComponentMapper<Position> mPosition;
    public ComponentMapper<Direction> mDirection;
    public ComponentMapper<Filling> mFilling;
    public ComponentMapper<PipeType> mPipeType;
    public ComponentMapper<NotTakingWater> mNotTakingWater;

    //public static final float FILL_RATE = 100f;

    @Override
    protected void initialize() {
        super.initialize();

        world.getAspectSubscriptionManager().get(Aspect.all(FillProgress.class, Position.class, Direction.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                int[] ids = entities.getData();
                for (int i = 0, s = entities.size(); s > i; i++) {
                    Position position = mPosition.get(ids[i]);
                    GridPoint2 intPos = floodGrid.getCellPosition(position.getPosition());
                    floodGrid.getValue(intPos.x, intPos.y).entity = ids[i];
                }
            }

            @Override
            public void removed(IntBag entities) {
            }
        });
    }

    public void initGrid(int width, int height) {
        this.floodGrid = new Grid<>(width, height, CELL_SIZE, new Vector2(0, 0), FloodNode::new);
    }

    /**
     * For each entity, check to see if it's water is at max capacity, if it is then try and divide all water between
     * all connected neighbours
     */
    @Override
    protected void process(int entityId) {
        FillProgress progress = mFillProgress.get(entityId);
        Position position = mPosition.get(entityId);
        Direction direction = mDirection.get(entityId);
        PipeType pipeType = mPipeType.get(entityId);

        GridPoint2 intPos = floodGrid.getCellPosition(position.getPosition());

        if(progress.current > progress.maxCapacity) {
            float toDistribute = progress.current - progress.maxCapacity;

            // Activate all surrounding tiles
            // Left, Right, Down, Up TODO Select neighbours based on pipe type and direction
            FloodNode[] neighbours = floodGrid.getNeighbours(intPos.x, intPos.y, FloodNode.class);
            int[] neighboursToCheck = getPipeNeighbours(pipeType, direction);

            int validConnectionCount = 0;
            int[] validConnections = new int[]{-1, -1, -1, -1};

            for(int neighbour : neighboursToCheck) {
                FloodNode node = neighbours[neighbour];
                // Invalid node
                if(node == null || node.entity == -1) continue;
                // Something is next to us, now we need to check that it has a connection joining our connection
                if(canConnectAndTakeWater(node.entity, Direction.Dir.values()[neighbour])){
                    // Both pipes can connect to each other and the remote pipe has capacity free
                    if(!mFilling.has(node.entity)) mFilling.create(node.entity); // Add filling component so it gets updates

                    // Track valid connections
                    validConnections[validConnectionCount] = neighbour;
                    validConnectionCount++;
                }
            }

            if(validConnectionCount == 0){
                // No connected pipes can accept water from here, add the NotTakingWater component so we don't accept anymore
                mNotTakingWater.create(entityId);
                // Drop the extra water we have
                progress.current = progress.maxCapacity;
                return;
            }

            float amountEach = toDistribute/validConnectionCount;

            for(int connection : validConnections){
                if(connection == -1) continue;
                FloodNode node = neighbours[connection];
                FillProgress currentProgress = mFillProgress.get(node.entity);
                currentProgress.current += amountEach;
            }

            progress.current = progress.maxCapacity;
        }
    }
    /*
     N     0
    WOE   3O1
     S     2
    */
    private static int[] getPipeNeighbours(PipeType pipeType, Direction direction){
        switch (pipeType.type){
            case L:
                switch (direction.dir){
                    case NORTH:
                        return new int[]{0, 1};
                    case EAST:
                        return new int[]{1, 2};
                    case SOUTH:
                        return new int[]{2, 3};
                    case WEST:
                        return new int[]{3, 0};
                }
                break;
            case Cross:
                return new int[]{0, 1, 2, 3};
            case Straight:
            case Source:
                if(direction.dir == Direction.Dir.NORTH || direction.dir == Direction.Dir.SOUTH){
                    return new int[]{0, 2};
                }else{
                    return new int[]{1, 3};
                }
            case Block:
                return new int[]{direction.dir.getValue()};
            case Tee:
                switch (direction.dir){
                    case NORTH:
                        return new int[]{1, 2, 3};
                    case EAST:
                        return new int[]{0, 2, 3};
                    case SOUTH:
                        return new int[]{0, 1, 3};
                    case WEST:
                        return new int[]{0, 1, 2};
                }
                break;
        }
        return new int[]{};
    }

    private boolean canConnect(int entityId, Direction.Dir otherDir){
        Direction direction = mDirection.get(entityId);
        PipeType pipeType = mPipeType.get(entityId);

        int[] validConnections = getPipeNeighbours(pipeType, direction);
        for(int i : validConnections){
            if(i == otherDir.getJoiningDirection()) return true;
        }
        return false;
    }

    private boolean canConnectAndTakeWater(int entityId, Direction.Dir otherDir){
        return canConnect(entityId, otherDir) && !mNotTakingWater.has(entityId);
    }
}

class FloodNode {
        public int x;
        public int y;

        @EntityId
        public int entity = -1;

        public FloodNode(int x, int y){
            this.x = x;
            this.y = y;
        }
}
