package web_updater;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

class UtilsTest {

	private static final String EXAMPLE_HTML = "<!DOCTYPE html>\n<html>\n<body>\n\n<h1>My First Heading</h1>\n\n<p>My first paragraph.</p>\n\n</body>\n</html>\n";
	private static final String EXAMPLE_HTML_2 = "<!DOCTYPE html>\n<html>\n<body>\n\n<h1>My First Heading</h1>\n\n<p>My second paragraph.</p>\n\n</body>\n</html>\n";
	private static final String EXAMPLE_HTML_COMMENT = "<!DOCTYPE html>\n<html>\n<body>\n\n<!-- This is a comment --><h1>My First Heading</h1>\n\n<p>My first paragraph.</p>\n\n</body>\n</html>\n";

	@Test
	void areDocumentsEqualReturnsTrueForSameObject() {
		Document d1 = Jsoup.parse(EXAMPLE_HTML);
		Document d2 = Jsoup.parse(EXAMPLE_HTML_2);
		assertTrue(Utils.areDocumentsEqual(d1, d1));
		assertTrue(Utils.areDocumentsEqual(d2, d2));
	}

	@Test
	void areDocumentsEqualReturnsFalseIfEitherNullButTrueIfBoth() {
		Document d1 = Jsoup.parse(EXAMPLE_HTML);
		Document d2 = Jsoup.parse(EXAMPLE_HTML_2);

		assertTrue(Utils.areDocumentsEqual(null, null));

		assertFalse(Utils.areDocumentsEqual(d1, null));
		assertFalse(Utils.areDocumentsEqual(d2, null));
		assertFalse(Utils.areDocumentsEqual(null, d1));
		assertFalse(Utils.areDocumentsEqual(null, d2));
	}

	@Test
	void areDocumentsEqualReturnsTrueForSameOuterHTML() {
		Document d1 = Jsoup.parse(EXAMPLE_HTML);
		Document d2 = Jsoup.parse(EXAMPLE_HTML);

		assertTrue(Utils.areDocumentsEqual(d1, d2));
		assertTrue(Utils.areDocumentsEqual(d2, d1));

		Document d3 = Jsoup.parse(EXAMPLE_HTML_2);
		Document d4 = Jsoup.parse(EXAMPLE_HTML_2);

		assertTrue(Utils.areDocumentsEqual(d3, d4));
		assertTrue(Utils.areDocumentsEqual(d4, d3));
	}

	@Test
	void areDocumentsEqualReturnsTrueForUnchangedDiffListWithComment() {
		Document d1 = Jsoup.parse(EXAMPLE_HTML);
		Document d2 = Jsoup.parse(EXAMPLE_HTML_COMMENT);
		assertTrue(Utils.areDocumentsEqual(d1, d2));
		assertTrue(Utils.areDocumentsEqual(d2, d1));
	}

	@Test
	void areDocumentsEqualReturnsFalseForDifferentHTML() {
		Document d1 = Jsoup.parse(EXAMPLE_HTML);
		Document d2 = Jsoup.parse(EXAMPLE_HTML_2);
		assertFalse(Utils.areDocumentsEqual(d1, d2));
		assertFalse(Utils.areDocumentsEqual(d2, d1));
	}

	@Test
	void areLinesEqualReturnsTrueIfStringsEqual() {
		String s1 = "This is a test string";
		String s2 = "This is a test string";
		assertTrue(Utils.areLinesEqual(s1, s1));
		assertTrue(Utils.areLinesEqual(s1, s2));
		assertTrue(Utils.areLinesEqual(s2, s1));
		assertTrue(Utils.areLinesEqual(s2, s2));
	}

