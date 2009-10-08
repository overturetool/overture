// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:47
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstCore.java

package org.overturetool.tools.astgen.vdm;

import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstVisitor

public class AstCore
{

    public AstCore()
        throws CGException
    {
    }

    public Character toUpper(Character pch)
        throws CGException
    {
        Character varRes_2 = null;
        Boolean cond_4 = null;
        HashSet var2_6 = new HashSet();
        HashSet set_8 = new HashSet();
        for(Enumeration enm_9 = ((Vector)UTIL.ConvertToList(lower)).elements(); enm_9.hasMoreElements(); set_8.add(enm_9.nextElement()));
        var2_6 = set_8;
        cond_4 = new Boolean(var2_6.contains(pch));
        if(cond_4.booleanValue())
        {
            HashSet tmpSet_10 = new HashSet();
            HashSet riseq_15 = new HashSet();
            int max_16 = lower.length();
            for(int i_17 = 1; i_17 <= max_16; i_17++)
                riseq_15.add(new Integer(i_17));

            tmpSet_10 = riseq_15;
            Integer i = null;
            boolean succ_12 = false;
            Boolean tmpB_18;
            for(Iterator enm_23 = tmpSet_10.iterator(); enm_23.hasNext() && !succ_12; succ_12 = tmpB_18.booleanValue())
            {
                Integer tmpElem_13 = UTIL.NumberToInt(enm_23.next());
                succ_12 = true;
                i = tmpElem_13;
                tmpB_18 = null;
                Character var1_19 = null;
                if(1 <= i.intValue() && i.intValue() <= lower.length())
                    var1_19 = new Character(lower.charAt(i.intValue() - 1));
                else
                    UTIL.RunTime("Run-Time Error:Illegal index");
                tmpB_18 = new Boolean(UTIL.equals(var1_19, pch));
            }

            if(!succ_12)
                UTIL.RunTime("Run-Time Error:The binding environment was empty");
            if(1 <= i.intValue() && i.intValue() <= upper.length())
                varRes_2 = new Character(upper.charAt(i.intValue() - 1));
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
        } else
        {
            varRes_2 = pch;
        }
        return varRes_2;
    }

    public String stripUnderscore(String pstr)
        throws CGException
    {
        String str = UTIL.ConvertToString(new String());
        Integer cnt = new Integer(1);
        Boolean up = new Boolean(true);
        while((new Boolean(cnt.intValue() <= (new Integer(pstr.length())).intValue())).booleanValue()) 
        {
            Boolean cond_6 = null;
            Character var1_7 = null;
            if(1 <= cnt.intValue() && cnt.intValue() <= pstr.length())
                var1_7 = new Character(pstr.charAt(cnt.intValue() - 1));
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
            cond_6 = new Boolean(UTIL.equals(var1_7, new Character('_')));
            if(cond_6.booleanValue())
            {
                up = (Boolean)UTIL.clone(new Boolean(true));
                cnt = UTIL.NumberToInt(UTIL.clone(new Integer(cnt.intValue() + (new Integer(1)).intValue())));
            } else
            {
                if(up.booleanValue())
                {
                    Character e_17 = null;
                    Character par_18 = null;
                    if(1 <= cnt.intValue() && cnt.intValue() <= pstr.length())
                        par_18 = new Character(pstr.charAt(cnt.intValue() - 1));
                    else
                        UTIL.RunTime("Run-Time Error:Illegal index");
                    e_17 = toUpper(par_18);
                    str = (new StringBuilder(String.valueOf(str))).append(e_17).toString();
                } else
                {
                    Character e_13 = null;
                    if(1 <= cnt.intValue() && cnt.intValue() <= pstr.length())
                        e_13 = new Character(pstr.charAt(cnt.intValue() - 1));
                    else
                        UTIL.RunTime("Run-Time Error:Illegal index");
                    str = (new StringBuilder(String.valueOf(str))).append(e_13).toString();
                }
                cnt = UTIL.NumberToInt(UTIL.clone(new Integer(cnt.intValue() + (new Integer(1)).intValue())));
                up = (Boolean)UTIL.clone(new Boolean(false));
            }
        }
        return str;
    }

    public String stringUpper(String pstr)
        throws CGException
    {
        String str = UTIL.ConvertToString(new String());
        Character ch = null;
        for(Iterator enm_10 = ((Vector)UTIL.ConvertToList(pstr)).iterator(); enm_10.hasNext();)
        {
            Character elem_3 = (Character)enm_10.next();
            ch = elem_3;
            str = (new StringBuilder(String.valueOf(str))).append(toUpper(ch)).toString();
        }

        return str;
    }

    public void accept(AstVisitor astvisitor)
        throws CGException
    {
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private static final String lower = new String("abcdefghijklmnopqrstuvwxyz");
    private static final String upper = new String("ABCDEFGHIJKLMNOPQRSTUVWXYZ");

}