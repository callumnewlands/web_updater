package web_updater;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static web_updater.model.Difference.DifferenceType.ADDED;
import static web_updater.model.Difference.DifferenceType.REMOVED;
import static web_updater.model.Difference.DifferenceType.UNCHANGED;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import web_updater.model.Difference;

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
		if (d1.outerHtml().equals(d2.outerHtml())) {
			return true;
		}

		List<Difference> differences = getDiffList(d1, d2);
		if (differences.stream().anyMatch(d -> d.getType() != UNCHANGED)) {
			return false;
		}
		return true;
	}

	private static String removeDatesTimesCommentsWhitespace(final String str) {

		final String TIME_PATTERN = "(?:\\d|[01]\\d|2[0-3]):[0-5]\\d(:[0-5]\\d)?";

		final String DATE_PATTERN_YYYY_MM_DD = "((19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01]))";
		final String DATE_PATTERN_MM_DD_YYYY = "((0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d)";
		final String DATE_PATTERN_DD_MM_YYYY = "((0[1-9]|[12][0-9]|3[01])[- /.](0[1-9]|1[012])[- /.](19|20)\\d\\d)";
		final String DATE_PATTERN_DAY = "((M|m)on|(T|t)ue(s)?|(W|w)ed(nes)?|(T|t)hu(rs)?|(F|f)ri|(S|s)at(ur)?|(S|s)un)(day)?";
		final String DATE_PATTERN_MONTH = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))";
		final String LONG_DATE_PATTERN =
				DATE_PATTERN_DAY + "[- /.]?(0[1-9]|[12][0-9]|3[01])?[- /.]?" + DATE_PATTERN_MONTH + "[- /.]?(0[1-9]|[12][0-9]|3[01])?[- /.]?";
		final String DATE_PATTERN =
				DATE_PATTERN_YYYY_MM_DD + "|" + DATE_PATTERN_MM_DD_YYYY + "|" + DATE_PATTERN_DD_MM_YYYY + "|" + LONG_DATE_PATTERN;

		final String HTML_COMMENT_PATTERN = "<!--(.*?)-->";

		String s = str.replaceAll(TIME_PATTERN, "");
		s = s.replaceAll(DATE_PATTERN, "");
		s = s.replaceAll(HTML_COMMENT_PATTERN, "");
		s = s.replaceAll(" ", "");

		return s;
	}

	public static List<Difference> getDiffList(Document oldHtml, Document newHtml) {
		LinkedList<String> oldLines = oldHtml.toString().lines().collect(Collectors.toCollection(LinkedList::new));
		LinkedList<String> newLines = newHtml.toString().lines().collect(Collectors.toCollection(LinkedList::new));

		List<Difference> differences = new ArrayList<>();

		while (!oldLines.isEmpty() || !newLines.isEmpty()) {
			String topOld = oldLines.pop();
			String topNew = newLines.pop();
			if (Utils.areLinesEqual(topOld, topNew)) {
				differences.add(new Difference(topOld, UNCHANGED));
				continue;
			}
			boolean inserted = false;
			for (int i = 0; i < Math.max(oldLines.size(), newLines.size()); i++) {
				if (i < newLines.size()) {
					if (Utils.areLinesEqual(topOld, newLines.get(i))) {
						for (int j = 0; j <= i; j++) {
							differences.add(new Difference(topNew, ADDED));
							topNew = newLines.pop();
						}
						differences.add(new Difference(topNew, UNCHANGED));
						inserted = true;
						break;
					}
				}
				if (i < oldLines.size()) {
					if (Utils.areLinesEqual(topNew, oldLines.get(i))) {
						for (int j = 0; j <= i; j++) {
							differences.add(new Difference(topOld, REMOVED));
							topOld = oldLines.pop();
						}
						differences.add(new Difference(topOld, UNCHANGED));
						inserted = true;
						break;
					}
				}
			}
			// TODO optimise this so that O(2n) is not needed for modified (removed and added) lines
			if (!inserted) {
				differences.add(new Difference(topOld, REMOVED));
				differences.add(new Difference(topNew, ADDED));
			}
		}

		return differences;
	}

	public static boolean areLinesEqual(final String l1, final String l2) {
		if (l1.equals(l2)) {
			return true;
		}

		String l1Edit = removeDatesTimesCommentsWhitespace(l1);
		String l2Edit = removeDatesTimesCommentsWhitespace(l2);

		return l1Edit.equals(l2Edit);
	}
}
