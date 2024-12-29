# TextSimplifier

The **TextSimplifier** is a Java application designed to simplify text files. The application uses **structured concurrency** and **virtual threads** for efficient and multi-threaded processing of large files.

## Author

- Kyrylo Kozlovskyi G00425385 (GitHub: [KyryloKozlovskyi](https://github.com/KyryloKozlovskyi/TextSimplifier))

## Description 

This console-based Java application simplifies text by replacing words with their most similar equivalents from the Google-1000 word list. Word embeddings are parsed into a thread-safe ConcurrentHashMap from a user-specified file. Users can process an input text file, where each word is matched to its closest Google-1000 word using either the Cosine Similarity or Euclidean Distance algorithm. The simplified text is written to a user-specified output file.

## Requirements

- Java SE-21

## How to Use
To run the application navigate to the directory containing the .jar file using the command line, and use the following command to execute the application:
```
java -cp ./oop.jar ie.atu.sw.Runner
```

Once the application is running, use the following options:
1. Enter 1 - to Specify the path to the Embeddings File.
2. Enter 2 - to Specify the path to the Google-1000 Word List File.
3. Enter 3 - to Specify the path to the Input Text File to simplify.
4. Enter 4 - to Specify the path to the Output File to save the simplified text.
5. Enter 5 - to Execute Simplification.
6. Enter 6 - to Quit the application.
   
When Executing Simplification choose a Similarity Comparison Algorithm:
1. Enter 1 - to Select Cosine Similarity.
2. Enter 2 - to Select Euclidean Distance.

### Note! You have to make sure the Embeddings file path, Google-1000 file path, Input Text file path, Output file path are specified and then select Similarity Comparison Algorithm before running the text simplification.
   
## Features
- **Virtual Threaded Processing** - Uses structured concurrency and virtual threads for efficient text simplification.

- **Customizable Settings** - Allows users to specify file paths, similarity algorithms, and input/output files through a command-line menu.

- **Multiple Similarity Algorithms** - Supports Cosine Similarity and Euclidean Distance for word comparison.

- **Dynamic Text Simplification** - Replaces words in the input text with their most similar counterparts from the Google-1000 word list.

- **Thread-Safe Operations** - Utilizes concurrent data structures for safe and efficient processing.
