// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   VDMSpecException.java

package jp.co.csk.vdm.toolbox.VDM;


// Referenced classes of package jp.co.csk.vdm.toolbox.VDM:
//            CGException

public class VDMSpecException extends CGException
{
	/**
	 * Overture Tool
	 */
	private static final long serialVersionUID = 8373799002740093350L;
	
    public VDMSpecException()
    {
        super("VDMSpecException");
        value = null;
    }

    public VDMSpecException(Object obj)
    {
        super("VDMSpecException: " + obj.toString());
        value = null;
        value = obj;
    }

    public Object getValue()
    {
        return value;
    }

    private Object value;
}