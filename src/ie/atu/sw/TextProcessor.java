package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TextProcessor implements Loader {
	private final CopyOnWriteArrayList<String> processedLines = new CopyOnWriteArrayList<>();

	@Override
	public void load(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			List<Thread> threads = new CopyOnWriteArrayList<>();

			// Process each line with a new thread
			while ((line = reader.readLine()) != null) {
				String finalLine = line;
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
		processedLines.add(line.toLowerCase());
	}

	// DEBUG
	public void debugPrint(int limit) {
		System.out.println("Debugging Text:");
		int count = 0;

		for (String line : processedLines) {
			System.out.println(line);
			count++;
			if (count >= limit) {
				break;
			}
		}

		System.out.println("Total lines loaded: " + processedLines.size());
	}

	public CopyOnWriteArrayList<String> getProcessedLines() {
		return processedLines;
	}
}
