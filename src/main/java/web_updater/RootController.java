package web_updater;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import web_updater.database.DBController;
import web_updater.model.WebPage;

// TODO Unit Tests
// TODO Acc. Tests
// TODO Highlight difference
// TODO fix false positives on difference check

@Controller
public class RootController {

	private static final int SECOND_MS = 1000;
	private static final int MINUTE_MS = 60 * SECOND_MS;
	private static final int HOUR_MS = 60 * MINUTE_MS;

	private DBController dbController;

	public RootController(DBController dbController) {
		this.dbController = dbController;
		try {
			dbController.addPageByURL("https://www.bbc.co.uk");
		} catch (MalformedURLException | URISyntaxException ignore) {
		}
	}

	@GetMapping( {"", "/"})
	public String getRoot(Model model) {
		checkForUpdates();
		model.addAttribute("pages", dbController.getAllPages());
		return "index";
	}

	@GetMapping("diff")
	public String getDiff(@RequestParam String url, Model model) {
		model.addAttribute("page", dbController.getPage(url));
		return "diff";
	}

	@PostMapping("ack-changes")
	public RedirectView ackChanges(@RequestParam String url) {
		dbController.setPageChanged(url, false);
		WebPage page = dbController.getPage(url);
		dbController.setPageOldHTML(url, page.getNewHtml());
		return new RedirectView("");
	}

	@PostMapping("add-watch")
	private RedirectView addURLToWatchList(@RequestParam String url) throws MalformedURLException, URISyntaxException {
		dbController.addPageByURL(url);
		return new RedirectView("");
	}

	/**
	 * Checks for updates on the pages in the list of URLs
	 */
	@Scheduled(fixedRate = HOUR_MS)
	@Async
	public void checkForUpdates() {

		List<WebPage> pages = dbController.getAllPages();

		pages.forEach(p -> {
			try {
				p.updateNewHtml();
				if (p.getOldHtml() == null) {
					ackChanges(p.getURL());
				} else {
					p.setChanged(!p.getOldHtml().hasSameValue(p.getNewHtml()));
				}
			} catch (IOException e) {
				e.printStackTrace();
				p.addError(e);
			}
			dbController.savePage(p);
		});
	}
}
