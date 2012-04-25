// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:18:22
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   OmlNode.java

package org.overturetool.traceviewer.ast.imp;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlContextInfo;
import org.overturetool.traceviewer.ast.itf.IOmlLexem;
import org.overturetool.traceviewer.ast.itf.IOmlNode;
import org.overturetool.traceviewer.ast.itf.IOmlVisitor;

public class OmlNode
    implements IOmlNode
{

    public OmlNode()
        throws CGException
    {
        ivInfo = new HashMap();
        ivLeftMostLexemIndex = null;
        ivRightMostLexemIndex = null;
        try
        {
            ivInfo = new HashMap();
            ivLeftMostLexemIndex = null;
            ivRightMostLexemIndex = null;
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public String identity()
        throws CGException
    {
        return new String("Node");
    }

    public void accept(IOmlVisitor pVisitor)
        throws CGException
    {
        pVisitor.visitNode(this);
    }

    public IOmlContextInfo getContextInfo(Long pci)
        throws CGException
    {
        if(!pre_getContextInfo(pci).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getContextInfo");
        return (IOmlContextInfo)ivInfo.get(pci);
    }

    public Boolean pre_getContextInfo(Long pci)
        throws CGException
    {
        Boolean varRes_2 = null;
        varRes_2 = new Boolean(ivInfo.containsKey(pci));
        return varRes_2;
    }

    public Long getContextInfoCount()
        throws CGException
    {
        Long rexpr_1 = null;
        HashSet unArg_2 = new HashSet();
        unArg_2.clear();
        unArg_2.addAll(ivInfo.keySet());
        rexpr_1 = new Long(unArg_2.size());
        return rexpr_1;
    }

    public Long addContextInfo(IOmlContextInfo pci)
        throws CGException
    {
        Long res = null;
        Long var1_2 = null;
        HashSet unArg_3 = new HashSet();
        unArg_3.clear();
        unArg_3.addAll(ivInfo.keySet());
        var1_2 = new Long(unArg_3.size());
        res = new Long(var1_2.intValue() + (new Long(1L)).intValue());
        HashMap rhs_6 = new HashMap();
        HashMap var2_8 = new HashMap();
        var2_8 = new HashMap();
        var2_8.put(res, pci);
        HashMap m1_15 = (HashMap)ivInfo.clone();
        HashMap m2_16 = var2_8;
        HashSet com_11 = new HashSet();
        com_11.addAll(m1_15.keySet());
        com_11.retainAll(m2_16.keySet());
        boolean all_applies_12 = true;
        Object d_13;
        for(Iterator bb_14 = com_11.iterator(); bb_14.hasNext() && all_applies_12; all_applies_12 = m1_15.get(d_13).equals(m2_16.get(d_13)))
            d_13 = bb_14.next();

        if(!all_applies_12)
            UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
        m1_15.putAll(m2_16);
        rhs_6 = m1_15;
        ivInfo = (HashMap)UTIL.clone(rhs_6);
        return res;
    }

    public IOmlNode setLexemIndex(Long pli)
        throws CGException
    {
        if((new Boolean(UTIL.equals(ivLeftMostLexemIndex, null))).booleanValue())
            ivLeftMostLexemIndex = UTIL.NumberToLong(UTIL.clone(pli));
        else
        if((new Boolean(ivLeftMostLexemIndex.intValue() > pli.intValue())).booleanValue())
            ivLeftMostLexemIndex = UTIL.NumberToLong(UTIL.clone(pli));
        if((new Boolean(UTIL.equals(ivRightMostLexemIndex, null))).booleanValue())
            ivRightMostLexemIndex = UTIL.NumberToLong(UTIL.clone(pli));
        else
        if((new Boolean(ivRightMostLexemIndex.intValue() < pli.intValue())).booleanValue())
            ivRightMostLexemIndex = UTIL.NumberToLong(UTIL.clone(pli));
        return this;
    }

    public IOmlNode setLexem(IOmlLexem plexem)
        throws CGException
    {
        IOmlNode rexpr_2 = null;
        Long par_3 = null;
        par_3 = plexem.getIndex();
        rexpr_2 = setLexemIndex(par_3);
        return rexpr_2;
    }

    public Boolean hasPositionInfo()
        throws CGException
    {
        Boolean rexpr_1 = null;
        if((rexpr_1 = new Boolean(UTIL.IsInteger(ivLeftMostLexemIndex) && ivLeftMostLexemIndex.intValue() >= 0)).booleanValue())
            rexpr_1 = new Boolean(UTIL.IsInteger(ivRightMostLexemIndex) && ivRightMostLexemIndex.intValue() >= 0);
        return rexpr_1;
    }

    public Long getLeftMostLexemIndex()
        throws CGException
    {
        return ivLeftMostLexemIndex;
    }

    public Long getRightMostLexemIndex()
        throws CGException
    {
        return ivRightMostLexemIndex;
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private HashMap ivInfo;
    private Long ivLeftMostLexemIndex;
    private Long ivRightMostLexemIndex;
    public static final String prefix = new String("Oml");

}