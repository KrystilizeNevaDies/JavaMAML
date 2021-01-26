package maml.values;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import maml.MAMLFile;

public class MAMLTable implements MAMLValue {
	protected Map<String, MAMLValue> table;
	
	public MAMLTable() {
		table = new HashMap<String, MAMLValue>();
	}
	
	@Override
	public String toString() {
		return toString(1, new ArrayList<MAMLValue>(), true);
	}
	
	public String toFileString() {
		return toString(0, new ArrayList<MAMLValue>(), false);
	}
	
	public String toString(int depth, ArrayList<MAMLValue> arrayList, boolean addBrackets) {
		String s = "";
		
		// Add start bracket if applicable
		if (addBrackets)
			s += MAMLFile.tableOpen + "\n";
		else
			s += "\n";
		
		String tab = "    ";
		for (Entry<String, MAMLValue> entry : table.entrySet()) {
			if (!arrayList.contains(entry.getValue()))
			if (entry.getValue() instanceof MAMLTable) {
				arrayList.add(entry.getValue());
				s += tab.repeat(depth) + entry.getKey() + " = " + ((MAMLTable) entry.getValue()).toString(depth + 1, arrayList, true) + "\n";
			} else {
				s += tab.repeat(depth) + entry.getKey() + " = " + entry.getValue().toString() + "\n";
			}
			
		}
		if (addBrackets)
			s += tab.repeat(depth - 1) + MAMLFile.tableClose;
		return s;
	}
	
	public void set(String key, MAMLValue value) {
		this.table.put(key, value);
	}
	
	public void add(MAMLValue value) {
		Integer i = 0;
		
		while (table.get(i.toString()) != null)
			i++;
		
		this.table.put(i.toString(), value);
	}
	
	public MAMLTable get(String key) {
		return (MAMLTable) table.get(key);
	}
	
	public MAMLValue getMAMLValue(String key) {
		return table.get(key);
	}
	
	public boolean exists(String key) {
		return table.containsKey(key);
	}
	
	public Map<String, MAMLValue> getTable(String key) {
		return ((MAMLTable) table.get(key)).getJavaTable();
	}
	
	public String getString(String key) {
		return ((MAMLString) table.get(key)).getValue();
	}
	
	public double getNumber(String key) {
		return ((MAMLNumber) table.get(key)).getValue();
	}
	
	public boolean getBoolean(String key) {
		return ((MAMLBoolean) table.get(key)).getValue();
	}
	
	public Map<String, MAMLValue> getJavaTable() {
		return table;
	}

	@Override
	public String asKey() {
		new Throwable("Tables cannot be used as keys.").printStackTrace();
		return null;
	}
}