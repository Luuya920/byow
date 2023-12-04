package core;

import tileengine.TETile;

/**
 * The Avatar class represents a character within the world, having a position, an image, and life points.
 * This class is used to manage the state and behavior of the avatar, such as its position in the world
 * and its life points during the game.
 */
public class Avatar {
    private Point startPos;
    private Point pos;
    private TETile img;
    private int life;

    /**
     * Constructs a new Avatar with a starting position, current position, and an image.
     * The avatar's life points are initialized to 3.
     *
     * @param startPos The starting position of the avatar.
     * @param pos      The current position of the avatar.
     * @param img      The tile image to represent the avatar.
     */
    public Avatar(Point startPos, Point pos, TETile img) {
        this.startPos = startPos;
        this.pos = pos;
        this.img = img;
        this.life = 3;
    }
    /**
     * Gets the current position of the avatar.
     *
     * @return The current position as a Point object.
     */
    public Point getPos() {
        return pos;
    }
    /**
     * Gets the starting position of the avatar.
     *
     * @return The starting position as a Point object.
     */
    public Point getStartPos() {
        return startPos;
    }
    /**
     * Gets the current life points of the avatar.
     *
     * @return The number of life points.
     */
    public int getLife() {
        return life;
    }
    /**
     * Sets the life points of the avatar to the specified value.
     *
     * @param x The new number of life points.
     */
    public void setLife(int x) {
        this.life = x;
    }
    /**
     * Reduces the life points of the avatar by 1, down to a minimum of 0.
     */
    public void reduceLife() {
        if (life > 0) {
            life--;
        }
    }
    /**
     * Gets the image tile of the avatar.
     *
     * @return The image tile representing the avatar.
     */
    public TETile getImg() {
        return img;
    }
}
