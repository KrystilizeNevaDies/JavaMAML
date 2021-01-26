package maml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import maml.values.MAMLBoolean;
import maml.values.MAMLNumber;
import maml.values.MAMLPointer;
import maml.values.MAMLString;
import maml.values.MAMLTable;
import maml.values.MAMLValue;

public class MAMLFile {
	public static final String commentStartStr = "/";
	public static final String commentMiddleStr = "*";
	public static final String setValueStr = "=";
	public static final String delimStr = ";";
	public static final String tableOpenStr = "{";
	public static final String tableCloseStr = "}";
	public static final String dynamicStringOpenStr = "<";
	public static final String dynamicStringCloseStr = ">";
	public static final char commentStart = commentStartStr.charAt(0);
	public static final char commentMiddle = commentMiddleStr.charAt(0);
	public static final char setValue = setValueStr.charAt(0);
	public static final char delim = delimStr.charAt(0);
	public static final char tableOpen = tableOpenStr.charAt(0);
	public static final char tableClose = tableCloseStr.charAt(0);
	public static final char dynamicStringOpen = dynamicStringOpenStr.charAt(0);
	public static final char dynamicStringClose = dynamicStringCloseStr.charAt(0);
	
	String file;
	StringBuffer buffer;
	
	boolean debug = false;
	
	/**
	 * A small logging function for debugging
	 * @param str
	 */
	private void log(String str) {
		if (debug)
			System.out.println(str);
	}
	
	public MAMLFile(File file) throws IOException {
		this.file = Files.readString(file.toPath(), StandardCharsets.US_ASCII);
	}
	
	public MAMLFile(String contents) {
		this.file = contents;
	}
	
	public MAMLTable parse() {
		// Create new table
		MAMLTable table = new MAMLTable();
		
		String strBuffer = file;
		
		// Remove comments
		strBuffer = strBuffer.replaceAll("([" + commentStart + "][" + commentStart + "].*)", "");
		
		// Compile into single line 
		strBuffer = strBuffer.replaceAll("\r?\n|\r", delim + "");
		
		// Remove multi-line strings
		strBuffer = strBuffer.replaceAll("[" + commentStart + "][" + commentMiddle + "](.*?)[" + commentMiddle + "][" + commentStart + "]", "");
		
		// Remove double occurances of ; 
		strBuffer = strBuffer.replaceAll(delim + "+", delim + "");
		
		// Remove spaces/tabs
		strBuffer = strBuffer.replaceAll(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)", "");
		strBuffer = strBuffer.replaceAll("\t(?=([^\"]*\"[^\"]*\")*[^\"]*$)", "");
		
		// Convert buffer
		this.buffer = new StringBuffer(strBuffer);
		
		// Check if buffer starts with break
		if (this.buffer.charAt(0) == delim)
			this.buffer = buffer.delete(0, 1);
		
		// Read the table
		readTable(table);
		
		// Resolve pointers
		resolvePointers(table, table);
		
		// Print in debug
		log(table.toString());
		
		return table;
	}
	
	/**
	 * Writes a string as maml values into provided table
	 * 
	 * @param i current position in string
	 * @param buffer string as char array
	 * @param table table to write to
	 */
	private void readTable(MAMLTable table) {
		log("readTable");
		if (buffer.length() == 0) {
			return;
		}
		
		try {
			while (buffer.length() > 0 && buffer.charAt(0) != tableClose)
				readKeyAndValue(table);
		} catch (Throwable e) {}
		if (buffer.length() > 0 && buffer.charAt(0) == tableClose)
			buffer = buffer.delete(0, 1);
	}
	
	private void readKeyAndValue(MAMLTable table) {
		// new Throwable().printStackTrace();
		log("readKeyAndValue");
		
		
		Character first = buffer.charAt(0);
		
		if (Character.isLetterOrDigit(first)) {
			log("1");
			// Key
			MAMLValue key = readValue();
			
			// Check if Value not specified, if so, add key as value
			if (buffer.charAt(0) == delim) {
				table.add(key);
				log("2");
				return;
			}
			
			// Value
			MAMLValue value = readValue();
			
			if (key == null)
				System.out.println(buffer.charAt(0) + " key is null");
			
			// Set pair in table
			table.set(key.asKey(), value);
			log("4");
		} else if (first == delim) {
			buffer = buffer.delete(0, 1);
		} else {
			// Value
			MAMLValue value = readValue();
			
			// Set pair in table
			table.add(value);
			log("5");
		}
	}
	
