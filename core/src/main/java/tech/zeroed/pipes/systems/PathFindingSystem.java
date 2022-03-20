package tech.zeroed.pipes.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.annotations.All;
import com.artemis.annotations.Exclude;
import com.artemis.systems.IteratingSystem;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.pipes.components.*;
import tech.zeroed.pipes.utilities.Grid;

import static tech.zeroed.pipes.Globals.CELL_SIZE;

@All({NeedsPath.class, Position.class})
@Exclude({Blocked.class})
public class PathFindingSystem extends IteratingSystem {
    private Grid<PathNode> grid;

    ComponentMapper<Position> mPosition;
    ComponentMapper<NeedsPath> mNeedsPath;
    ComponentMapper<Blocked> mBlocked;
    ComponentMapper<Path> mPath;
    ComponentMapper<Flags> mFlags;

    private Array<PathNode> openList;
    private Array<PathNode> closedList;

    public void initGrid(int width, int height){
        this.grid = new Grid<>(width, height, CELL_SIZE, new Vector2(0, 0), PathNode::new);
    }

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(Flags.class, Position.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                int[] ids = entities.getData();
                for (int i = 0, s = entities.size(); s > i; i++) {
                    Position pos = mPosition.get(ids[i]);
                    Flags flags = mFlags.get(ids[i]);

                    grid.getValue(grid.getCellPosition(pos.getPosition())).passable = flags.GRID_PASSABLE;
                }

            }

