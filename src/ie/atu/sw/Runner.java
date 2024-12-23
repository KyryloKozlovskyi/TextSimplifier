package ie.atu.sw;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Runner {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		try {
			String embeddingsFile = "./embeddings.txt";
			String googleWordsFile = "./google-1000.txt";
			String inputTextFile = "./sampleText.txt";
			String outputFile = "./output.txt";

			// Initialize processors and components
			EmbeddingProcessor embeddingProcessor = new EmbeddingProcessor();
			GoogleProcessor googleProcessor = new GoogleProcessor();
			Mapper mapper = new Mapper();
			SimilarityFinder similarityFinder = new SimilarityFinder();
			TextSimplifier textSimplifier = new TextSimplifier(similarityFinder);

			// Load embeddings and Google-1000 words
			System.out.println("Loading word embeddings...");
			embeddingProcessor.load(embeddingsFile);

			System.out.println("Loading Google-1000 word list...");
			googleProcessor.load(googleWordsFile);

			var embeddings = embeddingProcessor.getEmbeddings();
			var googleEmbeddings = mapper.generateMapping(embeddings, googleProcessor.getGoogleWords());

			// Load and process the input text file
			TextProcessor textProcessor = new TextProcessor();
			System.out.println("Loading input text...");
			textProcessor.load(inputTextFile);

			System.out.println("Simplifying text...");
			CopyOnWriteArrayList<String> simplifiedLines = new CopyOnWriteArrayList<>();
			for (String line : textProcessor.getProcessedLines()) {
				String simplifiedLine = textSimplifier.simplifyText(line, embeddings, googleEmbeddings);
				simplifiedLines.add(simplifiedLine);
			}

			// Save the simplified text to the output file
			System.out.println("Saving simplified text to file...");
			TextProcessor.saveToFile(outputFile, simplifiedLines);

			System.out.println("Simplified text has been saved to: " + outputFile);

		} catch (Exception e) {
			System.err.println("An error occurred: " + e.getMessage());
		} finally {
			scanner.close();
		}
	}
}
