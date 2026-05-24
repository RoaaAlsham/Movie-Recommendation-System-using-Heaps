package algorithm;

/**
 * CosineSimilarity — measures how "similar" two users are based on their ratings.
 *
 * ═══════════════════════════════════════════════════════════════════════
 * WHY COSINE SIMILARITY ?
 * ═══════════════════════════════════════════════════════════════════════
 *
 * Our user vectors are SPARSE: most of the 9018 values are 0 (unrated).
 * User A might have rated 50 movies. User B might have rated 200 movies.
 * The raw overlap might be only 10 movies.
 *
 * COSINE SIMILARITY only cares about the ANGLE between two vectors in
 * 9018-dimensional space. It measures whether users rate the SAME movies
 * similarly, ignoring how many movies each rated total.
 *
 * FORMULA:
 *
 *   cos(A, B) = (A · B) / (||A|| × ||B||)
 *
 *   where:
 *     A · B   = dot product   = Σ (a_i × b_i)   [sum over all movies]
 *     ||A||   = magnitude     = √(Σ a_i²)
 *     ||B||   = magnitude     = √(Σ b_i²)
 *
 * RESULT RANGE: 0.0 to 1.0 (since all ratings are non-negative: 0–5)
 *   • 1.0 = perfectly similar rating patterns (same movies, same scores)
 *   • 0.0 = no overlap at all (no movie rated by both users)
 *
 * EXAMPLE:
 *   User A = [5, 3, 0, 0, 4]
 *   User B = [4, 0, 0, 0, 5]
 *
 *   dot     = 5×4 + 3×0 + 0×0 + 0×0 + 4×5 = 40
 *   ||A||   = √(25 + 9 + 0 + 0 + 16) = √50 ≈ 7.071
 *   ||B||   = √(16 + 0 + 0 + 0 + 25) = √41 ≈ 6.403
 *   cos     = 40 / (7.071 × 6.403) ≈ 0.883  ← quite similar
 *
 * SPARSE VECTOR OPTIMIZATION NOTE:
 *   With 9018 dimensions mostly zero, we loop over all 9018 positions.
 *   The multiply 0×anything = 0, so only the ~50–200 non-zero positions
 *   actually contribute to the dot product. The loop is still O(n) but
 *   cheap in practice because most multiplications are just 0.
 */
public class CosineSimilarity {

    /**
     * Computes cosine similarity between two rating vectors.
     *
     * Both arrays must be the same length (number of movies in the dataset).
     * Values of 0 mean "not rated" — they contribute nothing to the similarity.
     *
     * @param a ratings vector for user A (length = number of movies)
     * @param b ratings vector for user B (length = number of movies)
     * @return similarity in [0.0, 1.0], or 0.0 if either vector is all zeros
     */
    public static double compute(float[] a, float[] b) {
        if (a == null || b == null || a.length != b.length) {
            throw new IllegalArgumentException(
                "Both rating vectors must be non-null and the same length."
            );
        }

        double dotProduct    = 0.0;
        double magnitudeA    = 0.0;
        double magnitudeB    = 0.0;

        for (int i = 0; i < a.length; i++) {
            dotProduct += (double) a[i] * b[i];   // contribution to dot product
            magnitudeA += (double) a[i] * a[i];   // sum of squares for ||A||
            magnitudeB += (double) b[i] * b[i];   // sum of squares for ||B||
        }

        // EDGE CASE: one of the users has rated zero movies.
        // Their vector is all zeros, magnitude = 0, division is undefined.
        // We define similarity as 0.0 in this case (no common ground).
        if (magnitudeA == 0.0 || magnitudeB == 0.0) {
            return 0.0; //avoid dividing by zero :)
        }

        // Final formula: dot / (sqrt(sumA²) × sqrt(sumB²))
        return dotProduct / (Math.sqrt(magnitudeA) * Math.sqrt(magnitudeB));
    }
}