package heap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MaxHeap — a pointer-based (node-linked) max-heap ordered by similarity score.
 *
 * ═══════════════════════════════════════════════════════════════════════
 * WHAT IS A HEAP AND WHY DO WE USE IT?
 * ═══════════════════════════════════════════════════════════════════════
 * A max-heap is a COMPLETE BINARY TREE where every node's value is
 * greater than or equal to its children's values. This means the ROOT
 * always holds the MAXIMUM element.
 *
 * For this project, "value" = cosine similarity score.
 * After inserting all 600 users into the heap, the root is the user
 * most similar to the target. We then call extractMax() X times to
 * get the top-X most similar users — each extraction is O(log n).
 *
 * Alternative: sort all 600 users by similarity → O(n log n).
 * Heap approach for top-X: O(n log n) insert + O(X log n) extract
 *   = O(n log n) total, but conceptually shows the heap structure the
 *   assignment asks for, and is faster if X << n. 
 * //??? how did we calculated the time complexity extraction and insertion functions
 *
 * ═══════════════════════════════════════════════════════════════════════
 * COMPLETE BINARY TREE — THE SHAPE RULE
 * ═══════════════════════════════════════════════════════════════════════
 * A heap must always be a COMPLETE binary tree: every level is fully
 * filled except possibly the last, which is filled left to right.
 *
 * Example with 6 nodes (indices shown for reference):
 *
 *              [1]            ← level 0 (root)
 *            /     \
 *          [2]     [3]        ← level 1 (full)
 *         /   \   /
 *       [4]  [5] [6]          ← level 2 (filled left-to-right, stops at 6)
 *
 * In an ARRAY heap, position 7 (next insert) is the right child of node 3.
 * In our NODE heap, we find that position using binary path navigation.
 *
 * ═══════════════════════════════════════════════════════════════════════
 * THE BINARY PATH TRICK — HOW TO FIND ANY NODE BY INDEX
 * ═══════════════════════════════════════════════════════════════════════
 * With no array, how do we know WHERE to attach the next inserted node?
 * Answer: convert the TARGET POSITION NUMBER to binary, then follow the
 * bits as a path through the tree (0 = go left, 1 = go right), skipping
 * the leading 1-bit.
 *
 * Why does this work? Because in a complete binary tree, if you number
 * nodes top-to-bottom left-to-right starting at 1, the binary representation
 * of a node's index encodes exactly the path from root to that node.
 *
 * Example: find node at position 6 → binary(6) = "110"
 *   - Skip leading 1  → "10"
 *   - Follow: 1 = go right (from root)
 *   - Follow: 0 = go left
 *   - You arrive at node 6. ✓
 *
 * Position 7 → binary(7) = "111" → skip "1" → follow "11" → right, right
 * Position 4 → binary(4) = "100" → skip "1" → follow "00" → left, left
 *
 * We use this to find:
 *   a) The PARENT of the next insert position (navigate to position size/2+...)
 *   b) The LAST NODE in the heap (navigate to position = current size)
 *
 * ═══════════════════════════════════════════════════════════════════════
 * THE TWO CORE OPERATIONS
 * ═══════════════════════════════════════════════════════════════════════
 *
 * INSERT (+ heapify-up):
 *   1. Place new node at next available position (rightmost spot on last level)
 *   2. Compare with parent: if new node > parent, swap their DATA (not pointers)
 *   3. Repeat step 2 going upward until heap property is restored or we reach root
 *   Time: O(log n)
 *
 * EXTRACT-MAX (+ heapify-down):
 *   1. Save root's data (it's the max)
 *   2. Copy last node's data into root, then delete the last node
 *   3. Compare root with its children: swap with the LARGER child if it's bigger
 *   4. Repeat step 3 going downward until heap property is restored or we're a leaf
 *   Time: O(log n)
 *
 * WHY SWAP DATA, NOT POINTERS?
 *   Swapping pointer references in a linked tree requires updating 6+ pointers
 *   (both children and the parent of both nodes, plus possibly the root reference).
 *   It is error-prone and adds no benefit. Instead we swap just the payload fields
 *   (similarity, userId, ratings) — much simpler and equally correct.
 */
public class MaxHeap {

