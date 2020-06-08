package web_updater;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WebpageManager {

	public static final int RATE_MS = 5000;

	@Scheduled(fixedRate = RATE_MS)
	public void getOraclePage() throws IOException {
		Document doc = Jsoup.connect("http://oracle.com/").get();
		System.out.println(doc);
	}
}
