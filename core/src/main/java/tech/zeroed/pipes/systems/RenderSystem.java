package tech.zeroed.pipes.systems;

import com.artemis.ComponentMapper;
import com.artemis.annotations.All;
import com.artemis.annotations.One;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import tech.zeroed.pipes.components.*;

@All({Position.class, Direction.class})
@One({SpriteComponent.class, Animator.class})
public class RenderSystem extends IteratingSystem {
    //private ComponentMapper<DynamicEntitySprite> mDynamicEntitySprite;
    private ComponentMapper<Animator> mAnimator;
    //private ComponentMapper<GhostUnit> mGhost;
    //private ComponentMapper<BuildNeeded> mBuild;

    WordCameraSystem cameraSystem;

    private ShapeRenderer sr;
    protected ComponentMapper<Position> mPosition;
    protected ComponentMapper<SpriteComponent> mSprite;
    protected ComponentMapper<Direction> mDirection;

    private SpriteBatch batch;

    private final Color invalid = new Color(1,0,0,0.7f);
    private final Color valid = new Color(0,1,0,0.7f);
    private final Color ghost = new Color(1,1,1,0.5f);

    private Vector2 origin = new Vector2();
    private Vector2 translation = new Vector2();
    private Vector2 dimensions = new Vector2();


    @Override
    protected void initialize() {
        super.initialize();

        batch = new SpriteBatch(100);
    }

    @Override
    protected void begin() {
        super.begin();
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        super.end();
        batch.end();
    }

    @Override
    protected void process(int entityId) {
        TextureRegion texture = getTextureForEntity(entityId);
        if(texture == null)
            return;
        Position position = mPosition.get(entityId);
        Direction dir = mDirection.get(entityId);
        float rotation = dir.getDirInDeg();

        getTextureModifier(entityId, texture, origin, translation, dimensions);


        //boolean ghostView = mBuild.has(entityId) || mGhost.has(entityId);

//        if(ghostView){
//            Color color = ghost;
//            if(mGhost.has(entityId))
//                color = mGhost.get(entityId).validPosition ? valid : invalid;
//
//            batch.setColor(color);
//        }else{
        if(mAnimator.has(entityId) && mAnimator.get(entityId).color != null)
            batch.setColor(mAnimator.get(entityId).color);
        //}

        batch.draw(texture, position.x+translation.x, position.y+translation.y, origin.x, origin.y, dimensions.x, dimensions.y, 1, 1, rotation);

        batch.setColor(Color.WHITE);
    }

    private void getTextureModifier(int entityId, TextureRegion texture, Vector2 origin, Vector2 translation, Vector2 dimensions) {
        if(mAnimator.has(entityId)){
            Animator animator = mAnimator.get(entityId);
            origin.set(animator.originX, animator.originY);
            translation.set(animator.translateX, animator.translateY);
            dimensions.x = animator.width != 0 ? animator.width : texture.getRegionWidth();
            dimensions.y = animator.height != 0 ? animator.height : texture.getRegionHeight();
        }else {
            origin.set(mPosition.get(entityId).getOrigin());
            translation.setZero();
            dimensions.set(texture.getRegionWidth(), texture.getRegionHeight());
        }
    }

    public TextureRegion getTextureForEntity(int entityId){
        TextureRegion texture = null;
        if(mAnimator.has(entityId)){
            Animator animator = mAnimator.get(entityId);
            animator.elapsedTime += world.delta;
            texture = animator.animation.getKeyFrame(animator.elapsedTime);
        }
//        else if(mDynamicEntitySprite.has(entityId)){
//            texture = mDynamicEntitySprite.get(entityId).currentSprite;
//        }
        else if(mSprite.has(entityId)) {
            texture = mSprite.get(entityId).sprite;
        }

        return texture;
    }

//    public float getTextureRotation(int entityId){
//        if(!mFacing.has(entityId)){
//            if(mAnimator.has(entityId)) {
//                Animator animator = mAnimator.get(entityId);
//                return animator.rotation;
//            }
//            return 0;
//        }
//        DirectionFacing facing = mFacing.get(entityId);
//        float rotation = 0;
//        switch (facing.facing){
//            case LEFT:
//                rotation = 90;
//                break;
//            case RIGHT:
//                rotation = 270;
//                break;
//            case UP:
//                rotation = 0;
//                break;
//            case DOWN:
//                rotation = 180;
//                break;
//            case LEFT_UP:
//                rotation = 45;
//                break;
//            case LEFT_DOWN:
//                rotation = 135;
//                break;
//            case RIGHT_UP:
//                rotation = 315;
//                break;
//            case RIGHT_DOWN:
//                rotation = 225;
//                break;
//        }
//        return rotation;
//    }


    @Override
    protected void dispose() {
        super.dispose();
        batch.dispose();
        batch = null;
    }


    public RenderSystem() {
        super();
        this.sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
    }
}
