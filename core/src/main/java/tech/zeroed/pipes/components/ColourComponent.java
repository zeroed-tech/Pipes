package tech.zeroed.pipes.components;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

public class ColourComponent extends Component {
    public Color color;

    public ColourComponent(){}

    public ColourComponent(Color color){
        this.color = color;
    }
}
