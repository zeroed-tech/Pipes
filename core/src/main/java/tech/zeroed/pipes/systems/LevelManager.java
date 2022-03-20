package tech.zeroed.pipes.systems;

import com.artemis.*;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import tech.zeroed.pipes.Globals;
import tech.zeroed.pipes.components.LevelComponent;
import tech.zeroed.pipes.components.LoadedLevel;
import tech.zeroed.pipes.components.UnloadedLevel;
import tech.zeroed.pipes.levels.Level;

@All({LevelComponent.class, UnloadedLevel.class})
public class LevelManager extends IteratingSystem {
    protected EntityCreationSystem entityCreationSystem;
    //protected GridSystem gridSystem;
    //protected PathFindingSystem pathFindingSystem;
    protected FloodSystem floodSystem;

    protected ComponentMapper<LevelComponent> mLevelComponent;
    protected ComponentMapper<UnloadedLevel> mu;

    @Override
    protected void inserted(int entityId) {
        LevelComponent lc = mLevelComponent.get(entityId);
        TiledMap tilemap = new TmxMapLoader().load("maps/"+lc.levelFile);
        TiledMapTileLayer l = (TiledMapTileLayer)tilemap.getLayers().get(0);

        lc.level = new Level() {
            @Override
            public int getWidth() {
                return l.getWidth();
            }

            @Override
            public int getHeight() {
                return l.getHeight();
            }

            @Override
            public String getName() {
                return lc.levelFile.substring(0, lc.levelFile.length()-4);
            }
        };

        // TODO replace with event
        floodSystem.initGrid(lc.level.getWidth(), lc.level.getHeight());
        //gridSystem.initGrid(lc.level.getWidth(), lc.level.getHeight());
        //pathFindingSystem.initGrid(lc.level.getWidth(), lc.level.getHeight());

        for(MapLayer layer : tilemap.getLayers()){
            TiledMapTileLayer tl = (TiledMapTileLayer)layer;
            for(int x = 0; x < tl.getWidth(); x++){
                for(int y = 0; y < tl.getHeight(); y++) {
                    TiledMapTileLayer.Cell cell = tl.getCell(x,y);
                    if(cell == null) continue;
                    TiledMapTile tile = cell.getTile();
                    int cellRotation = cell.getRotation();
                    if(cell.getFlipVertically()) cellRotation = 2;
                    entityCreationSystem.createEntity(tile.getProperties().get("Type", String.class), x * Globals.CELL_SIZE, y * Globals.CELL_SIZE, cellRotation);
                }
            }
        }

        // Remove unloaded level and add loaded level components
        mu.remove(entityId);
        world.getEntity(entityId).edit().add(new LoadedLevel());
        Gdx.app.log(this.getClass().getSimpleName(), "Loaded "+lc.level.getName());
    }

    @Override
    protected void process(int entityId) {
    }
}
