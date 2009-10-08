// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstMapType.java

package org.overturetool.tools.astgen.vdm;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstType, AstVisitor

public class AstMapType extends AstType
{

    public AstMapType()
        throws CGException
    {
        fromtp = null;
        totp = null;
    }

    public AstMapType(AstType pfrtp, AstType ptotp)
        throws CGException
    {
        fromtp = null;
        totp = null;
        fromtp = (AstType)UTIL.clone(pfrtp);
        totp = (AstType)UTIL.clone(ptotp);
    }

    public AstType getDomType()
        throws CGException
    {
        return fromtp;
    }

    public AstType getRngType()
        throws CGException
    {
        return totp;
    }

    public void accept(AstVisitor pv)
        throws CGException
    {
        pv.visit(this);
    }

    public Boolean isMapType()
        throws CGException
    {
        return new Boolean(true);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private AstType fromtp;
    private AstType totp;

}