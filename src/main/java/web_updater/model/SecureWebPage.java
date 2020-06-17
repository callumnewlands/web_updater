package web_updater.model;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.MapKeyColumn;
import javax.persistence.PrimaryKeyJoinColumn;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import web_updater.database.EncryptionConverter;

@Entity
@PrimaryKeyJoinColumn(name = "URL")
public class SecureWebPage extends WebPage {

	@ElementCollection
	@MapKeyColumn(name = "field")
	@Column(name = "value")
	@Convert(attributeName = "value", converter = EncryptionConverter.class)
	Map<String, String> postData;
	private String loginURL;

	public SecureWebPage() {
	}

	public SecureWebPage(String url, String loginURL, Map<String, String> postData) {
		super(url);
		this.loginURL = loginURL;
		this.postData = postData;
	}

	@Override
	public void updateNewHtml() throws IOException {

		String[] postDataStrings = postData.entrySet().stream()
				.flatMap(p -> Stream.of(p.getKey(), p.getValue()))
				.toArray(String[]::new);

		Connection.Response response = Jsoup.connect(this.loginURL)
				.data(postDataStrings)
				.method(Connection.Method.POST)
				.execute();

		this.setNewHtml(response.parse());
	}
}
