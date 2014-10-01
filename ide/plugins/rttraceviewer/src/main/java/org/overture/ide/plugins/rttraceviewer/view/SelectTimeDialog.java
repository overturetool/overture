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


import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class SelectTimeDialog extends TitleAreaDialog
{

   
	public SelectTimeDialog(Shell parentShell, List<Long> pTimes)
    {
        super(parentShell);
        currentTime = 0L;
        theTimes = null;
        theCombo = null;
        selectedTime = 0L;
        theTimes = pTimes;
    }

    public SelectTimeDialog(Shell parentShell, List<Long> pTimes, long pcurTime)
    {
        super(parentShell);
        currentTime = 0L;
        theTimes = null;
        theCombo = null;
        selectedTime = 0L;
        theTimes = pTimes;
        currentTime = pcurTime;
    }

    @Override
	public void create()
    {
        super.create();
        setTitle("Select time");
        setMessage("Select the appropriate starting time for the execution overview");
    }

    @Override
	protected Control createDialogArea(Composite parent)
    {
        Composite area = new Composite(parent, 0);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 15;
        layout.marginHeight = 20;
        layout.horizontalSpacing = 15;
        area.setLayout(layout);
        Label theLabel = new Label(area, 16384);
        theLabel.setText("Draw diagram from");
        theCombo = new Combo(area, 2060);
        GridData gd = new GridData();
        gd.widthHint = 200;
        theCombo.setLayoutData(gd);
        theCombo.addSelectionListener(new SelectionAdapter() {

            @Override
			public void widgetSelected(SelectionEvent e)
            {
                int idx = theCombo.getSelectionIndex();
                if(idx >= 0)
                {
                    Long theValue = (Long)theTimes.get(idx);
                    selectedTime = theValue.longValue();
                } else
                {
                    selectedTime = currentTime;
                }
            }

        });
        int cnt = 0;
        int idx = 0;

        for(Long t : theTimes) 
        {
        	theCombo.add(t.toString());
        	
            if(t.longValue() == currentTime)
            {
                idx = cnt;
            }
            
            cnt++;
        }
        
        theCombo.select(idx);
        return area;
    }

    private long currentTime;
    private List<Long> theTimes;
    private Combo theCombo;
    public long selectedTime;



}
