package tech.zeroed.pipes.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteComponent extends Component {
    public TextureRegion sprite;

    public SpriteComponent setSprite(TextureRegion sprite){
        this.sprite = sprite;
        return this;
    }
}