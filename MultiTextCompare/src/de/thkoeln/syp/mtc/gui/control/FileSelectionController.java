package de.thkoeln.syp.mtc.gui.control;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.api.IXMLParseError;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;
import de.thkoeln.syp.mtc.gui.view.FileSelectionView;
import de.thkoeln.syp.mtc.gui.view.FileView;
import de.thkoeln.syp.mtc.gui.view.PopupView;
import de.thkoeln.syp.mtc.steuerung.services.IFileImporter;
import de.thkoeln.syp.mtc.steuerung.services.ITextvergleicher;
import de.thkoeln.syp.mtc.steuerung.services.IXMLvergleicher;

public class FileSelectionController extends JFrame {
	private Management management;
	private JPanel panel;
	private FileDialog fd;
	private JFileChooser fc;
	private File[] selection;
	private List<File> selectionList;
	private List<File> lastComparisonFiles;
	private IMatrix matrix;
	private IFileImporter fileImporter;
	private ITextvergleicher textvergleicher;
	private IXMLvergleicher xmlvergleicher;
	private int mode;

	public FileSelectionController(FileSelectionView fileSelectionView) {
		// Management Variablen
		management = Management.getInstance();
		management.setFileSelectionController(this);
		fileImporter = management.getFileImporter();
		textvergleicher = management.getTextVergleicher();
		xmlvergleicher = management.getXmlvergleicher();

		// Panel & neue Matrix fuer den naechsten Vergleich
		panel = new JPanel();
		matrix = new IMatrixImpl();
		lastComparisonFiles = new ArrayList<File>();

		// Implementation der Button Methoden
		fileSelectionView.addSetRootListener(new SetRootListener());
		fileSelectionView.addSearchListener(new SearchListener());
		fileSelectionView.addAddFilesListener(new AddFilesListener());
		fileSelectionView.addDeleteListener(new DeleteListener());
		fileSelectionView.addResetListener(new ResetListener());
		fileSelectionView.addCompareListener(new CompareListener());
		fileSelectionView.addFileViewListener(new FileViewListener());

		// Wurzelverzeichnis anzeigen
		fileSelectionView.getLblRootPath().setText(
				fileImporter.getConfig().getRootDir());
	}

