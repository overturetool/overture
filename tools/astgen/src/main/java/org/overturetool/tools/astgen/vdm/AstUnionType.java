// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:49
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstUnionType.java

package org.overturetool.tools.astgen.vdm;

import java.util.HashSet;
import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstType, AstVisitor
@SuppressWarnings("all")
public class AstUnionType extends AstType
{

    public AstUnionType()
        throws CGException
    {
        lhs = null;
        rhs = null;
    }

    public AstUnionType(AstType plhs, AstType prhs)
        throws CGException
    {
        lhs = null;
        rhs = null;
        lhs = (AstType)UTIL.clone(plhs);
        rhs = (AstType)UTIL.clone(prhs);
    }

    public AstType getLhs()
        throws CGException
    {
        return lhs;
    }

    public AstType getRhs()
        throws CGException
    {
        return rhs;
    }

    public HashSet flatten()
        throws CGException
    {
        HashSet res = new HashSet();
        Boolean cond_1 = null;
        cond_1 = lhs.isUnionType();
        if(cond_1.booleanValue())
        {
            AstUnionType aut = (AstUnionType)lhs;
            HashSet rhs_4 = new HashSet();
            HashSet var2_6 = new HashSet();
            var2_6 = aut.flatten();
            rhs_4 = (HashSet)res.clone();
            rhs_4.addAll(var2_6);
            res = (HashSet)UTIL.clone(rhs_4);
        } else
        {
            res.add(lhs);
        }
        Boolean cond_7 = null;
        cond_7 = rhs.isUnionType();
        if(cond_7.booleanValue())
        {
            AstUnionType aut = (AstUnionType)rhs;
            HashSet rhs_10 = new HashSet();
            HashSet var2_12 = new HashSet();
            var2_12 = aut.flatten();
            rhs_10 = (HashSet)res.clone();
            rhs_10.addAll(var2_12);
            res = (HashSet)UTIL.clone(rhs_10);
        } else
        {
            res.add(rhs);
        }
        return res;
    }

    public void accept(AstVisitor pv)
        throws CGException
    {
        pv.visit(this);
    }

    public Boolean isUnionType()
        throws CGException
    {
        return new Boolean(true);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private AstType lhs;
    private AstType rhs;

}