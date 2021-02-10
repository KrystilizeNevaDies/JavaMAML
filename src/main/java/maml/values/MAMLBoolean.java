package maml.values;

public class MAMLBoolean implements MAMLValue {
	boolean value;
	
	public MAMLBoolean(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}
	
	@Override
	public String asString() {
		return String.valueOf(value);
	}
	
	@Override
	public String toString() {
		return "MAMLBoolean(" + value + ")";
	}

	public static MAMLBoolean parseString(String stringToParse) {
		MAMLTable.log("Parsing Boolean:" + stringToParse);
		
		return new MAMLBoolean(stringToParse.equals("true"));
	}
	
	public boolean equals(MAMLBoolean bool) {
		return bool.getValue() == this.getValue();
	}
}