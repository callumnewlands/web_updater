package web_updater.database;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import web_updater.model.SecureWebPage;
import web_updater.model.WebPage;
import web_updater.security.UserDetails;

@Controller
@RequestMapping(path = "/admin/db")
public class DBController {

	@Autowired
	WebPageRepository<WebPage> webPageRepository;

	@Autowired
	WebPageRepository<SecureWebPage> secureWebPageRepository;

	@ModelAttribute
	public void addUser(Model model, @AuthenticationPrincipal UserDetails user) {
		model.addAttribute("user", user);
	}

	// TODO Unit Tests https://spring.io/guides/gs/testing-web/
	@GetMapping( {"", "/"})
	public String getAllPagesPage(Model model) {
		model.addAttribute("pages", getAllPages());
		return "db_all";
	}

	// TODO test
	public void addPageByURL(String url) throws MalformedURLException, URISyntaxException {
		// Check validity of URL - will throw exception if invalid
		new URL(url).toURI();

		// TODO don't allow "localhost" or equivalent to be added
		if (getAllPages().stream().map(WebPage::getURL).noneMatch(s -> s.equals(url))) {
			webPageRepository.save(new WebPage(url));
		}
	}

	public void removePageByURL(String url) {
		webPageRepository.deleteById(url);
	}

	// TODO test
	public void addSecurePage(String url, String loginURL, Map<String, String> postData) throws MalformedURLException, URISyntaxException {
		// Check validity of URL - will throw exception if invalid
		new URL(url).toURI();

		// TODO don't allow "localhost" or equivalent to be added
		if (getAllPages().stream().map(WebPage::getURL).noneMatch(s -> s.equals(url))) {
			secureWebPageRepository.save(new SecureWebPage(url, loginURL, postData));
		}
	}

	public WebPage getPage(String url) {
		return webPageRepository.getOne(url);
	}

	public List<WebPage> getAllPages() {
		return webPageRepository.findAll();
	}

	// TODO test
	@Transactional
	public void setPageChanged(String url, boolean b) {
		WebPage p = getPage(url);
		p.setChanged(b);
		webPageRepository.save(p);
	}

	// TODO test
	@Transactional
	public void setPageOldHTML(String url, Document value) {
		WebPage p = getPage(url);
		p.setOldHtml(value);
		webPageRepository.save(p);
	}

	public void savePage(WebPage p) {
		webPageRepository.save(p);
	}

	// TODO test
	public void ackChanges(String url) {
		setPageChanged(url, false);
		WebPage page = getPage(url);
		setPageOldHTML(url, page.getNewHtml());
	}

}
