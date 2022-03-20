package tech.zeroed.pipes.utilities;

public class GridNode<T> {
    public int x;
    public int y;

    public T id;
    public boolean impassable;

    public GridNode(int x, int y){
        this.x = x;
        this.y = y;
        this.id = null;
    }

    public GridNode<T> setId(T id){
        this.id = id;
        return this;
    }

    public boolean canBuild(){
        return id == null && !impassable;
    }

    public boolean hasEntity(){
        return id != null;
    }
}