package ie.atu.sw;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class SimilarityFinder {
	// Find the most similar word to the word represented by the targetVector
	public String findMostSimilar(double[] targetVector, Map<String, double[]> allEmbeddings) {
		final SimilarityResult result = new SimilarityResult(); // Shared result object
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			// Iterate over all embeddings and calculate the similarity concurrently
			allEmbeddings.forEach((word, vector) -> scope.fork(() -> {
				double similarity = calculateCosineSimilarity(targetVector, vector);
				result.updateIfHigher(word, similarity);
				return null;
			}));
			// Wait for all threads to finish
			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during similarity calculation: " + e.getMessage());
		}
		// Return the word with the highest similarity
		return result.getWord();
	}

	// Calculate the cosine similarity between two vectors
	private double calculateCosineSimilarity(double[] vector1, double[] vector2) {
		double dotProduct = 0.0;
		double normA = 0.0;
		double normB = 0.0;
		// Calculate the dot product and the norms of the two vectors
		for (int i = 0; i < vector1.length; i++) {
			dotProduct += vector1[i] * vector2[i];
			normA += vector1[i] * vector1[i];
			normB += vector2[i] * vector2[i];
		}
		return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
	}

	// Helper class to store the best matching word and its similarity
	private static class SimilarityResult {
		// Volatile to ensure visibility across threads
		private volatile String bestWord = null;
		private volatile double highestSimilarity = Double.MIN_VALUE;

		// Update the best word if the similarity is higher than the current highest
		public synchronized void updateIfHigher(String word, double similarity) {
			if (similarity > highestSimilarity) {
				highestSimilarity = similarity;
				bestWord = word;
			}
		}

		// Get the best matching word
		public String getWord() {
			return bestWord;
		}
	}
}
