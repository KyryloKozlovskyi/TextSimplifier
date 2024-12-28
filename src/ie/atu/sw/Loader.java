package ie.atu.sw;

import java.io.IOException;

/**
 * The Loader interface defines a contract for loading files.
 */
public interface Loader {

	/**
	 * Loads a file from the specified file path.
	 *
	 * Implementing classes must provide the logic to open, read, and process the
	 * file. This method is designed to be generic and can be applied to any type of
	 * file processing task.
	 *
	 * @param filePath The path to the file to be loaded.
	 * @throws IOException If an error occurs while reading the file.
	 */
	void load(String filePath) throws IOException;
}
