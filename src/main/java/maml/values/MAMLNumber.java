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
		String output = String.format("%f", value);
		while (output.endsWith("0"))
			output = output.substring(0, output.length() - 1);
		
		if (output.endsWith("."))
			output = output.substring(0, output.length() - 1);
			
		return output;
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