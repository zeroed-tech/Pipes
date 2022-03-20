package tech.zeroed.pipes.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import tech.zeroed.pipes.components.Flags;
import tech.zeroed.pipes.components.Position;
import tech.zeroed.pipes.utilities.Grid;
import tech.zeroed.pipes.utilities.GridNode;

import static tech.zeroed.pipes.Globals.CELL_SIZE;

@All({Flags.class, Position.class})
public class GridSystem extends IteratingSystem {
    protected ComponentMapper<Flags> mFlags;
    protected ComponentMapper<Position> mPosition;

    // This grid holds all units and building
    private Grid<GridNode<Integer>> grid;

    public void initGrid(int width, int height){
        grid = new Grid<>(width, height, CELL_SIZE, new Vector2(0, 0), GridNode::new);
    }

    @Override
    protected void inserted(int entityId) {
        Flags flags = mFlags.get(entityId);
        if(!flags.GRID) return;// This entity doesn't belong on the grid

        Position pos = mPosition.get(entityId);
        GridPoint2 gridPoint = grid.getCellPosition(pos.getPosition());

        GridNode<Integer> gn = new GridNode<Integer>(gridPoint.x, gridPoint.y).setId(entityId);
        gn.impassable = flags.GRID_PASSABLE;
        grid.setValue(gridPoint.x, gridPoint.y, gn);
    }

    @Override
    protected void process(int entityId) {

    }
}
