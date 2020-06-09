package web_updater;

import java.util.ArrayList;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class RootController {

	private static final int SECOND_MS = 1000;
	private static final int MINUTE_MS = 60 * SECOND_MS;
	private static final int HOUR_MS = 60 * MINUTE_MS;

	private List<WebPage> pages = new ArrayList<>();

	public RootController() {
		addURLToWatchList("https://www.google.co.uk");
	}

	@GetMapping( {"", "/"})
	public String getRoot(Model model) {
		checkForUpdates();
		model.addAttribute("pages", pages);
		return "main";
	}

	@GetMapping("diff")
	public String getDiff(@RequestParam String url, Model model) {
		model.addAttribute("page", pages.stream().filter(page -> page.getURL().equals(url)).findFirst().orElseThrow());
		return "diff";
	}

	@PostMapping("ackChanges")
	public RedirectView ackChanges(@ModelAttribute WebPage page) {
		page.acknowledgeChanges();
		return new RedirectView("");
	}

	private void addURLToWatchList(String url) {
		pages.add(new WebPage(url));
		checkForUpdates();
	}

	/**
	 * Checks for updates on the pages in the list of URLs
	 */
	@Scheduled(fixedRate = HOUR_MS)
	@Async
	public void checkForUpdates() {
		System.out.println("Checking for Updates!");
		pages.forEach(WebPage::update);
	}
}
