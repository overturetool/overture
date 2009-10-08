// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstDefinitions.java

package org.overturetool.tools.astgen.vdm;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstCore, AstComposite, AstShorthand, AstType, 
//            AstVisitor

public class AstDefinitions extends AstCore
{

    public AstDefinitions()
        throws CGException
    {
        composites = new HashMap();
        shorthands = new HashMap();
        inherit = new HashMap();
        prefix = null;
        vdm_package = null;
        directory = null;
        top = null;
        composites = (HashMap)UTIL.clone(new HashMap());
        shorthands = (HashMap)UTIL.clone(new HashMap());
        inherit = (HashMap)UTIL.clone(new HashMap());
        vdm_package = (Vector)UTIL.ConvertToList(UTIL.clone(new Vector()));
        directory = UTIL.ConvertToString(UTIL.clone(new Vector()));
        prefix = UTIL.ConvertToString(UTIL.clone(new Vector()));
        top = (Vector)UTIL.ConvertToList(UTIL.clone(new Vector()));
    }

    public void addComposite(AstComposite pcomposite)
        throws CGException
    {
        Boolean cond_2 = null;
        String var1_3 = null;
        var1_3 = pcomposite.getName();
        HashSet var2_4 = new HashSet();
        var2_4.clear();
        var2_4.addAll((Collection)composites.keySet());
        cond_2 = new Boolean(!var2_4.contains(var1_3));
        if(cond_2.booleanValue())
        {
            HashMap rhs_6 = new HashMap();
            HashMap var2_8 = new HashMap();
            String tmpVar1_9 = null;
            tmpVar1_9 = pcomposite.getName();
            var2_8 = new HashMap();
            var2_8.put(tmpVar1_9, pcomposite);
            HashMap m1_15 = (HashMap)composites.clone();
            HashMap m2_16 = var2_8;
            HashSet com_11 = new HashSet();
            com_11.addAll((Collection)m1_15.keySet());
            com_11.retainAll((Collection)m2_16.keySet());
            boolean all_applies_12 = true;
            Object d_13;
            for(Iterator bb_14 = com_11.iterator(); bb_14.hasNext() && all_applies_12; all_applies_12 = m1_15.get(d_13).equals(m2_16.get(d_13)))
                d_13 = bb_14.next();

            if(!all_applies_12)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_15.putAll(m2_16);
            rhs_6 = m1_15;
            composites = (HashMap)UTIL.clone(rhs_6);
        }
    }

    public void addShorthand(AstShorthand pshorthand)
        throws CGException
    {
        Boolean cond_2 = null;
        String var1_3 = null;
        var1_3 = pshorthand.getName();
        HashSet var2_4 = new HashSet();
        var2_4.clear();
        var2_4.addAll((Collection)shorthands.keySet());
        cond_2 = new Boolean(!var2_4.contains(var1_3));
        if(cond_2.booleanValue())
        {
            HashMap rhs_6 = new HashMap();
            HashMap var2_8 = new HashMap();
            String tmpVar1_9 = null;
            tmpVar1_9 = pshorthand.getName();
            var2_8 = new HashMap();
            var2_8.put(tmpVar1_9, pshorthand);
            HashMap m1_15 = (HashMap)shorthands.clone();
            HashMap m2_16 = var2_8;
            HashSet com_11 = new HashSet();
            com_11.addAll((Collection)m1_15.keySet());
            com_11.retainAll((Collection)m2_16.keySet());
            boolean all_applies_12 = true;
            Object d_13;
            for(Iterator bb_14 = com_11.iterator(); bb_14.hasNext() && all_applies_12; all_applies_12 = m1_15.get(d_13).equals(m2_16.get(d_13)))
                d_13 = bb_14.next();

            if(!all_applies_12)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_15.putAll(m2_16);
            rhs_6 = m1_15;
            shorthands = (HashMap)UTIL.clone(rhs_6);
        }
    }

    public void addInherit(String pkey, String pval)
        throws CGException
    {
        Boolean cond_3 = null;
        HashSet var2_5 = new HashSet();
        var2_5.clear();
        var2_5.addAll((Collection)inherit.keySet());
        cond_3 = new Boolean(!var2_5.contains(pkey));
        if(cond_3.booleanValue())
        {
            HashMap rhs_7 = new HashMap();
            HashMap var2_9 = new HashMap();
            var2_9 = new HashMap();
            var2_9.put(pkey, pval);
            HashMap m1_16 = (HashMap)inherit.clone();
            HashMap m2_17 = var2_9;
            HashSet com_12 = new HashSet();
            com_12.addAll((Collection)m1_16.keySet());
            com_12.retainAll((Collection)m2_17.keySet());
            boolean all_applies_13 = true;
            Object d_14;
            for(Iterator bb_15 = com_12.iterator(); bb_15.hasNext() && all_applies_13; all_applies_13 = m1_16.get(d_14).equals(m2_17.get(d_14)))
                d_14 = bb_15.next();

            if(!all_applies_13)
                UTIL.RunTime("Run-Time Error:Map Merge: Incompatible maps");
            m1_16.putAll(m2_17);
            rhs_7 = m1_16;
            inherit = (HashMap)UTIL.clone(rhs_7);
        }
    }

