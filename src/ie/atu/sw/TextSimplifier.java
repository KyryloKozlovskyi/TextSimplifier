package ie.atu.sw;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * TextSimplifier is responsible for simplifying lines of text by replacing
 * words with their most similar counterparts from a given embedding map.
 *
 * The class uses structured concurrency to process lines and words in parallel.
 */
public class TextSimplifier {

	private final SimilarityFinder similarityFinder;

	/**
	 * TextSimplifier constructor with the specified similarity finder.
	 *
	 * @param similarityFinder The SimilarityFinder to use for finding similar
	 *                         words.
	 */
	public TextSimplifier(SimilarityFinder similarityFinder) {
		this.similarityFinder = similarityFinder;
	}

	/**
	 * Simplifies each line in the given list of lines using structured concurrency.
	 *
	 * Each line is processed in a separate virtual thread, and words are replaced
	 * with their most similar equivalents based on the provided embeddings.
	 *
	 * @param lines            The lines of text to simplify.
	 * @param embeddings       The map of all embeddings.
	 * @param googleEmbeddings The map of Google-1000 embeddings.
	 * @return A list of simplified text lines.
	 *
	 *         Running time: O(n * m), where n is the number of lines, m is the
	 *         average number of words per line, considering the embeddings map
	 *         stays the same and the vector dimensionality does not change.
	 */
	public CopyOnWriteArrayList<String> simplifyLines(CopyOnWriteArrayList<String> lines,
			ConcurrentHashMap<String, double[]> embeddings, ConcurrentHashMap<String, double[]> googleEmbeddings) {
		CopyOnWriteArrayList<String> simplifiedLines = new CopyOnWriteArrayList<>();
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			// Process each line in a separate virtual thread
			for (String line : lines) {
				scope.fork(() -> {
					// Simplify the line and add it to the list
					String simplifiedLine = simplifyText(line, embeddings, googleEmbeddings);
					simplifiedLines.add(simplifiedLine);
					return null;
				});
			}
			// Wait for all tasks to complete and throw an exception if any failed
			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during line simplification: " + e.getMessage());
		}
		return simplifiedLines;
	}

	/**
	 * Simplifies a single line of text using structured concurrency for words.
	 *
	 * Each word in the line is processed in a separate virtual thread, and replaced
	 * with the most similar word from the Google-1000 embeddings.
	 *
	 * @param line             The line to simplify.
	 * @param embeddings       The map of all embeddings.
	 * @param googleEmbeddings The map of Google-1000 embeddings.
	 * @return The simplified line.
	 *
	 *         Running time: O(n), where n is the number of words in the line,
	 *         considering the dimensionality of the embeddings stays the same.
	 */
	public String simplifyText(String line, ConcurrentHashMap<String, double[]> embeddings,
			ConcurrentHashMap<String, double[]> googleEmbeddings) {
		ConcurrentHashMap<Integer, String> simplifiedWords = new ConcurrentHashMap<>();
		String[] words = line.split("\\s+");
		try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			// Process each word in a separate virtual thread
			for (int i = 0; i < words.length; i++) {
				final int index = i;
				scope.fork(() -> {
					String word = words[index];
					double[] embedding = embeddings.get(word.toLowerCase());
					// Find the most similar word from the Google-1000 embeddings
					String simplifiedWord = (embedding != null)
							? similarityFinder.findMostSimilar(embedding, googleEmbeddings)
							: word;
					simplifiedWords.put(index, simplifiedWord); // Add simplified word to the map
					return null;
				});
			}
			// Wait for all tasks to complete
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
