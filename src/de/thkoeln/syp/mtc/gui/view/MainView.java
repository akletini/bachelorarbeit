package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import de.thkoeln.syp.mtc.datenhaltung.api.IConfig;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IComparisonImpl;
import de.thkoeln.syp.mtc.gui.control.MainController;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.control.MouseAdapterMatrix;
import de.thkoeln.syp.mtc.gui.resources.DefaultTableHeaderCellRenderer;
import de.thkoeln.syp.mtc.gui.resources.RowNumberTable;
import de.thkoeln.syp.mtc.logging.Logger;

/**
 * Visualisierung des Hauptmenues
 * 
 * @author Allen Kletinitch
 *
 */
public class MainView extends JFrame {

	private static final long serialVersionUID = 8634086168819911638L;
	private Management management;
	private JLabel quickAccess;
	private JPanel panel;
	private JTable tableMatrix;
	private JToolBar toolBar;
	private JButton btnDateiauswahl, btnKonfig, btnDeleteLog, btnZoomIn,
			btnZoomOut, btnHilfe, btnParseErrorList;
	private JScrollPane scrollPaneMatrix, scrollPaneFiles;
	private RowNumberTable rowTable;
	private JTextPane textArea;
	private JMenuBar menuBar;
	private JMenu menuFile, menuConfig, menuLogging, menuHelp;
	private JMenuItem fileSelection, saveComparison, saveComparisonAs,
			loadComparison;
	private JMenuItem saveConfig, saveConfigAs, loadConfig, settings;
	private JMenuItem clearLog, showLog;
	private JCheckBoxMenuItem info, warning, error;
	private JMenuItem about, tutorial, javadoc;

	private JProgressBar progressBar;
	private Logger logger;
	private IConfig config;

