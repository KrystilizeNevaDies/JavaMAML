package maml.values;

import java.util.Iterator;
import java.util.List;

import maml.MAMLFile;

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
		while (buffer.contains(MAMLFile.dynamicStringOpenStr) && buffer.contains(MAMLFile.dynamicStringCloseStr)) {
			end = 0;
			start = buffer.length() - 1;
			
			
			
			// Find other pointers ending symbol
			while(buffer.charAt(end) != MAMLFile.dynamicStringClose) {
				end++;
			}
			
			// Find other pointers starting symbol
			start = end;
			while(buffer.charAt(start) != MAMLFile.dynamicStringOpen) {
				start--;
			}
			
			// Create new pointer
			String pointer = buffer.substring(start + 1, end);
			
			// Resolve pointer and change buffer
			buffer =
				buffer.substring(0, start) + 
				new MAMLPointer(pointer).resolve(table).asKey() +
				buffer.substring(end, buffer.length() - 1);
		}
		
		
		// Pointer should now look like SomeKey.AnotherKey
		
		// Split pointer by periods
		String[] split = buffer.split("[.]");
		
		// Create iterator
		List<String> list = List.of(split);
		Iterator<String> itr = list.iterator();
		
		// Create buffer value to be returned
		MAMLValue valueBuffer = table;
		
		// Iterate through string splits
		while (itr.hasNext()) {
			// Get the next key
			String key = itr.next();
			
			// If value found with key, set valueBuffer to that value
			if (((MAMLTable) valueBuffer).exists(key))
				valueBuffer = ((MAMLTable) valueBuffer).getMAMLValue(key);
			else
				// Else return key as value
				return new MAMLString(key);
		}
		
		// Finally, return valueBuffer
		return valueBuffer;
	}

	@Override
	public String asKey() {
		return "";
	}
}
