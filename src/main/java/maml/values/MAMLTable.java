package maml.values;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MAMLTable implements MAMLValue {
	protected Map<MAMLValue, MAMLValue> table;
	
	public static boolean debug = false;
	
	public static final String dynamicStringCloseStr = ">";
	public static final String dynamicStringOpenStr = "<";
	public static final String tableCloseStr = "}";
	public static final String tableOpenStr = "{";
	public static final String delimStr = ";";
	public static final String setValueStr = "=";
	public static final String commentMiddleStr = "*";
	public static final String commentStartStr = "/";
	public static final char dynamicStringClose = dynamicStringCloseStr.charAt(0);
	public static final char dynamicStringOpen = dynamicStringOpenStr.charAt(0);
	public static final char tableClose = tableCloseStr.charAt(0);
	public static final char tableOpen = tableOpenStr.charAt(0);
	public static final char delim = delimStr.charAt(0);
	public static final char setValue = setValueStr.charAt(0);
	public static final char commentMiddle = commentMiddleStr.charAt(0);
	public static final char commentStart = commentStartStr.charAt(0);

	
	public MAMLTable() {
		table = new LinkedHashMap<MAMLValue, MAMLValue>();
	}
	
	@Override
	public String toString() {
		return "MAMLTable(" + asString(0, new ArrayList<MAMLValue>(), true).replaceAll("\n", ";") + ")";
	}
	
	public String toFileString() {
		return asString(0, new ArrayList<MAMLValue>(), false);
	}
	
	public String asString(int depth, ArrayList<MAMLValue> arrayList, boolean addBrackets) {
		String s = "";
		
		// Add start bracket if applicable
		if (addBrackets)
			s += MAMLTable.tableOpen + "\n";
		else
			s += "\n";
		
		String tab = "    ";
		for (Entry<MAMLValue, MAMLValue> entry : table.entrySet()) {
			if (!arrayList.contains(entry.getValue())) {
				
				// Get key + value strings
				String key = entry.getKey().asString();
				String value = entry.getValue().asString();
				
				if (entry.getKey() instanceof MAMLTable)
					key = ((MAMLTable) entry.getKey()).asString(depth + 1, arrayList, true);
				if (entry.getValue() instanceof MAMLTable)
					value = ((MAMLTable) entry.getValue()).asString(depth + 1, arrayList, true);
				
				s += tab.repeat(depth) + key + " = " + value + "\n";
			}
		}
		
		if (addBrackets)
			s += tab.repeat(depth - 1) + MAMLTable.tableClose;
		return s;
	}
	
	public void set(MAMLValue key, MAMLValue value) {
		this.table.put(key, value);
	}
	
	public void add(MAMLValue value) {
		Integer i = 0;
		
		while (get(new MAMLNumber(i)) != null)
			i++;
		
		this.table.put(new MAMLNumber(i), value);
	}
	
	public MAMLValue get(MAMLValue key) {
		for(MAMLValue value : table.keySet()) {
			if (value.asString().equals(key.asString()))
				return this.table.get(value);
		}
		return null;
	}
	
	public MAMLValue get(String key) {
		for(MAMLValue value : table.keySet()) {
			if (value.asString().equals(key))
				return this.table.get(value);
		}
		return null;
	}
	
	public boolean exists(MAMLValue key) {
		for(MAMLValue value : table.keySet()) {
			if (value.asString().equals(key.asString()))
				return true;
		}
		return false;
	}
	
	public MAMLTable getTable(MAMLValue key) {
		var output = ((MAMLTable) get(key));
		
		if (output == null)
			return null;
		
		return output;
	}
	
	public MAMLTable getTable(String key) {
		return getTable(new MAMLString(key));
	}
	
	public String getString(MAMLValue key) {
		var output = ((MAMLString) get(key));
		
		if (output == null)
			return null;
		
		return output.getValue();
	}
	
	public String getString(String key) {
		return getString(new MAMLString(key));
	}
	
	public double getNumber(MAMLValue key) {
		var output = ((MAMLNumber) get(key));
		
		return output.getValue();
	}
	
	public double getNumber(String key) {
		return getNumber(new MAMLString(key));
	}
	
	public boolean getBoolean(MAMLValue key) {
		var output = ((MAMLBoolean) get(key));
		
		return output.getValue();
	}
	
	public boolean getBoolean(String key) {
		return getBoolean(new MAMLString(key));
	}
	
	public Map<MAMLValue, MAMLValue> getValue() {
		return table;
	}

	@Override
	public String asString() {
		return asString(1, new ArrayList<MAMLValue>(), true);
	}

	public static MAMLTable parseString(String stringToParse) {
		
		// Remove single-line comments
		stringToParse = stringToParse.replaceAll("([" + MAMLTable.commentStart + "][" + MAMLTable.commentStart + "].*)", "");
		
		// Remove line breaks
		stringToParse = stringToParse.replaceAll("\r?\n|\r", MAMLTable.delimStr);
		
		// Remove multi-line commands
		stringToParse = stringToParse.replaceAll("[" + MAMLTable.commentStart + "][" + MAMLTable.commentMiddle + "](.*?)[" + MAMLTable.commentMiddle + "][" + MAMLTable.commentStart + "]", "");
		
		// Remove double occurances of ; 
		stringToParse = stringToParse.replaceAll(MAMLTable.delimStr + "+", MAMLTable.delimStr);
		
		// Remove spaces/tabs
		stringToParse = stringToParse.replaceAll(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)", "");
		stringToParse = stringToParse.replaceAll("\t(?=([^\"]*\"[^\"]*\")*[^\"]*$)", "");
		
		MAMLTable.log("Parsing Table:" + stringToParse);
		
		MAMLTable table = new MAMLTable();
		
		StringBuffer buffer = new StringBuffer(stringToParse);
		
		// Remove opening brackets
		buffer.delete(0, 1);
		
		// Add Delim to end
		buffer.append(MAMLTable.delim);

		
		// Continuously iterate over the values in the table
		readLoop(table, buffer);
		
		// Resolve pointers
		resolvePointers(table, table);
		
		return table;
	}
	
	private static void resolvePointers(MAMLTable table, MAMLTable master) {
		table.getValue().forEach((key, value) -> {
			if (value instanceof MAMLTable)
				resolvePointers((MAMLTable) value, master);
			else if (value instanceof MAMLPointer) {
				MAMLValue newValue = ((MAMLPointer) value).resolve(master);
				MAMLTable.log("Resolving Pointer: Value: " + value.asString() + " resolved Value: " + newValue.asString());
				table.set(key, newValue);
			}
		});
	}
	
	private static void readLoop(MAMLTable table, StringBuffer buffer) {
		while(true) {
			
			// Check for delim
			while (!buffer.toString().equals("") && buffer.charAt(0) == MAMLTable.delim) {
				buffer.delete(0, 1);
			}
			
			// Check for table end
			if (buffer.toString().equals("") || buffer.charAt(0) == MAMLTable.tableClose) {
				MAMLTable.log("<- TABLE END ->");
				return;
			}
			
			MAMLTable.log("Reading new Value:");
			// get Key/Value
			MAMLValue key = nextValue(buffer);
			
			String nextChar = String.valueOf(buffer.charAt(0));
			
			MAMLTable.log("DEBUG: " + nextChar);
			
			switch(nextChar) {
				case MAMLTable.setValueStr:
					MAMLTable.log("Key detected, reading it's corresponding value...");
					// Another value detected
					buffer.delete(0, 1);
					MAMLValue value = nextValue(buffer);
					
					// Set Key -> Value pair
					table.set(key, value);
					break;
				case MAMLTable.tableOpenStr:
					MAMLTable.log("Key -> Table detected, generating the key's corresponding table...");
					// Table detected
					MAMLValue tableValue = nextValue(buffer);
					table.set(key, tableValue);
					break;
				case MAMLTable.delimStr:
					// No other value detected
					MAMLTable.log("Value detected, generating it's corresponding key...");
					int i = 0;
					while (table.exists(new MAMLNumber(i))) {
						i++;
					}
					table.set(new MAMLNumber(i), key);
					break;
			}
		}
	}
	
	private static MAMLValue nextValue(StringBuffer buffer) {
		char nextChar = buffer.charAt(0);
		
		String stringValue;
		
		int indent;
		
		switch (String.valueOf(nextChar)) {
			case MAMLTable.tableOpenStr:
				
				// Table detected, get full table string.
				stringValue = "";
				
				indent = 0;
				
				while (true) {
					if (buffer.charAt(0) == MAMLTable.tableOpen)
						indent++;
					if (buffer.charAt(0) == MAMLTable.tableClose)
						indent--;
					stringValue += buffer.charAt(0);
					buffer.delete(0, 1);
					
					// Return a new table parsed from the string.
					if (indent == 0)
						return MAMLTable.parseString(stringValue);
				}
			case MAMLTable.dynamicStringOpenStr:
				// Pointer detected, get full pointer string.
				stringValue = "";
				
				indent = 0;
				
				while (true) {
					if (buffer.charAt(0) == MAMLTable.dynamicStringOpen)
						indent++;
					if (buffer.charAt(0) == MAMLTable.dynamicStringClose)
						indent--;
					stringValue += buffer.charAt(0);
					buffer.delete(0, 1);
					
					// Return a new pointer parsed from the string.
					if (indent == 0)
						return MAMLPointer.parseString(stringValue);
				}
			case "'":
			case "\"":
				// Read string
				stringValue = "";
				
				while(true) {
					
					stringValue += buffer.charAt(0);
					buffer.delete(0, 1);
					
					if (buffer.charAt(0) == nextChar) {
						stringValue += buffer.charAt(0);
						buffer.delete(0, 1);
						return MAMLString.parseString(stringValue);
					}
				}
			case MAMLTable.delimStr:
				new Throwable("Invalid delim").printStackTrace();
				return null;
			default:
				
				// String or boolean detected.
				String name = "";
				while (
					buffer.charAt(0) != MAMLTable.delim
					&& buffer.charAt(0) != MAMLTable.setValue
					&& buffer.charAt(0) != MAMLTable.tableOpen
					&& buffer.charAt(0) != MAMLTable.tableClose
				) {
					name += buffer.charAt(0);
					buffer = buffer.delete(0, 1);
				}
				
				// Check if name is nil
				if (name.length() == 0)
					new Throwable("Name not correctly found: " + buffer.charAt(0)).printStackTrace();
				
				// Check for number
				if (name.replaceAll("[0-9]+([.]+[0-9]+)?", "").equals(""))
					return MAMLNumber.parseString(name);
				
				if (name.startsWith("0x") || name.startsWith("0b"))
					return MAMLNumber.parseString(name);
				
				// Check for boolean
				if (name.equals("true") || name.equals("false"))
					return MAMLBoolean.parseString(name);
				
				return MAMLString.parseString('"' + name + '"');
		}
	}
	
	public boolean equals(MAMLTable table) {
		return table.asString().equals(this.asString());
	}

	/**
	 * A small logging function for debugging
	 * @param str
	 */
	public static void log(Object str) {
		if (debug)
			System.out.println(str);
	}
}