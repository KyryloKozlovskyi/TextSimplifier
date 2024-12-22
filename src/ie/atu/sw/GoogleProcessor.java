package ie.atu.sw;

import java.util.concurrent.CopyOnWriteArraySet;

public class GoogleProcessor extends AbstractProcessor {
	private final CopyOnWriteArraySet<String> googleWords = new CopyOnWriteArraySet<>();

	@Override
	protected void processLine(String line) {
		googleWords.add(line.trim());
	}

	public void debugPrint(int limit) {
		System.out.println("Debugging Google Words:");
		int count = 0;
		for (String word : googleWords) {
			System.out.println(word);
			if (++count >= limit)
				break;
		}
		System.out.println("Total Google words loaded: " + googleWords.size());
	}

	public CopyOnWriteArraySet<String> getGoogleWords() {
		return googleWords;
	}
}
