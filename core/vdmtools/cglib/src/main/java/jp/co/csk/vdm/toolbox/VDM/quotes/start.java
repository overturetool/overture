// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 17:00:31
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   start.java

package jp.co.csk.vdm.toolbox.VDM.quotes;


public class start
{

    public start()
    {
        if(hc == 0)
            hc = super.hashCode();
    }

    public int hashCode()
    {
        return hc;
    }

    public boolean equals(Object obj)
    {
        return obj instanceof start;
    }

    public String toString()
    {
        return "<start>";
    }

    private static int hc = 0;

}