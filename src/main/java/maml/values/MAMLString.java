package maml.values;

public class MAMLString implements MAMLValue {
	private String value;
	
	public MAMLString(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	@Override
	public String asKey() {
		return value;
	}
	
	@Override
	public String toString() {
		return '"' + value + '"';
	}
}