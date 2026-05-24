package heap;

/**
 * HeapNode represents a single node in the max-heap.
 *
 * WHY A NODE CLASS (not an array)?
 * ─────────────────────────────────
 * The project explicitly forbids array-based heaps. In a typical textbook
 * heap, you store everything in an array and compute parent/child positions
 * with index arithmetic: parent(i) = (i-1)/2, left(i) = 2i+1, right(i) = 2i+2.
 *
 * Here we build a POINTER-BASED heap instead. Each node is an object that
 * holds actual Java references (pointers) to its parent, left child, and
 * right child — exactly like a binary tree node. The heap property (parent
 * similarity >= children similarity) is maintained through swapping, not
 * through index math.
 *
 * WHAT DOES EACH NODE STORE?
 * ──────────────────────────
 * • similarity  – cosine similarity score between this user and the target.
 *                 This is the VALUE the heap is ordered by (max at root).
 * • userId      – which user this node represents (their row ID from the CSV).
 * • ratings     – the full 9018-length float array of this user's movie ratings.
 *                 We store it here so that once we extract the top-K nodes from
 *                 the heap, we can immediately look up their movie ratings without
 *                 going back to the HashMap.
 * • left, right – pointers to child nodes (null if no child).
 * • parent      – pointer to parent node (null for root).
 *                 We need the parent pointer during heapify-up: when we insert
 *                 a new node at the bottom and bubble it upward, we must be able
 *                 to walk UP the tree. A standard binary tree doesn't have parent
 *                 pointers, but heaps need them.
 *                 ??? why A standard binary tree doesn't have parent pointers, but heaps need them              
 */
public class HeapNode {

    public double   similarity;   // the key: what the heap is sorted by
    public int      userId;       // which user this represents
    public float[]  ratings;      // their full ratings vector (9018 floats)
    public HeapNode left;         // left child (null if none)
    public HeapNode right;        // right child (null if none)
    public HeapNode parent;       // parent node (null for root)

    /**
     * Constructs a new heap node.
     *
     * @param userId     the user's ID from main_data1.csv
     * @param similarity cosine similarity to the target user (0.0 – 1.0)
     * @param ratings    the user's full ratings array (length = number of movies)
     */
    public HeapNode(int userId, double similarity, float[] ratings) {
        this.userId     = userId;
        this.similarity = similarity;
        this.ratings    = ratings;
        this.left       = null;
        this.right      = null;
        this.parent     = null;
    }

    @Override
    public String toString() {
        return "HeapNode{userId=" + userId + ", similarity=" + String.format("%.4f", similarity) + "}";
    }
}
