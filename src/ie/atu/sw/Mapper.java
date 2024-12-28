package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * Mapper is responsible for generating a mapping between words and their
 * corresponding embeddings.
 */
public class Mapper {

	/**
	 * Generates a mapping between words and their embeddings.
	 *
	 * This method processes the given set of words and retrieves their
	 * corresponding embeddings from the provided map. It uses structured
	 * concurrency to handle each word in parallel.
	 *
	 * @param embeddings The map of all embeddings, where keys are words and values
	 *                   are their vector representations.
	 * @param words      The set of words to map to their embeddings.
	 * @return A map containing the words and their corresponding embeddings.
	 *
	 *         Running time: O(n), where n is the number of words in the input set.
	 */
	public ConcurrentHashMap<String, double[]> generateMapping(ConcurrentHashMap<String, double[]> embeddings,
			CopyOnWriteArraySet<String> words) {
		// Create a concurrent hash map to store the resulting embeddings
		ConcurrentHashMap<String, double[]> googleEmbeddings = new ConcurrentHashMap<>();
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			for (String word : words) {
				// Fork a new thread to process each word
				scope.fork(() -> {
					double[] embedding = embeddings.get(word);
					if (embedding != null) {
						googleEmbeddings.put(word, embedding); // Store the embedding in the result map
					} else {
						System.err.println("No embedding found for word: " + word);
					}
					return null;
				});
			}
			// Wait for all threads to complete and propagate exceptions if any
			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Task interrupted: " + e.getMessage());
		}

		return googleEmbeddings;
	}
}
