package ie.atu.sw;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Runner serves as the entry point for the Virtual Threaded Text Simplifier
 * application. It provides a command-line interface (CLI) for users to: -
 * Specify file paths for the embeddings file, Google-1000 file, input text
 * file, and output file. - Choose a similarity algorithm (Cosine Similarity or
 * Euclidean Distance). - Execute the text simplification process.
 */
public class Runner {

	/**
	 * The main method initiates the CLI and handles user interactions.
	 *
	 * @param args Command-line arguments (not used in this application).
	 */
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Default file paths with options for user input
		String embeddingsFile = "./embeddings.txt";
		String googleWordsFile = "./google-1000.txt";
		String inputTextFile = "./sampleText.txt";
		String outputFile = "./output.txt";

		// Infinite loop to display the menu until the user chooses to quit
		while (true) {
			displayMenu();
			System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
			System.out.print("Select Option [1-6]: ");
			int choice = scanner.nextInt();
			scanner.nextLine();
			switch (choice) {
			// Embeddings file path
			case 1 -> {
				System.out.print(ConsoleColour.GREEN + "Enter the path to the embeddings file: " + ConsoleColour.RESET);
				embeddingsFile = scanner.nextLine();
			}
			// Google-1000 word list file path
			case 2 -> {
				System.out.print(ConsoleColour.GREEN + "Enter the path to the Google-1000 word list file: "
						+ ConsoleColour.RESET);
				googleWordsFile = scanner.nextLine();
			}
			// Input text file path
			case 3 -> {
				System.out.print(ConsoleColour.GREEN + "Enter the path to the input text file: " + ConsoleColour.RESET);
				inputTextFile = scanner.nextLine();
			}
			// Output file path
			case 4 -> {
				System.out.print(ConsoleColour.GREEN + "Enter the path to the output file: " + ConsoleColour.RESET);
				outputFile = scanner.nextLine();
			}
			// Execute simplification
			case 5 -> executeSimplification(embeddingsFile, googleWordsFile, inputTextFile, outputFile);
			// Quit
			case 6 -> {
				System.out.println(ConsoleColour.YELLOW + "Exiting the program. Goodbye!" + ConsoleColour.RESET);
				scanner.close();
				return;
			}
			// Invalid option
			default ->
				System.out.println(ConsoleColour.RED + "Invalid option. Please try again." + ConsoleColour.RESET);
			}
		}
	}

	/**
	 * Displays the application menu with available options.
	 */
	private static void displayMenu() {
		System.out.println(ConsoleColour.WHITE);
		System.out.println("************************************************************");
		System.out.println("*     ATU - Dept. of Computer Science & Applied Physics    *");
		System.out.println("*                                                          *");
		System.out.println("*             Virtual Threaded Text Simplifier             *");
		System.out.println("*                                                          *");
		System.out.println("************************************************************");
		System.out.println("(1) Specify Embeddings File");
		System.out.println("(2) Specify Google 1000 File");
		System.out.println("(3) Specify Input Text File");
		System.out.println("(4) Specify Output File (default: ./out.txt)");
		System.out.println("(5) Execute Simplification");
		System.out.println("(6) Quit");
		System.out.println(ConsoleColour.RESET);
	}

	/**
	 * Executes the text simplification process by: - Loading the embeddings and
	 * Google-1000 word list. - Mapping embeddings to the Google-1000 words. -
	 * Simplifying the input text file. - Saving the simplified text to the
	 * specified output file.
	 *
	 * @param embeddingsFile  The path to the embeddings file.
	 * @param googleWordsFile The path to the Google-1000 word list file.
	 * @param inputTextFile   The path to the input text file.
	 * @param outputFile      The path to the output text file.
	 */
	private static void executeSimplification(String embeddingsFile, String googleWordsFile, String inputTextFile,
			String outputFile) {
		if (embeddingsFile == null || googleWordsFile == null || inputTextFile == null) {
			System.out.println(
					ConsoleColour.RED + "Please specify all required files before execution." + ConsoleColour.RESET);
			return;
		}
		// Handle user selection of similarity algorithm
		SimilarityFinder.SimilarityAlgorithm algorithm = chooseSimilarityAlgorithm();
		try {
			// Load embeddings, Google-1000 words, and input text
			System.out.println(ConsoleColour.BLUE + "Loading embeddings..." + ConsoleColour.RESET);
			EmbeddingProcessor embeddingProcessor = new EmbeddingProcessor();
			embeddingProcessor.load(embeddingsFile);

			// Load Google 1000 words
			System.out.println(ConsoleColour.BLUE + "\nLoading Google-1000 words..." + ConsoleColour.RESET);
			GoogleProcessor googleProcessor = new GoogleProcessor();
			googleProcessor.load(googleWordsFile);

			// Map embeddings to Google-1000 words
			System.out.println(ConsoleColour.BLUE + "\nMapping embeddings..." + ConsoleColour.RESET);
			Mapper mapper = new Mapper();
			var googleEmbeddings = mapper.generateMapping(embeddingProcessor.getEmbeddings(),
					googleProcessor.getGoogleWords());

			// Load input text
			System.out.println(ConsoleColour.BLUE + "\nLoading input text..." + ConsoleColour.RESET);
			TextProcessor textProcessor = new TextProcessor();
			textProcessor.load(inputTextFile);

			// Initialise the similarity finder and text simplifier
			System.out.println(ConsoleColour.BLUE + "\nSimplifying text using " + algorithm + " similarity..."
					+ ConsoleColour.RESET);
			SimilarityFinder similarityFinder = new SimilarityFinder(algorithm);
			TextSimplifier textSimplifier = new TextSimplifier(similarityFinder);

			// Simplify the lines using the processed lines, embeddings, and Google-1000
			// embeddings
			CopyOnWriteArrayList<String> simplifiedLines = textSimplifier.simplifyLines(
					textProcessor.getProcessedLines(), embeddingProcessor.getEmbeddings(), googleEmbeddings);

			// Save the simplified text to the output file
			System.out.println(ConsoleColour.BLUE + "\nSaving simplified text..." + ConsoleColour.RESET);
			TextProcessor.saveToFile(outputFile, simplifiedLines);

			// Display success message
			System.out.println(
					ConsoleColour.GREEN + "Simplified text has been saved to: " + outputFile + ConsoleColour.RESET);
		} catch (Exception e) {
			// Display error message
			System.err.println(ConsoleColour.RED + "An error occurred: " + e.getMessage() + ConsoleColour.RESET);
		}
	}

	/**
	 * Prompts the user to select a similarity algorithm for text simplification.
	 *
	 * @return The selected similarity algorithm.
	 */
	private static SimilarityFinder.SimilarityAlgorithm chooseSimilarityAlgorithm() {
		Scanner scanner = new Scanner(System.in);
		// Display the options for similarity algorithms
		System.out.println(ConsoleColour.YELLOW + "\nChoose Similarity Algorithm:" + ConsoleColour.RESET);
		System.out.println("(1) Cosine Similarity");
		System.out.println("(2) Euclidean Distance");
		System.out.print("Select Option [1-2]: ");
		// Read user input
		int choice = scanner.nextInt();
		scanner.nextLine();
		// Return the selected algorithm
		return (choice == 1) ? SimilarityFinder.SimilarityAlgorithm.COSINE
				: SimilarityFinder.SimilarityAlgorithm.EUCLIDEAN;
	}
}
