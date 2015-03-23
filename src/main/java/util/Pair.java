package util;

/**
 * Created by Nathan on 3/10/2015.
 */
public class Pair<T1, T2> {
    private T1 x;
    private T2 y;

    public Pair(T1 x, T2 y) {
        this.x = x;
        this.y = y;
    }

    public T1 getX() { return x; }
    public T2 getY() { return y; }
    public void setX(T1 x) { this.x = x; }
    public void setY(T2 y) { this.y = y; }
}
