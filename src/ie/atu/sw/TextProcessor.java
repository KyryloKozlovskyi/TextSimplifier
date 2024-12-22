package ie.atu.sw;

import java.util.concurrent.CopyOnWriteArrayList;

public class TextProcessor extends AbstractProcessor {
	private final CopyOnWriteArrayList<String> processedLines = new CopyOnWriteArrayList<>();

	@Override
	protected void processLine(String line) {
		processedLines.add(line.toLowerCase());
	}

	public void debugPrint(int limit) {
		System.out.println("Debugging Text:");
		int count = 0;
		for (String line : processedLines) {
			System.out.println(line);
			if (++count >= limit)
				break;
		}
		System.out.println("Total lines loaded: " + processedLines.size());
	}

	public CopyOnWriteArrayList<String> getProcessedLines() {
		return processedLines;
	}
}
