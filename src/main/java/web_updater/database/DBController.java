package web_updater.database;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import web_updater.model.SecureWebPage;
import web_updater.model.WebPage;

@Controller
@RequestMapping(path = "/db")
public class DBController {

	@Autowired
	WebPageRepository<WebPage> webPageRepository;

	@Autowired
	WebPageRepository<SecureWebPage> secureWebPageRepository;

	public void addPageByURL(String url) throws MalformedURLException, URISyntaxException {
		// Check validity of URL - will throw exception if invalid
		new URL(url).toURI();

		// TODO don't allow "localhost" or equivalent to be added
		if (getAllPages().stream().map(WebPage::getURL).noneMatch(s -> s.equals(url))) {
			webPageRepository.save(new WebPage(url));
		}
	}

	public void addSecurePage(String url, String loginURL, Map<String, String> postData) throws MalformedURLException, URISyntaxException {
		// Check validity of URL - will throw exception if invalid
		new URL(url).toURI();

		// TODO don't allow "localhost" or equivalent to be added
		if (getAllPages().stream().map(WebPage::getURL).noneMatch(s -> s.equals(url))) {
			secureWebPageRepository.save(new SecureWebPage(url, loginURL, postData));
		}
	}

	@GetMapping("/all")
	public String getAllPagesPage(Model model) {
		model.addAttribute("pages", getAllPages());
		return "db_all";
	}

	public WebPage getPage(String url) {
		return webPageRepository.getOne(url);
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
