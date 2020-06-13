package web_updater;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import javax.transaction.Transactional;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "/db")
public class DBController {

	@Autowired
	WebPageRepository webPageRepository;

	public void addURL(@RequestParam String url) throws MalformedURLException, URISyntaxException {
		// Check validity of URL - will throw exception if invalid
		new URL(url).toURI();

		// TODO don't allow "localhost" or equivalent to be added
		if (getAllPages().stream().map(WebPage::getURL).noneMatch(s -> s.equals(url))) {
			webPageRepository.save(new WebPage(url));
		}
	}

	@GetMapping("/all")
	public String getAllPagesPage(Model model) {
		List<WebPage> pages = getAllPages();
		pages.forEach(p -> {
			if (p.getOldHtml() != null) {
				p.getOldHtml().outputSettings().prettyPrint(true);
			}
			if (p.getNewHtml() != null) {
				p.getNewHtml().outputSettings().prettyPrint(true);
			}
		});
		model.addAttribute("pages", pages);
		return "db_all";
	}

	public List<WebPage> getAllPages() {
		return webPageRepository.findAll();
	}

	@Transactional
	public void setPageChanged(String url, boolean b) {
		WebPage p = getPage(url);
		p.setChanged(b);
		webPageRepository.save(p);
	}

	public WebPage getPage(String url) {
		return webPageRepository.getOne(url);
	}

	@Transactional
	public void setPageOldHTML(String url, Document value) {
		WebPage p = getPage(url);
		p.setOldHtml(value);
		webPageRepository.save(p);
	}

	public void savePage(WebPage p) {
		webPageRepository.save(p);
	}
}
