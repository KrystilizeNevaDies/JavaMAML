package maml.values;

public class MAMLNumber implements MAMLValue {
	private double value;
	
	public MAMLNumber(double value) {
		this.value = value;
	}

	public double getValue() {
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