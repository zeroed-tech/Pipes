package tech.zeroed.pipes.components;

import com.artemis.Component;

public class BoundingBox extends Component {
    public float width;
    public float height;

    public BoundingBox(){}

    public BoundingBox(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public BoundingBox set(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public BoundingBox set(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }
}