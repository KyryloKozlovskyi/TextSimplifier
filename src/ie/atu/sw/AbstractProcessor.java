package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public abstract class AbstractProcessor implements Loader {

	@Override
	// Load the file and process each line concurrently using StructuredTaskScope
	public void load(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
				var scope = new StructuredTaskScope.ShutdownOnFailure()) {
			String line;
			// Read each line and process it concurrently
			while ((line = reader.readLine()) != null) {
				String currentLine = line;
				// Fork a new thread to process each line
				scope.fork(() -> {
					try {
						process(currentLine);
					} catch (Exception e) {
						System.err.println("Failed to process line: " + currentLine + ". Error: " + e.getMessage());
					}
					return null;
				});
			}
			// Wait for all threads to finish
			scope.join();
			scope.throwIfFailed();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			System.err.println("Error during file processing: " + e.getMessage());
		}
	}

	// Abstract method to process each line
	protected abstract void process(String line);
}
