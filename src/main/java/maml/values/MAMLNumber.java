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
	public String asString() {
		return String.format("%f", value);
	}
	
	@Override
	public String toString() {
		return "MAMLNumber(" + value + ")";
	}

	public static MAMLNumber parseString(String stringToParse) {
		MAMLTable.log("Parsing Number:" + stringToParse);
		if (stringToParse.length() > 2)
			switch(stringToParse.substring(0, 2)) {
				case "0x":
					return new MAMLNumber(Double.valueOf(Long.parseLong(stringToParse.substring(2), 16)));
				case "0b":
					return new MAMLNumber(Double.valueOf(Integer.parseInt(stringToParse.substring(2), 2)));
				default:
					return new MAMLNumber(Double.valueOf(stringToParse));
			}
		else
			return new MAMLNumber(Double.valueOf(stringToParse));
	}
	
	public boolean equals(MAMLNumber number) {
		return number.getValue() == this.getValue();
	}
}