package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * SimilarityFinder computes the similarity between a target vector and a set of
 * word embeddings. It supports two algorithms: Cosine Similarity and Euclidean
 * Distance.
 *
 * This class uses structured concurrency to compute similarity scores in
 * parallel. It is thread-safe and can be used by multiple threads concurrently.
 */
public class SimilarityFinder {

	/**
	 * Enum representing the supported similarity algorithms. COSINE -
	 * CosineSimilarity and EUCLIDEAN - Euclidean Distance.
	 */
	public enum SimilarityAlgorithm {
		COSINE, EUCLIDEAN
	}

	private final SimilarityAlgorithm algorithm;

	/**
	 * SimilarityFinder constructor with the specified algorithm.
	 *
	 * @param algorithm The similarity algorithm to use (COSINE or EUCLIDEAN).
	 */
	public SimilarityFinder(SimilarityAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Finds the most similar word to the target vector using the specified
	 * algorithm.
	 *
	 * This method iterates through all embeddings and calculates their similarity
	 * to the target vector. It uses structured concurrency.
	 * 
	 * Running time: O(n), where n is the number of embeddings and the vector size
	 * never changes.
	 *
	 * @param targetVector  The target vector to compare against.
	 * @param allEmbeddings A map of word embeddings.
	 * @return The word with the highest similarity (for COSINE) or the lowest
	 *         distance (for EUCLIDEAN).
	 */
	public String findMostSimilar(double[] targetVector, ConcurrentHashMap<String, double[]> allEmbeddings) {
		final SimilarityResult result = new SimilarityResult();
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			// Fork a task for each word embedding
			allEmbeddings.forEach((word, vector) -> scope.fork(() -> {
				// Calculate similarity using the specified algorithm
				double similarity = switch (algorithm) {
				case COSINE -> calculateCosineSimilarity(targetVector, vector);
				case EUCLIDEAN -> calculateEuclideanDistance(targetVector, vector);
				};
				result.updateIfBetter(word, similarity); // Update the best word
				return null;
			}));

			scope.join(); // Wait for all tasks to complete
			scope.throwIfFailed(); // Propagate exceptions if any
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during similarity calculation: " + e.getMessage());
		}

		return result.getWord();
	}

	/**
	 * Calculates the Cosine Similarity between two vectors.
	 * 
	 * Running time: O(n), where n is the dimension of the vector.
	 * 
	 * @param vector1 The first vector.
	 * @param vector2 The second vector.
	 * @return The cosine similarity between the two vectors.
	 */
	private double calculateCosineSimilarity(double[] vector1, double[] vector2) {
		double dotProduct = 0.0, normA = 0.0, normB = 0.0;
		// Calculate dot product and norms
		for (int i = 0; i < vector1.length; i++) {
			dotProduct += vector1[i] * vector2[i];
			normA += vector1[i] * vector1[i];
			normB += vector2[i] * vector2[i];
		}
		// If norms are 0 return 0 to avoid division by zero else calculate cosine
		// similarity
		return (normA == 0 || normB == 0) ? 0.0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	/**
	 * Calculates the Euclidean Distance between two vectors.
	 * 
	 * Running time: O(n), where n is the dimension of the vector.
	 *
	 * @param vector1 The first vector.
	 * @param vector2 The second vector.
	 * @return The Euclidean distance between the two vectors.
	 */
	private double calculateEuclideanDistance(double[] vector1, double[] vector2) {
		double sumOfSquares = 0.0;
		// Calculate sum of squares of differences
		for (int i = 0; i < vector1.length; i++) {
			sumOfSquares += Math.pow(vector1[i] - vector2[i], 2);
		}
		// Return square root of sum of squares
		return Math.sqrt(sumOfSquares);
	}

	/**
	 * Internal helper class to store the best word and its similarity score.
	 *
	 * This class is thread-safe to allow concurrent updates from multiple threads.
	 */
	private class SimilarityResult {
		private volatile String bestWord = null;
		// Initialize best score to negative infinity for cosine similarity and positive
		// infinity for euclidean distance
		// Positive infinity and Negative infinity are used to ensure better scores.
		private volatile double bestScore = (algorithm == SimilarityAlgorithm.COSINE) ? Double.NEGATIVE_INFINITY
				: Double.POSITIVE_INFINITY;

		/**
		 * Updates the best word and score if the current score is better.
		 *
		 * @param word  The current word being evaluated.
		 * @param score The similarity score for the current word.
		 */
		public synchronized void updateIfBetter(String word, double score) {
			boolean isBetter = (algorithm == SimilarityAlgorithm.COSINE && score > bestScore)
					|| (algorithm == SimilarityAlgorithm.EUCLIDEAN && score < bestScore);

			if (isBetter) {
				bestScore = score;
				bestWord = word;
			}
		}

		/**
		 * Returns the word with the best similarity score.
		 *
		 * @return The word with the best score.
		 */
		public String getWord() {
			return bestWord;
		}
	}
}
