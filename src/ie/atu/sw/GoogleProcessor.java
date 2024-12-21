package ie.atu.sw;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class GoogleProcessor implements Loader {
	private final CopyOnWriteArraySet<String> googleWords = new CopyOnWriteArraySet<>();

	@Override
	// Load the Google words from the file into the set
	public void load(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			List<Thread> threads = new CopyOnWriteArrayList<>();

			// Process each line with a new thread
			while ((line = reader.readLine()) != null) {
				String finalLine = line.trim();
				// Create a new thread for each line
				Thread thread = Thread.ofVirtual().start(() -> googleWords.add(finalLine));
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

	// DEBUG
	public void debugPrint(int limit) {
		System.out.println("Debugging Google Words:");
		int count = 0;

		for (String word : googleWords) {
			System.out.println(word);
			count++;
			if (count >= limit) {
				break;
			}
		}

		System.out.println("Total Google words loaded: " + googleWords.size());
	}

	public CopyOnWriteArraySet<String> getGoogleWords() {
		return googleWords;
	}
}
