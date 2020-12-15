package de.thkoeln.syp.mtc.gui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import de.thkoeln.syp.mtc.datenhaltung.api.IMatrix;
import de.thkoeln.syp.mtc.datenhaltung.impl.IAehnlichkeitImpl;
import de.thkoeln.syp.mtc.datenhaltung.impl.IMatrixImpl;

public class MatrixView extends JFrame {
	JPanel panel;
	JTable table;
	JButton testButton;
	private static DecimalFormat df = new DecimalFormat("0.00");
	int index;
	
	public MatrixView(IMatrix matrix, int anzahlDateien, String[] nameDateien){
		panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));
		index = 0;
		List<IAehnlichkeitImpl> list = matrix.getInhalt();
		
		String[][] data = new String[anzahlDateien][anzahlDateien];
		
		for(int i=0; i<anzahlDateien; i++){
			data[i][i] = "1.00";
			for(int j=i+1; j<anzahlDateien; j++){
				double wert = list.get(index).getWert();
				String wertString = df.format(wert);
				data[i][j] = wertString;
				data[j][i] = wertString;
				index++;
				}
		}
		System.out.println(anzahlDateien);
		table = new JTable(data, nameDateien);
		panel.add(new JScrollPane(table));
		
		this.add(panel, BorderLayout.CENTER);
		this.setTitle("Matrix");
		this.setSize(400, 400);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.pack();
//		int r = (int) ((255* (100 - (wert*100))) / 100);
//		int g = (int) ((255*(wert*100) / 100));
//		Color  wertFarbe   = new Color(r, g,  0);
		
		//table.getColumnModel().getColumn(0).setCellRenderer(new RowHeaderRenderer());
		
//		for(int i=0; i<16; i++){
//			double wert = Math.random();
//			String wertString = df.format(wert);
//			testButton = new JButton(""+ wertString);
//			
//			int r = (int) ((255* (100 - (wert*100))) / 100);
//			int g = (int) ((255*(wert*100) / 100));
//			Color  wertFarbe   = new Color(r, g,  0);
//			
//			testButton.setOpaque(true);
//			testButton.setBackground(wertFarbe);
//			testButton.setForeground(Color.BLACK);
//		
//			panel.add(testButton);
//		}
	}
}