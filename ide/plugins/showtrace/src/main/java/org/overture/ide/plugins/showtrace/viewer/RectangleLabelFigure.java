// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   RectangleLabelFigure.java

package org.overture.ide.plugins.showtrace.viewer;

import org.eclipse.draw2d.*;

public class RectangleLabelFigure extends RectangleFigure
{

    public RectangleLabelFigure(Label pLabel)
    {
        theLabel = null;
        theLabel = pLabel;
        ToolbarLayout theRectLayout = new ToolbarLayout();
        theRectLayout.setMinorAlignment(0);
        setLayoutManager(theRectLayout);
        add(new Label());
        add(theLabel);
    }

    public void setSize(Long x1, Long x2)
    {
        setSize(x1.intValue(), x2.intValue());
    }

    public void setSolid()
    {
        setLineStyle(1);
    }

    public void setDot()
    {
        setLineStyle(3);
    }

    public void setDash()
    {
        setLineStyle(2);
    }

    public void setDashDot()
    {
        setLineStyle(4);
    }

    public void setDashDotDot()
    {
        setLineStyle(5);
    }

    private Label theLabel;
}