package ie.atu.sw;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class Mapper {

	public ConcurrentHashMap<String, double[]> generateMapping(Map<String, double[]> embeddings, Set<String> words) {
		ConcurrentHashMap<String, double[]> googleEmbeddings = new ConcurrentHashMap<>();

		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			for (String word : words) {
				scope.fork(() -> {
					double[] embedding = embeddings.get(word);
					if (embedding != null) {
						googleEmbeddings.put(word, embedding);
					} else {
						System.err.println("No embedding found for word: " + word);
					}
					return null;
				});
			}

			scope.join(); // Wait for all tasks to complete
			scope.throwIfFailed(); // Propagate exceptions if any
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Task interrupted: " + e.getMessage());
		}

		return googleEmbeddings;
	}

	// DEBUG
	public void printEmbeddings(ConcurrentHashMap<String, double[]> embeddings) {
		System.out.println("Embeddings:");
		embeddings.forEach((word, vector) -> {
			System.out.println(word + " -> " + vectorToString(vector));
		});
		System.out.println("Total embeddings: " + embeddings.size());
	}

	private String vectorToString(double[] vector) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < vector.length; i++) {
			sb.append(vector[i]);
			if (i < vector.length - 1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
