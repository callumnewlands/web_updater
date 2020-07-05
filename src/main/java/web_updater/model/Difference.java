package web_updater.model;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
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
}
