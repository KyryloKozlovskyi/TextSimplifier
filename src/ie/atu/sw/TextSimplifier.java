package ie.atu.sw;

import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class TextSimplifier {

	private final SimilarityFinder similarityFinder;

	public TextSimplifier(SimilarityFinder similarityFinder) {
		this.similarityFinder = similarityFinder;
	}

	public CopyOnWriteArrayList<String> simplifyLines(CopyOnWriteArrayList<String> lines,
			Map<String, double[]> embeddings, Map<String, double[]> googleEmbeddings) {
		CopyOnWriteArrayList<String> simplifiedLines = new CopyOnWriteArrayList<>();

		for (String line : lines) {
			String simplifiedLine = simplifyText(line, embeddings, googleEmbeddings);
			simplifiedLines.add(simplifiedLine);
		}

		return simplifiedLines;
	}

	public String simplifyText(String line, Map<String, double[]> embeddings, Map<String, double[]> googleEmbeddings) {
		String[] words = line.split("\\s+");
		StringBuilder simplifiedLine = new StringBuilder();

		for (String word : words) {
			double[] embedding = embeddings.get(word.toLowerCase());
			if (embedding != null) {
				String mostSimilar = similarityFinder.findMostSimilar(embedding, googleEmbeddings);
				simplifiedLine.append(mostSimilar != null ? mostSimilar : word).append(" ");
			} else {
				simplifiedLine.append(word).append(" ");
			}
		}

		return simplifiedLine.toString().trim();
	}
}
