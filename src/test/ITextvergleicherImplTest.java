package test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.thkoeln.syp.mtc.datenhaltung.impl.IComparisonImpl;
import de.thkoeln.syp.mtc.steuerung.impl.IFileImporterImpl;
import de.thkoeln.syp.mtc.steuerung.impl.ITextvergleicherImpl;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;

public class ITextvergleicherImplTest {

	static List<File> textdateien;
	static ITextvergleicher iText;
	List<IComparisonImpl> paarungen;

	@BeforeClass
	public static void beforeAllTests() {
		textdateien = new ArrayList<File>();
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileA.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileB.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileC.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileD.txt"));
		textdateien.add(new File(System.getProperty("user.dir")
				+ File.separator + "/src/test/testFiles/FileE.txt"));
		
		IFileImporter fileImporter = new IFileImporterImpl();
		fileImporter.getConfig().setBestMatch(false);
		fileImporter.getConfig().setMatchAt(0.6 );
		fileImporter.getConfig().setMatchingLookahead(0);

		iText = new ITextvergleicherImpl();
		iText.setFileImporter(fileImporter);
	}

	@Test
	public void test_getVergleichspaarungen() {
		paarungen = iText.getVergleiche(textdateien);
		assertEquals(10, paarungen.size());
	}

	@Test
	public void test_vergleichWortFuerWort() {
		paarungen = iText.getVergleiche(textdateien);
		double[] aehnlichkeit = new double[paarungen.size()];
		iText.vergleicheZeilenweise(paarungen);

		for (int i = 0; i < paarungen.size(); i++) {
			aehnlichkeit[i] = paarungen.get(i).getValue();
		}
		assertEquals(0.5, aehnlichkeit[4], 0.0000001);
	}

	@Test
	public void test_vergleichZeichenmenge() {
		paarungen = iText.getVergleiche(textdateien);
		double[] aehnlichkeit = new double[paarungen.size()];
		iText.vergleicheUeberGanzesDokument(paarungen);
		for (int i = 0; i < paarungen.size(); i++) {
			aehnlichkeit[i] = paarungen.get(i).getValue();
		}
		assertEquals(0.518, aehnlichkeit[4], 0.001);
	}

}
