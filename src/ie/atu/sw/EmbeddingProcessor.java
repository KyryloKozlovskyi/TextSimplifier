package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EmbeddingProcessor implements Loader {
	private final ConcurrentHashMap<String, double[]> embeddings = new ConcurrentHashMap<>(); // Store word embeddings

	@Override
	// Load embeddings from a file
	public void load(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			List<Thread> threads = new CopyOnWriteArrayList<>(); // Store threads for processing lines during file read

			// Process each line with a new thread
			while ((line = reader.readLine()) != null) {
				String finalLine = line;
				// Create a new thread to process the line
				Thread thread = Thread.ofVirtual().start(() -> processLine(finalLine));
				threads.add(thread);
			}

			// Wait for all threads to finish
			for (Thread thread : threads) {
				thread.join();
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void processLine(String line) {
		String[] parts = line.split(",");
		String word = parts[0];
		double[] vector = new double[parts.length - 1];
		for (int i = 1; i < parts.length; i++) {
			vector[i - 1] = Double.parseDouble(parts[i]);
		}
		embeddings.put(word, vector);
	}

	// DEBUGGING ONLY: Print the embeddings to the console
	public void debugPrint(int limit) {
		System.out.println("Debugging Embeddings:");
		int count = 0;

		for (Map.Entry<String, double[]> entry : embeddings.entrySet()) {
			String word = entry.getKey();
			double[] vector = entry.getValue();

			// Print word and vector
			System.out.println(word + " -> " + vectorToString(vector));

			// Stop after printing the specified number of entries
			count++;
			if (count >= limit) {
				break;
			}
		}

		System.out.println("Total embeddings loaded: " + embeddings.size());
	}

	/**
	 * Converts a double array to a string representation.
	 */
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

	public ConcurrentHashMap<String, double[]> getEmbeddings() {
		return embeddings;
	}
}
