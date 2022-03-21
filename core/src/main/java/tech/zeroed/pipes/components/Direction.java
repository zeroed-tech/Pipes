package tech.zeroed.pipes.components;

import com.artemis.Component;

public class Direction extends Component {
    public enum Dir {
        NORTH(0),
        EAST(1),
        SOUTH(2),
        WEST(3);

        private final int value;
        private Dir(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public int getJoiningDirection(){
            switch (value){
                case 0:
                    return SOUTH.getValue();
                case 1:
                    return WEST.getValue();
                case 2:
                    return NORTH.getValue();
                case 3:
                    return EAST.getValue();
            }
            return -1;
        }
    }

    public Dir dir = Dir.NORTH;

    public Direction setDir(Dir dir){
        this.dir = dir;
        return this;
    }

    /**
     * Tiled rotates NWSE for some reason so need to compensate for this here
     */
    public Direction setDirFromTilemap(int dirCode){
        Dir d = Dir.NORTH;
        switch (dirCode){
            case 1:
                d = Dir.WEST;
                break;
            case 2:
                d = Dir.SOUTH;
                break;
            case 3:
                d = Dir.EAST;
                break;
        }

        return setDir(d);
    }

    public float getDirInDeg(){
        switch (dir){
            case NORTH:
                return 0;
            case EAST:
                return 270;
            case SOUTH:
                return 180;
            case WEST:
                return 90;
        }
        return 0;
    }
}
