// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:34
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   INDEX_OUT_OF_RANGE.java

package jp.co.csk.vdm.toolbox.VDM;


// Referenced classes of package jp.co.csk.vdm.toolbox.VDM:
//            VDMRunTimeException

public class INDEX_OUT_OF_RANGE extends VDMRunTimeException
{

	/**
	 * Overture Tool
	 */
	private static final long serialVersionUID = 8373799002740093350L;

	public INDEX_OUT_OF_RANGE()
    {
        super("Index out of range.");
    }

    public INDEX_OUT_OF_RANGE(String s)
    {
        super(s + ": Index out of range.");
    }
}