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
	public String asString() {
		if (value.contains("'") || value.contains("\"") || value.length() == 0) {
			return '"' + value + '"';
		} else {
			return value;
		}
	}
	
	@Override
	public String toString() {
		return "MAMLString(" + value + ")";
	}

	public static MAMLString parseString(String stringToParse) {
		MAMLTable.log("Parsing String:" + stringToParse);
		
		int size = stringToParse.length() - 1;
		// Remove quotation marks
		if (size > 0) {
			// Front
			if (stringToParse.charAt(0) == '"' || stringToParse.charAt(0) == '\'') {
				stringToParse = stringToParse.substring(1);
			}
			
			// Back
			if (stringToParse.charAt(size - 1) == '"' || stringToParse.charAt(size - 1) == '\'') {
				stringToParse = stringToParse.substring(0, size - 1);
			}
		}
		
		MAMLTable.log("Result: " + stringToParse);
		
		return new MAMLString(stringToParse);
	}
	
	public boolean equals(MAMLString string) {
		return string.asString().equals(this.asString());
	}
}