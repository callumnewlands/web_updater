package web_updater;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Converter
public class JSoupDocumentConverter implements AttributeConverter<Document, String> {

	@Override
	public String convertToDatabaseColumn(Document document) {
		if (document == null) {
			return null;
		}
		return document.toString();
	}

	@Override
	public Document convertToEntityAttribute(String s) {
		if (s == null) {
			return null;
		}
		return Jsoup.parse(s);
	}
}
