package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

/**
 * AbstractProcessor provides a base class for processing files line by line
 * using structured concurrency. Subclasses must implement the process method to
 * define the logic for handling each line.
 */
public abstract class AbstractProcessor implements Loader {

	/**
	 * Loads a file and processes each line concurrently.
	 *
	 * Each line of the file is read and processed in a separate virtual thread.
	 * Structured concurrency ensures all tasks are managed properly.
	 *
	 * @param filePath The path to the file to be processed.
	 * @throws IOException If an error occurs while reading the file.
	 *
	 *                     Running time: O(n), where n is the number of lines in the
	 *                     file.
	 */
	@Override
	public void load(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
				var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			String line;
			// Process each line in a separate virtual thread
			while ((line = reader.readLine()) != null) {
				String currentLine = line;
				scope.fork(() -> {
					try {
						process(currentLine); // Process the line
					} catch (Exception e) {
						System.err.println("Failed to process line: " + currentLine + ". Error: " + e.getMessage());
					}
					return null;
				});
			}
			// Wait for all threads to complete and handle exceptions
			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during file processing: " + e.getMessage());
		}
	}

	/**
	 * Processes a single line of the file.
	 *
	 * This method is called for each line in the file. Subclasses must implement
	 * this method to define how each line should be handled.
	 *
	 * @param line A single line from the file.
	 *
	 *             Running time: Implementation-specific.
	 */
	protected abstract void process(String line);
}
