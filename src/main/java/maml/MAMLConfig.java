package maml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import maml.values.MAMLTable;
import maml.values.MAMLValue;
/**
 * Class used for convienient methods to add configuration to your server
 * @author Krystilize
 *
 */
public class MAMLConfig {
	private ArrayList<Entry<String, MAMLValue>> table;

	public MAMLConfig(MAMLTable table) {
		// Convert all previous values to MAMLConfig storage
		this.table = new ArrayList<Entry<String, MAMLValue>>();
		table.getJavaTable().forEach((key, value) -> {
			SimpleEntry<String, MAMLValue> newEntry = new AbstractMap.SimpleEntry<String, MAMLValue>(key, value);
			this.table.add(newEntry);
		});
	}
	
	public MAMLConfig() {
		this.table = new ArrayList<Entry<String, MAMLValue>>();
	}
	
	/**
	 * Write configuration to specified file
	 * @param file
	 * @throws IOException
	 */
	public void flush(File file) throws IOException {
		assert(file.canWrite());
		FileWriter writer = new FileWriter(file);
		
		writer.write(configToTable().toString());
		
		writer.close();
	}
	
	/**
	 * Creates a maml representation of the config
	 * @return
	 */
	public String configToFileString() {
		var s = new ArrayList<String>();
		
		table.forEach((entry) -> {
			s.add(entry.getKey() + " = " + entry.getValue());
		});
		
		String[] split = s.toArray(new String[s.size()]);
		
		String returnStr = "";
		
		for (String str : split) {
			returnStr += str + "\n";
		}
		
		return returnStr;
	}
	
	/**
	 * Sets a value mapped to a key
	 */
	public void setValue(String key, MAMLValue value) {
		
		// Check if value exists
		if (this.containsKey(key)) {
			replaceValue(key, value);
		}
		
		// Else add value
		var entry = new AbstractMap.SimpleEntry<String, MAMLValue>(key, value);
		this.table.add(entry);
	}

	private void replaceValue(String key, MAMLValue value) {
		for (int i = 0; i < table.size(); i++) {
			var entry = table.get(i);
			
			var newEntry = new AbstractMap.SimpleEntry<String, MAMLValue>(key, value);
			
			if (entry.getKey().equals(key))
				table.set(i, newEntry);
		}
	}
	
	public boolean containsKey(String key) {
		ArrayList<String> keys = new ArrayList<String>();
		this.table.forEach((entry) -> {
			if (entry.getKey().equals(key))
				keys.add(key);
		});
		return (keys.size() > 0);
	}
	
	/**
	 * Converts this configuration file into a MAMLTable
	 * @return
	 */
	public MAMLTable configToTable() {
		MAMLTable newTable = new MAMLTable();
		
		table.forEach((entry) -> {
			newTable.set(entry.getKey(), entry.getValue());
		});
		
		return newTable;
	}
	
	/**
	 * Converts a MAMLTable into a MAMLConfig
	 * @return
	 */
	public static MAMLConfig tableToConfig(MAMLTable table) {
		MAMLConfig config = new MAMLConfig();
		
		table.getJavaTable().forEach((key, value) -> {
			config.setValue(key, value);
		});
		
		return config;
	}
}