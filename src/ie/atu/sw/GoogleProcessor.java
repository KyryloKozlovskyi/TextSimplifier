package ie.atu.sw;

import java.util.concurrent.CopyOnWriteArraySet;

/**
 * GoogleProcessor is a subclass of AbstractProcessor that processes lines from
 * the Google-1000 word list and stores them in a thread-safe set.
 */
public class GoogleProcessor extends AbstractProcessor {
	private final CopyOnWriteArraySet<String> googleWords = new CopyOnWriteArraySet<>();

	/**
	 * Processes a single line from the Google-1000 word list.
	 *
	 * @param line A single line from the Google-1000 file.
	 *
	 *             Running time: O(1).
	 */
	@Override
	protected void process(String line) {
		googleWords.add(line.trim());
	}

	/**
	 * Returns a copy of the googleWords set.
	 *
	 * @return A copy of the googleWords set.
	 *
	 *         Running time: O(n), where n is the number of words in the set.
	 */
	public CopyOnWriteArraySet<String> getGoogleWords() {
		return new CopyOnWriteArraySet<>(googleWords);
	}
}
