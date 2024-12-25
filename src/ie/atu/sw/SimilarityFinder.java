package ie.atu.sw;

import java.util.Map;
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

	public String findMostSimilar(double[] targetVector, Map<String, double[]> allEmbeddings) {
		final SimilarityResult result = new SimilarityResult(); // Shared result object

		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			allEmbeddings.forEach((word, vector) -> scope.fork(() -> {
				double similarity = (algorithm == SimilarityAlgorithm.COSINE)
						? calculateCosineSimilarity(targetVector, vector)
						: calculateEuclideanDistance(targetVector, vector);

				result.updateIfBetter(word, similarity, algorithm);
				return null;
			}));

			scope.join();
			scope.throwIfFailed();
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

		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	private double calculateEuclideanDistance(double[] vector1, double[] vector2) {
		double sumOfSquares = 0.0;

		for (int i = 0; i < vector1.length; i++) {
			sumOfSquares += Math.pow(vector1[i] - vector2[i], 2);
		}

		return Math.sqrt(sumOfSquares);
	}

	private static class SimilarityResult {
		private volatile String bestWord = null;
		private volatile double bestScore = Double.MAX_VALUE;

		public synchronized void updateIfBetter(String word, double score, SimilarityAlgorithm algorithm) {
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
