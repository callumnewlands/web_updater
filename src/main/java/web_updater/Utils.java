package web_updater;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class Utils {

	private Utils() {
	}

	public static boolean areDocumentsEqual(Document d1, Document d2) {
		if (d1 == d2) {
			return true;
		}

		if (d1 == null || d2 == null) {
			return false;
		}

		d1 = Jsoup.parse(d1.outerHtml());
		d2 = Jsoup.parse(d2.outerHtml());

		return d1.outerHtml().equals(d2.outerHtml());

//		// BFS to check equality
//		Element currentOne = d1;
//		Queue<Element> fringeOne = new LinkedList<>();
//		Element currentTwo = d2;
//		Queue<Element> fringeTwo = new LinkedList<>();
//
//		do {
//			if (currentOne.childrenSize() != currentTwo.childrenSize() ||
//				!currentOne.tag().equals(currentTwo.tag()) ||
//				!currentOne.attributes().equals(currentTwo.attributes())) {
//				return false;
//			}
//			fringeOne.addAll(currentOne.children());
//			fringeTwo.addAll(currentTwo.children());
//			currentOne = fringeOne.remove();
//			currentTwo = fringeTwo.remove();
//		} while (!fringeOne.isEmpty());

//		return true;
	}
}