	class SetRootListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			fc = new JFileChooser();
			Action details = fc.getActionMap().get("viewTypeDetails");
			details.actionPerformed(null);
			fc.setCurrentDirectory(new File(fileImporter.getConfig()
					.getRootDir()));
			fc.setDialogTitle("moin");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.showOpenDialog(management.getFileSelectionView());
			fileImporter.setRootDir(fc.getSelectedFile());
			management.updateWurzelpfad();
			management.getFileSelectionView().pack();
		}
	}

	class SearchListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			List<File> reference = new ArrayList<File>(
					fileImporter.getTextdateien());

			// Importiert & startet Suche ueber Wurzelverzeichnis
			fileImporter.importTextRoot(management.getFileSelectionView()
					.getTextFieldFileName().getText()
					+ getFileExt());
			fileImporter.getRootImporter().start();
			try {
				fileImporter.getRootImporter().join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Gibt einen Hinweis aus, falls keine neuen Dateien gefunden wurden
			if (fileImporter.getTextdateien().equals(reference))
				new PopupView("Hinweis",
						"Bei dieser Suche wurden keine weiteren Dateien gefunden");

			// Aktualisiert Anzeige
			setRdbtn(fileImporter.getTextdateien().isEmpty());
			updateListFilePath();

			fileImporter.getConfig().setDateiname(
					management.getFileSelectionView().getTextFieldFileName()
							.getText());
			fileImporter.getConfig().setDateityp(getFileExt());
			fileImporter.exportConfigdatei();
			mode = management.getFileSelectionView().getRadioButton();
		}
	}

	class AddFilesListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			// Windows Dateiauswahl
			fd = new FileDialog(management.getFileSelectionView(),
					"File selection", FileDialog.LOAD);
			fd.setLocationRelativeTo(null);
			fd.setMultipleMode(true);
			fd.setDirectory(fileImporter.getConfig().getRootDir());
			fd.setFile("*" + getFileExt());
			fd.setVisible(true);

			// Die Dateien dem FileImporter uebergeben
			selection = fd.getFiles();
			selectionList = new ArrayList<File>();
			for (File f : selection) {
				selectionList.add(f);
			}
			fileImporter.importTextdateien(selectionList);

			// Fenster zentrieren
			management.getFileSelectionView().setLocationRelativeTo(null);

			// Ggf. Radio Buttons ausgrauen und gewaehlten Dateityp speichern
			setRdbtn(fileImporter.getTextdateien().isEmpty());
			fileImporter.getConfig().setDateityp(getFileExt());
			mode = management.getFileSelectionView().getRadioButton();

			// JList Anzeige aktualisieren
			updateListFilePath();
		}
	}

	class DeleteListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Datei(en) aus dem FileImporter loeschen
			for (File f : getListSelection()) {
				// System.out.println(f.getAbsolutePath());
				fileImporter.deleteImport(f);
			}

			// Anzeige aktualsieren
			updateListFilePath();
			if (management.getFileSelectionView().getModel().isEmpty())
				management.getFileSelectionView().getLblFileCount()
						.setText("0");
		}
	}

	class ResetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// Zueruecksetzen der Auswahl im FileImporter & der Anzeige
			fileImporter.deleteImports();
			fileImporter.deleteTempFiles();
			management.getFileSelectionView().getModel().clear();
			management.getFileSelectionView().getLblFileCount().setText("0");
			setRdbtn(true);
		}
	}

	class CompareListener implements ActionListener {
		public void actionPerformed(ActionEvent e){

			management.appendToLog("Start comparing...");
			Thread comparisonThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						long start_time = System.nanoTime();
						int anzDateien = fileImporter.getTextdateien().size();
						if (anzDateien < 2) {
							new PopupView("Error",
									"Please select at least two files for comparison");
							return;
						}

						fileImporter.deleteTempFiles();
						fileImporter.createTempFiles();
						xmlvergleicher.clearErrorList();

						// XML Vergleich
						if (mode == 1) {
							fileImporter.createDiffTempFiles(xmlvergleicher
									.xmlPrepare(fileImporter.getTempFilesMap()));
							for (IXMLParseError error : xmlvergleicher
									.getErrorList())
								management.appendToLog(error.getMessage());
						}
						// Standard Vergleich
						else {
							fileImporter.createDiffTempFiles(fileImporter
									.getTempFilesMap());
						}

						// Vergleich
						fileImporter.normTempFiles();
						textvergleicher.getTempfilesFromHashMap(management
								.getFileImporter().getTempFilesMap());
						textvergleicher.getVergleiche(textvergleicher
								.getTempFiles());

						if (fileImporter.getConfig().getLineMatch() == false) {
							textvergleicher.vergleicheUeberGanzesDokument();
						} else {
							textvergleicher.vergleicheZeilenweise();

						}

						management.getMainView().updateMatrix(
								textvergleicher.getMatrix(), anzDateien,
								getFileNames(anzDateien));

						lastComparisonFiles.clear();
						lastComparisonFiles.addAll(fileImporter
								.getTextdateien());

			
						long end_time = System.nanoTime();
						double time_difference = (end_time - start_time) / 1e6;
						String timeDiffAsString;
						if(time_difference > 1000){
							time_difference *= 1000;
							timeDiffAsString = " (time taken: "
									+ time_difference + " s)";
						}
						else {
							timeDiffAsString = " (time taken: "
									+ time_difference + " ms)";
						}
						if (!xmlvergleicher.getErrorList().isEmpty()) {
							management
									.appendToLog("A matrix with "
											+ anzDateien
											+ " files has been created, but the file selection contained "
											+ xmlvergleicher.getErrorList()
													.size() + " XML errors."
											+ timeDiffAsString);
						}

						else {
							management.appendToLog("A matrix with "
									+ anzDateien
									+ " files has been created successfully!"
									+ timeDiffAsString);
						}
					} catch (final Exception ex) {
						
					}
				}

			});
			
			comparisonThread.start();
			
		}
	}

	class FileViewListener extends MouseAdapter {

		public void mouseClicked(MouseEvent evt) {

			JList list = (JList) evt.getSource();
			if (Management.getInstance().getFileView() == null) {
				management.setFileView(new FileView());
			}
			if (evt.getClickCount() == 2) {

				int index = list.locationToIndex(evt.getPoint());
				String fileName = management.getFileSelectionView().getModel()
						.get(index).split("\\|")[1].trim();
				File selectedFile = new File(fileName);

				try {
					BufferedReader input = new BufferedReader(
							new InputStreamReader(new FileInputStream(
									selectedFile), "UTF-8"));
					management.getFileView().getTextArea().setText(null);
					management.getFileView().getTextArea()
							.read(input, "Reading file...");
					management.getFileView().getTextArea().setCaretPosition(0);

					management.getFileView().getFrame().setTitle(fileName);
					management.getFileView().getFrame().setVisible(true);

				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}
	}

	// Gibt je nach Radiobutton Auswahl die Dateiendung als String zurueck
	private String getFileExt() {
		if (management.getFileSelectionView().getRadioButton() == 0)
			return ".txt";
		if (management.getFileSelectionView().getRadioButton() == 1)
			return ".xml";
		if (management.getFileSelectionView().getRadioButton() == 2)
			return ".json";
		else
			return "";
	}

	// Alle Buttons enablen/disablen
	private void setRdbtn(boolean b) {
		management.getFileSelectionView().getRdbtnTxt().setEnabled(b);
		management.getFileSelectionView().getRdbtnXml().setEnabled(b);
		management.getFileSelectionView().getRdbtnJson().setEnabled(b);
		management.getFileSelectionView().getRdbtnAll().setEnabled(b);
	}

	// Aktualsieren der JList + Anzahl an Dateien
	private void updateListFilePath() {
		int importSize = fileImporter.getTextdateien().size();
		String[] fileNames = getFileNames(importSize);
		management.getFileSelectionView().getModel().clear();
		for (int i = 0; i < importSize; i++) {
			management
					.getFileSelectionView()
					.getModel()
					.addElement(
							fileNames[i]
									+ " |  "
									+ fileImporter.getTextdateien().get(i)
											.getAbsolutePath());

			management.getFileSelectionView().getLblFileCount()
					.setText(String.valueOf(importSize));

		}
	}

	// Gibt passenden Buchstaben fuer Index
	private String intToFilename(int n) {
		char[] buf = new char[(int) java.lang.Math.floor(java.lang.Math
				.log(25 * (n + 1)) / java.lang.Math.log(26))];
		for (int i = buf.length - 1; i >= 0; i--) {
			n--;
			buf[i] = (char) ('A' + n % 26);
			n /= 26;
		}
		return new String(buf);
	}

	// Gibt vollstaendige Liste wieder (A,B,C,..AA,AB,AC,..)
	private String[] getFileNames(int length) {
		String[] fileNames = new String[length];

		for (int i = 0; i < fileNames.length; i++) {
			fileNames[i] = intToFilename(i + 1);
		}
		return fileNames;
	}

	// Konvertiert die Anzeige Liste in Liste der Pfade (ohne bspw. "AB: ")
	private List<String> convertToPaths(List<String> list) {
		List<String> pathList = new ArrayList<String>();
		for (int i = 0; i < management.getFileSelectionView().getListFilePath()
				.getSelectedValuesList().size(); i++) {
			pathList.add(list.get(i).split("\\|")[1].trim());
		}
		return pathList;
	}

	// Gibt die Liste an zurzeit ausgewaehlten Dateien wieder
	private List<File> getListSelection() {
		List<String> selectedPaths = convertToPaths(management
				.getFileSelectionView().getListFilePath()
				.getSelectedValuesList());
		List<File> selectedFiles = new ArrayList<File>();
		for (String s : selectedPaths) {
			Path path = Paths.get(s);
			selectedFiles.add(path.toFile());
		}
		return selectedFiles;
	}

	// - Getter -

	public File[] getAuswahl() {
		return selection;
	}

	public IMatrix getMatrix() {
		return matrix;
	}

	public int getMode() {
		return mode;
	}
}