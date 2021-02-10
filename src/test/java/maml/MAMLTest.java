package maml;

import java.io.File;
import java.io.IOException;

import maml.values.MAMLTable;

public class MAMLTest {
	public static void main(String[] arguments) {
		File javaFile = new File("test.maml");
		// Parse file
		MAMLTable table = null;
		
		try {
			table = MAMLFile.parse(javaFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// DO TESTS HERE:
		
		System.out.println(table.toFileString());
	}
}
