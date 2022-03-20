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

import static tech.zeroed.pipes.Globals.CELL_SIZE;

@All({FillProgress.class, Position.class, Direction.class, Filling.class})
public class FloodSystem extends IteratingSystem {
    public Grid<FloodNode> floodGrid;

    public ComponentMapper<FillProgress> mFillProgress;
    public ComponentMapper<Position> mPosition;
    public ComponentMapper<Direction> mDirection;
    public ComponentMapper<Filling> mFilling;

    public static final float FILL_RATE = 20f;

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

    @Override
    protected void process(int entityId) {
        FillProgress progress = mFillProgress.get(entityId);
        Position position = mPosition.get(entityId);
        Direction direction = mDirection.get(entityId);

        if(progress.current >= progress.maxCapacity) return;

        GridPoint2 intPos = floodGrid.getCellPosition(position.getPosition());

        progress.current += FILL_RATE * world.delta;

        if(progress.current >= progress.maxCapacity) {
            // Activate all surrounding tiles
            // Left, Right, Down, Up TODO Select neighbours based on pipe type and direction
            FloodNode[] neighbours = floodGrid.getNeighbours(intPos.x, intPos.y, FloodNode.class);
            for(FloodNode node : neighbours) {
                // Invalid node
                if(node == null || node.entity == -1) continue;
                if(mFilling.has(node.entity)) continue; // already filling
                //TODO It would be nice to increase the rate that a  pipe fills if it has multiple connections to it
                mFilling.create(node.entity);
            }
        }
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
