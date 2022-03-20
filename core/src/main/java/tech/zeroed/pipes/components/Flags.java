package tech.zeroed.pipes.components;

import com.artemis.Component;

/**
 * Holds various flags that can alter how systems deal with this entity
 */
public class Flags extends Component {
    // If true then add this item to the grid
    public boolean GRID = false;
    // If true, this item can be pathed through
    public boolean GRID_PASSABLE = true;
}
