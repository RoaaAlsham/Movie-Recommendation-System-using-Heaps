package data;

import java.io.*;
import java.util.*;

/**
 * DataLoader — reads and parses all three CSV files into Java data structures.
 *
 * ═══════════════════════════════════════════════════════════════════════
 * WHY THESE DATA STRUCTURES?
 * ═══════════════════════════════════════════════════════════════════════
 *
 * 1. USER-MOVIE MATRIX  →  LinkedHashMap<Integer, float[]>
 *    ─────────────────────────────────────────────────────
 *    We need to:
 *      a) Iterate over ALL users to compute similarity (for-each loop)
 *      b) Look up a specific user's ratings by ID in O(1)
 *
 *    Options considered:
 *    • 2D array (int[][]):  Fast, but we'd need to know the exact number of
 *      users and movies at compile time, and userId would have to map to
 *      a 0-based row index manually. Fragile if the CSV changes.
 *
 *    • ArrayList<float[]>: Good for iteration, but O(n) lookup by userId.
 *
 *    • HashMap<Integer, float[]>: O(1) lookup by userId, easy iteration.
 *      We use LinkedHashMap specifically because it PRESERVES INSERTION ORDER
 *      (the order rows appear in the CSV). This makes iteration predictable
 *      and debugging easier.
 *
 *    The ratings are stored as float[] (not double[] or int[]):
 *      • Ratings are 0–5 — they fit in float with no precision loss.
 *      • float is 4 bytes vs double's 8 bytes. With 600 users × 9018 movies,
 *        a float[] matrix uses 600 × 9018 × 4 = ~21 MB.
 *        A double[] matrix would use ~43 MB — double the memory for no benefit.
 *
 *    The COLUMN INDEX MAP (movieIdToColIndex):
 *      The CSV header row is: user_id, 1, 2, 3, ..., 9018
 *      The column numbers ARE the movieIds. Column 0 = "user_id" (skip it),
 *      column 1 = movieId 1, column 2 = movieId 2, etc.
 *      For this data-set they happen to be sequential, but we build the map
 *      explicitly so the code works even if movie IDs are non-sequential.
 *
 * 2. MOVIE TITLES  →  HashMap<Integer, String>
 *    ──────────────────────────────────────────
 *    movies.csv has 5401 rows: movieId, title, genres
 *    We only need movieId → title for the final recommendation display.
 *    HashMap gives O(1) lookup by movieId — we call this thousands of times
 *    (once per movie per user during recommendation), so O(1) matters.
 *
 * 3. TARGET USERS  →  LinkedHashMap<Integer, float[]>
 *    ──────────────────────────────────────────────────
 *    target_user.csv has 10 rows, same format as main_data1.csv.
 *    We store them as LinkedHashMap<userId, ratingsArray> so Teammate B can:
 *      • Populate the ComboBox with the user IDs (keys)
 *      • Look up the selected user's vector in O(1) (values)
 *
 * ═══════════════════════════════════════════════════════════════════════
 * CSV FORMAT NOTES (from actual file inspection)
 * ═══════════════════════════════════════════════════════════════════════
 * main_data1.csv:
 *   Header:  user_id,1,2,3,...,9018        (9019 columns total)
 *   Row:     1,4,0,0,0,4,0,...             (userId then 9018 ratings)
 *   Users:   600 rows (IDs 1–600)
 *
 * movies.csv:
 *   Header:  movieId,title,genres
 *   Row:     1,Toy Story (1995),Adventure|Animation|...
 *   Movies:  5401 rows
 *   NOTE:    The title may contain commas (e.g. "Grumpier Old Men, The (1995)")
 *            so we split on comma only TWICE (limit=3) to avoid breaking titles.
 *
 * target_user.csv:
 *   Same format as main_data1.csv but only 10 rows (IDs 601–610)
 */
public class DataLoader {

