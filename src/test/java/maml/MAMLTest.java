package maml;

import java.io.File;
import java.io.IOException;

import maml.values.MAMLTable;

public class MAMLTest {
	public static void main(String[] arguments) {
		File javaFile = new File("test.maml");
		MAMLFile file = null;
		try {
			file = new MAMLFile(javaFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Parse file
		MAMLTable table = file.parse();
		
		// Get Config
		MAMLConfig config = MAMLConfig.tableToConfig(table);
		
		// Read from table
		System.out.println(config.configToFileString());
	}
}
