package tech.zeroed.pipes.components;

import com.artemis.Component;

public class PipeType extends Component {
    public enum Type {
        L,
        Cross,
        Straight,
        Block,
        Tee,
        Source
    }
    public Type type;

    public PipeType setType(Type type){
        this.type = type;
        return this;
    }

    public PipeType setType(String type){
        return setType(Type.valueOf(type));
    }
}
