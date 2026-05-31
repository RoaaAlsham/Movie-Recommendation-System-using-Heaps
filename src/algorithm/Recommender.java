/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package algorithm;
import data.DataLoader;
import heap.HeapNode;
import heap.MaxHeap;

import java.io.IOException;
import java.util.*;

/**
 *
 * @author macbookpro
 */
public class Recommender {
    


/**
 * Recommender — orchestrates the full recommendation pipeline.
 *
 * This is the class Teammate B calls from the GUI. It:
 *   1. Holds the loaded data (user matrix, movie titles, column index maps)
 *   2. Exposes one main method: getRecommendations(targetVector, X, K)
 *
 * ═══════════════════════════════════════════════════════════════════════
 * FULL ALGORITHM WALKTHROUGH
 * ═══════════════════════════════════════════════════════════════════════
 *
 * Given a target user's ratings vector (length = 9018):
 *
 *  Step 1 — Compute similarity
 *    For each of the 600 users in main_data1:
 *      sim = CosineSimilarity.compute(targetVector, userVector)
 *    This is O(n × m) where n=600 users, m=9018 movies.
 *
 *  Step 2 — Build the max-heap
 *    Insert every user as a HeapNode(userId, sim, ratings).
 *    After all 600 inserts, the ROOT holds the most similar user.
 *    Heap build = O(n log n).
 *
 *  Step 3 — Extract top-X users
 *    Call extractMax() X times → O(X log n).
 *    Result: X HeapNodes in descending similarity order.
 *
 *  Step 4 — Get top-K movies per user
 *    For each of the X extracted users:
 *      - Collect their (movieId, rating) pairs where rating > 0
 *      - Exclude movies the TARGET user has already rated
 *      - Sort by rating descending
 *      - Take the first K
 *    Total: O(X × m log m) in worst case, typically much less (sparse).
 *
 *  Step 5 — Translate movieIds to titles
 *    Use movieTitles HashMap: O(1) per lookup.
 *
 *  Final result: List<String> of X×K movie title strings.
 *
 * ═══════════════════════════════════════════════════════════════════════
 * REVERSE COLUMN INDEX MAP — colIndexToMovieId
 * ═══════════════════════════════════════════════════════════════════════
 * DataLoader gives us movieId → colIndex so we know which array position
 * corresponds to which movie.
 * But when we iterate over a ratings array (position 0, 1, 2, ...) and
 * want to know which movie each position represents, we need the REVERSE:
 *   colIndex → movieId
 * So we build this reverse map once at load time.
 *
 * Example:
 *   movieIdToColIndex: {1→0, 2→1, 3→2, 4→3, ...}
 *   colIndexToMovieId: {0→1, 1→2, 2→3, 3→4, ...}   ← reverse
 *
 * For this data-set they happen to be the identity mapping (movieId i is
 * at column i-1), but we build it properly so it works for any ordering.
 */


    // ── Loaded data ───────────────────────────────────────────────────
    /** All 600 users from main_data1.csv: userId → ratingsArray */
    private final LinkedHashMap<Integer, float[]> userMatrix;

    /** All 10 target users from target_user.csv: userId → ratingsArray */
    private final LinkedHashMap<Integer, float[]> targetUsers;

    /** Movie title lookup: movieId → title string */
    private final HashMap<Integer, String> movieTitles;

    /**
     * Maps a 0-based column index in the ratings array back to its movieId.
     * Built by reversing the movieIdToColIndex map from DataLoader.
     * colIndexToMovieId[i] = the movieId stored at ratings[i]
     */
    private final int[] colIndexToMovieId;

    /** The number of movie columns (9018 for this dataset) */
    private final int numMovies;

    // ─────────────────────────────────────────────────────────────────
    // CONSTRUCTOR
    // ─────────────────────────────────────────────────────────────────

