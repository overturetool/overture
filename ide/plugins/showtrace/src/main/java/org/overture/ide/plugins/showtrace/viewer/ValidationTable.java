/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
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
// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:15
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   ValidationTable.java

package org.overture.ide.plugins.showtrace.viewer;

import java.io.*;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            TracefileViewer

public class ValidationTable
{

    ValidationTable(Composite theComposite, IViewCallback theCallback)
    {
        theTable = null;
        theViewer = null;
        if(!$assertionsDisabled && theComposite == null)
            throw new AssertionError();
        if(!$assertionsDisabled && theCallback == null)
        {
            throw new AssertionError();
        } else
        {
            theViewer = theCallback;
            theTable = new Table(theComposite, 0x10804);
            theTable.setHeaderVisible(true);
            theTable.setLinesVisible(true);
            theTable.addSelectionListener(new SelectionAdapter() {

                @Override
				public void widgetSelected(SelectionEvent event)
                {
                    int current = theTable.getSelectionIndex();
                    if(current != -1)
                    {
                        if(current != prev)
                            prev = -1;
                        TableItem selection = theTable.getItem(current);
                        if(selection.getText(0) == "  FAIL")
                        {
                            String time;
                            String thrid;
                            if(prev == -1)
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
//                final ValidationTable this$0;
//
//            
//            {
//                this$0 = ValidationTable.this;
//                super();
//                prev = -1;
//            }
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
            return;
        }
    }

    public void parseValidationFile(String fname)
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
        if(fname != null)
            try
            {
                StreamTokenizer tokens = new StreamTokenizer(new FileReader(fname));
                tokens.eolIsSignificant(true);
                tokens.quoteChar(qchar);
                tokens.parseNumbers();
                while(tokens.nextToken() != StreamTokenizer.TT_EOF && !abort) 
                    if(cnt != 1 || tokens.ttype != StreamTokenizer.TT_EOL)
                        if(cnt == 1 && tokens.ttype == qchar)
                        {
                            vrname = new String(tokens.sval);
                            theItem = new TableItem(theTable, 0);
                            theItem.setText(cnt, tokens.sval);
                            cnt++;
                        } else
                        if(cnt == 2 && tokens.ttype == qchar)
                        {
                            new String(tokens.sval);
                            theItem.setText(cnt, tokens.sval);
                            cnt++;
                        } else
                        if(cnt == 3 && tokens.ttype == StreamTokenizer.TT_NUMBER)
                        {
                            stime = new Long(Math.round(tokens.nval));
                            theItem.setText(cnt, stime.toString());
                            theItem.setBackground(0, ColorConstants.red);
                            theItem.setForeground(0, ColorConstants.white);
                            theItem.setText(0, "  FAIL");
                            cnt++;
                        } else
                        if(cnt == 3 && tokens.ttype == StreamTokenizer.TT_WORD)
                        {
                            stime = null;
                            theItem.setText(0, "  PASS");
                            theItem.setBackground(0, ColorConstants.green);
                            cnt++;
                        } else
                        if(cnt == 4 && tokens.ttype == StreamTokenizer.TT_EOL)
                            cnt = 1;
                        else
                        if(cnt == 4 && tokens.ttype == StreamTokenizer.TT_NUMBER)
                        {
                            sthrid = new Long(Math.round(tokens.nval));
                            theItem.setText(cnt, sthrid.toString());
                            theViewer.addLowerError(stime, sthrid, vrname);
                            cnt++;
                        } else
                        if(cnt == 4 && tokens.ttype == StreamTokenizer.TT_WORD)
                        {
                            stime = null;
                            cnt++;
                        } else
                        if(cnt == 5 && tokens.ttype == StreamTokenizer.TT_NUMBER)
                        {
                            dtime = new Long(Math.round(tokens.nval));
                            theItem.setText(cnt, dtime.toString());
                            cnt++;
                        } else
                        if(cnt == 5 && tokens.ttype == StreamTokenizer.TT_WORD)
                        {
                            dtime = null;
                            cnt++;
                        } else
                        if(cnt == 6 && tokens.ttype == StreamTokenizer.TT_NUMBER)
                        {
                            dthrid = new Long(Math.round(tokens.nval));
                            theItem.setText(cnt, dthrid.toString());
                            theViewer.addUpperError(dtime, dthrid, vrname);
                            cnt++;
                        } else
                        if(cnt == 6 && tokens.ttype == StreamTokenizer.TT_WORD)
                        {
                            dthrid = null;
                            cnt++;
                        } else
                        if(cnt == 7 && tokens.ttype == StreamTokenizer.TT_EOL)
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
                if(abort)
                    theViewer.showMessage((new StringBuilder("syntax error in validation conjecture file \"")).append(fname).append("\"").toString());
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        for(cnt = 0; cnt < 6; cnt++)
            theTable.getColumn(cnt).pack();

        theViewer.updateOverviewPage();
    }

    private Table theTable;
    private IViewCallback theViewer;
//    private static final String FAIL = "  FAIL";
//    private static final String PASS = "  PASS";
    static final boolean $assertionsDisabled = false; //!org/overturetool/tracefile/viewer/ValidationTable.desiredAssertionStatus();



}