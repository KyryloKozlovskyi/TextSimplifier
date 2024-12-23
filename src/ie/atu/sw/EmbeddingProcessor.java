package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;

public class EmbeddingProcessor extends AbstractProcessor {
	private final ConcurrentHashMap<String, double[]> embeddings = new ConcurrentHashMap<>();

	@Override
	// Process each line of the embeddings file
	protected void process(String line) {
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

	public ConcurrentHashMap<String, double[]> getEmbeddings() {
		return embeddings;
	}
}
