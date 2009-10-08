// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:14
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TracefileParser.java

package org.overture.ide.plugins.showtrace.viewer;

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