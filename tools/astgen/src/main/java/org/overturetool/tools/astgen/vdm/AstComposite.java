// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:46
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstComposite.java

package org.overturetool.tools.astgen.vdm;

import java.util.Vector;
import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstCore, AstVisitor
@SuppressWarnings("all")
public class AstComposite extends AstCore
{

    public AstComposite()
        throws CGException
    {
        name = null;
        fields = null;
        adts = null;
    }

    public AstComposite(String pname, Vector pfields)
        throws CGException
    {
        name = null;
        fields = null;
        adts = null;
        name = UTIL.ConvertToString(UTIL.clone(pname));
        fields = (Vector)UTIL.ConvertToList(UTIL.clone(pfields));
        adts = (Boolean)UTIL.clone(new Boolean(false));
    }

    public String getName()
        throws CGException
    {
        return stripUnderscore(name);
    }

    public Vector getFields()
        throws CGException
    {
        return fields;
    }

    public void setAdts(Boolean padts)
        throws CGException
    {
        adts = (Boolean)UTIL.clone(padts);
    }

    public Boolean hasAdts()
        throws CGException
    {
        return adts;
    }

    public void accept(AstVisitor pv)
        throws CGException
    {
        pv.visit(this);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private String name;
    private Vector fields;
    private Boolean adts;

}