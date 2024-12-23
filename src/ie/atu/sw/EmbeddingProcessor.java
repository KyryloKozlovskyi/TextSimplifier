package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;

public class EmbeddingProcessor extends AbstractProcessor {
	private final ConcurrentHashMap<String, double[]> embeddings = new ConcurrentHashMap<>();

	@Override
	// Process a line and store the word and its vector in the embeddings map
	protected void process(String line) {
		String[] parts = line.split(",");
		String word = parts[0];
		double[] vector = new double[parts.length - 1];
		for (int i = 1; i < parts.length; i++) {
			vector[i - 1] = Double.parseDouble(parts[i]);
		}
		embeddings.put(word, vector);
	}

	// Return a copy of the embeddings map
	public ConcurrentHashMap<String, double[]> getEmbeddings() {
		return new ConcurrentHashMap<>(embeddings);
	}
}
