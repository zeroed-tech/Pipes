package tech.zeroed.pipes.systems.debug;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import tech.zeroed.pipes.Globals;
import tech.zeroed.pipes.components.FillProgress;
import tech.zeroed.pipes.components.Position;
import tech.zeroed.pipes.systems.WordCameraSystem;

@All({FillProgress.class, Position.class})
public class FloodGridRenderer extends IteratingSystem {
    public ComponentMapper<FillProgress> mFillProgress;
    public ComponentMapper<Position> mPosition;

    public ShapeRenderer shapeRenderer;
    public WordCameraSystem wordCameraSystem;


    @Override
    protected void initialize() {
        super.initialize();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(Color.BLUE);
    }

    @Override
    protected void begin() {
        super.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(wordCameraSystem.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setAutoShapeType(true);
    }

    @Override
    protected void process(int entityId) {
        FillProgress progress = mFillProgress.get(entityId);
        Position position = mPosition.get(entityId);
        Color c = shapeRenderer.getColor();
        c.a = 0.5f;
        shapeRenderer.setColor(c);
        shapeRenderer.rect(position.x, position.y + Globals.CELL_SIZE - Globals.CELL_SIZE * progress.getProgress(), Globals.CELL_SIZE, Globals.CELL_SIZE * progress.getProgress());
    }

    @Override
    protected void end() {
        super.end();
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    protected void dispose() {
        super.dispose();
        shapeRenderer.dispose();
        shapeRenderer = null;
    }
}
