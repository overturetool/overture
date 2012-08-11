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
// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TracefileParser.java

package org.overture.ide.plugins.showtraceNextGen.viewer;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.traceviewer.parser.TraceParser;

// Referenced classes of package org.overturetool.tracefile.viewer:
//            TracefileMarker

public class TracefileParser extends TraceParser
{

    public TracefileParser(String arg0, TracefileMarker pmrkrs)
    {
        super(arg0);
        theMarkers = null;
        theMarkers = pmrkrs;
    }

    public TracefileParser(String arg0, String arg1, TracefileMarker pmrkrs)
    {
        super(arg0, arg1);
        theMarkers = null;
        theMarkers = pmrkrs;
    }

    public TracefileParser()
    {
        theMarkers = null;
    }

    public TracefileParser(boolean arg0)
    {
        super(arg0);
        theMarkers = null;
    }

    @Override
	public void yyerror(String msg)
    {
        if(theMarkers != null)
            try
            {
                String theError = (new StringBuilder(String.valueOf(msg))).append(" at line ").append(getLine()).toString();
                theError = (new StringBuilder(String.valueOf(theError))).append(", column ").append(getColumnStart()).append("-").append(getColumnEnd()).toString();
                theMarkers.addError(theError, new Integer(getLine()));
                errors++;
            }
            catch(CGException cge)
            {
                cge.printStackTrace();
            }
    }

    private TracefileMarker theMarkers;
}