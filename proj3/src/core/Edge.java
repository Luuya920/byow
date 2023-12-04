package core;

/**
 * The Edge class represents a connection between two rooms in the world, commonly used
 * to form hallways or paths. Each edge has an associated weight, which typically represents
 * the distance from one room to the other. This class implements the
 * Comparable interface to allow for edges to be easily sorted by their weight, which is
 * useful in algorithms that require sorting edges, such as Kruskal's minimum spanning tree algorithm.
 */
public class Edge implements Comparable<Edge> {
    private Room room1;
    private Room room2;
    private int weight;

    /**
     * Full constructor for Edge objects.
     *
     * @param r1     the room
     * @param r2     the other room
     * @param weight the distance between two rooms
     */
    public Edge(Room r1, Room r2, int weight) {
        this.room1 = r1;
        this.room2 = r2;
        this.weight = weight;
    }

    /**
     * Retrieves the first room connected by this edge.
     *
     * @return The first room object.
     */
    public Room getRoom1() {
        return room1;
    }

    /**
     * Retrieves the second room connected by this edge.
     *
     * @return The second room object.
     */
    public Room getRoom2() {
        return room2;
    }

    /**
     * Retrieves the weight of this edge.
     *
     * @return The weight representing the cost or distance between the two connected rooms.
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Compares this edge with another edge based on their weights. This method is used
     * for sorting purposes and follows the contract specified by the Comparable interface.
     *
     * @param other The other edge to compare against.
     * @return A negative integer, zero, or a positive integer as this edge is less than,
     * equal to, or greater than the specified edge.
     */
    @Override
    public int compareTo(Edge other) {
        return this.weight - other.getWeight();
    }
}
