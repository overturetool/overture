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
package org.overture.ide.plugins.rttraceviewer.draw;

import org.eclipse.draw2d.*;

public class RectangleLabelFigure extends RectangleFigure implements TraceFigure
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

	@Override
	public void dispose()
	{
	}
}
