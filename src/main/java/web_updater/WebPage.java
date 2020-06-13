package web_updater;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
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
	private Document oldHtml = null;
	@Lob
	@Convert(converter = JSoupDocumentConverter.class)
	private Document newHtml;

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

	public void setChanged(Boolean changed) {
		this.changed = changed;
	}

	public List<Exception> getErrors() {
		return errors;
	}

	public Document getOldHtml() {
		return oldHtml;
	}

	public Document getNewHtml() {
		return newHtml;
	}

	public void setOldHtml(Document oldHtml) {
		this.oldHtml = oldHtml;
	}

	public void setNewHtml(Document newHtml) {
		this.newHtml = newHtml;
	}

	public void addError(IOException e) {
		errors.add(e);
	}

	@Override
	public String toString() {
		return "WebPage{" +
				"URL='" + URL + '\'' +
				", changed=" + changed +
				", errors=" + errors +
				", oldHtml=" + oldHtml.nodeName() + "..." +
				", newHtml=" + newHtml.nodeName() + "..." +
				'}';
	}
}

