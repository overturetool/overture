/*******************************************************************************
 * Copyright (c) 2009, 2013 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.guibuilder.internal;

import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.table.AbstractTableModel;

/**
 * A window possessing a table with the current global instances
 * @author carlos
 *
 */
public class InstanceListUiContainer extends JFrame implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The swing table widget requires an abstract table model
	 *  for what we want to do
	 * @author carlos
	 *
	 */
	private class MyTableModel extends AbstractTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private Vector<String> columnNames = null;
		private InstanceList rowData = null;

		public MyTableModel() {			
			super();
			columnNames = new Vector<String>();
			columnNames.addElement("Name");
			columnNames.addElement("Value");
		}

		public boolean isCellEditable(int row, int col) {
			return false;
		}

		public int getRowCount() {
			if (rowData ==null) 
				return 0;
			return rowData.size();
		}

		public int getColumnCount() {
			return columnNames.size();
		}

		public String getColumnName(int col) {
			return columnNames.get(col);
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if ( (rowIndex > getRowCount() -1 ) || (columnIndex > getColumnCount()-1) )
				return null;
			// out of bound
			if(columnIndex > columnNames.size())
				return null;

			switch(columnIndex) {
			case 0:
				return rowData.get(rowIndex).getName();
			case 1:
				return rowData.get(rowIndex).getValue();
			default:
			}
			
			return null;
		}
		
		public void setRowData(InstanceList instanceList ) {
			this.rowData = instanceList;
		}
	}

	
	/** subclass for JTable in order to correct a old bug... 
	 * (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4127936)
	 * @author carlos
	 *
	 */
	class MyJTable extends JTable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public boolean getScrollableTracksViewportWidth() {
			if (autoResizeMode != AUTO_RESIZE_OFF) {
				if (getParent() instanceof JViewport) {
					return (((JViewport)getParent()).getWidth() > getPreferredSize().width);
				}
			}
			return false;
		}
		
	}
	
	private MyTableModel tableModel = null;
	private JScrollPane scrollPane = null;
	private JTable table = null;

	/**
	 * Constructor
	 * @param instanceList The instance list object to monitor. The information to fill the table will be obtained from this.
	 */
	public InstanceListUiContainer( InstanceList instanceList ) {
		
		super("List of Instances");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(230,130);
		table = new MyJTable();
		tableModel = new MyTableModel();
		tableModel.setRowData(instanceList);
		instanceList.addObserver(this);
		table.setModel(tableModel);
		scrollPane = new JScrollPane(table);
		table.setPreferredScrollableViewportSize(new Dimension(500,70));
		table.setFillsViewportHeight(true);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setContentPane(scrollPane);		
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		tableModel.fireTableDataChanged();
	}
	
}