    private HeapNode root;   // the root of the tree (always holds the max similarity)
    private int      size;   // how many nodes are currently in the heap

    /** Creates an empty max-heap. */
    public MaxHeap() {
        this.root = null;
        this.size = 0;
    }

    // ─────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────

    /**
     * Inserts a new node into the heap.
     *
     * Steps:
     *  1. Find the parent of the next insertion position using binary path.
     *  2. Attach newNode as left or right child of that parent.
     *  3. Set newNode's parent pointer.
     *  4. Heapify upward to restore heap property.
     *
     * @param newNode a HeapNode with similarity already set
     */
    public void insert(HeapNode newNode) {
        if (root == null) {
            // Special case: tree is empty, the new node becomes the root
            root = newNode;
            size = 1;
            return;
        }

        size++;

        // Find which existing node will become the parent of the new node.
        // The new node occupies position `size` in the complete binary tree.
        // Its parent is at position `size / 2`.
        HeapNode parent = navigateToPosition(size / 2);

        // Attach new node to the correct side of the parent
        if (size % 2 == 0) {
            // Even index → left child
            //??? why did we suppose that even size has its left child empty so we placed the new node at its place
            //
            parent.left = newNode;
        } else {
            // Odd index → right child
            parent.right = newNode;
        }
        newNode.parent = parent;

        // The heap property may now be violated: new node might be larger than parent.
        // Bubble it up until it's in the right place.
        heapifyUp(newNode);
    }

    /**
     * Removes and returns the node with the highest similarity (the root).
     *
     * Steps:
     *  1. Save root's data.
     *  2. Find the last node (at position `size`) and copy its data into root.
     *  3. Detach the last node from the tree.
     *  4. Heapify downward from root.
     *
     * @return the HeapNode that was at the root (max similarity), or null if empty
     */
    public HeapNode extractMax() {
        if (root == null) return null;

        // Capture the root's data to return it
        HeapNode result = new HeapNode(root.userId, root.similarity, root.ratings);

        if (size == 1) {
            // Only one node: just clear the tree
            root = null;
            size = 0;
            return result;
        }

        // Find the last node and copy its data into root
        HeapNode lastNode = navigateToPosition(size);
        root.userId     = lastNode.userId;
        root.similarity = lastNode.similarity;
        root.ratings    = lastNode.ratings;

        // Detach the last node from its parent
        HeapNode lastParent = lastNode.parent;
        if (lastParent != null) {
            if (lastParent.right == lastNode) {
                lastParent.right = null;
            } else {
                lastParent.left = null;
            }
        }
        lastNode.parent = null; //??? why did we set the lastNode's parent to null
        size--;

        // Root's data has changed — it might now be smaller than its children.
        // Push it down to restore the heap property.
        heapifyDown(root);

        return result;
    }

    /**
     * Extracts the top-k nodes by similarity.
     * This destructively removes them from the heap (they are the max k elements).
     *
     * @param k how many top-similar users to retrieve
     * @return list of HeapNodes in descending similarity order
     */
    public List<HeapNode> getTopK(int k) {
        List<HeapNode> result = new ArrayList<>();
        for (int i = 0; i < k && root != null; i++) {
            result.add(extractMax());
        }
        return result;
    }

    /** Returns the number of nodes currently in the heap. */
    public int size() {
        return size;
    }

