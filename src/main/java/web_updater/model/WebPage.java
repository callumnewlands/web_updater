package web_updater.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Transient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import web_updater.database.JSoupDocumentConverter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class WebPage {
	@Id
	private String URL;
	private Boolean changed = false;
	@ElementCollection
	private List<String> errors = new ArrayList<>();
	@Lob
	@Convert(converter = JSoupDocumentConverter.class)
	private Document oldHtml = null;
	@Lob
	@Convert(converter = JSoupDocumentConverter.class)
	private Document newHtml;

	@Transient
	List<Difference> differences;

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

	public List<String> getErrors() {
		return errors;
	}

	public Document getOldHtml() {
		return oldHtml;
	}

	public List<Difference> getDiffList() {
		return differences;
	}

	public void setDiffList(List<Difference> diffList) {
		this.differences = diffList;
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
		errors.add(e.getMessage());
	}

	public void updateNewHtml() throws IOException {
		this.setNewHtml(Jsoup.connect(this.URL).get());
	}

	@Override
	public String toString() {
		return "WebPage{" +
				"URL='" + URL + '\'' +
				", changed=" + changed +
				", errors=" + errors +
				", oldHtml=" + (oldHtml == null ? "null" : oldHtml.nodeName()) + "..." +
				", newHtml=" + (newHtml == null ? "null" : newHtml.nodeName()) + "..." +
				'}';
	}

}

