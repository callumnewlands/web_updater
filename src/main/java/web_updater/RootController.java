package web_updater;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import web_updater.database.DBController;
import web_updater.model.AddSecureURLRequest;
import web_updater.model.AddURLRequest;
import web_updater.model.WebPage;

// TODO Unit Tests
// TODO Acc. Tests
// TODO Highlight difference
// TODO all granularity of differences
// TODO last updated timestamp
// TODO ignore comments - toggleable?

@Controller
public class RootController {

	private static final int SECOND_MS = 1000;
	private static final int MINUTE_MS = 60 * SECOND_MS;
	private static final int HOUR_MS = 60 * MINUTE_MS;

	private DBController dbController;

	public RootController(DBController dbController) {
		this.dbController = dbController;
	}

	@GetMapping( {"", "/"})
	public String getRoot(Model model) {
//		checkForUpdates();
		model.addAttribute("pages", dbController.getAllPages());
		model.addAttribute("req", new AddURLRequest());
		model.addAttribute("secReq", new AddSecureURLRequest());
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
	public RedirectView addURLToWatchList(@ModelAttribute AddURLRequest req) throws MalformedURLException, URISyntaxException {
		dbController.addPageByURL(req.getUrl());
		return new RedirectView("");
	}

	@PostMapping("add-watch-secure")
	public RedirectView addURLToWatchList(@ModelAttribute AddSecureURLRequest req) throws MalformedURLException, URISyntaxException, JsonProcessingException {
		Map<String, String> postData = new ObjectMapper().readValue("{" + req.getPostData() + "}", Map.class);

		dbController.addSecurePage(req.getUrl(), req.getLoginUrl(), postData);
		return new RedirectView("");
	}


	@GetMapping("update")
	public RedirectView update() {
		checkForUpdates();
		return new RedirectView("");
	}

	/**
	 * Checks for updates on the pages in the list of URLs
	 */
	@Scheduled(fixedRate = MINUTE_MS)
	@Async
	public void checkForUpdates() {

		List<WebPage> pages = dbController.getAllPages();

		pages.forEach(p -> {
			try {
				p.updateNewHtml();
				if (p.getOldHtml() == null) {
					ackChanges(p.getURL());
				} else {
					p.setChanged(!Utils.areDocumentsEqual(p.getOldHtml(), p.getNewHtml()));
				}
			} catch (IOException e) {
				e.printStackTrace();
				p.addError(e);
			}
			dbController.savePage(p);
		});
	}
}
