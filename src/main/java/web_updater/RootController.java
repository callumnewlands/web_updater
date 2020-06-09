package web_updater;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

	@GetMapping( {"", "/"})
	public String getRoot(Model model) {
		model.addAttribute("name", "Test Name");
		return "main";
	}

}
