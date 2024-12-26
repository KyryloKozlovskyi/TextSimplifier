package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class SimilarityFinder {

	public enum SimilarityAlgorithm {
		COSINE, EUCLIDEAN
	}

	private final SimilarityAlgorithm algorithm;

	public SimilarityFinder(SimilarityAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Finds the most similar word to the target vector using the specified
	 * algorithm.
	 *
	 * @param targetVector  The target vector to compare.
	 * @param allEmbeddings A map of word embeddings.
	 * @return The word with the highest similarity to the target vector.
	 */
	public String findMostSimilar(double[] targetVector, ConcurrentHashMap<String, double[]> allEmbeddings) {
		final SimilarityResult result = new SimilarityResult();

		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			allEmbeddings.forEach((word, vector) -> scope.fork(() -> {
				double similarity = switch (algorithm) {
				case COSINE -> calculateCosineSimilarity(targetVector, vector);
				case EUCLIDEAN -> calculateEuclideanDistance(targetVector, vector);
				};

				result.updateIfBetter(word, similarity);
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

	private double calculateCosineSimilarity(double[] vector1, double[] vector2) {
		double dotProduct = 0.0, normA = 0.0, normB = 0.0;

		for (int i = 0; i < vector1.length; i++) {
			dotProduct += vector1[i] * vector2[i];
			normA += vector1[i] * vector1[i];
			normB += vector2[i] * vector2[i];
		}

		return (normA == 0 || normB == 0) ? 0.0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	private double calculateEuclideanDistance(double[] vector1, double[] vector2) {
		double sumOfSquares = 0.0;

		for (int i = 0; i < vector1.length; i++) {
			sumOfSquares += Math.pow(vector1[i] - vector2[i], 2);
		}

		return Math.sqrt(sumOfSquares);
	}

	private class SimilarityResult {
		private volatile String bestWord = null;
		private volatile double bestScore = (algorithm == SimilarityAlgorithm.COSINE) ? Double.NEGATIVE_INFINITY
				: Double.POSITIVE_INFINITY;

		public synchronized void updateIfBetter(String word, double score) {
			boolean isBetter = (algorithm == SimilarityAlgorithm.COSINE && score > bestScore)
					|| (algorithm == SimilarityAlgorithm.EUCLIDEAN && score < bestScore);

			if (isBetter) {
				bestScore = score;
				bestWord = word;
			}
		}

		public String getWord() {
			return bestWord;
		}
	}
}
