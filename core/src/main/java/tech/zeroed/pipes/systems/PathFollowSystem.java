package tech.zeroed.pipes.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import tech.zeroed.pipes.components.Path;
import tech.zeroed.pipes.components.Position;

@All({Path.class})
public class PathFollowSystem extends IteratingSystem {
    ComponentMapper<Path> mPath;
    ComponentMapper<Position> mPosition;

    @Override
    protected void process(int entityId) {
        Path path = mPath.get(entityId);

        Position position = mPosition.get(entityId);
        Vector2 currentPosition = position.getOffsetPosition();

        Vector2 waypoint = path.waypoints.get(path.currentWaypoint);

        // Calculate the distance we currently are away from the waypoint
        float distanceToWaypoint =  waypoint.dst(currentPosition);
        float distanceToMove = 30 * world.delta;
        if(distanceToWaypoint > distanceToMove){

            // Calculate the normailised direction to the next waypoint
            Vector2 direction = new Vector2(waypoint.x - currentPosition.x, waypoint.y - currentPosition.y).nor();
            // Scale the move direction based on movement speed and delta time
            direction.scl(30 * world.delta);

            currentPosition.add(direction);
            position.setOffsetPosition(currentPosition);
        }else{
            position.setOffsetPosition(waypoint);
            path.currentWaypoint++;
            if(path.currentWaypoint >= path.waypoints.size){
                mPath.remove(entityId);
            }
        }
    }
}
