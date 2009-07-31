// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:12
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Line.java

package org.overturetool.eclipse.plugins.showtrace.viewer;

import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.geometry.Point;

public class Line extends Polyline
{

    public Line(Long px1, Long py1, Long px2, Long py2)
    {
        addPoint(new Point(px1.longValue(), py1.longValue()));
        addPoint(new Point(px2.longValue(), py2.longValue()));
    }

    public void setLineWidth(Long width)
    {
        setLineWidth(width.intValue());
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
}