package ie.atu.sw;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class SimilarityFinder {

	public String findMostSimilar(double[] targetVector, Map<String, double[]> allEmbeddings) {
		if (targetVector == null || targetVector.length == 0) {
			throw new IllegalArgumentException("Target vector cannot be null or empty");
		}

		final MinDistanceResult result = new MinDistanceResult();

		// Calculate the Euclidean distance between the target vector and each word's
		// vector
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			allEmbeddings.forEach((word, vector) -> scope.fork(() -> {
				double distance = calculateEuclideanDistance(targetVector, vector);
				result.updateIfCloser(word, distance);
				return null;
			}));

			scope.join(); // Wait for all tasks to complete
			scope.throwIfFailed(); // Propagate exceptions if any
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during distance calculation: " + e.getMessage());
		}

		return result.getWord();
	}

	// Calculate the Euclidean distance between two vectors
	private double calculateEuclideanDistance(double[] vector1, double[] vector2) {
		double sumOfSquares = 0.0;
		for (int i = 0; i < vector1.length; i++) {
			sumOfSquares += Math.pow(vector1[i] - vector2[i], 2);
		}
		return Math.sqrt(sumOfSquares);
	}

	// Helper class to store the closest word and its distance
	private static class MinDistanceResult {
		private volatile String closestWord = null;
		private volatile double minDistance = Double.MAX_VALUE;

		public synchronized void updateIfCloser(String word, double distance) {
			if (distance < minDistance) {
				minDistance = distance;
				closestWord = word;
			}
		}

		public String getWord() {
			return closestWord;
		}
	}
}
