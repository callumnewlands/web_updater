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

		List<String> d1Lines = d1.toString().lines().collect(Collectors.toList());
		List<String> d2Lines = d2.toString().lines().collect(Collectors.toList());
		List<Difference> differences = getDiffList(d1Lines, d2Lines);
		if (differences.stream().anyMatch(d -> d.getType() != UNCHANGED)) {
			return false;
		}
		return true;
	}

	private static String removeDatesTimesCommentsWhitespace(final String str) {

		final String TIME_PATTERN = "[Tt]?(?:\\d|[01]\\d|2[0-3]):[0-5]\\d(:[0-5]\\d)?(.\\d+)?[Zz]?|" +
				"([Tt]imestamp\\s*[-:]?\\s*\\d\\d*\\.?\\d\\d*)|" +
				"(([Aa]bout)?\\s*\\d+\\s*([Mm]inute(s?)|[Hh]our(s?)|[Dd]ay(s?))\\s([Aa]go)?)";

		final String SEPARATOR_PATTERN = "[-\\s*/.]";
		final String DAY_OF_MONTH_PATTERN = "(([12][0-9]|3[01]|0?[1-9])(st|nd|th)?)";
		final String DATE_PATTERN_YYYY_MM_DD = "((19|20)\\d\\d" + SEPARATOR_PATTERN + "(0[1-9]|1[012])" + SEPARATOR_PATTERN + DAY_OF_MONTH_PATTERN;
		final String DATE_PATTERN_MM_DD_YY = "((0[1-9]|1[012])" + SEPARATOR_PATTERN + DAY_OF_MONTH_PATTERN + SEPARATOR_PATTERN + "(19|20)?\\d\\d)";
		final String DATE_PATTERN_DD_MM_YY = DAY_OF_MONTH_PATTERN + SEPARATOR_PATTERN + "(0[1-9]|1[012])" + SEPARATOR_PATTERN + "(19|20)?\\d\\d)";
		final String DATE_PATTERN_DAY = "(([Mm])on|([Tt])ue(s)?|([Ww])ed(nes)?|([Tt])hu(rs)?|([Ff])ri|([Ss])at(ur)?|([Ss])un)(day)?";
		final String DATE_PATTERN_MONTH = "(Jan(uary)?|Feb(ruary)?|Mar(ch)?|Apr(il)?|May|Jun(e)?|Jul(y)?|Aug(ust)?|Sep(tember)?|Oct(ober)?|Nov(ember)?|Dec(ember))";
		final String LONG_DATE_PATTERN =
				"(" + DATE_PATTERN_DAY + SEPARATOR_PATTERN + "?(" + DATE_PATTERN_MONTH + ")?"  + SEPARATOR_PATTERN +  "?" + DAY_OF_MONTH_PATTERN  + SEPARATOR_PATTERN + "?(" + DATE_PATTERN_MONTH + ")?"  + SEPARATOR_PATTERN + "?\\d*)";
		final String DATE_PATTERN =
				DATE_PATTERN_YYYY_MM_DD + "|" + DATE_PATTERN_MM_DD_YY + "|" + DATE_PATTERN_DD_MM_YY + "|" + LONG_DATE_PATTERN;

		final String HTML_COMMENT_PATTERN = "<!--(.*?)-->";

		String s = str.replaceAll(TIME_PATTERN, "");
		s = s.replaceAll(DATE_PATTERN, "");
		s = s.replaceAll(HTML_COMMENT_PATTERN, "");
		s = s.replaceAll(" ", "");

		return s;
	}

	public static boolean areLinesEqual(final String l1, final String l2) {
		if (l1.equals(l2)) {
			return true;
		}

		String l1Edit = removeDatesTimesCommentsWhitespace(l1);
		String l2Edit = removeDatesTimesCommentsWhitespace(l2);

		return l1Edit.equals(l2Edit);
	}

	public static List<Difference> getDiffList(List<String> oldLines, List<String> newLines) {
		LinkedList<String> oldLinesStack = new LinkedList<>(oldLines);
		LinkedList<String> newLinesStack = new LinkedList<>(newLines);
		List<Difference> differences = new ArrayList<>();

		while (!oldLinesStack.isEmpty() || !newLinesStack.isEmpty()) {
			String topOld = oldLinesStack.peek();
			String topNew = newLinesStack.peek();

			if (oldLinesStack.isEmpty()) {
				for (String s : newLinesStack) {
					differences.add(new Difference(s, ADDED));
				}
				break;
			} else if (newLinesStack.isEmpty()) {
				for (String s : oldLinesStack) {
					differences.add(new Difference(s, REMOVED));
				}
				break;
			}

			if (Utils.areLinesEqual(topOld, topNew)) {
				differences.add(new Difference(topOld, UNCHANGED));
				oldLinesStack.pop();
				newLinesStack.pop();
				continue;
			}
			boolean inserted = false;
			for (int i = 0; i < Math.max(oldLinesStack.size(), newLinesStack.size()); i++) {
				if (i < newLinesStack.size()) {
					for (int j = 0; j <= Math.min(i, oldLinesStack.size() - 1); j++) {
						if (Utils.areLinesEqual(oldLinesStack.get(j), newLinesStack.get(i))) {
							topOld = oldLinesStack.pop();
							topNew = newLinesStack.pop();
							// removed: everything up to old[j]
							for (int k = 0; k < j; k++) {
								differences.add(new Difference(topOld, REMOVED));
								topOld = oldLinesStack.pop();
							}
							// added: everything up to new[i]
							for (int k = 0; k < i; k++) {
								differences.add(new Difference(topNew, ADDED));
								topNew = newLinesStack.pop();
							}
							// unchanged: old[j] == new[i]
							differences.add(new Difference(topNew, UNCHANGED));
							inserted = true;
							break;
						}
					}
					if (inserted) {
						break;
					}
				}
				if (i < oldLinesStack.size()) {
					for (int j = 0; j <= Math.min(i, newLinesStack.size() - 1); j++) {
						if (Utils.areLinesEqual(oldLinesStack.get(i), newLinesStack.get(j))) {
							topOld = oldLinesStack.pop();
							topNew = newLinesStack.pop();
							// removed: everything up to old[i]
							for (int k = 0; k < i; k++) {
								differences.add(new Difference(topOld, REMOVED));
								topOld = oldLinesStack.pop();
							}
							// added: everything up to new[j]
							for (int k = 0; k < j; k++) {
								differences.add(new Difference(topNew, ADDED));
								topNew = newLinesStack.pop();
							}
							// unchanged: old[j] == new[i]
							differences.add(new Difference(topNew, UNCHANGED));
							inserted = true;
							break;
						}
					}
					if (inserted) {
						break;
					}
				}
			}
			// reached if the last element of the list has been modified (removed and added)
			if (!inserted) {
				differences.add(new Difference(topOld, REMOVED));
				differences.add(new Difference(topNew, ADDED));
				oldLinesStack.pop();
				newLinesStack.pop();
			}
		}

		return differences;
	}

}
