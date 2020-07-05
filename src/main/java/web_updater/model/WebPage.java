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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import web_updater.database.JSoupDocumentConverter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class WebPage {

	@Transient
	List<Difference> differences;

	@Id
	private String URL;

	private Boolean changed = false;

	@ElementCollection
	@Setter(AccessLevel.NONE)
	private List<String> errors = new ArrayList<>();

	@Lob
	@Convert(converter = JSoupDocumentConverter.class)
	private Document oldHtml = null;

	@Lob
	@Convert(converter = JSoupDocumentConverter.class)
	private Document newHtml;

	public WebPage(String URL) {
		this.URL = URL;
	}

	public void addError(IOException e) {
		errors.add(e.getMessage());
	}

	public void updateNewHtml() throws IOException {
		this.newHtml = Jsoup.connect(this.URL).get();
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

