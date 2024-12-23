package ie.atu.sw;

import java.util.concurrent.CopyOnWriteArraySet;

public class GoogleProcessor extends AbstractProcessor {
	private final CopyOnWriteArraySet<String> googleWords = new CopyOnWriteArraySet<>();

	@Override
	protected void process(String line) {
		googleWords.add(line.trim());
	}

	public CopyOnWriteArraySet<String> getGoogleWords() {
		return googleWords;
	}
}
