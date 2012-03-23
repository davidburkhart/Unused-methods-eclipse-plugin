package unused.methods.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceAsString {

	private final String resourcePath;

	public ResourceAsString(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public String read() throws IOException {
		StringBuffer result = new StringBuffer();
		InputStream resourceAsStream = getClass().getResourceAsStream(resourcePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
		String line = reader.readLine();
		while (line != null) {
			result.append(line).append("\n");
			line = reader.readLine();
		}
		reader.close();
		return result.toString();
	}
}
