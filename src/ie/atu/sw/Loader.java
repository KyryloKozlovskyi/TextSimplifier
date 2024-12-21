package ie.atu.sw;

import java.io.IOException;

public interface Loader {
	// Abstract method to load file
	void load(String filePath) throws IOException;
}