	public MainView() {
		management = Management.getInstance();
		config = management.getFileImporter().getConfig();
		// Initialize logging
		management.setLogger(new Logger());
		logger = management.getLogger();
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		Date date = new Date();
		logger.writeToLogFile(
				"Open Application MultiTextCompare (" + formatter.format(date)
						+ ")", true);
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();

		// Icons
		Icon iconCompare = null, iconConfig = null, iconQuestion = null, iconInfo = null, iconSave = null, iconImport = null, iconDelete = null, iconPlus = null, iconMinus = null, iconError = null;
		try {
			iconCompare = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("fileIconSmall.png")));
			iconConfig = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("settingsSmall.png")));
			iconQuestion = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("questionSmall.png")));
			iconInfo = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("infoSmall.png")));
			iconSave = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("saveSmall.png")));
			iconImport = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("importSmall.png")));
			iconDelete = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("deleteSmall.png")));
			iconPlus = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("plus.png")));
			iconMinus = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("minus.png")));
			iconError = new ImageIcon(ImageIO.read(classLoader
					.getResourceAsStream("parseError.png")));
		} catch (IOException e1) {
			logger.setMessage(e1.toString(), Logger.LEVEL_ERROR);
		}

		// Panel
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new MigLayout("", "[grow]",
				"[30px:n:100px,top][grow,center][80px:n:160px,grow,bottom]"));
		// Menu-Bar
		menuBar = new JMenuBar();

		// Menus
		menuFile = new JMenu("    File    ");
		fileSelection = new JMenuItem("New Comparison", iconCompare);
		saveComparison = new JMenuItem("Save Comparison", iconSave);
		saveComparisonAs = new JMenuItem("Save Comparison As");
		loadComparison = new JMenuItem("Load Comparison", iconImport);
		menuBar.add(menuFile);

		menuConfig = new JMenu("    Configuration    ");
		saveConfig = new JMenuItem("Save Configuration", iconSave);
		saveConfigAs = new JMenuItem("Save Configuration As");
		loadConfig = new JMenuItem("Load Configuration", iconImport);
		settings = new JMenuItem("Settings", iconConfig);
		menuBar.add(menuConfig);

		menuLogging = new JMenu("    Log    ");
		clearLog = new JMenuItem("Clear");
		info = new JCheckBoxMenuItem("Show Infos");
		info.setState(config.getShowInfos());
		warning = new JCheckBoxMenuItem("Show Warnings");
		warning.setState(config.getShowWarnings());
		error = new JCheckBoxMenuItem("Show Errors");
		error.setState(config.getShowErrors());
		showLog = new JMenuItem("Show Log");
		menuBar.add(menuLogging);
		javadoc = new JMenuItem("Show JavaDoc");

		menuHelp = new JMenu("    Help    ");
		about = new JMenuItem("About", iconInfo);
		tutorial = new JMenuItem("Open Instruction File", iconQuestion);
		menuBar.add(menuHelp);

		// Add submenus
		// File
		menuFile.add(fileSelection);
		menuFile.addSeparator();
		menuFile.add(saveComparison);
		menuFile.add(saveComparisonAs);
		menuFile.add(loadComparison);

		// Config
		menuConfig.add(saveConfig);
		menuConfig.add(saveConfigAs);
		menuConfig.add(loadConfig);
		menuConfig.addSeparator();
		menuConfig.add(settings);

		// Log
		menuLogging.add(clearLog);
		menuLogging.addSeparator();
		menuLogging.add(info);
		menuLogging.add(warning);
		menuLogging.add(error);
		menuLogging.addSeparator();
		menuLogging.add(showLog);

		// Help
		menuHelp.add(about);
		menuHelp.add(tutorial);
		menuHelp.add(javadoc);

		// Toolbar inkl. Buttons
		toolBar = new JToolBar();
		quickAccess = new JLabel("Quick Access: ");
		toolBar.setFloatable(false);
		toolBar.add(quickAccess);

		panel.add(toolBar, "flowx,cell 0 0,alignx left");
		btnDateiauswahl = new JButton(iconCompare);
		btnDateiauswahl.setToolTipText("Open file selection");
		toolBar.add(btnDateiauswahl);
		toolBar.addSeparator();
		btnKonfig = new JButton(iconConfig);
		btnKonfig.setToolTipText("Open settings");
		toolBar.add(btnKonfig);
		toolBar.addSeparator();
		btnZoomIn = new JButton(iconPlus);
		btnZoomIn.setToolTipText("Zoom in on matrix");
		toolBar.add(btnZoomIn);
		btnZoomOut = new JButton(iconMinus);
		btnZoomOut.setToolTipText("Zoom out of matrix");
		toolBar.add(btnZoomOut);
		toolBar.addSeparator();
		btnDeleteLog = new JButton(iconDelete);
		btnDeleteLog.setToolTipText("Clear output log");
		toolBar.add(btnDeleteLog);
		toolBar.addSeparator();
		btnParseErrorList = new JButton(iconError);
		btnParseErrorList.setToolTipText("Show parse errors");
		toolBar.add(btnParseErrorList);
		toolBar.addSeparator();
		btnHilfe = new JButton(iconQuestion);
		btnHilfe.setToolTipText("Open help document");
		toolBar.add(btnHilfe);
		progressBar = new JProgressBar();
		toolBar.add(progressBar);
		progressBar.setVisible(false);

		// TextArea (Ausgabe)
		textArea = new JTextPane();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
		textArea.setEditable(false);
		scrollPaneFiles = new JScrollPane(textArea);
		scrollPaneFiles
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panel.add(scrollPaneFiles, "flowx,cell 0 2,grow");

		// Frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 960, 540);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setMinimumSize(new Dimension(800, 600));
		this.setTitle("MultiTextCompare");
		this.setContentPane(panel);
		this.setJMenuBar(menuBar);
		try {
			this.setIconImage(ImageIO.read(classLoader
					.getResourceAsStream("icon.png")));
		} catch (IOException e) {
			logger.setMessage(e.toString(), Logger.LEVEL_ERROR);
		}
		this.setFocusable(false);

		Management.getInstance().setMainController(new MainController(this));
	}

	// Erstellen der Matrix
	public void updateMatrix(IMatrix matrix, int anzahlDateien,
			String[] nameDateien) {
		List<IComparisonImpl> listMatrix = matrix.getInhalt(); // Aehnlichkeitswerte
		management.getComparisons().addAll(listMatrix);
		String[][] data = new String[anzahlDateien][anzahlDateien]; // String
																	// Array zum
																	// befuellen
																	// der
																	// Matrix
		DecimalFormat df = new DecimalFormat("0.000"); // Formatieren der
														// Aehnlichkeitswerte
		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
		int index = 0; // Zusaetzliche Index Variable fuer die for-Schleife

		// data Array wird mit Aehnlichkeitswerten befuellt
		for (int i = 0; i < anzahlDateien; i++) {
			data[i][i] = "1.000";
			for (int j = i + 1; j < anzahlDateien; j++) {
				double wert = listMatrix.get(index).getValue();
				String wertString = df.format(wert);
				data[i][j] = wertString;
				data[j][i] = wertString;
				index++;
			}
		}

		// Matrix wird erstellt
		tableMatrix = new JTable(data, nameDateien) {

			/**
			 * 
			 */
			private static final long serialVersionUID = -7131793748431267699L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);
				this.setOpaque(true);
				// normal paint
				if (!management.isMatrixGreyedOut()) {
					Object value = getModel().getValueAt(row, col);
					double wert = Double.valueOf(value.toString());
					Color wertFarbe = getColor(wert);
					comp.setBackground(wertFarbe);
					comp.setForeground(Color.BLACK);
				}
				// repaint cells in grey and leave relevant cells colored
				else {

					int indexCol = management.getReferenceCol();
					int indexRow = management.getReferenceRow();
					comp.setBackground(Color.GRAY);
					comp.setForeground(Color.BLACK);

					if ((row == indexRow || col == indexCol) && !(row == col)) {
						Object value = getModel().getValueAt(row, col);
						double wert = Double.valueOf(value.toString());
						Color wertFarbe = getColor(wert);
						comp.setBackground(wertFarbe);
						comp.setForeground(Color.BLACK);
					}

				}
				return comp;
			}

			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {

					/**
					 * 
					 */
					private static final long serialVersionUID = 7949169729076337028L;

					@Override
					public Dimension getPreferredSize() {
						Dimension d = super.getPreferredSize();
						d.height = 30;
						return d;
					}

					@Override
					public String getToolTipText(MouseEvent e) {
						int index = columnModel
								.getColumnIndexAtX(e.getPoint().x);
						if (index >= 0) {
							int realIndex = columnModel.getColumn(index)
									.getModelIndex();

							if (!management.isNewSelection())
								return management.getPaths()[realIndex];
						}
						if (index != -1)
							logger.setMessage(
									"It is not possible to display the file names after altering the file selection.",
									Logger.LEVEL_WARNING);
						return null;
					}

				};
			}
		};

		// Matrix Parameter
		JTableHeader header = tableMatrix.getTableHeader();
		header.setDefaultRenderer(new DefaultTableHeaderCellRenderer());

		header.setResizingAllowed(false);
		tableMatrix.getTableHeader().setReorderingAllowed(false);
		tableMatrix.setRowHeight(60);
		tableMatrix.setDefaultEditor(Object.class, null);
		tableMatrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Wenn noetig alte MatrixPane loeschen und Neue auf das Panel legen
		if (scrollPaneMatrix != null)
			panel.remove(scrollPaneMatrix);
		scrollPaneMatrix = new JScrollPane(tableMatrix);

		scrollPaneMatrix.addMouseWheelListener(new MouseWheelListener() {
			final JScrollBar horizontalScrollBar = scrollPaneMatrix
					.getHorizontalScrollBar();

			// Horizontales Scrollen
			@Override
			public void mouseWheelMoved(MouseWheelEvent evt) {
				if (evt.isControlDown() || evt.isShiftDown()) {
					scrollPaneMatrix.setWheelScrollingEnabled(false);
				} else {
					scrollPaneMatrix.setWheelScrollingEnabled(true);
				}
				if (evt.isShiftDown()) {

					if (evt.getWheelRotation() >= 1)// mouse wheel was rotated
													// down/ towards the user
					{
						int iScrollAmount = evt.getScrollAmount();
						int iNewValue = horizontalScrollBar.getValue()
								+ horizontalScrollBar.getBlockIncrement()
								* iScrollAmount
								* Math.abs(evt.getWheelRotation());
						if (iNewValue <= horizontalScrollBar.getMaximum()) {
							horizontalScrollBar.setValue(iNewValue);
						}
					} else if (evt.getWheelRotation() <= -1)// mouse wheel was
															// rotated up/away
															// from the user
					{
						int iScrollAmount = evt.getScrollAmount();
						int iNewValue = horizontalScrollBar.getValue()
								- horizontalScrollBar.getBlockIncrement()
								* iScrollAmount
								* Math.abs(evt.getWheelRotation());
						if (iNewValue >= 0) {
							horizontalScrollBar.setValue(iNewValue);
						}
					}
				}
			}
		});

		panel.add(scrollPaneMatrix, "cell 0 1,grow");

		// Zentrieren der Aehnlichkeitswerte
		DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tableMatrix
				.getDefaultRenderer(Object.class);
		renderer.setHorizontalAlignment(SwingConstants.CENTER);

		// Modifikation der Matrix fuer Zeilenbenennung
		rowTable = new RowNumberTable(tableMatrix);
		rowTable.setFilenames(nameDateien);
		scrollPaneMatrix.setRowHeaderView(rowTable);

		scrollPaneMatrix.setCorner(JScrollPane.UPPER_LEFT_CORNER,
				rowTable.getTableHeader());
		scrollPaneMatrix.getViewport().setScrollMode(
				JViewport.BACKINGSTORE_SCROLL_MODE);

		SwingUtilities.updateComponentTreeUI(this);

		// MouseAdapter um Matrix klickbar zu machen
		MouseAdapterMatrix mouseAdapterMatrix = new MouseAdapterMatrix();
		tableMatrix.addMouseListener(mouseAdapterMatrix);
		tableMatrix.getTableHeader().addMouseListener(mouseAdapterMatrix);
	}

	// Generierung der Farbe passend zum Aehnlichkeitswert
	private Color getColor(double value) {
		double h = value * 0.3; // Hue
		double s = 0.9; // Saturation
		double b = 0.9; // Brightness

		return Color.getHSBColor((float) h, (float) s, (float) b);
	}

	// -- Getter --
	public JTextPane getTextArea() {
		return textArea;
	}

	public JTable getTableMatrix() {
		return tableMatrix;
	}

	public JScrollPane getMatrixScrollpane() {
		return scrollPaneMatrix;
	}

	public RowNumberTable getRowNumberTable() {
		return rowTable;
	}

	public JCheckBoxMenuItem getInfo() {
		return info;
	}

	public JCheckBoxMenuItem getWarning() {
		return warning;
	}

	public JCheckBoxMenuItem getError() {
		return error;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public JMenuItem getJavadoc() {
		return javadoc;
	}

	public JMenuItem getMenuSave() {
		return saveComparison;
	}

	public JMenuItem getMenuSaveAs() {
		return saveComparisonAs;
	}

	public void setJavadoc(JMenuItem javadoc) {
		this.javadoc = javadoc;
	}

	// Setter

	public void setInfo(JCheckBoxMenuItem info) {
		this.info = info;
	}

	public void setWarning(JCheckBoxMenuItem warning) {
		this.warning = warning;
	}

	public void setError(JCheckBoxMenuItem error) {
		this.error = error;
	}

	// -- Methoden um die Buttons auf den Controller zu verweisen --

	public JButton getBtnParseErrorList() {
		return btnParseErrorList;
	}

	public void setBtnParseErrorList(JButton btnParseErrorList) {
		this.btnParseErrorList = btnParseErrorList;
	}

	public void addFileSelectionListener(ActionListener e) {
		btnDateiauswahl.addActionListener(e);
	}

	public void addConfigListener(ActionListener e) {
		btnKonfig.addActionListener(e);
	}

	public void addHelpListener(ActionListener e) {
		btnHilfe.addActionListener(e);
	}

	public void addToolbarLogClearListener(ActionListener e) {
		btnDeleteLog.addActionListener(e);
	}

	public void addToolbarZoomInListener(ActionListener e) {
		btnZoomIn.addActionListener(e);
	}

	public void addToolbarZoomOutListener(ActionListener e) {
		btnZoomOut.addActionListener(e);
	}

	public void addToolbarShowParseErrorListListener(ActionListener e) {
		btnParseErrorList.addActionListener(e);
	}

	public void addZoomListener(MouseWheelListener e) {
		addMouseWheelListener(e);
	}

	// Menu button listeners

	public void addMenuSaveComparisonListener(ActionListener e) {
		saveComparison.addActionListener(e);
	}

	public void addMenuSaveAsComparisonListener(ActionListener e) {
		saveComparisonAs.addActionListener(e);
	}

	public void addMenuLoadComparisonListener(ActionListener e) {
		loadComparison.addActionListener(e);
	}

	public void addMenuFileSelection(ActionListener e) {
		fileSelection.addActionListener(e);
	}

	public void addMenuAboutListener(ActionListener e) {
		about.addActionListener(e);
	}

	public void addMenuHelpListener(ActionListener e) {
		tutorial.addActionListener(e);
	}

	public void addLogClearListener(ActionListener e) {
		clearLog.addActionListener(e);
	}

	public void addMenuSettingsListener(ActionListener e) {
		settings.addActionListener(e);
	}

	public void addMenuShowInfosListener(ActionListener e) {
		info.addActionListener(e);
	}

	public void addMenuShowWarningsListener(ActionListener e) {
		warning.addActionListener(e);
	}

	public void addMenuShowErrorsListener(ActionListener e) {
		error.addActionListener(e);
	}

	public void addMenuShowLogListener(ActionListener e) {
		showLog.addActionListener(e);
	}

	public void addMenuImportConfigListener(ActionListener e) {
		loadConfig.addActionListener(e);
	}

	public void addMenuSaveConfigAsListener(ActionListener e) {
		saveConfigAs.addActionListener(e);
	}

	public void addMenuSaveConfigListener(ActionListener e) {
		saveConfig.addActionListener(e);
	}

	public void addMenuShowJavadocListener(ActionListener e) {
		javadoc.addActionListener(e);
	}

}
