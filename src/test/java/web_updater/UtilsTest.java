package web_updater;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static web_updater.model.Difference.DifferenceType.ADDED;
import static web_updater.model.Difference.DifferenceType.REMOVED;
import static web_updater.model.Difference.DifferenceType.UNCHANGED;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import web_updater.model.Difference;

class UtilsTest {

	private static final String EXAMPLE_HTML = "<!DOCTYPE html>\n<html>\n<body>\n\n<h1>My First Heading</h1>\n\n<p>My first paragraph.</p>\n\n</body>\n</html>\n";
	private static final String EXAMPLE_HTML_2 = "<!DOCTYPE html>\n<html>\n<body>\n\n<h1>My First Heading</h1>\n\n<p>My second paragraph.</p>\n\n</body>\n</html>\n";
	private static final String EXAMPLE_HTML_COMMENT = "<!DOCTYPE html>\n<html>\n<body>\n\n<!-- This is a comment --><h1>My First Heading</h1>\n\n<p>My first paragraph.</p>\n\n</body>\n</html>\n";

	private static IntStream getIntsTo10() {
		return IntStream.range(0, 10);
	}

	private static Stream<Arguments> getListIndexArgs() {
		return IntStream.range(1, 10).boxed().flatMap(x -> IntStream.range(0, 10 - x).mapToObj(y -> Arguments.of(x, y)));
	}

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

	@ParameterizedTest
	@MethodSource("getIntsTo10")
	void getDiffListOnSameLists(int size) {
		String[] lines = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		List<Difference> actual = Utils.getDiffList(List.of(lines).subList(0, size), List.of(lines).subList(0, size));
		List<Difference> expected = Arrays.stream(lines).limit(size).map(
				s -> new Difference(s, UNCHANGED))
				.collect(Collectors.toList());
		assertThat(actual, is(expected));
	}

	@ParameterizedTest
	@MethodSource("getListIndexArgs")
	void getDiffListSingleBlockAdditions(int sublistSize, int index) {
		String[] lines = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		List<String> completeList = List.of(lines);
		List<String> subList = List.of(lines).subList(index, sublistSize + index);
		List<Difference> actual = Utils.getDiffList(subList, completeList);
		List<Difference> expected = subList.stream().map(
				s -> new Difference(s, UNCHANGED))
				.collect(Collectors.toList());
		// additions before sublist
		for (int i = 0; i < index; i++) {
			expected.add(i, new Difference(lines[i], ADDED));
		}
		// additions after sublist
		for (int i = index + sublistSize; i < 10; i++) {
			expected.add(i, new Difference(lines[i], ADDED));
		}
		assertThat(actual, is(expected));
	}

	@ParameterizedTest
	@MethodSource("getListIndexArgs")
	void getDiffListSingleBlockRemovals(int sublistSize, int index) {
		String[] lines = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		List<String> completeList = List.of(lines);
		List<String> subList = List.of(lines).subList(index, sublistSize + index);
		List<Difference> actual = Utils.getDiffList(completeList, subList);
		List<Difference> expected = subList.stream().map(
				s -> new Difference(s, UNCHANGED))
				.collect(Collectors.toList());
		// removals before sublist
		for (int i = 0; i < index; i++) {
			expected.add(i, new Difference(lines[i], REMOVED));
		}
		// removals after sublist
		for (int i = index + sublistSize; i < 10; i++) {
			expected.add(i, new Difference(lines[i], REMOVED));
		}
		assertThat(actual, is(expected));
	}

	@ParameterizedTest
	@MethodSource("getIntsTo10")
	void getDiffListSingleModification(int index) {
		String[] lines = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
		List<String> completeList = List.of(lines);
		ArrayList<String> modifiedList = new ArrayList<>(completeList);
		modifiedList.remove(index);
		modifiedList.add(index, "X");

		List<Difference> actual = Utils.getDiffList(completeList, modifiedList);
		List<Difference> expected = Arrays.stream(lines).map(
				s -> new Difference(s, UNCHANGED)).collect(Collectors.toList());
		Difference removed = expected.remove(index);
		expected.add(index, new Difference(removed.getLine(), REMOVED));
		expected.add(index + 1, new Difference("X", ADDED));
		assertThat(actual, is(expected));
	}
}