package de.thkoeln.syp.mtc.gui.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import net.miginfocom.swing.MigLayout;
import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.gui.control.MainController;
import de.thkoeln.syp.mtc.gui.control.Management;
import de.thkoeln.syp.mtc.gui.resources.MouseAdapterMatrix;
import de.thkoeln.syp.mtc.gui.resources.RowNumberTable;

public class MainView extends JFrame {
	private Management management;
	private JPanel panel;
	private JTable tableMatrix;
	private JToolBar toolBar;
	private JButton btnDateiauswahl, btnKonfig, btnHilfe, btnAbout;
	private JScrollPane scrollPaneMatrix, scrollPaneFiles;
	private RowNumberTable rowTable;
	private JTextArea textArea;

	public MainView() {
		management = Management.getInstance();

		// Panel
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new MigLayout("", "[grow]",
				"[30px:n:100px,top][grow,center][80px:n:160px,grow,bottom]"));

		// Toolbar inkl. Buttons
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		panel.add(toolBar, "flowx,cell 0 0,alignx center");
		btnDateiauswahl = new JButton("File selection");
		toolBar.add(btnDateiauswahl);
		btnKonfig = new JButton("Configuration");
		toolBar.add(btnKonfig);
		btnHilfe = new JButton("Help");
		toolBar.add(btnHilfe);
		btnAbout = new JButton("About");
		toolBar.add(btnAbout);

		// TextArea (Ausgabe)
		textArea = new JTextArea();
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
		this.setTitle("MultiTextCompare");
		this.setContentPane(panel);
		try {
			this.setIconImage(ImageIO.read(new File("res/icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Management.getInstance().setMainController(new MainController(this));
	}

	// Erstellen der Matrix
	public void updateMatrix(IMatrix matrix, int anzahlDateien,
			String[] nameDateien) {

		List<IAehnlichkeitImpl> listMatrix = management.getTextvergleicher()
				.getMatrix().getInhalt(); // Aehnlichkeitswerte
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
				double wert = listMatrix.get(index).getWert();
				String wertString = df.format(wert);
				data[i][j] = wertString;
				data[j][i] = wertString;
				index++;
			}
		}

		// Matrix wird erstellt
		tableMatrix = new JTable(data, nameDateien) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);
				if (getSelectionModel().isSelectionEmpty()) {
					Object value = getModel().getValueAt(row, col);
					double wert = Double.valueOf(value.toString());
					Color wertFarbe = getColor(wert);
					comp.setBackground(wertFarbe);
				} else {
					int indexCol = getSelectedColumn();
					int indexRow = getSelectedRow();
					comp.setBackground(Color.GRAY);

					if (row == indexRow || col == indexCol) {
						Object value = getModel().getValueAt(row, col);
						double wert = Double.valueOf(value.toString());
						Color wertFarbe = getColor(wert);
						comp.setBackground(wertFarbe);
					}

				}
				return comp;
			}

			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(columnModel) {
					public String getToolTipText(MouseEvent e) {
						int index = columnModel
								.getColumnIndexAtX(e.getPoint().x);
						if (index >= 0) {
							int realIndex = columnModel.getColumn(index)
									.getModelIndex();

							if (!management.getFileSelectionController()
									.getNewSelection())
								return management.getPaths()[realIndex];
						}
						if (index != -1)
							management
									.appendToLog("It is not possible to display the file names after altering the file selection.");
						return null;
					}
				};
			}
		};

		// Matrix Parameter
		tableMatrix.getTableHeader().setReorderingAllowed(false);
		tableMatrix.setRowHeight(60);
		tableMatrix.setDefaultEditor(Object.class, null);
		tableMatrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		// Wenn noetig alte MatrixPane loeschen und Neue auf das Panel legen
		if (scrollPaneMatrix != null)
			panel.remove(scrollPaneMatrix);
		scrollPaneMatrix = new JScrollPane(tableMatrix);

		scrollPaneMatrix.addMouseWheelListener(new MouseWheelListener() {
			final JScrollBar verticalScrollBar = scrollPaneMatrix
					.getVerticalScrollBar();
			final JScrollBar horizontalScrollBar = scrollPaneMatrix
					.getHorizontalScrollBar();
			
			//Horizontales Scrollen
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
	public JTextArea getTextArea() {
		return textArea;
	}

	public JTable getTableMatrix() {
		return tableMatrix;
	}

	public JScrollPane getMatrixScrollpane() {
		return scrollPaneMatrix;
	}

	// -- Methoden um die Buttons auf den Controller zu verweisen --
	public void addFileSelectionListener(ActionListener e) {
		btnDateiauswahl.addActionListener(e);
	}

	public void addConfigListener(ActionListener e) {
		btnKonfig.addActionListener(e);
	}

	public void addHelpListener(ActionListener e) {
		btnHilfe.addActionListener(e);
	}

	public void addAboutListener(ActionListener e) {
		btnAbout.addActionListener(e);
	}

	public void addZoomListener(MouseWheelListener e) {
		addMouseWheelListener(e);
	}

}
