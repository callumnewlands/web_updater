package web_updater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Entity
public class WebPage {
	@Id
	private String URL;
	private Boolean changed = false;
	@ElementCollection
	private List<Exception> errors = new ArrayList<>();
	@Lob
	@Convert(converter = JSoupDocumentConverter.class)
	private Document oldPage = null;
	@Lob
	@Convert(converter = JSoupDocumentConverter.class)
	private Document newPage;

	public WebPage() {
	}

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
			return false; // Watch has just been added for this page
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

