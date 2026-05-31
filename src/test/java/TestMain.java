import algorithm.Recommender;

import java.util.List;

/**
 * TestMain — verifies Teammate A's backend works before Teammate B starts the GUI.
 *
 * Run this directly to confirm:
 *   1. All CSV files load correctly
 *   2. The heap builds and extracts properly
 *   3. Recommendations come back as real movie titles
 *
 * Expected output example (will vary by target user selection):
 *
 *   [DataLoader] Loaded 600 users with 9018 movie columns from: .../main_data1.csv
 *   [DataLoader] Loaded 5401 movie titles from: .../movies.csv
 *   [DataLoader] Loaded 10 users with 9018 movie columns from: .../target_user.csv
 *   [Recommender] Ready. 600 main users, 10 target users, 5401 movie titles.
 *   [Recommender] Heap built with 600 users.
 *   [Recommender] Top 3 users extracted:
 *      userId=42   similarity=0.9312
 *      userId=187  similarity=0.8874
 *      userId=301  similarity=0.8120
 *   ...
 *   === Recommendations for target user 601 (X=3, K=5) ===
 *   1. Pulp Fiction (1994)
 *   2. The Shawshank Redemption (1994)
 *   ...
 */
public class TestMain {

    public static void main(String[] args) {
        // ── File paths — adjust these to match your project structure ──
        // If running from the project root directory:
        String mainDataPath    = "E:\\Projects\\Movie_Recommendation_System_using_Heaps\\src\\main\\java\\data\\main_data.csv";
        String moviesPath      = "E:\\Projects\\Movie_Recommendation_System_using_Heaps\\src\\main\\java\\data\\movies.csv";
        String targetUsersPath = "E:\\Projects\\Movie_Recommendation_System_using_Heaps\\src\\main\\java\\data\\target_user.csv";

        try {
            // ── Load everything ──────────────────────────────────────
            Recommender recommender = new Recommender(mainDataPath, moviesPath, targetUsersPath);

            // ── Test: list target user IDs (should be 601–610) ───────
            List<Integer> targetIds = recommender.getTargetUserIds();
            System.out.println("\nTarget user IDs: " + targetIds);

            // ── Test: get recommendations for the first target user ───
            int targetUserId = targetIds.get(0);   // e.g. user 601
            float[] targetVector = recommender.getTargetUserVector(targetUserId);

            int X = 3;   // top-3 most similar users
            int K = 5;   // top-5 movies from each

            List<String> recs = recommender.getRecommendations(targetVector, X, K);

            System.out.println("\n=== Recommendations for target user "
                    + targetUserId + " (X=" + X + ", K=" + K + ") ===");
            for (int i = 0; i < recs.size(); i++) {
                System.out.println((i + 1) + ". " + recs.get(i));
            }

            // ── Test: try a different target user ────────────────────
            if (targetIds.size() > 1) {
                int userId2 = targetIds.get(1);
                float[] vec2 = recommender.getTargetUserVector(userId2);
                List<String> recs2 = recommender.getRecommendations(vec2, X, K);
                System.out.println("\n=== Recommendations for target user "
                        + userId2 + " (X=" + X + ", K=" + K + ") ===");
                for (int i = 0; i < recs2.size(); i++) {
                    System.out.println((i + 1) + ". " + recs2.get(i));
                }
            }

            // ── Test: random movies for Screen B combo box ───────────
            List<String> randomMovies = recommender.getRandomMovieTitles(10);
            System.out.println("\n=== 10 random movies for Screen B combo box ===");
            randomMovies.forEach(System.out::println);

        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}