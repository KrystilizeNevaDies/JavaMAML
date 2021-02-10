package maml.values;

public class MAMLPointer implements MAMLValue {
	String name;
	MAMLTable table;
	
	public MAMLPointer(String name) {
		this.name = name;
	}
	
	public MAMLValue resolve(MAMLTable table) {
		String buffer = name;
		
		// recursively resolve other pointers
		int end;
		int start;
		// While other pointers exist within this one
		while (buffer.contains(MAMLTable.dynamicStringOpenStr) && buffer.contains(MAMLTable.dynamicStringCloseStr)) {
			end = 0;
			start = buffer.length() - 1;
			
			
			
			// Find other pointers ending symbol
			while(buffer.charAt(end) != MAMLTable.dynamicStringClose) {
				end++;
			}
			
			// Find other pointers starting symbol
			start = end;
			while(buffer.charAt(start) != MAMLTable.dynamicStringOpen) {
				start--;
			}
			
			// Create new pointer
			String pointer = buffer.substring(start + 1, end);
			
			// Resolve new pointer
			MAMLValue resolve = new MAMLPointer(pointer).resolve(table);
			
			String resolveString;
			
			// Check for pointer, if found, resolve
			while (resolve instanceof MAMLPointer)
				resolve = ((MAMLPointer) resolve).resolve(table);
			
			// Remove quotation marks if applicable
			resolveString = MAMLString.parseString(resolve.asString()).getValue();
			
			// construct new pointer
			String newPointer = buffer.substring(0, start) + resolveString + buffer.substring(end + 1, buffer.length());
			
			// resolve new pointer + return value
			return new MAMLPointer(newPointer).resolve(table);
		}
		
		// Pointer should now look like SomeKey.AnotherKey
		
		// Split pointer by periods
		String[] split = buffer.split("[.]");
		
		// Create buffer value to be returned
		MAMLValue valueBuffer = table;
		
		// Iterate through string splits
		for (String key : split) {
			
			MAMLString mamlKey = new MAMLString(key);
			
			// If value found with key, set valueBuffer to that value
			if (((MAMLTable) valueBuffer).exists(mamlKey)) {
				valueBuffer = ((MAMLTable) valueBuffer).get(mamlKey);
			} else {
				// Else return key as value
				return mamlKey;
			}
		}
		
		// Finally, return valueBuffer
		return valueBuffer;
	}
	
	public static MAMLPointer parseString(String stringToParse) {
		MAMLTable.log("Parsing Pointer:" + stringToParse);
		int d = 0;
		String returnString = "";
		
		while (true) {
			// Depth
			if (stringToParse.charAt(0) == MAMLTable.dynamicStringOpen)
				d++;
			
			if (stringToParse.charAt(0) == MAMLTable.dynamicStringClose)
				d--;
			
			// Iterate through chars
			returnString += stringToParse.charAt(0);
			stringToParse = stringToParse.substring(1);
			
			// Check for depth of 0
			if (d == 0) {
				return new MAMLPointer(returnString);
			}
		}
	}

	@Override
	public String asString() {
		return this.name;
	}
	
	@Override
	public String toString() {
		return "MAMLPointer(" + name + ")";
	}
}
