# Virtual Threaded Text Simplifier

The **Virtual Threaded Text Simplifier** is a Java-based application designed to simplify text files by replacing words with their most similar equivalents from the Google-1000 word list. The application uses **structured concurrency** and **virtual threads** for efficient and multi-threaded processing of large datasets.

---

## Features

- **File Input/Output**:
  - Load word embeddings, Google-1000 words, and input text files.
  - Save simplified text to an output file.
  
- **Similarity Algorithms**:
  - Supports **Cosine Similarity** and **Euclidean Distance** for word replacement.

- **Concurrent Processing**:
  - Uses structured concurrency to process lines and words in parallel.


---

## Technologies Used

- **Java 17+**
  - Virtual Threads
  - Structured Concurrency
- **Data Structures**:
  - `ConcurrentHashMap`
  - `CopyOnWriteArrayList`
- **ANSI Escape Codes**:
  - For colorful and interactive terminal output.

---

## Installation

### Prerequisites

### Clone the Repository

```bash
git clone https://github.com/YourUsername/Virtual-Threaded-Text-Simplifier.git
cd Virtual-Threaded-Text-Simplifier
