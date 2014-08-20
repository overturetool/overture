/*
 * #%~
 * VDM Tools Code Generator library
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