    /**
     * Creates a Recommender by loading all three CSV files.
     *
     * @param mainDataPath    path to main_data1.csv
     * @param moviesPath      path to movies.csv
     * @param targetUsersPath path to target_user.csv
     * @throws IOException if any file cannot be read
     */
    public Recommender(String mainDataPath, String moviesPath, String targetUsersPath)
            throws IOException {

        // 1. Load the main user-movie matrix
        //    movieIdToColIndex will be populated by loadUserMatrix
        Map<Integer, Integer> movieIdToColIndex = new HashMap<>();
        userMatrix = DataLoader.loadUserMatrix(mainDataPath, movieIdToColIndex);

        // 2. Build the REVERSE map: colIndex → movieId
        //    We need an array of size = number of movie columns
        numMovies        = movieIdToColIndex.size();
        colIndexToMovieId = new int[numMovies];
        for (Map.Entry<Integer, Integer> entry : movieIdToColIndex.entrySet()) {
            int movieId  = entry.getKey();
            int colIndex = entry.getValue();
            colIndexToMovieId[colIndex] = movieId;
            // Now colIndexToMovieId[0] = 1 (movieId 1 is at column 0)
            //     colIndexToMovieId[1] = 2 (movieId 2 is at column 1)
            //     etc.
        }

        // 3. Load movie titles
        movieTitles = DataLoader.loadMovieTitles(moviesPath);

        // 4. Load target users (same CSV format, separate file)
        //    We pass a fresh map but ignore its output — we already have
        //    colIndexToMovieId from the main matrix (identical headers).
        Map<Integer, Integer> dummy = new HashMap<>();
        targetUsers = DataLoader.loadTargetUsers(targetUsersPath, dummy);

        System.out.println("[Recommender] Ready. "
                + userMatrix.size() + " main users, "
                + targetUsers.size() + " target users, "
                + movieTitles.size() + " movie titles.");
    }

    // ─────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────

    /**
     * Returns the IDs of all target users (for Teammate B's ComboBox).
     * Order matches the CSV row order (601, 602, ..., 610).
     *
     * @return list of target userId integers
     */
    public List<Integer> getTargetUserIds() {
        return new ArrayList<>(targetUsers.keySet());
    }

    /**
     * Returns the ratings vector for a specific target user.
     * Teammate B calls this after the user selects a userId in the ComboBox.
     *
     * @param userId the target user's ID (e.g. 601)
     * @return the float[] ratings array, or null if userId not found
     */
    public float[] getTargetUserVector(int userId) {
        return targetUsers.get(userId);
    }

    /**
     * Picks 10 random movie titles from the loaded movie set.
     * Used by Teammate B to populate the ComboBox in Screen B.
     *
     * @return List of 10 randomly selected movie title strings
     */
    public List<String> getRandomMovieTitles(int count) {
        List<String> allTitles = new ArrayList<>(movieTitles.values());
        Collections.shuffle(allTitles, new Random());
        return allTitles.subList(0, Math.min(count, allTitles.size()));
    }

    /**
     * Looks up the column index for a given movie title.
     * Used by Teammate B (Screen B) to convert a selected movie title
     * back to a column index so the user-vector can be built.
     *
     * Returns -1 if the title is not found.
     *
     * @param title the movie title string (must match movies.csv exactly)
     * @return 0-based column index into the ratings array, or -1 if not found
     */
    public int getColIndexForTitle(String title) {
        for (Map.Entry<Integer, String> entry : movieTitles.entrySet()) {
            if (entry.getValue().equals(title)) {
                int movieId = entry.getKey();
                // Find colIndex: scan colIndexToMovieId
                for (int col = 0; col < colIndexToMovieId.length; col++) {
                    if (colIndexToMovieId[col] == movieId) return col;
                }
            }
        }
        return -1;
    }

    /**
     * Creates an empty user vector of the correct length (= number of movies).
     * Teammate B uses this to build a fresh ratings vector for Screen B.
     *
     * @return float[] of length numMovies, all zeros
     */
    public float[] createEmptyUserVector() {
        return new float[numMovies];
    }

