package web_updater;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path = "/db")
public class DBController {

	@Autowired
	WebPageRepository webPageRepository;

	public void addURL(@RequestParam String url) throws MalformedURLException, URISyntaxException {
		// Check validity of URL - will throw exception if invalid
		new URL(url).toURI();

		// TODO don't allow "localhost" or equivalent to be added
		if (getAllURLs().stream().map(WebPage::getURL).noneMatch(s -> s.equals(url))) {
			webPageRepository.save(new WebPage(url));
		}
	}

	@GetMapping(path = "/all")
	public @ResponseBody
	List<WebPage> getAllURLs() {
		// This returns a JSON or XML with the users
		return webPageRepository.findAll();
	}

}
