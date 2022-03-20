package tech.zeroed.pipes.components;

import com.artemis.Component;

public class Direction extends Component {
    public enum Dir {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    public Dir dir = Dir.NORTH;

    public Direction setDir(Dir dir){
        this.dir = dir;
        return this;
    }

    public Direction setDir(int dirCode){
        Dir d = Dir.NORTH;
        switch (dirCode){
            case 1:
                d = Dir.EAST;
                break;
            case 2:
                d = Dir.SOUTH;
                break;
            case 3:
                d = Dir.WEST;
                break;
        }

        return setDir(d);
    }

    public float getDirInDeg(){
        switch (dir){
            case NORTH:
                return 0;
            case EAST:
                return 90;
            case SOUTH:
                return 180;
            case WEST:
                return 270;
        }
        return 0;
    }
}
