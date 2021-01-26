package maml.values;

public class MAMLBoolean implements MAMLValue{
	boolean value;
	
	public MAMLBoolean(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}
	
	@Override
	public String asKey() {
		return String.valueOf(value);
	}
	
	@Override
	public String toString() {
		return value + "";
	}
}