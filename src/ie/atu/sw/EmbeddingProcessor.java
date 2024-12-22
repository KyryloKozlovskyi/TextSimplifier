package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;

public class EmbeddingProcessor extends AbstractProcessor {
	private final ConcurrentHashMap<String, double[]> embeddings = new ConcurrentHashMap<>();

	@Override
	// Process each line of the embeddings file
	protected void processLine(String line) {
		String[] parts = line.split(",");
		String word = parts[0];
		// Extract the vector from the line
		double[] vector = new double[parts.length - 1];
		for (int i = 1; i < parts.length; i++) {
			vector[i - 1] = Double.parseDouble(parts[i]);
		}
		// Store the word and vector in the map
		embeddings.put(word, vector);
	}

	// DEBUGGING
	public void debugPrint(int limit) {
		System.out.println("Debugging Embeddings:");
		int count = 0;
		for (var entry : embeddings.entrySet()) {
			System.out.println(entry.getKey() + " -> " + vectorToString(entry.getValue()));
			if (++count >= limit)
				break;
		}
		System.out.println("Total embeddings loaded: " + embeddings.size());
	}

	private String vectorToString(double[] vector) {
		StringBuilder sb = new StringBuilder("[");
		for (int i = 0; i < vector.length; i++) {
			sb.append(vector[i]);
			if (i < vector.length - 1)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	// Getter for embeddings
	public ConcurrentHashMap<String, double[]> getEmbeddings() {
		return embeddings;
	}
}
