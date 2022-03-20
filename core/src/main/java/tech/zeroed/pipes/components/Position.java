package tech.zeroed.pipes.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class Position extends Component {
    public float x;
    public float y;

    public float originX;
    public float originY;

    public Position set(Vector2 position) {
        return this.set(position.x, position.y);
    }

    public Position set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Position setOffsetPosition(Vector2 position) {
        this.set(position.x-originX, position.y-originY);
        return this;
    }

    public Position setOrigin(float x, float y) {
        this.originX = x;
        this.originY = y;
        return this;
    }

    public Vector2 getOrigin() {
        return new Vector2(originX, originY);
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public Vector2 getOffsetPosition(){
        return getPosition().add(originX, originY);
    }
}
