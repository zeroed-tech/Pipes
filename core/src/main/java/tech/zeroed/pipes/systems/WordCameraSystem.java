package tech.zeroed.pipes.systems;

import com.artemis.Aspect;
import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.EntitySubscription;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import tech.zeroed.pipes.components.LevelComponent;
import tech.zeroed.pipes.components.LoadedLevel;
import tech.zeroed.pipes.levels.Level;

import static tech.zeroed.pipes.Globals.CELL_SIZE;

public class WordCameraSystem extends BaseSystem {
    protected ComponentMapper<LevelComponent> ml;

    public FitViewport viewport;
    public OrthographicCamera camera;

    InputSystem inputManager;
    LevelManager levelManager;

    private final float moveSpeed = 3.5f;

    private float zoomFactor;

    private Vector3 currentMoveOffset;

    private int viewWidth, viewHeight;
    private float worldWidth, worldHeight;

    @Override
    protected void initialize() {
        world.getAspectSubscriptionManager().get(Aspect.all(LevelComponent.class, LoadedLevel.class)).addSubscriptionListener(new EntitySubscription.SubscriptionListener() {
            @Override
            public void inserted(IntBag entities) {
                int[] ids = entities.getData();
                assert ids.length == 1;
                Level level = ml.get(ids[0]).level;

                zoomFactor = 1;
                worldWidth = level.getWidth() * CELL_SIZE;
                worldHeight = level.getHeight() * CELL_SIZE;

                viewport.setWorldSize(worldWidth, worldHeight);
                viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }

            @Override
            public void removed(IntBag entities) {
            }
        });

        viewWidth = 1920;
        viewHeight = 1080;

        camera = new OrthographicCamera();
        camera.zoom = 1;

        viewport = new FitViewport(10, 10, camera);
        //viewport.setScreenBounds(0, 0, 1920, 1080);

        currentMoveOffset = new Vector3();
    }

    @Override
    protected void processSystem() {
        viewport.apply();

        if(inputManager.LEFT)
            currentMoveOffset.x = -moveSpeed*zoomFactor;
        else if(inputManager.RIGHT)
            currentMoveOffset.x = moveSpeed*zoomFactor;
        else
            currentMoveOffset.x = 0;

        if(inputManager.UP)
            currentMoveOffset.y = moveSpeed*zoomFactor;
        else if(inputManager.DOWN)
            currentMoveOffset.y = -moveSpeed*zoomFactor;
        else
            currentMoveOffset.y = 0;

        camera.translate(currentMoveOffset);

        zoomFactor = MathUtils.clamp(zoomFactor + inputManager.scrollAmount*0.01f, 0.3f, 1f);
        camera.zoom = zoomFactor;
        camera.update();

        // Camera clamping code from https://stackoverflow.com/questions/47644078/clamp-camera-to-map-zoom-issue
        float zoom = camera.zoom;
        float zoomedHalfWorldWidth = zoom * camera.viewportWidth / 2;
        float zoomedHalfWorldHeight = zoom * camera.viewportHeight / 2;

        //min and max values for camera's x coordinate
        float minX = zoomedHalfWorldWidth;
        float maxX = worldWidth - zoomedHalfWorldWidth;

        //min and max values for camera's y coordinate
        float minY = zoomedHalfWorldHeight;
        float maxY = worldHeight - zoomedHalfWorldHeight;

        camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        if(minY < maxY)
            camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
        else
            camera.position.y = minY;

        camera.update();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
//        this.viewport.setScreenY(Gdx.graphics.getHeight() - viewport.getScreenHeight());
//        this.viewport.setScreenX(Gdx.graphics.getWidth() - viewport.getScreenWidth());
    }
}