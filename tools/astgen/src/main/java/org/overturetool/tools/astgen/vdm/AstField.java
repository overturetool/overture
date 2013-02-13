// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstField.java

package org.overturetool.tools.astgen.vdm;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstCore, AstType, AstVisitor

public class AstField extends AstCore
{

    public AstField()
        throws CGException
    {
        lhs = null;
        rhs = null;
    }

    public AstField(String plhs, AstType prhs)
        throws CGException
    {
        lhs = null;
        rhs = null;
        lhs = UTIL.ConvertToString(UTIL.clone(plhs));
        rhs = (AstType)UTIL.clone(prhs);
    }

    public String getName()
        throws CGException
    {
        return stripUnderscore(lhs);
    }

    public String getRawName()
        throws CGException
    {
        return lhs;
    }

    public AstType getType()
        throws CGException
    {
        return rhs;
    }

    public void setType(AstType ptp)
        throws CGException
    {
        rhs = (AstType)UTIL.clone(ptp);
    }

    public void accept(AstVisitor pv)
        throws CGException
    {
        pv.visit(this);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private String lhs;
    private AstType rhs;

}