package web_updater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebPage {

	private String URL;
	private Boolean changed;
	private List<Exception> errors = new ArrayList<>();
	private Document oldPage = null;
	private Document newPage;

	public WebPage(String URL) {
		this.URL = URL;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String URL) {
		this.URL = URL;
	}

	public Boolean getChanged() {
		return changed;
	}

	public List<Exception> getErrors() {
		return errors;
	}

	public Document getOldHTML() {
		return oldPage;
	}

	public Document getNewHTML() {
		return newPage;
	}

	private boolean hasChanged() {
		if (oldPage == null) {
			return false;
		}
		return oldPage.hasSameValue(newPage);
	}

	void update() {
		try {
			newPage = Jsoup.connect(this.URL).get();
			this.changed = hasChanged();
			if (oldPage == null) {
				this.oldPage = this.newPage;
			}
		} catch (IOException e) {
			e.printStackTrace();
			errors.add(e);
		}
	}

	public void acknowledgeChanges() {
		System.out.println("ACK");
		this.changed = false;
		this.oldPage = this.newPage;
	}
}

