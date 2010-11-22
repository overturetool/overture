// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:17:42
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TraceParserVal.java

package org.overturetool.traceviewer.parser;


public class TraceParserVal
{

    public TraceParserVal()
    {
    }

    public TraceParserVal(long val)
    {
        ival = val;
    }

    public TraceParserVal(double val)
    {
        dval = val;
    }

    public TraceParserVal(String val)
    {
        sval = val;
    }

    public TraceParserVal(Object val)
    {
        obj = val;
    }

    public long ival;
    public double dval;
    public String sval;
    public Object obj;
}