            @Override
            public void removed(IntBag entities) {
            }
        });
    }

    @Override
    protected void process(int entityId) {
        Position pos = mPosition.get(entityId);
        NeedsPath np = mNeedsPath.get(entityId);
        Array<PathNode> nodes = findPath(pos.getPosition(), np.target);
        if(nodes == null){
            Gdx.app.log(this.getClass().getSimpleName(), "Failed to find path for entity "+entityId);
            mBlocked.create(entityId);
            return;
        }

        mPath.create(entityId).setWaypointTarget(pathToWaypoints(nodes));
        mNeedsPath.remove(entityId);
    }

    public Array<PathNode> findPathBetweenEntities(int source, int target){
        if(!mPosition.has(source) || !mPosition.has(target)) return null;

        Vector2 sourcePos = mPosition.get(source).getOffsetPosition();
        Vector2 targetPos = mPosition.get(target).getOffsetPosition();

        return findPath(sourcePos, targetPos);
    }

    /**
     *
     * @param sourcePos worldCoordinates
     * @param targetPos worldCoordinates
     * @return path or null
     */
    public Array<PathNode> findPath(Vector2 sourcePos, Vector2 targetPos) {
        return findPath(sourcePos, targetPos, false, false);
    }

    /**
     *
     * @param sourcePos worldCoordinates
     * @param targetPos worldCoordinates
     * @param allowDiagonal if path should include diagonals
     * @param allowImpassable if path should ignore impassable cells
     * @return path or null
     */
    public Array<PathNode> findPath(Vector2 sourcePos, Vector2 targetPos, boolean allowDiagonal, boolean allowImpassable) {
        grid.getCellPosition(sourcePos);
        grid.getCellPosition(targetPos);
        return findPath((int)sourcePos.x, (int)sourcePos.y, (int)targetPos.x, (int)targetPos.y, allowDiagonal, allowImpassable);
    }

    /**
     *
     * @param startX grid coordinates
     * @param startY grid coordinates
     * @param endX grid coordinates
     * @param endY grid coordinates
     * @return path or null
     */
    public Array<PathNode> findPath(int startX, int startY, int endX, int endY){
        return findPath(startX, startY, endX, endY, false, false);
    }

    /**
     *
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param allowDiagonal
     * @param allowImpassable
     * @return
     */
    public Array<PathNode> findPath(int startX, int startY, int endX, int endY, boolean allowDiagonal, boolean allowImpassable){
        PathNode startNode = grid.getValue(startX, startY);
        PathNode endNode = grid.getValue(endX, endY);
        if(startNode == null || endNode == null) return null;

        openList = new Array<>(false, 200, PathNode.class);
        openList.add(startNode);

        closedList = new Array<>(false, 200, PathNode.class);

        // Set all nodes initial values to int max
        for(int x = 0; x < grid.getWidth(); x++){
            for(int y = 0; y < grid.getHeight(); y++){
                // Get the current node
                PathNode node = grid.getValue(x, y);
                if(!allowImpassable && !node.passable){
                    closedList.add(node);
                    continue;
                }
                node.gCost = Integer.MAX_VALUE;
                node.calculateFCode();
                node.cameFrom = null;
            }
        }

        startNode.gCost = 0;
        startNode.hCost = calculateDistanceCost(startNode, endNode);
        startNode.calculateFCode();

        while (openList.size > 0){
            PathNode currentNode = getLowestFCostNode(openList);
            if(currentNode == endNode){
                // Reached goal
                return calculatePath(endNode);
            }

            openList.removeValue(currentNode, true);
            closedList.add(currentNode);

            for(PathNode neighbourNode : getNeighbourList(currentNode, allowDiagonal)){
                if(closedList.contains(neighbourNode, true)) continue;
                int gCost = currentNode.gCost + calculateDistanceCost(currentNode, neighbourNode);
                if(gCost < neighbourNode.gCost){
                    neighbourNode.cameFrom = currentNode;
                    neighbourNode.gCost = gCost;
                    neighbourNode.hCost = calculateDistanceCost(neighbourNode, endNode);
                    neighbourNode.calculateFCode();
                    if(!openList.contains(neighbourNode, true)){
                        openList.add(neighbourNode);
                    }
                }
            }
        }

        // Path not found
        return null;
    }

    private Array<PathNode> getNeighbourList(PathNode node){
        return getNeighbourList(node, false);
    }

    private Array<PathNode> getNeighbourList(PathNode node, boolean allowDiagonal){
        Array<PathNode> neighbours = new Array<>(8);
        if(node.x - 1 >= 0){
            // Left
            neighbours.add(grid.getValue(node.x-1, node.y));
            if(allowDiagonal) {
                // Left down
                if (node.y - 1 >= 0) neighbours.add(grid.getValue(node.x - 1, node.y - 1));
                //Left up
                if (node.y + 1 < grid.getHeight())
                    neighbours.add(grid.getValue(node.x - 1, node.y + 1));
            }
        }
        if(node.x +1 < grid.getWidth()){
            // Right
            neighbours.add(grid.getValue(node.x+1, node.y));
            if(allowDiagonal) {
                // Right down
                if (node.y - 1 >= 0) neighbours.add(grid.getValue(node.x + 1, node.y - 1));
                // Right up
                if (node.y + 1 < grid.getHeight())
                    neighbours.add(grid.getValue(node.x + 1, node.y + 1));
            }
        }

        // Down
        if(node.y-1 >= 0) neighbours.add(grid.getValue(node.x, node.y-1));
        // Up
        if(node.y+1 < grid.getHeight()) neighbours.add(grid.getValue(node.x, node.y+1));

        return neighbours;
    }

    private Array<PathNode> calculatePath(PathNode endNode) {
        Array<PathNode> path = new Array<>(true, 200, PathNode.class);

        PathNode currentNode = endNode;
        path.add(currentNode);
        while(currentNode.cameFrom != null){
            currentNode = currentNode.cameFrom;
            path.add(currentNode);
        }
        path.reverse();
        return path;
    }

    private int calculateDistanceCost(PathNode a, PathNode b){
        int MOVE_STRAIGHT_COST = 10;
        int MOVE_DIAGONAL_COST = 14;

        int xDistance = Math.abs(a.x - b.x);
        int yDistance = Math.abs(a.y - b.y);

        int remaining = Math.abs(xDistance - yDistance);

        return MOVE_DIAGONAL_COST * Math.min(xDistance, yDistance) + MOVE_STRAIGHT_COST * remaining;
    }

    private PathNode getLowestFCostNode(Array<PathNode> nodes){
        PathNode lowestFCostNode = nodes.get(0);
        for(int i = 1; i < nodes.size; i++){
            PathNode node = nodes.items[i];
            if(node.fCost < lowestFCostNode.fCost){
                lowestFCostNode = node;
            }
        }
        return lowestFCostNode;
    }

    public Array<Vector2> pathToWaypoints(Array<PathNode>path){
        Array<Vector2> waypoints = new Array<>(true, path.size, Vector2.class);
        for(PathNode node : path){
            Vector2 point = new Vector2(node.x, node.y);
            point.scl(grid.getCellSize()).add(grid.getCellSize()/2, grid.getCellSize()/2);//.add(grid.getCellSize()*0.5f, grid.getCellSize()*0.5f);
            waypoints.add(point);
        }
        return waypoints;
    }

    /**
     * Checks all tiles in the specified range to ensure they are valid and not already occupied
     * @param gridPosition
     * @param width
     * @param height
     * @return
     */
    public boolean validateGridRange(Vector2 gridPosition, int width, int height) {
        for(int x = (int) gridPosition.x; x < gridPosition.x+width; x++){
            for(int y = (int) gridPosition.y; y < gridPosition.y+height; y++) {
                // Make sure the specified cell is on the grid and not already passable (contains a path)
                if(!grid.isValid(x, y) || grid.getValue(x,y).occupied)
                    return false;
            }
        }
        return true;
    }

    /**
     * Set a grid reference to be occupied
     * @param x
     * @param y
     */
    public void makeOccupied(int x, int y){
        if(grid.isValid(x,y))
            grid.getValue(x, y).occupied = true;
    }

    public void makePassable(int x, int y){
        if(grid.isValid(x,y))
            grid.getValue(x, y).passable = true;
    }

    public static int CalculatePathLength(Array<PathNode>path){
        int pathLength = 0;
        for(PathNode node : path){
            pathLength += node.fCost;
        }
        return pathLength;
    }

    public void makeOccupied(Vector2 gridPosition, int width, int height) {
        for(int x = (int) gridPosition.x; x < gridPosition.x+width; x++){
            for(int y = (int) gridPosition.y; y < gridPosition.y+height; y++) {
                makeOccupied(x, y);
            }
        }
    }

    /**
     * @param e1
     * @param e2
     * @return The number of cells between the two entities
     */
    public int getGridDistanceBetweenEntities(int e1, int e2){
        return Grid.GetGridDistance(
                grid.getCellPosition(mPosition.get(e1).getOffsetPosition().cpy()),
                grid.getCellPosition(mPosition.get(e2).getOffsetPosition().cpy())
        );
    }

    class PathNode {
        public int x;
        public int y;

        public int gCost;
        public int hCost;
        public int fCost;

        // Should be considered for regular path finding
        public boolean passable;
        // Contains a structure of some kind (path, building etc)
        public boolean occupied;

        public PathNode cameFrom;

        public PathNode(int x, int y){
            this.x = x;
            this.y = y;
            passable = true;
            occupied = false;
        }

        @Override
        public String toString() {
            return "X: "+x+"\nY: "+y+" Passable: "+passable;
        }

        public void calculateFCode() {
            fCost = gCost + hCost;
        }
    }
}
