// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Tuple.java

package jp.co.csk.vdm.toolbox.VDM;

import java.io.Serializable;

// Referenced classes of package jp.co.csk.vdm.toolbox.VDM:
//            INDEX_OUT_OF_RANGE, UTIL

public class Tuple
    implements Cloneable, Serializable
{
	/**
	 * Overture Tool
	 */
	private static final long serialVersionUID = 8373799002740093350L;
	
    public Tuple()
    {
        t = new Object[0];
    }

    public Tuple(int i)
    {
        t = new Object[i];
    }

    public Object clone()
    {
        Tuple tuple = new Tuple(t.length);
        for(int i = 0; i < t.length; i++)
            tuple.t[i] = UTIL.clone(t[i]);

        return tuple;
    }

    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(!(obj instanceof Tuple))
            return false;
        Tuple tuple = (Tuple)obj;
        if(tuple.t.length != t.length)
            return false;
        for(int i = 0; i < t.length; i++)
            if(!UTIL.equals(t[i], tuple.t[i]))
                return false;

        return true;
    }

    public int hashCode()
    {
        int i = 0;
        for(int j = 0; j < Length(); j++)
            if(t[j] != null)
                i += t[j].hashCode();

        return i;
    }

    public Tuple SetField(int i, Object obj)
        throws INDEX_OUT_OF_RANGE
    {
        try
        {
            t[i - 1] = obj;
        }
        catch(ArrayIndexOutOfBoundsException arrayindexoutofboundsexception)
        {
            throw new INDEX_OUT_OF_RANGE("<Tuple.SetField>");
        }
        return this;
    }

    public Object GetField(int i)
        throws INDEX_OUT_OF_RANGE
    {
        Object obj;
        try
        {
            obj = t[i - 1];
        }
        catch(ArrayIndexOutOfBoundsException arrayindexoutofboundsexception)
        {
            throw new INDEX_OUT_OF_RANGE("<Tuple.GetField>");
        }
        return obj;
    }

    public int Length()
    {
        return t.length;
    }

    public String toString()
    {
        String s = new String();
        s = s + "mk_(";
        for(int i = 0; i < t.length; i++)
        {
            s = s + UTIL.toString(t[i]);
            if(i < t.length - 1)
                s = s + ",";
        }

        s = s + ")";
        return s;
    }

    protected Object t[];
}