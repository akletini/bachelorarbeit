package de.thkoeln.syp.mtc.gui.resources;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * "Verwandelt" eine Tabelle in eine Matrix 
 * @author Allen Kletinitch
 * 
 */
public class RowNumberTable extends JTable implements ChangeListener,
		PropertyChangeListener, TableModelListener {

	private static final long serialVersionUID = -5736931872898571250L;
	private JTable main;
	private String[] filenames;

	public RowNumberTable(JTable table) {
		
		main = table;
		main.addPropertyChangeListener(this);
		main.getModel().addTableModelListener(this);

		setFocusable(false);
		setAutoCreateColumnsFromModel(false);
		setSelectionModel(main.getSelectionModel());

		TableColumn column = new TableColumn();
		column.setHeaderValue("");
		column.setHeaderRenderer(new DefaultTableHeaderCellRenderer());
	    column.setResizable(false);
		addColumn(column);
		column.setCellRenderer(new RowNumberRenderer());

		getColumnModel().getColumn(0).setPreferredWidth(30);
		setPreferredScrollableViewportSize(getPreferredSize());
	}

	public void setFilenames(String[] filenames) {
		this.filenames = filenames;
	}

	public String[] getFilenames() {
		return filenames;
	}

	@Override
	public void addNotify() {
		super.addNotify();

		Component c = getParent();

		// Keep scrolling of the row table in sync with the main table.

		if (c instanceof JViewport) {
			JViewport viewport = (JViewport) c;
			viewport.addChangeListener(this);
		}
	}

	/*
	 * Delegate method to main table
	 */
	@Override
	public int getRowCount() {
		return main.getRowCount();
	}

	@Override
	public int getRowHeight(int row) {
		int rowHeight = main.getRowHeight(row);

		if (rowHeight != super.getRowHeight(row)) {
			super.setRowHeight(row, rowHeight);
		}

		return rowHeight;
	}

	/*
	 * No model is being used for this table so just use the row number as the
	 * value of the cell.
	 */
	@Override
	public Object getValueAt(int row, int column) {

		return filenames[row];
	}

	/*
	 * Don't edit data in the main TableModel by mistake
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	/*
	 * Do nothing since the table ignores the model
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {
	}

	//
	// Implement the ChangeListener Hier gehts kaputt
	//
	@Override
	public void stateChanged(ChangeEvent e) {
		// Keep the scrolling of the row table in sync with main table
		JViewport viewport = (JViewport) e.getSource();
		JScrollPane scrollPane = (JScrollPane)viewport.getParent();
		scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
	}

	//
	// Implement the PropertyChangeListener
	//
	public void propertyChange(PropertyChangeEvent e) {
		// Keep the row table in sync with the main table

		if ("selectionModel".equals(e.getPropertyName())) {
			setSelectionModel(main.getSelectionModel());
		}

		if ("rowHeight".equals(e.getPropertyName())) {
			repaint();
		}

		if ("model".equals(e.getPropertyName())) {
			main.getModel().addTableModelListener(this);
			revalidate();
		}
	}

	//
	// Implement the TableModelListener
	//
	@Override
	public void tableChanged(TableModelEvent e) {
		repaint();
		revalidate();
	}

	/*
	 * Attempt to mimic the table header renderer
	 */
	private static class RowNumberRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1753286549980889921L;

		public RowNumberRenderer() {
			setHorizontalAlignment(JLabel.CENTER);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			if (table != null) {
				JTableHeader header = table.getTableHeader();

				if (header != null) {
					setForeground(header.getForeground());
					setBackground(Color.white);
					setFont(header.getFont());
				}
			}

			if (isSelected) {
				setFont(getFont().deriveFont(Font.BOLD));
			}

			setText((value == null) ? "" : value.toString());
			Color color = UIManager.getColor("Table.gridColor");;
		    MatteBorder border = new MatteBorder(1, 0, 0, 0, color);
		    setBorder(border);

			return this;
		}
	}
}