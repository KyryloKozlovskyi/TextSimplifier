package ie.atu.sw;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class TextProcessor extends AbstractProcessor {
	private final CopyOnWriteArrayList<String> processedLines = new CopyOnWriteArrayList<>();

	@Override
	protected void process(String line) {
		processedLines.add(line.toLowerCase());
	}

	public CopyOnWriteArrayList<String> getProcessedLines() {
		return new CopyOnWriteArrayList<>(processedLines);
	}

	public static void saveToFile(String filePath, CopyOnWriteArrayList<String> text) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
				var scope = new StructuredTaskScope.ShutdownOnFailure()) {

			for (String line : text) {
				scope.fork(() -> {
					synchronized (writer) {
						writer.write(line);
						writer.newLine();
					}
					return null;
				});
			}

			scope.join(); // Wait for all tasks to complete
			scope.throwIfFailed(); // Propagate exceptions if any
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new IOException("Error while saving file: " + e.getMessage(), e);
		}
	}
}
