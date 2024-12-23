package ie.atu.sw;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

public class TextProcessor extends AbstractProcessor {
	private final CopyOnWriteArrayList<String> processedLines = new CopyOnWriteArrayList<>();

	@Override
	// Process the line by converting it to lowercase
	protected void process(String line) {
		processedLines.add(line.toLowerCase());
	}

	// Get a copy of the processed lines
	public CopyOnWriteArrayList<String> getProcessedLines() {
		return new CopyOnWriteArrayList<>(processedLines);
	}

	// Save the processed lines to a file
	public static void saveToFile(String filePath, CopyOnWriteArrayList<String> text) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			for (String line : text) {
				writer.write(line);
				writer.newLine();
			}
		}
	}
}
