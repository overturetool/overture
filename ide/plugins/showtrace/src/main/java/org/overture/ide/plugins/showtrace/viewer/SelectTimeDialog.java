// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   SelectTimeDialog.java

package org.overture.ide.plugins.showtrace.viewer;

import java.util.Iterator;
import java.util.Vector;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
@SuppressWarnings("rawtypes")
public class SelectTimeDialog extends TitleAreaDialog
{

   
	public SelectTimeDialog(Shell parentShell, Vector pTimes)
    {
        super(parentShell);
        currentTime = 0L;
        theTimes = null;
        theCombo = null;
        selectedTime = 0L;
        theTimes = pTimes;
    }

    public SelectTimeDialog(Shell parentShell, Vector pTimes, long pcurTime)
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

//            final SelectTimeDialog this$0;
//
//            
//            {
//                this$0 = SelectTimeDialog.this;
//                super();
//            }
        });
        Iterator iter = theTimes.iterator();
        int cnt = 0;
        int idx = 0;
        while(iter.hasNext()) 
        {
            Long theValue = (Long)iter.next();
            if(theValue.longValue() == currentTime)
                idx = cnt;
            theCombo.add(theValue.toString());
            cnt++;
        }
        theCombo.select(idx);
        return area;
    }

    private long currentTime;
    private Vector theTimes;
    private Combo theCombo;
    public long selectedTime;



}