	private MAMLValue readValue() {
		log("readValue");
		char nextChar = buffer.charAt(0);
		switch (String.valueOf(nextChar)) {
			case tableOpenStr:
				MAMLTable newTable = new MAMLTable();
				buffer = buffer.delete(0, 1);
				readTable(newTable);
				return newTable;
			case setValueStr:
				buffer = buffer.delete(0, 1);
				return readValue();
			case dynamicStringOpenStr:
				return new MAMLPointer(readPointer());
			case "'":
			case "\"":
				return new MAMLString(readString()); 
			case "0":
				if (buffer.charAt(1) == "x".charAt(0)) {
					double hex = readHex();
					return new MAMLNumber(hex);
				} else if (buffer.charAt(1) == "b".charAt(0)) {
					double bin = readBin();
					return new MAMLNumber(bin);
				}
			default:
				// Pointer or boolean detected.
				String name = "";
				while (
					buffer.charAt(0) != delim
					&& buffer.charAt(0) != setValue
					&& buffer.charAt(0) != tableOpen
				) {
					name += buffer.charAt(0);
					buffer = buffer.delete(0, 1);
				}
				
				// Check if name is nil
				if (name.length() == 0)
					new Throwable("Name not correctly found: " + buffer.charAt(0)).printStackTrace();
				
				// Check for number
				if (name.replaceAll("[0-9]+([.]+[0-9]+)?", "").equals(""))
					return new MAMLNumber(Double.valueOf(name));
				
				// Check for boolean
				if (name.equals("true"))
					return new MAMLBoolean(true);
				if (name.equals("false"))
					return new MAMLBoolean(false);
				
				return new MAMLString(name);
		}
	}
	
	private double readBin() {
		log("readBin");
		// Remove prefix
		buffer = buffer.delete(0, 2);
		
		// parse number
		String num = "";
		while (Character.isDigit(buffer.charAt(0))) {
			num += buffer.charAt(0);
			buffer = buffer.delete(0, 1);
		}
		
		// Return base 10 number
		return Integer.parseInt(num, 2);
	}

	private double readHex() {
		log("readHex");
		// Remove prefix
		buffer = buffer.delete(0, 2);
		
		// parse number
		String num = "";
		while (Character.isLetterOrDigit(buffer.charAt(0))) {
			num += buffer.charAt(0);
			buffer.delete(0, 1);
		}
		
		// Return base 10 number
		return Long.parseLong(num, 16);
	}

	private String readString() {
		log("readString");
		Character prefix = buffer.charAt(0);
		return readString(prefix);
	}
	
	private String readString(char suffix) {
		log("readString");
		String returnString = "";
		buffer = buffer.delete(0, 1);
		
		while (buffer.charAt(0) != suffix) {
			returnString += buffer.charAt(0);
			buffer = buffer.delete(0, 1);
		}
		
		buffer = buffer.delete(0, 1);
		
		return returnString;
	}
	
	private String readPointer() {
		log("readPointer");
		int d = 0;
		String returnString = "";
		
		while (true) {
			// Depth
			if (buffer.charAt(0) == dynamicStringOpen)
				d++;
			
			if (buffer.charAt(0) == dynamicStringClose)
				d--;
			
			// Iterate through chars
			returnString += buffer.charAt(0);
			buffer = buffer.delete(0, 1);
			
			// Check for depth of 0
			if (d == 0) {
				log("NEW POINTER -> " + returnString);
				return returnString;
			}
		}
	}
	
	private void resolvePointers(MAMLTable table, MAMLTable master) {
		table.getJavaTable().forEach((key, value) -> {
			if (value instanceof MAMLTable)
				resolvePointers((MAMLTable) value, master);
			
			if (value instanceof MAMLPointer) {
				MAMLValue newValue = ((MAMLPointer) value).resolve(master);
				table.set(key, newValue);
			}
		});
	}
}