package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IJSONHandlerImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.IJSONHandler;

public class IJSONHandlerImplTest {
	static File testdatei;
	static File erwarteteErgebnisse;
	static File testMap1;
	static File testMap2;
	static File testMap3;
	static File testMap4;
	
	static IJSONHandler iJSON;
	static IConfig config;
	static IFileImporter fileImporter;
	
	static Map<File, File> tempFiles;
	
	
	@BeforeClass
	public static void beforeAllTests() {
		IFileImporter fileImporter = new IFileImporterImpl();

		iJSON = new IJSONHandlerImpl(fileImporter);
		
		
	}
	
	@Test
	public void test_deleteValues() {
		iJSON.clearErrorList();
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/JSONTestFiles/deleteValues.json");		
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		try{
			erwarteteErgebnisse = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/JSONTestFiles/ExpectedOutcome/deleteValues.json");			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		
		String ist = iJSON.deleteValues(readFileToString(testdatei));
		String soll = readFileToString(erwarteteErgebnisse);
		
		assertEquals(soll, ist);
	
	}
	
	
	
	@Test
	public void test_sortKeysAlphabetical() {
		iJSON.clearErrorList();
		try{
			testdatei = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/JSONTestFiles/unsortedJSON.json");
			
			erwarteteErgebnisse = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/JSONTestFiles/ExpectedOutcome/sortedJSON.json");
			
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		

		String ist = iJSON.sortKeysAlphabetical(readFileToString(testdatei));
		String soll = readFileToString(erwarteteErgebnisse);
		
		assertEquals(soll, ist);
	
	}
	
	@Test
	public void test_jsonPrepare_Correct() throws IOException {
		iJSON.clearErrorList();
		
		FileInputStream instream = null;
		FileOutputStream outstream = null;
		
		try{
			testMap1 = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/JSONTestFiles/testFileMap1.json");
			
			testMap2 = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/JSONTestFiles/testFileMap2.json");
						
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		Map<File,File> testMap = new HashMap<File, File>();
		
		testMap.put(testMap1, testMap2);//correct
		
		Map<File,File> ergebnisMap;
		
		ergebnisMap = iJSON.jsonPrepare(testMap);
		
		assertTrue(iJSON.getErrorList().size() == 0);
		
		
	}
	
	
	@Test
	public void test_jsonPrepare_Incorrect() throws IOException {
		iJSON.clearErrorList();
		
		try{
			testMap3 = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/JSONTestFiles/testFileMap3.json");
			
			testMap4 = new File(System.getProperty("user.dir")
					+ File.separator + "/src/test/testFiles/JSONTestFiles/testFileMap4.json");
						
		}catch(Exception e){
			System.out.println("Fehler in beforeAllTests: Testdateien beschaedigt!");
			e.printStackTrace();
		}
		
		Map<File,File> testMap = new HashMap<File, File>();
		
		testMap.put(testMap3, testMap4);//faulty
		
		Map<File,File> ergebnisMap;
		
		ergebnisMap = iJSON.jsonPrepare(testMap);
		
		assertTrue(iJSON.getErrorList().size() > 0);
		
	}
	
	public static String readFileToString(File file) {
		
	      FileInputStream fileInputStream = null;
	      
	      byte[] fileInBytes = new byte[(int) file.length()];
	      
	      try {
	         fileInputStream = new FileInputStream(file);
	         fileInputStream.read(fileInBytes);
	         fileInputStream.close();
	      }
	      catch (Exception e) {
	         e.printStackTrace();
	      }
	      return new String(fileInBytes);
	}
}
