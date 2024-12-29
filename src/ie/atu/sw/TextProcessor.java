package ie.atu.sw;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * TextProcessor is responsible for processing lines of text by converting them
 * to lowercase and saving processed lines to a file. This class uses structured
 * concurrency for both processing and file saving tasks.
 */
public class TextProcessor extends AbstractProcessor {
	private final CopyOnWriteArrayList<String> processedLines = new CopyOnWriteArrayList<>();

	/**
	 * Processes a single line of text by converting it to lowercase.
	 *
	 * Each line is added to a thread-safe collection for later retrieval.
	 * 
	 * Running time: O(n), where n is the length of the line.
	 *
	 * @param line A single line of text from the file.
	 */
	@Override
	protected void process(String line) {
		processedLines.add(line.toLowerCase());
	}

	/**
	 * Returns a copy of the processed lines.
	 *
	 * The returned list contains all lines that have been processed.
	 * 
	 * Running time: O(n), where n is the number of processed lines.
	 * 
	 * @return A copy of the processed lines.
	 */
	public CopyOnWriteArrayList<String> getProcessedLines() {
		return new CopyOnWriteArrayList<>(processedLines);
	}

	/**
	 * Saves the processed lines to a file using structured concurrency.
	 *
	 * Each line is written to the file in a separate virtual thread.
	 * 
	 * Running time: O(n), where n is the number of lines to save.
	 *
	 * @param filePath The path to the output file.
	 * @param text     The lines to save to the file.
	 * @throws IOException If an I/O error occurs during file saving.
	 */
	public static void saveToFile(String filePath, CopyOnWriteArrayList<String> text) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
				var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			// Write each line to the file in a separate virtual thread
			for (String line : text) {
				scope.fork(() -> {
					// Synchronise the writer to prevent multiple threads from writing to the
					// file at the same time
					synchronized (writer) {
						writer.write(line);
						writer.newLine();
					}
					return null;
				});
			}
			// Wait for all tasks to complete and throw an exception if any task failed
			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Error while saving file: " + e.getMessage(), e);
		}
	}
}
