package maml;

import java.io.File;
import java.io.IOException;

import maml.values.MAMLTable;

public class MAMLTest {
	public static void main(String[] arguments) {
		File javaFile = new File("test.maml");
		// Parse file
		@SuppressWarnings("unused")
		MAMLTable table = null;
		
		try {
			table = MAMLFile.parse(javaFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// DO TESTS HERE:
		
		// table.whateveryouwanttotestthereareinfinitepossibilities
	}
}
