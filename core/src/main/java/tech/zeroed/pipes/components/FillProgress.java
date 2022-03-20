package tech.zeroed.pipes.components;

import com.artemis.Component;
import com.badlogic.gdx.math.MathUtils;

public class FillProgress extends Component {
    public float maxCapacity;
    public float current;

    public FillProgress setCurrent(float current) {
        this.current = current;
        return this;
    }

    public FillProgress setMaxCapacity(float maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public float getProgress(){
        return MathUtils.clamp(current/maxCapacity, 0f, 1f);
    }
}
