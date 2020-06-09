package web_updater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
public class WebpageManager {

	private static final int SECOND_MS = 1000;
	private static final int MINUTE_MS = 60 * SECOND_MS;
	private static final int HOUR_MS = 60 * MINUTE_MS;

	private List<String> URLs = new ArrayList<>();
	private Map<String, Document> oldPages;
	private Stack<String> changed;
	private Stack<Error> errors;

	public void addURLToWatchList(String url) {
		URLs.add(url);
	}

	public Stack<String> getChanged() {
		return changed;
	}

	public Stack<Error> getErrors() {
		return errors;
	}

	/**
	 * Checks for updates on the pages in the list of URLs
	 */
	@Scheduled(fixedRate = HOUR_MS)
	public void checkForUpdates() {
		URLs.forEach(url -> {
			try {
				Document oldHTML = oldPages.getOrDefault(url, null);
				Document newHTML = Jsoup.connect(url).get();
				if (!oldHTML.hasSameValue(newHTML)) {
					changed.add(url);
					oldPages.put(url, newHTML);
				}
			} catch (IOException e) {
				e.printStackTrace();
				errors.add(new Error(url, e.getMessage(), e.getStackTrace(), System.currentTimeMillis()));
			}
		});
	}

	class Error {
		String url;
		String message;
		StackTraceElement[] stackTrace;
		long timestamp;

		public Error(String url, String message, StackTraceElement[] stackTrace, long timestamp) {
			this.url = url;
			this.message = message;
			this.stackTrace = stackTrace;
			this.timestamp = timestamp;
		}
	}
}
