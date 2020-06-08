import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class WebpageManager {

	public static void main(String[] args) throws IOException {
		// Doesn't follow redirects from http to https and v.v.
		URL oracle = new URL("https://www.oracle.com/");
		BufferedReader in = new BufferedReader(
				new InputStreamReader(oracle.openStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}
}
