package core;

import edu.princeton.cs.algs4.Stack;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

/**
 * The AStarPathFinder class implements the A* pathfinding algorithm to find the shortest path
 * between two points on a grid. It uses a heuristic to guide the search for efficiency.
 *
 * @source https://www.geeksforgeeks.org/a-search-algorithm/
 * Converted the implementation to java and
 * changed it to be suitable for the game.
 */
public class AStarPathFinder {
    /**
     * A helper class to hold details about grid cells during pathfinding.
     */
    private static class Cell {
        int parentRow, parentCol;
        int f;
        int g;
        int h;

        /**
         * Constructs a cell with maximum values for cost and invalid parent indices.
         */
        Cell() {
            this.f = Integer.MAX_VALUE;
            this.g = Integer.MAX_VALUE;
            this.h = Integer.MAX_VALUE;
            this.parentRow = -1;
            this.parentCol = -1;
        }
    }

    /**
     * Reconstructs the path from the start to the goal point using the details stored in cells.
     *
     * @param cellDetails The grid of cells with their associated costs and parent information.
     * @param goal        The end point of the path to trace back.
     * @return A list of points representing the path from the start to the goal.
     */
    public List<Point> tracePath(Cell[][] cellDetails, Point goal) {
        List<Point> res = new ArrayList<>();
        int row = goal.getX();
        int col = goal.getY();

        Stack<Point> path = new Stack<>();
        while (!(cellDetails[row][col].parentRow == row && cellDetails[row][col].parentCol == col)) {
            path.push(new Point(row, col));
            int tempRow = cellDetails[row][col].parentRow;
            int tempCol = cellDetails[row][col].parentCol;
            row = tempRow;
            col = tempCol;
        }
        path.push(new Point(row, col));
        while (!path.isEmpty()) {
            res.add(path.pop());
        }

        return res;
    }

    /**
     * Finds the shortest path from the start to the goal point using the A* algorithm.
     *
     * @param world The grid representation of the world with tiles.
     * @param start The starting point for the path.
     * @param goal  The goal point for the path.
     * @return A list of points representing the shortest path found, or null if no path exists.
     */
    public List<Point> findPath(TETile[][] world, Point start, Point goal) {
        if (!isValid(start.getX(), start.getY()) || !isValid(goal.getX(), goal.getY())) {
            return null;
        }
        if (start.equals(goal)) {
            return List.of(start);
        }
        int row = world.length;
        int col = world[0].length;
        boolean[][] closedList = new boolean[row][col];
        Cell[][] cellDetails = new Cell[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                cellDetails[i][j] = new Cell();
            }
        }
        int i = start.getX(), j = start.getY();
        cellDetails[i][j].f = 0;
        cellDetails[i][j].g = 0;
        cellDetails[i][j].h = 0;
        cellDetails[i][j].parentRow = i;
        cellDetails[i][j].parentCol = j;
        Comparator<Point> comparator = Comparator.comparingInt(p -> cellDetails[p.getX()][p.getY()].f);
        PriorityQueue<Point> openList = new PriorityQueue<>(comparator);
        openList.add(new Point(i, j));

        while (!openList.isEmpty()) {
            Point p = openList.iterator().next();
            openList.remove(p);
            i = p.getX();
            j = p.getY();
            closedList[i][j] = true;
            int gNew, hNew, fNew;
            int[][] directions = new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] direction : directions) {
                int nR = i + direction[0];
                int nC = j + direction[1];
                if (isValid(nR, nC)) {
                    if (isDestination(nR, nC, goal)) {
                        cellDetails[nR][nC].parentRow = i;
                        cellDetails[nR][nC].parentCol = j;
                        return tracePath(cellDetails, goal);

                    } else if (!closedList[nR][nC] && world[nR][nC] != Tileset.WALL_GRAY) {
                        gNew = cellDetails[i][j].g + 1;
                        hNew = manhattanDistance(nR, nC, goal);
                        fNew = gNew + hNew;

                        if (cellDetails[nR][nC].f == Integer.MAX_VALUE || cellDetails[nR][nC].f > fNew) {
                            openList.add(new Point(nR, nC));
                            cellDetails[nR][nC].f = fNew;
                            cellDetails[nR][nC].g = gNew;
                            cellDetails[nR][nC].h = hNew;
                            cellDetails[nR][nC].parentRow = i;
                            cellDetails[nR][nC].parentCol = j;
                        }
                    }
                }
            }

        }
        return null;
    }

    /**
     * Checks if a point is the destination.
     *
     * @param x    The x-coordinate to check.
     * @param y    The y-coordinate to check.
     * @param dest The destination point.
     * @return True if the coordinates correspond to the destination point, false otherwise.
     */
    private boolean isDestination(int x, int y, Point dest) {
        return x == dest.getX() && y == dest.getY();
    }

    /**
     * Validates if a coordinate is within the boundaries of the grid.
     *
     * @param x The x-coordinate to check.
     * @param y The y-coordinate to check.
     * @return True if the coordinates are within the grid, false otherwise.
     */
    private boolean isValid(int x, int y) {
        return x >= 0 && x < 100 && y >= 0 && y < 60;
    }

    /**
     * Calculates the Manhattan distance between two points, which is used as a heuristic
     * for the A* algorithm.
     *
     * @param x The x-coordinate of the first point.
     * @param y The y-coordinate of the first point.
     * @param b The second point to calculate the distance to.
     * @return The Manhattan distance between the two points.
     */
    private int manhattanDistance(int x, int y, Point b) {
        return Math.abs(x - b.getX()) + Math.abs(y - b.getY());
    }
}
