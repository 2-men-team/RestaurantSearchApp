package ua.kpi.restaurants.logic.common.utils.metrics;

import org.jetbrains.annotations.NotNull;

/**
 * Implements <a href="https://en.wikipedia.org/wiki/Levenshtein_distance">Levenstein distance</a> metric
 *
 * It is used to retrieve similarity of two words
 *
 * @see Metric
 */
public final class Levenstein implements Metric<String, Integer> {
  private static final long serialVersionUID = -928827437231971185L;

  /**
   * Performs similarity retrieval
   *
   * Its time complexity is {@code O(m * n)} and memory complexity is {@code &theta;(n)}
   * where {@code m} and {@code n} are sizes of two words respectively
   *
   * @param a first word (must not be {@code null})
   * @param b second word (must not be {@code null})
   * @return retrieved similarity
   */
  @Override
  @NotNull
  public Integer apply(@NotNull String a, @NotNull String b) {
    int n = a.length(), m = b.length();

    if (n == 0) return m;
    if (m == 0) return n;

    int[] buffer = new int[n + 1]; // buffer[0] is never used
    for (int i = 1; i <= n; i++) buffer[i] = i;

    for (int i = 1; i <= m; i++) {
      int left = i, diagonal = i - 1;

      for (int j = 1; j <= n; j++) {
        int up = buffer[j];
        int cost = b.charAt(i - 1) != a.charAt(j - 1) ? 1 : 0;
        buffer[j] = Math.min(up + 1, Math.min(left + 1, diagonal + cost));
        left = buffer[j];
        diagonal = up;
      }
    }

    return buffer[n];
  }
}
