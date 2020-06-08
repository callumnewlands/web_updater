import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebpageManager {

	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.connect("http://oracle.org/").get();
		System.out.println(doc);
	}
}
