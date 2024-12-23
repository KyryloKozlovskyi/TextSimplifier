package ie.atu.sw;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class TextSimplifier {
	private final SimilarityFinder similarityFinder; // Dependency

	// Constructor
	public TextSimplifier(SimilarityFinder similarityFinder) {
		this.similarityFinder = similarityFinder;
	}

	// Simplify text by replacing words with their most similar words from a target
	public String simplifyText(String text, Map<String, double[]> allEmbeddings,
			Map<String, double[]> targetEmbeddings) {
		String[] words = text.split("\\s+");
		String[] results = new String[words.length]; // Array to store results concurrently
		// Process each word in parallel
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			for (int i = 0; i < words.length; i++) {
				final int index = i; // Store index for lambda
				scope.fork(() -> {
					// Find the most similar word from the target embeddings
					String word = words[index];
					double[] embedding = allEmbeddings.get(word.toLowerCase());
					// Store the result in the results array at the same indexes to maintain order
					results[index] = (embedding != null) ? similarityFinder.findMostSimilar(embedding, targetEmbeddings)
							: word;
					return null;
				});
			}
			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during text simplification: " + e.getMessage());
		}
		return String.join(" ", results).trim();
	}
}
