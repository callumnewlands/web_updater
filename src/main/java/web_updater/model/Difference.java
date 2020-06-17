package web_updater.model;

public class Difference {
	public enum DifferenceType {
		ADDED, REMOVED, UNCHANGED
	}

	private String line;
	private DifferenceType type;

	public Difference(String line, DifferenceType type) {
		this.line = line;
		this.type = type;
	}

	public String getLine() {
		return line;
	}

	public DifferenceType getType() {
		return type;
	}
}
