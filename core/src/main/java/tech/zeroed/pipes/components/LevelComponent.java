package tech.zeroed.pipes.components;

import com.artemis.Component;
import tech.zeroed.pipes.levels.Level;

public class LevelComponent extends Component {
    public Level level;

    public String levelFile;

    public LevelComponent setLevelFile(String levelFile){
        this.levelFile = levelFile;
        return this;
    }

    public LevelComponent setLevel(Level level){
        this.level = level;
        return this;
    }
}
