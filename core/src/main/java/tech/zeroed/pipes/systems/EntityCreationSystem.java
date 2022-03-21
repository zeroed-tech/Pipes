package tech.zeroed.pipes.systems;

import com.artemis.BaseSystem;
import com.artemis.EntityEdit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import tech.zeroed.pipes.components.*;

import static tech.zeroed.pipes.Globals.CELL_SIZE;

public class EntityCreationSystem extends BaseSystem {

    TextureAtlas atlas;

    @Override
    protected void initialize() {
        atlas = new TextureAtlas("Sprites/Pipes.atlas");
    }

    public void createEntity(String type, float x, float y, int cellRotation){

        switch (type){
            case "Tee":
            case "Cross":
            case "Straight":
            case "Block":
            case "L":
                createPipe(type, x, y, cellRotation);
                break;
            case "Source":
                createSource(type, x, y, cellRotation);
                break;
            default:
                Gdx.app.error(this.getClass().getSimpleName(), "Attempted to build unknown object "+type);
        }
    }

    public void createPipe(String type, float x, float y, int cellRotation){
        EntityEdit entity = world.createEntity().edit();
        entity.add(new Position().set(x, y).setOrigin(CELL_SIZE/2f, CELL_SIZE/2f));
        entity.add(new BoundingBox(CELL_SIZE, CELL_SIZE));
        entity.add(new SpriteComponent().setSprite(atlas.findRegion("Pipe_"+ type)));
        entity.add(new Direction().setDirFromTilemap(cellRotation));
        entity.add(new FillProgress().setMaxCapacity(100));
        entity.add(new PipeType().setType(type));
    }

    public void createSource(String type, float x, float y, int cellRotation){
        EntityEdit entity = world.createEntity().edit();
        entity.add(new Position().set(x, y).setOrigin(CELL_SIZE/2f, CELL_SIZE/2f));
        entity.add(new BoundingBox(CELL_SIZE, CELL_SIZE));
        entity.add(new SpriteComponent().setSprite(atlas.findRegion("Source")));
        entity.add(new Direction().setDirFromTilemap(cellRotation));
        entity.add(new FillProgress().setMaxCapacity(100.1f));
        entity.add(new Filling());
        entity.add(new PipeType().setType(type));
        entity.add(new Source());
        entity.add(new NotTakingWater());
    }

    @Override
    protected void processSystem() {

    }
}
