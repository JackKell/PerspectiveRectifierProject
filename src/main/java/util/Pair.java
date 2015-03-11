package util;

/**
 * Created by Nathan on 3/10/2015.
 */
public class Pair<Float, Short> {
    private Float x;
    private Short y;

    public Pair(Float x, Short y) {
        this.x = x;
        this.y = y;
    }

    public Float getX() { return x; }
    public Short getY() { return y; }
    public void setX(Float x) { this.x = x; }
    public void setY(Short y) { this.y = y; }
}