    /** Returns true if the heap is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    // ─────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────

    /**
     * Navigates to the node at position `index` in the complete binary tree
     * using the BINARY PATH TRICK.
     *
     * In a complete binary tree numbered 1..n top-to-bottom left-to-right,
     * the binary representation of `index` gives the exact path from root:
     *   - Convert index to binary: e.g. 5 → "101"
     *   - Skip the leading '1'      → "01"
     *   - Follow each bit: 0=left, 1=right
     *   - You arrive at node 5
     *
     * This works for ANY position, so we use it to find:
     *   - The parent of the next insert position (size / 2)
     *   - The last node currently in the tree (size)
     *
     * @param index 1-based position in the tree (root = 1)
     * @return the HeapNode at that position
     */
    private HeapNode navigateToPosition(int index) {
        // Build the list of bits for this index (in reverse order)
        List<Integer> bits = new ArrayList<>();
        int i = index;
        while (i > 1) {
            bits.add(i % 2);   // 0 = was left child, 1 = was right child
            i /= 2;
        }
        // `bits` is in reverse order (from leaf toward root), so reverse it
        Collections.reverse(bits);
        // Now bits gives the path from root's children down to `index`
        // (the leading 1 representing the root itself has already been removed)

        HeapNode current = root;
        for (int bit : bits) {
            if (bit == 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return current;
    }

    /**
     * Heapify-UP: after inserting a node at the bottom, bubble it up
     * until the heap property (parent >= children) is restored.
     *
     * We compare the node's similarity with its parent's similarity.
     * If the node is larger, we SWAP THEIR DATA (not pointers) and
     * move our reference upward. We stop when:
     *   - We reach the root (no parent), OR
     *   - The node's similarity <= parent's similarity
     *
     * Visualization for insert(0.95) into:
     *
     *         [0.90]                      [0.95]
     *        /      \         →          /      \
     *     [0.80]  [0.95]              [0.80]  [0.90]
     *                ↑ new                        ↑ swapped up
     */
    private void heapifyUp(HeapNode node) {
        while (node.parent != null && node.similarity > node.parent.similarity) {
            swapData(node, node.parent);
            node = node.parent;   // move reference upward after swap
        }
    }

    /**
     * Heapify-DOWN: after replacing the root with the last node during
     * extractMax(), push the (now potentially too-small) root value down
     * until heap property is restored.
     *
     * At each step, compare the current node with its LARGER child.
     * If the larger child is bigger than the current node, swap and continue.
     * Stop when:
     *   - The node has no children (it's a leaf), OR
     *   - Both children are smaller than the node
     *
     * Visualization for extractMax() — last node (0.60) was copied to root:
     *
     *         [0.60]                      [0.88]
     *        /      \         →          /      \
     *     [0.88]  [0.75]              [0.60]  [0.75]
     *                                    ↑ bubbled down, stops (leaf)
     */
    private void heapifyDown(HeapNode node) {
        while (true) {
            HeapNode largest = node;

            // Check if left child is larger than current node
            if (node.left != null && node.left.similarity > largest.similarity) {
                largest = node.left;
            }

            // Check if right child is larger than current largest
            if (node.right != null && node.right.similarity > largest.similarity) {
                largest = node.right;
            }

            if (largest == node) {
                // Neither child is larger: heap property satisfied, stop
                break;
            }

            // Swap with the larger child and continue downward
            swapData(node, largest);
            node = largest;
        }
    }

    /**
     * Swaps the DATA of two heap nodes without touching their structural pointers.
     *
     * WHY swap data instead of relinking pointers?
     * Swapping pointers in a doubly-linked tree (with parent pointers) requires
     * updating 6+ references per swap and handling many edge cases (root, siblings,
     * etc.). It is bug-prone and gains nothing. Swapping only the payload fields
     * (similarity, userId, ratings) achieves the exact same logical result in 5 lines.
     *
     * @param a first node
     * @param b second node
     */
    private void swapData(HeapNode a, HeapNode b) {
        double  tmpSim  = a.similarity;
        int     tmpId   = a.userId;
        float[] tmpRat  = a.ratings;

        a.similarity = b.similarity;
        a.userId     = b.userId;
        a.ratings    = b.ratings;

        b.similarity = tmpSim;
        b.userId     = tmpId;
        b.ratings    = tmpRat;
    }

    // ─────────────────────────────────────────────────────────────────
    // DEBUGGING UTILITY
    // ─────────────────────────────────────────────────────────────────

    /**
     * Prints the heap level by level (BFS-style) for debugging.
     * Useful to verify the heap property visually.
     */
    public void printHeap() {
        if (root == null) { System.out.println("(empty heap)"); return; }
        java.util.Queue<HeapNode> queue = new java.util.LinkedList<>();
        queue.add(root);
        int level = 0;
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            System.out.print("Level " + level + ": ");
            for (int i = 0; i < levelSize; i++) {
                HeapNode n = queue.poll();
                System.out.printf("%.4f(u%d) ", n.similarity, n.userId);
                if (n.left  != null) queue.add(n.left);
                if (n.right != null) queue.add(n.right);
            }
            System.out.println();
            level++;
        }
    }
}