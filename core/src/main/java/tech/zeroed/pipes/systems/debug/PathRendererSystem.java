package tech.zeroed.pipes.systems.debug;


import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import tech.zeroed.pipes.components.Path;
import tech.zeroed.pipes.systems.WordCameraSystem;

@All({Path.class})
public class PathRendererSystem extends IteratingSystem {
    ComponentMapper<Path> mPath;

    public ShapeRenderer shapeRenderer;
    public WordCameraSystem wordCameraSystem;


    @Override
    protected void initialize() {
        super.initialize();
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void begin() {
        super.begin();
        shapeRenderer.setProjectionMatrix(wordCameraSystem.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    }

    @Override
    protected void process(int entityId) {
        Path path = mPath.get(entityId);
        drawWaypoints(path.waypoints);
    }

    private void drawWaypoints(Array<Vector2> waypoints){
        shapeRenderer.setAutoShapeType(true);

        Vector2 tmp = new Vector2();
        Vector2 tmp2 = new Vector2();
        for (int i = 0; i < waypoints.size; i++){
            Vector2 waypoint = waypoints.items[i];
            tmp.set(waypoint);
            if(i == 0 || i == waypoints.size-1){
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(Color.RED);
            }
            if(i+1 != waypoints.size) {
                Vector2 nextWaypoint = waypoints.items[i+1];
                tmp2.set(nextWaypoint);
                shapeRenderer.set(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.GREEN);
                shapeRenderer.line(tmp, tmp2);
                shapeRenderer.circle(waypoint.x, waypoint.y, 3);
            }
        }
    }

    @Override
    protected void end() {
        super.end();
        shapeRenderer.end();
    }

    @Override
    protected void dispose() {
        super.dispose();
        shapeRenderer.dispose();
        shapeRenderer = null;
    }
}
