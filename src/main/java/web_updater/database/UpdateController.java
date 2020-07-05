package web_updater.database;

import java.io.IOException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import web_updater.Utils;
import web_updater.model.WebPage;

@Controller
@Log4j2
public class UpdateController {

	private static final int SECOND_MS = 1000;
	private static final int MINUTE_MS = 60 * SECOND_MS;
	private static final int HOUR_MS = 60 * MINUTE_MS;

	@Autowired
	private DBController dbController;

	/**
	 * Checks for updates on the pages in the list of URLs
	 */
	// TODO test
	@Scheduled(fixedRate = HOUR_MS)
	@Async
	public void checkForUpdates() {

		log.info("Start checking for updates");

		List<WebPage> pages = dbController.getAllPages();

		pages.forEach(p -> {
			try {
				p.updateNewHtml();
				if (p.getOldHtml() == null) {
					dbController.ackChanges(p.getURL());
				} else {
					p.setChanged(!Utils.areDocumentsEqual(p.getOldHtml(), p.getNewHtml()));
				}
			} catch (IOException e) {
				e.printStackTrace();
				p.addError(e);
			}
			dbController.savePage(p);
		});

		log.info("Finished checking for updates");

	}
}
