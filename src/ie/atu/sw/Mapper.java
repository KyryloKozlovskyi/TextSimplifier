package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class Mapper {
	// Generate mapping between words and their embeddings
	public ConcurrentHashMap<String, double[]> generateMapping(ConcurrentHashMap<String, double[]> embeddings,
			CopyOnWriteArraySet<String> words) {
		// Create a concurrent hash map to store the embeddings
		ConcurrentHashMap<String, double[]> googleEmbeddings = new ConcurrentHashMap<>();
		// Use structured concurrency to process words and map them to their embeddings
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			for (String word : words) {
				scope.fork(() -> {
					double[] embedding = embeddings.get(word);
					// If the embedding is found, add it to the map else print an error message
					if (embedding != null) {
						googleEmbeddings.put(word, embedding);
					} else {
						System.err.println("No embedding found for word: " + word);
					}
					return null;
				});
			}
			// Wait for all threads to finish
			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Task interrupted: " + e.getMessage());
		}
		return googleEmbeddings;
	}
}
