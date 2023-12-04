package core;

import utils.RandomUtils;

import java.util.Random;

/**
 * The Room object is used to represent a single room in the 2D world.
 * All Room objects have height, width, and points representing the bottom left and top right points
 */
public class Room {
    private final Point bottomLeft;
    private final Point topRight;
    private final int heigth;
    private final int width;

    /**
     * Full constructor for Room objects.
     *
     * @param bottomLeft The point representing the room's bottom corner
     * @param heigth     The height of the room
     * @param width      The widht of the room
     */
    public Room(Point bottomLeft, int heigth, int width) {
        this.bottomLeft = bottomLeft;
        this.topRight = new Point(getBottomLeft().getX() + width, getBottomLeft().getY() + heigth);
        this.heigth = heigth;
        this.width = width;
    }

    /**
     * Return the bottom left point of the room
     */
    public Point getBottomLeft() {
        return bottomLeft;
    }

    /**
     * Return the top right point of the room
     */
    public Point getTopRight() {
        return topRight;
    }

    /**
     * Return the middle point of the room
     */
    public Point getCenter() {
        int x = (getTopRight().getX() + getBottomLeft().getX()) / 2;
        int y = (getTopRight().getY() + getBottomLeft().getY()) / 2;

        return new Point(x, y);
    }

    /**
     * Given a room object, returns a distance between this and the other room.
     *
     * @param other The Room object
     */
    public int distanceFromOtherRoom(Room other) {
        int xDiff = getCenter().getX() - other.getCenter().getX();
        int yDiff = getCenter().getY() - other.getCenter().getY();
        double dist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

        return (int) Math.round(dist);
    }

    /**
     * Given a room object, checks whether the given room intersects with this room
     *
     * @param other The Room object that might overlap with this room
     */

    public boolean intersects(Room other) {
        return (getBottomLeft().getX() <= other.getTopRight().getX()
                && getTopRight().getX() >= other.getBottomLeft().getX()
                && getBottomLeft().getY() <= other.getTopRight().getY()
                && getTopRight().getY() >= other.getBottomLeft().getY());
    }

    /**
     * Retrieves a random point within the given room that is not a wall.
     *
     * @param rand Random object
     * @return A random point within the room.
     */
    public Point getRandomPointInRoom(Random rand) {
        int x = RandomUtils.uniform(rand, getBottomLeft().getX() + 1, getTopRight().getX() - 1);
        int y = RandomUtils.uniform(rand, getBottomLeft().getY() + 1, getTopRight().getY() - 1);

        return new Point(x, y);
    }

}
