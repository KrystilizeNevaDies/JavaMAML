package maml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import maml.values.MAMLTable;

public class MAMLFile {
	/**
	 * Parses this File into a MAMLTable 
	 * 
	 * @return table the parsed table
	 * @throws IOException 
	 */
	public static MAMLTable parse(File file) throws IOException {
		// Read file
		String fileString = Files.readString(file.toPath(), StandardCharsets.US_ASCII);
		
		// Make file into string representation of table
		fileString = MAMLTable.tableOpen + fileString + MAMLTable.tableClose;
		
		// Parse as MAMLTable
		return MAMLTable.parseString(fileString);
	}
	

	/**
	 * Parses this String object into a MAMLTable 
	 * 
	 * @return table the parsed table
	 * @throws IOException 
	 */
	public static MAMLTable parse(String mamlTable) throws IOException {
		// Read file
		String fileString = mamlTable;
		
		// Make file into string representation of table
		fileString = MAMLTable.tableOpen + fileString + MAMLTable.tableClose;
		
		// Parse as MAMLTable
		return MAMLTable.parseString(fileString);
	}
}