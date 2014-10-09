/*
 * #%~
 * RT Trace Viewer Plugin
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.plugins.rttraceviewer.view;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.overture.ide.plugins.rttraceviewer.TracefileViewerPlugin;

public class ValidationTable
{
	private Table theTable;
	private IViewCallback theViewer;
	
	final String STATUS_FAILED = "F";
	final String STATUS_PASSED = "P";
	
	final int COLUMN_INTERNAL_STATUS = 7;

	public ValidationTable(Composite theComposite)
	{
		theTable = null;
		theViewer = null;

		theTable = new Table(theComposite, 0x10804);
		theTable.setHeaderVisible(true);
		theTable.setLinesVisible(true);
		theTable.addSelectionListener(new SelectionAdapter()
		{

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				int current = theTable.getSelectionIndex();
				if (current != -1)
				{
					if (current != prev)
						prev = -1;
					TableItem selection = theTable.getItem(current);
					if (selection.getText(COLUMN_INTERNAL_STATUS).equals(STATUS_FAILED))
					{
						String time;
						String thrid;
						if (prev == -1)
						{
							time = selection.getText(3);
							thrid = selection.getText(4);
							prev = current;
						} else
						{
							time = selection.getText(5);
							thrid = selection.getText(6);
							prev = -1;
						}
						theViewer.panToTime(Long.parseLong(time), Long.parseLong(thrid));
					} else
					{
						prev = -1;
					}
				}
			}

			private int prev;
			// final ValidationTable this$0;
			//
			//
			// {
			// this$0 = ValidationTable.this;
			// super();
			// prev = -1;
			// }
		});
		TableColumn col1 = new TableColumn(theTable, 0);
		col1.setText("status");
		col1.pack();
		TableColumn col2 = new TableColumn(theTable, 0);
		col2.setText("name");
		col2.pack();
		TableColumn col3 = new TableColumn(theTable, 0);
		col3.setText("expression");
		col3.pack();
		TableColumn col4 = new TableColumn(theTable, 0);
		col4.setText("src time");
		col4.pack();
		TableColumn col5 = new TableColumn(theTable, 0);
		col5.setText("src thread");
		col5.pack();
		TableColumn col6 = new TableColumn(theTable, 0);
		col6.setText("dest time");
		col6.pack();
		TableColumn col7 = new TableColumn(theTable, 0);
		col7.setText("dest thread");
		col7.pack();
		TableColumn col8 = new TableColumn(theTable, SWT.Hide);
		col8.setText("Internal status");
//		col8.
		return;
	}
	
	public void setCallback(IViewCallback callback)
	{
		this.theViewer = callback;
	}

	public void parseValidationFile(Reader reader)
	{
		boolean abort = false;
		char qchar = '"';
		String vrname = "";
		Long sthrid = null;
		Long stime = null;
		Long dthrid = null;
		Long dtime = null;
		theTable.removeAll();
		TableItem theItem = null;
		int cnt = 1;
		if (reader != null)
		{
			try
			{
				StreamTokenizer tokens = new StreamTokenizer(reader);
				tokens.eolIsSignificant(true);
				tokens.quoteChar(qchar);
				tokens.parseNumbers();
				while (tokens.nextToken() != StreamTokenizer.TT_EOF && !abort)
				{
					if (cnt != 1 || tokens.ttype != StreamTokenizer.TT_EOL)
					{
						if (cnt == 1 && tokens.ttype == qchar)
						{
							vrname = new String(tokens.sval);
							theItem = new TableItem(theTable, 0);
							theItem.setText(cnt, tokens.sval);
							cnt++;
						} else if (cnt == 2 && tokens.ttype == qchar)
						{
							new String(tokens.sval);
							theItem.setText(cnt, tokens.sval);
							cnt++;
						} else if (cnt == 3
								&& tokens.ttype == StreamTokenizer.TT_NUMBER)
						{
							stime = new Long(Math.round(tokens.nval));
							theItem.setText(cnt, stime.toString());
//							theItem.setBackground(0, ColorConstants.red);
//							theItem.setForeground(0, ColorConstants.white);
							theItem.setText(COLUMN_INTERNAL_STATUS,STATUS_FAILED);
							theItem.setImage(TracefileViewerPlugin.getImageDescriptor("icons/faild_obj.png").createImage());
							cnt++;
						} else if (cnt == 3
								&& tokens.ttype == StreamTokenizer.TT_WORD)
						{
							stime = null;
							theItem.setText(COLUMN_INTERNAL_STATUS, STATUS_PASSED);
//							theItem.setBackground(0, ColorConstants.green);
							theItem.setImage(TracefileViewerPlugin.getImageDescriptor("icons/succes_obj.png").createImage());
							cnt++;
						} else if (cnt == 4
								&& tokens.ttype == StreamTokenizer.TT_EOL)
							cnt = 1;
						else if (cnt == 4
								&& tokens.ttype == StreamTokenizer.TT_NUMBER)
						{
							sthrid = new Long(Math.round(tokens.nval));
							theItem.setText(cnt, sthrid.toString());
							theViewer.addLowerError(stime, sthrid, vrname);
							cnt++;
						} else if (cnt == 4
								&& tokens.ttype == StreamTokenizer.TT_WORD)
						{
							stime = null;
							cnt++;
						} else if (cnt == 5
								&& tokens.ttype == StreamTokenizer.TT_NUMBER)
						{
							dtime = new Long(Math.round(tokens.nval));
							theItem.setText(cnt, dtime.toString());
							cnt++;
						} else if (cnt == 5
								&& tokens.ttype == StreamTokenizer.TT_WORD)
						{
							dtime = null;
							cnt++;
						} else if (cnt == 6
								&& tokens.ttype == StreamTokenizer.TT_NUMBER)
						{
							dthrid = new Long(Math.round(tokens.nval));
							theItem.setText(cnt, dthrid.toString());
							theViewer.addUpperError(dtime, dthrid, vrname);
							cnt++;
						} else if (cnt == 6
								&& tokens.ttype == StreamTokenizer.TT_WORD)
						{
							dthrid = null;
							cnt++;
						} else if (cnt == 7
								&& tokens.ttype == StreamTokenizer.TT_EOL)
						{
							stime = null;
							sthrid = null;
							dtime = null;
							dthrid = null;
							cnt = 1;
						} else
						{
							abort = true;
						}
					}
				}
				if (abort)
				{
					theViewer.showMessage("syntax error in validation conjecture file");
				}
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		for (cnt = 0; cnt < 6; cnt++)
		{
			theTable.getColumn(cnt).pack();
		}

		// MVQ; This is called from
		// theViewer.updateOverviewPage();
	}

	public void unlink(IViewCallback theCallback)
	{
		if(theViewer==theCallback)
		{
		theTable.clearAll();
		theViewer = null;
	}}

}
