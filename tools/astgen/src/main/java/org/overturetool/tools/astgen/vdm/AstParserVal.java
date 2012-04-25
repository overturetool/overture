// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:48
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstParserVal.java

package org.overturetool.tools.astgen.vdm;


public class AstParserVal
{

    public AstParserVal()
    {
    }

    public AstParserVal(int val)
    {
        ival = val;
    }

    public AstParserVal(double val)
    {
        dval = val;
    }

    public AstParserVal(String val)
    {
        sval = val;
    }

    public AstParserVal(Object val)
    {
        obj = val;
    }

    public int ival;
    public double dval;
    public String sval;
    public Object obj;
}