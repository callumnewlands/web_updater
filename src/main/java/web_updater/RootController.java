package web_updater;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import web_updater.database.DBController;
import web_updater.database.UpdateController;
import web_updater.model.AddSecureURLRequest;
import web_updater.model.AddURLRequest;
import web_updater.model.Difference;
import web_updater.model.WebPage;

// TODO Unit Tests
// TODO Acc. Tests
// TODO last updated timestamp
// TODO make ignoring timestamps etc toggleable

@Controller
public class RootController {

	private DBController dbController;
	@Autowired
	private UpdateController updateController;

	public RootController(DBController dbController) {
		this.dbController = dbController;
	}

	@GetMapping( {"", "/"})
	public String getRoot(Model model) {
		updateController.checkForUpdates();
		model.addAttribute("pages", dbController.getAllPages());
		model.addAttribute("req", new AddURLRequest());
		model.addAttribute("secReq", new AddSecureURLRequest());
		return "index";
	}

	@GetMapping("visual")
	public String getVisualisation(@RequestParam String url, Model model) {
		model.addAttribute("page", dbController.getPage(url));
		return "visualisation";
	}

	@GetMapping("diff")
	public String getDiff(@RequestParam String url, Model model) {
		WebPage page = dbController.getPage(url);

		List<String> oldLines = page.getOldHtml().toString().lines().collect(Collectors.toList());
		List<String> newLines = page.getNewHtml().toString().lines().collect(Collectors.toList());
		List<Difference> differences = Utils.getDiffList(oldLines, newLines);
		page.setDiffList(differences);
		model.addAttribute("page", page);
		return "diff";
	}

	@PostMapping("ack-changes")
	public RedirectView ackChanges(@RequestParam String url) {
		dbController.ackChanges(url);
		return new RedirectView("");
	}

	@PostMapping("add-watch")
	public RedirectView addURLToWatchList(@ModelAttribute AddURLRequest req) throws MalformedURLException, URISyntaxException {
		dbController.addPageByURL(req.getUrl());
		return ackChanges(req.getUrl());
	}

	@PostMapping("add-watch-secure")
	public RedirectView addURLToWatchList(@ModelAttribute AddSecureURLRequest req) throws MalformedURLException, URISyntaxException, JsonProcessingException {
		Map<String, String> postData = new ObjectMapper().readValue("{" + req.getPostData() + "}", Map.class);

		dbController.addSecurePage(req.getUrl(), req.getLoginUrl(), postData);
		return ackChanges(req.getUrl());
	}

}
