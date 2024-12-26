package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class TextSimplifier {

	private final SimilarityFinder similarityFinder;

	public TextSimplifier(SimilarityFinder similarityFinder) {
		this.similarityFinder = similarityFinder;
	}

	/**
	 * Simplifies each line in the given list of lines using structured concurrency.
	 *
	 * @param lines            The lines of text to simplify.
	 * @param embeddings       The map of all embeddings.
	 * @param googleEmbeddings The map of Google-1000 embeddings.
	 * @return A list of simplified text lines.
	 */
	public CopyOnWriteArrayList<String> simplifyLines(CopyOnWriteArrayList<String> lines,
			ConcurrentHashMap<String, double[]> embeddings, ConcurrentHashMap<String, double[]> googleEmbeddings) {
		CopyOnWriteArrayList<String> simplifiedLines = new CopyOnWriteArrayList<>();

		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			for (String line : lines) {
				scope.fork(() -> {
					String simplifiedLine = simplifyText(line, embeddings, googleEmbeddings);
					simplifiedLines.add(simplifiedLine);
					return null;
				});
			}

			scope.join(); // Wait for all tasks to complete
			scope.throwIfFailed(); // Propagate exceptions if any
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during line simplification: " + e.getMessage());
		}

		return simplifiedLines;
	}

	/**
	 * Simplifies a single line of text using structured concurrency for words.
	 *
	 * @param line             The line to simplify.
	 * @param embeddings       The map of all embeddings.
	 * @param googleEmbeddings The map of Google-1000 embeddings.
	 * @return The simplified line.
	 */
	public String simplifyText(String line, ConcurrentHashMap<String, double[]> embeddings,
			ConcurrentHashMap<String, double[]> googleEmbeddings) {
		ConcurrentHashMap<Integer, String> simplifiedWords = new ConcurrentHashMap<>();
		String[] words = line.split("\\s+");

		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			for (int i = 0; i < words.length; i++) {
				final int index = i;
				scope.fork(() -> {
					String word = words[index];
					double[] embedding = embeddings.get(word.toLowerCase());
					String simplifiedWord = (embedding != null)
							? similarityFinder.findMostSimilar(embedding, googleEmbeddings)
							: word;
					simplifiedWords.put(index, simplifiedWord);
					return null;
				});
			}

			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during word simplification: " + e.getMessage());
		}

		// Combine simplified words into a single line
		StringBuilder simplifiedLine = new StringBuilder();
		for (int i = 0; i < words.length; i++) {
			simplifiedLine.append(simplifiedWords.getOrDefault(i, words[i])).append(" ");
		}

		return simplifiedLine.toString().trim();
	}
}
