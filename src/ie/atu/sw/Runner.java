package ie.atu.sw;

import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Runner {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);

		// Default file paths
		// Change to null
		// Have default value for outputFile
		String embeddingsFile = "./embeddings.txt";
		String googleWordsFile = "./google-1000.txt";
		String inputTextFile = "./sampleText.txt";
		String outputFile = "./output.txt";

		// Infinite loop for the menu
		while (true) {
			// Display the menu
			displayMenu();

			// Take user input
			System.out.print(ConsoleColour.BLACK_BOLD_BRIGHT);
			System.out.print("Select Option [1-5]: ");
			int choice = scanner.nextInt();
			scanner.nextLine();

			switch (choice) {
			// Specify Embeddings File
			case 1 -> {
				System.out.print(ConsoleColour.GREEN + "Enter the path to the embeddings file: " + ConsoleColour.RESET);
				embeddingsFile = scanner.nextLine();
			}
			// Specify Google 1000 File
			case 2 -> {
				System.out.print(ConsoleColour.GREEN + "Enter the path to the Google-1000 word list file: "
						+ ConsoleColour.RESET);
				googleWordsFile = scanner.nextLine();
			}
			// Specify Input Text File
			case 3 -> {
				System.out.print(ConsoleColour.GREEN + "Enter the path to the input text file: " + ConsoleColour.RESET);
				inputTextFile = scanner.nextLine();
			}
			// Specify Output
			case 4 -> {
				System.out.print(ConsoleColour.GREEN + "Enter the path to the output file: " + ConsoleColour.RESET);
				outputFile = scanner.nextLine();
			}
			// Execute Simplification
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

	// Display the menu
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

	// Execute the text simplification
	private static void executeSimplification(String embeddingsFile, String googleWordsFile, String inputTextFile,
			String outputFile) {
		if (embeddingsFile == null || googleWordsFile == null || inputTextFile == null) {
			System.out.println(
					ConsoleColour.RED + "Please specify all required files before execution." + ConsoleColour.RESET);
			return;
		}

		try {
			System.out.println(ConsoleColour.BLUE + "Loading embeddings..." + ConsoleColour.RESET);
			// Create an EmbeddingProcessor object and load the embeddings file
			EmbeddingProcessor embeddingProcessor = new EmbeddingProcessor();
			embeddingProcessor.load(embeddingsFile);

			System.out.println(ConsoleColour.BLUE + "\nLoading Google-1000 words..." + ConsoleColour.RESET);
			// Create a GoogleProcessor object and load the Google-100
			GoogleProcessor googleProcessor = new GoogleProcessor();
			googleProcessor.load(googleWordsFile);

			System.out.println(ConsoleColour.BLUE + "\nMapping embeddings..." + ConsoleColour.RESET);
			// Create a Mapper object and generate the mapping between embeddings and
			// Google-1000 words
			Mapper mapper = new Mapper();
			var googleEmbeddings = mapper.generateMapping(embeddingProcessor.getEmbeddings(),
					googleProcessor.getGoogleWords());

			System.out.println(ConsoleColour.BLUE + "\nLoading input text..." + ConsoleColour.RESET);
			// Create a TextProcessor object and load the input text file
			TextProcessor textProcessor = new TextProcessor();
			textProcessor.load(inputTextFile);

			System.out.println(ConsoleColour.BLUE + "\nSimplifying text..." + ConsoleColour.RESET);
			// Create a SimilarityFinder object, a TextSimplifier object and simplify the
			// text
			SimilarityFinder similarityFinder = new SimilarityFinder();
			TextSimplifier textSimplifier = new TextSimplifier(similarityFinder);

			// Simplify the text
			CopyOnWriteArrayList<String> simplifiedLines = textSimplifier.simplifyLines(
					textProcessor.getProcessedLines(), embeddingProcessor.getEmbeddings(), googleEmbeddings);

			System.out.println(ConsoleColour.BLUE + "\nSaving simplified text..." + ConsoleColour.RESET);
			// Save the simplified text to the output file
			TextProcessor.saveToFile(outputFile, simplifiedLines);

			System.out.println(
					ConsoleColour.GREEN + "Simplified text has been saved to: " + outputFile + ConsoleColour.RESET);
		} catch (Exception e) {
			System.err.println(ConsoleColour.RED + "An error occurred: " + e.getMessage() + ConsoleColour.RESET);
		}
	}
}