    public String getInherit(String pkey)
        throws CGException
    {
        if(!pre_getInherit(pkey).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getInherit");
        return UTIL.ConvertToString(inherit.get(pkey));
    }

    public Boolean pre_getInherit(String pkey)
        throws CGException
    {
        return hasInherit(pkey);
    }

    public String getInheritPrefix(String pkey)
        throws CGException
    {
        String rexpr_2 = null;
        rexpr_2 = getPrefix().concat(getInherit(pkey));
        return rexpr_2;
    }

    public Boolean hasInherit(String pkey)
        throws CGException
    {
        Boolean rexpr_2 = null;
        rexpr_2 = new Boolean(inherit.containsKey(pkey));
        return rexpr_2;
    }

    public void setPrefix(String pstr)
        throws CGException
    {
        prefix = UTIL.ConvertToString(UTIL.clone(pstr));
    }

    public String getPrefix()
        throws CGException
    {
        return prefix;
    }

    public void setPackage(Vector psstr)
        throws CGException
    {
        vdm_package = (Vector)UTIL.ConvertToList(UTIL.clone(psstr));
    }

    public Vector getRawPackage()
        throws CGException
    {
        return vdm_package;
    }

    public String getPackage()
        throws CGException
    {
        String str = UTIL.ConvertToString(new String());
        String pstr = null;
        for(Iterator enm_15 = vdm_package.iterator(); enm_15.hasNext();)
        {
            String elem_2 = UTIL.ConvertToString(enm_15.next());
            pstr = elem_2;
            if((new Boolean(UTIL.equals(str, new Vector()))).booleanValue())
            {
                str = UTIL.ConvertToString(UTIL.clone(pstr));
            } else
            {
                String rhs_8 = null;
                String var1_9 = null;
                var1_9 = str.concat(new String("."));
                rhs_8 = var1_9.concat(pstr);
                str = UTIL.ConvertToString(UTIL.clone(rhs_8));
            }
        }

        return str;
    }

    public void setDirectory(String pdir)
        throws CGException
    {
        directory = UTIL.ConvertToString(UTIL.clone(pdir));
    }

    public String getDirectory()
        throws CGException
    {
        return directory;
    }

    public HashSet getShorthands()
        throws CGException
    {
        HashSet rexpr_1 = new HashSet();
        rexpr_1.clear();
        rexpr_1.addAll((Collection)shorthands.keySet());
        return rexpr_1;
    }

    public HashSet getUnionShorthands()
        throws CGException
    {
        HashSet rexpr_1 = new HashSet();
        HashSet res_s_2 = new HashSet();
        HashSet e_set_10 = new HashSet();
        e_set_10.clear();
        e_set_10.addAll((Collection)shorthands.keySet());
        String id = null;
        for(Iterator enm_13 = e_set_10.iterator(); enm_13.hasNext();)
        {
            String elem_12 = UTIL.ConvertToString(enm_13.next());
            id = elem_12;
            Boolean pred_4 = null;
            AstType obj_5 = null;
            AstShorthand obj_6 = null;
            obj_6 = (AstShorthand)shorthands.get(id);
            obj_5 = obj_6.getType();
            pred_4 = obj_5.isUnionType();
            if(pred_4.booleanValue())
                res_s_2.add(id);
        }

        rexpr_1 = res_s_2;
        return rexpr_1;
    }

    public AstShorthand getShorthand(String pshnm)
        throws CGException
    {
        if(!pre_getShorthand(pshnm).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getShorthand");
        return (AstShorthand)shorthands.get(pshnm);
    }

    public Boolean pre_getShorthand(String pshnm)
        throws CGException
    {
        return isShorthand(pshnm);
    }

    public Boolean isShorthand(String pshnm)
        throws CGException
    {
        Boolean rexpr_2 = null;
        rexpr_2 = new Boolean(shorthands.containsKey(pshnm));
        return rexpr_2;
    }

    public HashSet getComposites()
        throws CGException
    {
        HashSet rexpr_1 = new HashSet();
        rexpr_1.clear();
        rexpr_1.addAll((Collection)composites.keySet());
        return rexpr_1;
    }

    public AstComposite getComposite(String pcpnm)
        throws CGException
    {
        if(!pre_getComposite(pcpnm).booleanValue())
            UTIL.RunTime("Run-Time Error:Precondition failure in getComposite");
        return (AstComposite)composites.get(pcpnm);
    }

    public Boolean pre_getComposite(String pcpnm)
        throws CGException
    {
        return isComposite(pcpnm);
    }

    public Boolean isComposite(String pcpnm)
        throws CGException
    {
        Boolean rexpr_2 = null;
        rexpr_2 = new Boolean(composites.containsKey(pcpnm));
        return rexpr_2;
    }

    public void setTop(Vector ptop)
        throws CGException
    {
        top = (Vector)UTIL.ConvertToList(UTIL.clone(ptop));
    }

    public Vector getTop()
        throws CGException
    {
        return top;
    }

    public void accept(AstVisitor pv)
        throws CGException
    {
        pv.visit(this);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private HashMap composites;
    private HashMap shorthands;
    private HashMap inherit;
    private String prefix;
    private Vector vdm_package;
    private String directory;
    private Vector top;

}