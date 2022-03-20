package tech.zeroed.pipes.components;

import com.artemis.Component;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animator extends Component {
    public float elapsedTime;
    public Animation<TextureRegion> animation;
    // Rotation origin
    public float originX, originY;

    // Translate offset
    public float translateX, translateY;

    // Dimension modifiers
    public float width, height;

    public Color color;
    public float rotation;

    public Animator setAnimation(Animation<TextureRegion> animation){
        this.animation = animation;
        return this;
    }

}