package core;

/**
 * The WeightedQuickUnion class is used to represent Union Find data structure.
 *
 * @source algs4.cs.princeton.edu
 */
public class WeightedQuickUnion {
    private int[] parent;
    private int[] size;


    /**
     * Full constructor for WeightedQuickUnion objects.
     *
     * @param count The number of components
     */
    public WeightedQuickUnion(int count) {
        parent = new int[count];
        size = new int[count];
        for (int i = 0; i < count; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    /**
     * Validates if the given index is valid
     *
     * @param v The index to be checked.
     */
    private void validate(int v) {
        if (v < 0 || v >= parent.length) {
            throw new IllegalArgumentException("index " + v + " is out of range!");
        }
    }

    /**
     * Returns the root of the element
     *
     * @param v an element
     */
    public int find(int v) {
        validate(v);
        int root = v;
        while (root != parent[root]) {
            root = parent[root];
        }
        while (v != root) {
            int newp = parent[v];
            parent[v] = root;
            v = newp;
        }
        return root;
    }

    /**
     * Returns true if the two elements are in the same set
     *
     * @param p one element
     * @param q the other element
     */
    public boolean connected(int p, int q) {

        return find(p) == find(q);
    }

    /**
     * Connect two elements
     *
     * @param p one element
     * @param q the other element
     */
    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) {
            return;
        }

        if (size[rootP] < size[rootQ]) {
            parent[rootP] = rootQ;
            size[rootQ] += size[rootP];
        } else {
            parent[rootQ] = rootP;
            size[rootP] += size[rootQ];
        }
    }
}
