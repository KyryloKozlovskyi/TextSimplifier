# TextSimplifier

The **TextSimplifier** is a Java application designed to simplify text files. The application uses **structured concurrency** and **virtual threads** for efficient and multi-threaded processing of large files.

## Author

- Kyrylo Kozlovskyi G00425385 (GitHub: [KyryloKozlovskyi](https://github.com/KyryloKozlovskyi/TextSimplifier))

## Description 

This console-based Java application simplifies text by replacing words with their most similar equivalents from the Google-1000 word list. Word embeddings are parsed into a thread-safe ConcurrentHashMap from a user-specified file. Users can process an input text file, where each word is matched to its closest Google-1000 word using either the Cosine Similarity or Euclidean Distance algorithm. The simplified text is written to a user-specified output file.

## Requirements

- Java SE-21

## How to Use

## Features