    /**
     * Loads the user-movie matrix from main_data1.csv (or target_user.csv).
     *
     * Returns a LinkedHashMap where:
     *   key   = userId (integer from first column)
     *   value = float[] of length (numMovies), indexed by column position 0..n-1
     *
     * Also returns the column-index → movieId mapping via the movieIdToColIndex 
     * output parameter so Recommender can translate column positions back to IDs.
     * 
     * ??? the function takes movieIdToColIndex as input parameter and its not returned so how it get used
     *
     * @param filePath         path to the CSV file
     * @param movieIdToColIndex a Map<Integer,Integer> to be FILLED by this method:
     *                          movieId → column index (0-based, excluding user_id col)
     *                          Pass in a new empty HashMap, it will be populated.
     * @return LinkedHashMap<userId, ratingsArray>
     */
    public static LinkedHashMap<Integer, float[]> loadUserMatrix(
            String filePath,
            Map<Integer, Integer> movieIdToColIndex) throws IOException {

        LinkedHashMap<Integer, float[]> userMatrix = new LinkedHashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        // ── Parse header row ──────────────────────────────────────
        // Header format: "user_id,1,2,3,...,9018"
        // We build movieIdToColIndex from this so we know which column = which movieId
        String headerLine = reader.readLine();
        if (headerLine == null) throw new IOException("Empty file: " + filePath);

        String[] headers = headerLine.split(",");
        // headers[0] = "user_id"  (skip)
        // headers[1] = "1"        → movieId 1, column index 0
        // headers[2] = "2"        → movieId 2, column index 1
        // ...
        int numMovies = headers.length - 1;  // subtract 1 for the user_id column

        for (int col = 1; col < headers.length; col++) {
            int movieId  = Integer.parseInt(headers[col].trim());
            int colIndex = col - 1;   // 0-based index into the ratings array
            movieIdToColIndex.put(movieId, colIndex);
        }

        // ── Parse data rows ───────────────────────────────────────
        // Each row: "userId,r1,r2,...,r9018"
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;   // skip blank lines at end of file

            String[] parts = line.split(",");
            int userId = Integer.parseInt(parts[0].trim());

            float[] ratings = new float[numMovies];
            for (int col = 1; col < parts.length; col++) {
                ratings[col - 1] = Float.parseFloat(parts[col].trim());
            }

            userMatrix.put(userId, ratings);
        }

        reader.close();
        System.out.println("[DataLoader] Loaded " + userMatrix.size()
                + " users with " + numMovies + " movie columns from: " + filePath);
        return userMatrix;
    }

    /**
     * Loads the movie title lookup table from movies.csv.
     *
     * Returns a HashMap where:
     *   key   = movieId (integer)
     *   value = title string (e.g. "Toy Story (1995)")
     *
     * NOTE: Some movie titles contain commas, so we split with a limit of 3:
     *   "1,Toy Story (1995),Adventure|Animation"  → ["1", "Toy Story (1995)", "Adventure|Animation"]
     *   "3,Grumpier Old Men, The (1995),Comedy"   → ["3", "Grumpier Old Men, The (1995)", "Comedy"]
     *                                                       ↑ title preserved correctly
     *
     * @param filePath path to movies.csv
     * @return HashMap<movieId, title>
     */
    public static HashMap<Integer, String> loadMovieTitles(String filePath) throws IOException {
        HashMap<Integer, String> titles = new HashMap<>();

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        reader.readLine(); // skip header: "movieId,title,genres"

        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Limit split to 3 parts: [movieId, title, genres]
            // Without limit, a title like "Heat, The" would split into 4 parts.
            String[] parts = line.split(",", 3);
            if (parts.length < 2) continue;

            int    movieId = Integer.parseInt(parts[0].trim());
            String title   = parts[1].trim();
            titles.put(movieId, title);
        }

        reader.close();
        System.out.println("[DataLoader] Loaded " + titles.size() + " movie titles from: " + filePath);
        return titles;
    }

    /**
     * Loads target users from target_user.csv.
     *
     * Uses the same format as the main matrix, so we reuse loadUserMatrix().
     * The movieIdToColIndex returned should be identical to the one from
     * the main matrix (same header), so you can safely use either for
     * column-to-movieId lookups.
     *
     * @param filePath         path to target_user.csv
     * @param movieIdToColIndex will be filled with movieId → colIndex mapping
     * @return LinkedHashMap<userId, ratingsArray> for the 10 target users
     */
    public static LinkedHashMap<Integer, float[]> loadTargetUsers(
            String filePath,
            Map<Integer, Integer> movieIdToColIndex) throws IOException {
        // Reuse the same parsing logic — the file format is identical
        return loadUserMatrix(filePath, movieIdToColIndex);
    }
}