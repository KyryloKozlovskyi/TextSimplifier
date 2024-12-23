package ie.atu.sw;

import java.util.concurrent.CopyOnWriteArraySet;

public class GoogleProcessor extends AbstractProcessor {
	private final CopyOnWriteArraySet<String> googleWords = new CopyOnWriteArraySet<>();

	@Override
	// Process each line and add it to the googleWords list
	protected void process(String line) {
		googleWords.add(line.trim());
	}

	// Return the googleWords list
	public CopyOnWriteArraySet<String> getGoogleWords() {
		return new CopyOnWriteArraySet<>(googleWords);
	}
}