    /**
     * THE MAIN METHOD — computes movie recommendations for a given user vector.
     *
     * This is the method Teammate B calls from both GUI screens.
     *
     * @param targetVector the target user's ratings array (length = numMovies)
     *                     0 means "not rated", 1–5 means rated.
     * @param X            number of similar users to extract from the heap
     * @param K            number of top-rated movies to take from each similar user
     * @return List<String> of X×K movie titles (may be fewer if not enough rated movies)
     */
    public List<String> getRecommendations(float[] targetVector, int X, int K) {
        if (targetVector == null || targetVector.length != numMovies) {
            throw new IllegalArgumentException("Target vector must have length " + numMovies);
        }
        if (X <= 0 || K <= 0) {
            throw new IllegalArgumentException("X and K must be positive integers.");
        }

        // ── Step 1 & 2: Compute similarity for all users and insert into heap ──
        MaxHeap heap = new MaxHeap();

        for (Map.Entry<Integer, float[]> entry : userMatrix.entrySet()) {
            int     userId  = entry.getKey();
            float[] ratings = entry.getValue();

            double sim = CosineSimilarity.compute(targetVector, ratings);

            // Insert this user into the heap.
            // After all inserts, root = most similar user.
            heap.insert(new HeapNode(userId, sim, ratings));
        }

        System.out.println("[Recommender] Heap built with " + heap.size() + " users.");

        // ── Step 3: Extract top-X most similar users ──────────────────────────
        List<HeapNode> topUsers = heap.getTopK(X);

        System.out.println("[Recommender] Top " + X + " users extracted:");
        for (HeapNode node : topUsers) {
            System.out.printf("   userId=%d  similarity=%.4f%n", node.userId, node.similarity);
        }

        // ── Step 4 & 5: Get top-K movies for each similar user ────────────────
        List<String> recommendations = new ArrayList<>();

        for (HeapNode similarUser : topUsers) {
            List<String> topMovies = getTopKMoviesForUser(similarUser.ratings, targetVector, K);
            recommendations.addAll(topMovies);
            System.out.println("[Recommender] userId=" + similarUser.userId
                    + " contributed " + topMovies.size() + " movies.");
        }

        return recommendations;
    }

    // ─────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────

    /**
     * Finds the top-K highest rated movies for one similar user,
     * excluding movies the target user has already rated.
     *
     * HOW IT WORKS:
     *  1. Build a list of (rating, movieTitle) pairs from the similar user's
     *     ratings array, skipping movies the target has already seen.
     *  2. Sort the list by rating descending (highest first).
     *  3. Return the first K titles.
     *
     * WHY EXCLUDE TARGET'S ALREADY-RATED MOVIES?
     *  Recommending "Toy Story" to someone who already rated "Toy Story" is
     *  pointless. We only suggest new movies.
     *
     * WHY NOT USE ANOTHER HEAP HERE?
     *  We could insert each rated movie into a heap and extract K max-rated ones.
     *  However, each user has at most ~200 rated movies, so a simple sort is
     *  perfectly fast (200 log 200 ≈ 1500 comparisons). Using another heap for
     *  such small input would add code complexity for no real gain.
     *
     * @param userRatings    the similar user's ratings array
     * @param targetRatings  the target user's ratings array (to exclude already-seen)
     * @param K              how many movies to return
     * @return list of up to K movie title strings, sorted by rating descending
     */
    private List<String> getTopKMoviesForUser(float[] userRatings, float[] targetRatings, int K) {
        // Build a list of [rating, movieTitle] for all movies this user rated
        // that the TARGET has NOT yet rated.
        List<float[]> ratedMovies = new ArrayList<>();
        // We store: ratedMovies[i] = {rating, colIndex}  (colIndex to look up title later)

        for (int col = 0; col < userRatings.length; col++) {
            // Skip unrated movies (rating = 0)
            if (userRatings[col] <= 0) continue;

            // Skip movies the target user already rated
            if (targetRatings[col] > 0) continue;

            // Get the movieId for this column
            int movieId = colIndexToMovieId[col];

            // Only include if we have a title for this movie
            if (!movieTitles.containsKey(movieId)) continue;

            ratedMovies.add(new float[]{userRatings[col], col});
        }

        // Sort by rating descending: highest-rated movies first
        ratedMovies.sort((a, b) -> Float.compare(b[0], a[0]));

        // Take the first K and translate to titles
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < Math.min(K, ratedMovies.size()); i++) {
            int    colIndex = (int) ratedMovies.get(i)[1];
            int    movieId  = colIndexToMovieId[colIndex];
            String title    = movieTitles.get(movieId);
            titles.add(title);
        }

        return titles;
    }
}

