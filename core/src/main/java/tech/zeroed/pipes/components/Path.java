package tech.zeroed.pipes.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Path extends Component {
    public Array<Vector2> waypoints;
    public int currentWaypoint;

    /**
     * If true the value of target should be ignored
     */
    public boolean noTarget = false;

    public Path setWaypointTarget(Array<Vector2> waypoints){
        this.waypoints = waypoints;
        this.currentWaypoint = 0;
        return this;
    }

    public void noTarget(){
        noTarget = true;
    }

    /**
     * Takes the first 2 way points and discards the rest. Used when units need to recalculate
     * paths every few waypoints
     * @param waypoints
     * @return
     */
    public Path setShortWaypointTarget(Array<Vector2> waypoints) {
        Array<Vector2> subpoints = new Array<>(Vector2.class);
        subpoints.add(waypoints.get(0));
        if(waypoints.size > 1)
            subpoints.add(waypoints.get(1));
        return this.setWaypointTarget(subpoints);
    }

    public void extendWaypoints(Array<Vector2> path) {
        this.waypoints.addAll(path);
    }
}
