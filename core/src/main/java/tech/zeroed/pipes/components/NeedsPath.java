package tech.zeroed.pipes.components;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;

public class NeedsPath extends Component {
    public Vector2 target = new Vector2();

    public NeedsPath() {
        super();
    }

    public NeedsPath(Vector2 target) {
        super();
        this.target = target;
    }
}
