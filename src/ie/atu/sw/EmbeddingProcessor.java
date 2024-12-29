package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;

/**
 * EmbeddingProcessor is a subclass of AbstractProcessor that processes lines
 * from an embeddings file and stores word vectors in a thread-safe map.
 */
public class EmbeddingProcessor extends AbstractProcessor {
	private final ConcurrentHashMap<String, double[]> embeddings = new ConcurrentHashMap<>();

	/**
	 * Processes a single line of the embeddings file.
	 *
	 * Each line is split into a word and its corresponding vector components. The
	 * word is used as the key, and the vector is stored as a double array in the
	 * embeddings map.
	 * 
	 * Running time: O(n), where n is the number of components in the vector.
	 *
	 * @param line A single line from the embeddings file.
	 */
	@Override
	protected void process(String line) {
		String[] parts = line.split(",");
		String word = parts[0];
		double[] vector = new double[parts.length - 1];
		// Parse vector components from line
		for (int i = 1; i < parts.length; i++) {
			vector[i - 1] = Double.parseDouble(parts[i]);
		}
		embeddings.put(word, vector); // Add word vector to map
	}

	/**
	 * Returns a copy of the embeddings map.
	 * 
	 * Running time: O(n), where n is the number of entries in the map.
	 *
	 * @return A copy of the embeddings map.
	 */
	public ConcurrentHashMap<String, double[]> getEmbeddings() {
		return new ConcurrentHashMap<>(embeddings);
	}
}