	@Test
	void areLinesEqualReturnsFalseIfStringsDifferentNoFiltering() {
		String s1 = "This is a test string";
		String s2 = "This is not a test string";
		String s3 = "This is also not a test string";

		assertFalse(Utils.areLinesEqual(s1, s2));
		assertFalse(Utils.areLinesEqual(s2, s1));

		assertFalse(Utils.areLinesEqual(s1, s3));
		assertFalse(Utils.areLinesEqual(s3, s1));

		assertFalse(Utils.areLinesEqual(s2, s3));
		assertFalse(Utils.areLinesEqual(s3, s2));
	}

	@Test
	void areLinesEqualReturnsTrueForCommentDifferences() {
		String s1 = "This is a test string";
		String s2 = "This is <!-- not --> a test string";
		String s3 = "This is <!-- also not --> a test string";

		assertTrue(Utils.areLinesEqual(s1, s2));
		assertTrue(Utils.areLinesEqual(s2, s1));

		assertTrue(Utils.areLinesEqual(s1, s3));
		assertTrue(Utils.areLinesEqual(s3, s1));

		assertTrue(Utils.areLinesEqual(s2, s3));
		assertTrue(Utils.areLinesEqual(s3, s2));
	}

	@Test
	void areLinesEqualReturnsTrueForTimeDifferences() {
		String s1 = "This is a test string";
		String s2 = "This is 23:14 a test string";
		String s3 = "This is 22:10:00 a test string";

		assertTrue(Utils.areLinesEqual(s1, s2));
		assertTrue(Utils.areLinesEqual(s2, s1));

		assertTrue(Utils.areLinesEqual(s1, s3));
		assertTrue(Utils.areLinesEqual(s3, s1));

		assertTrue(Utils.areLinesEqual(s2, s3));
		assertTrue(Utils.areLinesEqual(s3, s2));
	}

	@Test
	void areLinesEqualReturnsTrueForTimestampDifferences() {
		String s1 = "This is a test string";
		String s2 = "This is timestamp 1240929 a test string";
		String s3 = "This is Timestamp : 09123894 a test string";

		assertTrue(Utils.areLinesEqual(s1, s2));
		assertTrue(Utils.areLinesEqual(s2, s1));

		assertTrue(Utils.areLinesEqual(s1, s3));
		assertTrue(Utils.areLinesEqual(s3, s1));

		assertTrue(Utils.areLinesEqual(s2, s3));
		assertTrue(Utils.areLinesEqual(s3, s2));
	}

	@Test
	void areLinesEqualReturnsTrueForWhitespaceDifferences() {
		String s1 = "This is a test string";
		String s2 = "This is         a test string";
		String s3 = "This    is a     test  string";

		assertTrue(Utils.areLinesEqual(s1, s2));
		assertTrue(Utils.areLinesEqual(s2, s1));

		assertTrue(Utils.areLinesEqual(s1, s3));
		assertTrue(Utils.areLinesEqual(s3, s1));

		assertTrue(Utils.areLinesEqual(s2, s3));
		assertTrue(Utils.areLinesEqual(s3, s2));
	}

	@Test
	void areLinesEqualReturnsTrueForDateDifferences() {
		String s1 = "This is a test string";
		String s2 = "This is 2010-09-11 a test string";
		String s3 = "This is 06/14/20 a test string";
		String s4 = "This is 14/06/1945 a test string";

		assertTrue(Utils.areLinesEqual(s1, s2));
		assertTrue(Utils.areLinesEqual(s2, s1));

		assertTrue(Utils.areLinesEqual(s1, s3));
		assertTrue(Utils.areLinesEqual(s3, s1));

		assertTrue(Utils.areLinesEqual(s1, s4));
		assertTrue(Utils.areLinesEqual(s4, s1));

		assertTrue(Utils.areLinesEqual(s2, s3));
		assertTrue(Utils.areLinesEqual(s3, s2));

		assertTrue(Utils.areLinesEqual(s2, s4));
		assertTrue(Utils.areLinesEqual(s4, s2));

		assertTrue(Utils.areLinesEqual(s3, s4));
		assertTrue(Utils.areLinesEqual(s4, s3));
	}

	@Test
	void getDiffList() {
	}

}