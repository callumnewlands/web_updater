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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Difference that = (Difference) o;
		return this.line.equals(that.line) && this.type == that.type;
	}

	@Override
	public String toString() {
		return "Difference{" +
				"line='" + line + '\'' +
				", type=" + type +
				'}';
	}
}
