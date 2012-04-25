// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:48
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstQuotedType.java

package org.overturetool.tools.astgen.vdm;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstType, AstVisitor

public class AstQuotedType extends AstType
{

    public AstQuotedType()
        throws CGException
    {
        name = null;
    }

    public AstQuotedType(String pname)
        throws CGException
    {
        name = null;
        name = UTIL.ConvertToString(UTIL.clone(pname));
    }

    public String getName()
        throws CGException
    {
        return stringUpper(stripUnderscore(name));
    }

    public void accept(AstVisitor pv)
        throws CGException
    {
        pv.visit(this);
    }

    public Boolean isQuotedType()
        throws CGException
    {
        return new Boolean(true);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private String name;

}