// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Token.java

package jp.co.csk.vdm.toolbox.VDM;


// Referenced classes of package jp.co.csk.vdm.toolbox.VDM:
//            UTIL

public class Token
{

    public Token(Object obj)
    {
        vdmValue = obj;
    }

    public Object GetValue()
    {
        return vdmValue;
    }

    public boolean equals(Object obj)
    {
        if(!(obj instanceof Token))
            return false;
        else
            return UTIL.equals(vdmValue, ((Token)obj).vdmValue);
    }

    public String toString()
    {
        return "mk_token(" + UTIL.toString(vdmValue) + ")";
    }

    Object vdmValue;
}