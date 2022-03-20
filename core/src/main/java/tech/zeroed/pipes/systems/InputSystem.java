package tech.zeroed.pipes.systems;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class InputSystem extends BaseSystem {
    WordCameraSystem cameraSystem;

    public final InputMultiplexer multiplexer;
    public boolean LEFT,RIGHT,UP,DOWN;
    public boolean rightClickStarted;
    public boolean touchStarted;
    public boolean touchInProgress;
    public Vector3 touchPos = new Vector3();
    public Vector2 unprojectedTouchPos = new Vector2();
    public Vector2 currentMousePos = new Vector2();
    public float scrollAmount = 0;

    public InputSystem(InputMultiplexer multiplexer) {
        this.multiplexer = multiplexer;
    }

    @Override
    protected void processSystem() {

    }

    @Override
    protected void end() {
        scrollAmount = 0;
    }

    @Override
    protected void initialize() {
        super.initialize();
        Gdx.input.setInputProcessor(multiplexer);
        multiplexer.addProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                switch (keycode){
                    case Input.Keys.LEFT:
                    case Input.Keys.A:
                        LEFT = true;
                        return true;
                    case Input.Keys.RIGHT:
                    case Input.Keys.D:
                        RIGHT = true;
                        return true;
                    case Input.Keys.UP:
                    case Input.Keys.W:
                        UP = true;
                        return true;
                    case Input.Keys.DOWN:
                    case Input.Keys.S:
                        DOWN = true;
                        return true;
                }

                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                switch (keycode){
                    case Input.Keys.LEFT:
                    case Input.Keys.A:
                        LEFT = false;
                        return true;
                    case Input.Keys.RIGHT:
                    case Input.Keys.D:
                        RIGHT = false;
                        return true;
                    case Input.Keys.UP:
                    case Input.Keys.W:
                        UP = false;
                        return true;
                    case Input.Keys.DOWN:
                    case Input.Keys.S:
                        DOWN = false;
                        return true;
                }
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                touchPos.set(screenX, screenY, 0);
                cameraSystem.camera.unproject(touchPos, cameraSystem.viewport.getScreenX(), cameraSystem.viewport.getScreenY(), cameraSystem.viewport.getScreenWidth(), cameraSystem.viewport.getScreenHeight());
                unprojectedTouchPos.set(touchPos.x, touchPos.y);
                currentMousePos.set(unprojectedTouchPos);

                if(button == Input.Buttons.RIGHT) {
                    rightClickStarted = true;
                    return false;
                }
                if(button != Input.Buttons.LEFT) return false;

                Rectangle rectangle = new Rectangle(
                        cameraSystem.viewport.getScreenX(),
                        cameraSystem.viewport.getScreenY(),
                        cameraSystem.viewport.getScreenWidth(),
                        cameraSystem.viewport.getScreenHeight());

                touchStarted = rectangle.contains(screenX, screenY);
                touchInProgress = touchStarted;

                return touchStarted;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                touchInProgress = false;
                unprojectedTouchPos.setZero();
                return true;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                touchPos.set(screenX, screenY, 0);
                cameraSystem.camera.unproject(touchPos, cameraSystem.viewport.getScreenX(), cameraSystem.viewport.getScreenY(), cameraSystem.viewport.getScreenWidth(), cameraSystem.viewport.getScreenHeight());
                currentMousePos.set(touchPos.x, touchPos.y);
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                touchPos.set(screenX, screenY, 0);
                cameraSystem.camera.unproject(touchPos, cameraSystem.viewport.getScreenX(), cameraSystem.viewport.getScreenY(), cameraSystem.viewport.getScreenWidth(), cameraSystem.viewport.getScreenHeight());
                currentMousePos.set(touchPos.x, touchPos.y);
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                scrollAmount = amountY;
                return true;
            }
        });
    }
}