// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:48
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstShorthand.java

package org.overturetool.tools.astgen.vdm;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstCore, AstType, AstVisitor

public class AstShorthand extends AstCore
{

    public AstShorthand()
        throws CGException
    {
        lhs = null;
        rhs = null;
        isqt = null;
        istn = null;
    }

    public AstShorthand(String plhs, AstType prhs)
        throws CGException
    {
        lhs = null;
        rhs = null;
        isqt = null;
        istn = null;
        lhs = UTIL.ConvertToString(UTIL.clone(plhs));
        rhs = (AstType)UTIL.clone(prhs);
        isqt = (Boolean)UTIL.clone(new Boolean(false));
        istn = (Boolean)UTIL.clone(new Boolean(false));
    }

    public String getName()
        throws CGException
    {
        return stripUnderscore(lhs);
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

    public void setQuotedTypeUnion()
        throws CGException
    {
        isqt = (Boolean)UTIL.clone(new Boolean(true));
    }

    public Boolean isQuotedTypeUnion()
        throws CGException
    {
        return isqt;
    }

    public void setTypeNameUnion()
        throws CGException
    {
        istn = (Boolean)UTIL.clone(new Boolean(true));
    }

    public Boolean isTypeNameUnion()
        throws CGException
    {
        return istn;
    }

    public void accept(AstVisitor pv)
        throws CGException
    {
        pv.visit(this);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private String lhs;
    private AstType rhs;
    private Boolean isqt;
    private Boolean istn;

}