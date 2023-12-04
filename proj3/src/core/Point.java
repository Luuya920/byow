package core;

/**
 * The Point object is used to represent a single point in the 2D world.
 * All Point objects have x and y values indicating its position in the 2D world.
 */
public class Point {
    private int x;
    private int y;

    /**
     * Full constructor for Point objects.
     *
     * @param x The number representing the x-axis location of the point
     * @param y The number representing the y-axis location of the point
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the x-position of the point
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-position of the point
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the x-position of the point
     *
     * @param x new position of x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the y-position of the point
     *
     * @param y new position of x
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Compares this point to the specified object for equality.
     *
     * @param obj The object to compare this Point against.
     * @return true if the given object represents a Point equivalent to this point,
     * false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point && this.x == ((Point) obj).x && this.y == ((Point) obj).y;
    }
}
