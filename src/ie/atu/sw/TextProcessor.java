package ie.atu.sw;

import java.util.concurrent.CopyOnWriteArrayList;

public class TextProcessor extends AbstractProcessor {
	private final CopyOnWriteArrayList<String> processedLines = new CopyOnWriteArrayList<>();

	@Override
	protected void process(String line) {
		processedLines.add(line.toLowerCase());
	}

	public CopyOnWriteArrayList<String> getProcessedLines() {
		return processedLines;
	}
}
