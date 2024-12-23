package ie.atu.sw;

import java.util.Map;

public class TextSimplifier {

	private final SimilarityFinder similarityFinder;

	public TextSimplifier(SimilarityFinder similarityFinder) {
		this.similarityFinder = similarityFinder;
	}

	// Simplify the text by replacing each word with the most similar word in the
	public String simplifyText(String text, Map<String, double[]> allEmbeddings,
			Map<String, double[]> targetEmbeddings) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		// Split the text into words
		StringBuilder simplifiedText = new StringBuilder();
		String[] words = text.split("\\s+");

		// For each word, find the most similar word in the target embeddings
		for (String word : words) {
			double[] embedding = allEmbeddings.get(word);
			String replacement = word;

			if (embedding != null) {
				String mostSimilar = similarityFinder.findMostSimilar(embedding, targetEmbeddings);
				if (mostSimilar != null) {
					replacement = mostSimilar;
				}
			}

			simplifiedText.append(replacement).append(" ");
		}

		return simplifiedText.toString().trim();
	}
}
