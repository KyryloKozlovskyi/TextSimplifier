package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public abstract class AbstractProcessor implements Loader {

	@Override
	public void load(String filePath) throws IOException {
		// Use try-with-resources to ensure the reader is closed
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
				// Use Structured Concurrency
				var scope = new StructuredTaskScope.ShutdownOnFailure()) {

			String line;

			// Submit tasks to process each line
			while ((line = reader.readLine()) != null) {
				String currentLine = line;
				scope.fork(() -> {
					// Process the line
					process(currentLine);
					return null;
				});
			}

			scope.join(); // Wait for all tasks to complete
			scope.throwIfFailed(); // Check for any exceptions

		} catch (InterruptedException | ExecutionException e) {
			// Restore interrupted status and log the exception
			Thread.currentThread().interrupt();
			System.err.println("Error: " + e.getMessage());
		}
	}

	// Abstract method to process each line
	protected abstract void process(String line);
}
