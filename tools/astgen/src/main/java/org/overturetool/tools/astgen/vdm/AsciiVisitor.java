// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:46
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AsciiVisitor.java

package org.overturetool.tools.astgen.vdm;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstVisitor, AstDefinitions, AstComposite, AstShorthand, 
//            AstType, AstField, AstTypeName, AstQuotedType, 
//            AstUnionType, AstSeqOfType, AstSetOfType, AstOptionalType, 
//            AstMapType
@SuppressWarnings("all")
public class AsciiVisitor extends AstVisitor
{

    public AsciiVisitor()
        throws CGException
    {
        res = null;
        try
        {
            res = UTIL.ConvertToString(new String());
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    private void pushLevel()
        throws CGException
    {
        level = UTIL.NumberToInt(UTIL.clone(new Integer(level.intValue() + (new Integer(2)).intValue())));
    }

    private void popLevel()
        throws CGException
    {
        level = UTIL.NumberToInt(UTIL.clone(new Integer(level.intValue() - (new Integer(2)).intValue())));
    }

    private String nl()
        throws CGException
    {
        String str = new String("\n");
        for(int ilb_3 = 1; ilb_3 <= level.intValue(); ilb_3++)
        {
            Integer i = new Integer(ilb_3);
            String rhs_4 = null;
            rhs_4 = str.concat(new String(" "));
            str = UTIL.ConvertToString(UTIL.clone(rhs_4));
        }

        return str;
    }

    public void visit(AstDefinitions ad)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstDefinitions(["));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        Boolean cond_5 = null;
        Integer var1_6 = null;
        HashSet unArg_7 = new HashSet();
        unArg_7 = ad.getComposites();
        var1_6 = new Integer(unArg_7.size());
        cond_5 = new Boolean(var1_6.intValue() > (new Integer(0)).intValue());
        if(cond_5.booleanValue())
        {
            HashSet cmps = new HashSet();
            cmps = ad.getComposites();
            pushLevel();
            while((new Boolean((new Integer(cmps.size())).intValue() > (new Integer(0)).intValue())).booleanValue()) 
            {
                String rhs_14 = null;
                rhs_14 = res.concat(nl());
                res = UTIL.ConvertToString(UTIL.clone(rhs_14));
                HashSet tmpSet_17 = new HashSet();
                tmpSet_17 = cmps;
                String cmp = null;
                boolean succ_19 = false;
                for(Iterator enm_21 = tmpSet_17.iterator(); enm_21.hasNext() && !succ_19;)
                {
                    String tmpElem_20 = UTIL.ConvertToString(enm_21.next());
                    succ_19 = true;
                    cmp = tmpElem_20;
                }

                if(!succ_19)
                    UTIL.RunTime("Run-Time Error:The binding environment was empty");
                AstComposite obj_23 = null;
                obj_23 = ad.getComposite(cmp);
                obj_23.accept(this);
                HashSet rhs_26 = new HashSet();
                HashSet var2_28 = new HashSet();
                var2_28 = new HashSet();
                var2_28.add(cmp);
                rhs_26 = (HashSet)cmps.clone();
                rhs_26.removeAll(var2_28);
                cmps = (HashSet)UTIL.clone(rhs_26);
                if((new Boolean((new Integer(cmps.size())).intValue() > (new Integer(1)).intValue())).booleanValue())
                {
                    String rhs_34 = null;
                    rhs_34 = res.concat(new String(","));
                    res = UTIL.ConvertToString(UTIL.clone(rhs_34));
                }
            }
            popLevel();
            String rhs_38 = null;
            rhs_38 = res.concat(nl());
            res = UTIL.ConvertToString(UTIL.clone(rhs_38));
        }
        String rhs_41 = null;
        rhs_41 = res.concat(new String("],["));
        res = UTIL.ConvertToString(UTIL.clone(rhs_41));
        Boolean cond_44 = null;
        Integer var1_45 = null;
        HashSet unArg_46 = new HashSet();
        unArg_46 = ad.getShorthands();
        var1_45 = new Integer(unArg_46.size());
        cond_44 = new Boolean(var1_45.intValue() > (new Integer(0)).intValue());
        if(cond_44.booleanValue())
        {
            HashSet shs = new HashSet();
            shs = ad.getShorthands();
            pushLevel();
            while((new Boolean((new Integer(shs.size())).intValue() > (new Integer(0)).intValue())).booleanValue()) 
            {
                String rhs_53 = null;
                rhs_53 = res.concat(nl());
                res = UTIL.ConvertToString(UTIL.clone(rhs_53));
                HashSet tmpSet_56 = new HashSet();
                tmpSet_56 = shs;
                String sh = null;
                boolean succ_58 = false;
                for(Iterator enm_60 = tmpSet_56.iterator(); enm_60.hasNext() && !succ_58;)
                {
                    String tmpElem_59 = UTIL.ConvertToString(enm_60.next());
                    succ_58 = true;
                    sh = tmpElem_59;
                }

                if(!succ_58)
                    UTIL.RunTime("Run-Time Error:The binding environment was empty");
                AstShorthand obj_62 = null;
                obj_62 = ad.getShorthand(sh);
                obj_62.accept(this);
                HashSet rhs_65 = new HashSet();
                HashSet var2_67 = new HashSet();
                var2_67 = new HashSet();
                var2_67.add(sh);
                rhs_65 = (HashSet)shs.clone();
                rhs_65.removeAll(var2_67);
                shs = (HashSet)UTIL.clone(rhs_65);
                if((new Boolean((new Integer(shs.size())).intValue() > (new Integer(1)).intValue())).booleanValue())
                {
                    String rhs_73 = null;
                    rhs_73 = res.concat(new String(","));
                    res = UTIL.ConvertToString(UTIL.clone(rhs_73));
                }
            }
            popLevel();
            String rhs_77 = null;
            rhs_77 = res.concat(nl());
            res = UTIL.ConvertToString(UTIL.clone(rhs_77));
        }
        String rhs_80 = null;
        rhs_80 = res.concat(new String("])\n"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_80));
        String rhs_83 = null;
        String var1_84 = null;
        String var1_85 = null;
        var1_85 = res.concat(new String("prefix = \""));
        String var2_88 = null;
        var2_88 = ad.getPrefix();
        var1_84 = var1_85.concat(var2_88);
        rhs_83 = var1_84.concat(new String("\"\n"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_83));
        String rhs_90 = null;
        String var1_91 = null;
        String var1_92 = null;
        var1_92 = res.concat(new String("package = \""));
        String var2_95 = null;
        var2_95 = ad.getPackage();
        var1_91 = var1_92.concat(var2_95);
        rhs_90 = var1_91.concat(new String("\"\n"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_90));
        String rhs_97 = null;
        String var1_98 = null;
        String var1_99 = null;
        var1_99 = res.concat(new String("directory = \""));
        String var2_102 = null;
        var2_102 = ad.getDirectory();
        var1_98 = var1_99.concat(var2_102);
        rhs_97 = var1_98.concat(new String("\""));
        res = UTIL.ConvertToString(UTIL.clone(rhs_97));
    }

    public void visit(AstShorthand ash)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstShorthand("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        pushLevel();
        String rhs_6 = null;
        String var1_7 = null;
        String var1_8 = null;
        String var1_9 = null;
        var1_9 = res.concat(new String("\""));
        String var2_12 = null;
        var2_12 = ash.getName();
        var1_8 = var1_9.concat(var2_12);
        var1_7 = var1_8.concat(new String("\","));
        rhs_6 = var1_7.concat(nl());
        res = UTIL.ConvertToString(UTIL.clone(rhs_6));
        AstType obj_16 = null;
        obj_16 = ash.getType();
        obj_16.accept(this);
        String rhs_18 = null;
        rhs_18 = res.concat(new String(")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_18));
        popLevel();
    }

    public void visit(AstComposite ac)
        throws CGException
    {
        Vector fields = null;
        fields = ac.getFields();
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstComposite("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        String rhs_5 = null;
        String var1_6 = null;
        String var1_7 = null;
        var1_7 = res.concat(new String("\""));
        String var2_10 = null;
        var2_10 = ac.getName();
        var1_6 = var1_7.concat(var2_10);
        rhs_5 = var1_6.concat(new String("\",["));
        res = UTIL.ConvertToString(UTIL.clone(rhs_5));
        if((new Boolean((new Integer(fields.size())).intValue() > (new Integer(0)).intValue())).booleanValue())
        {
            pushLevel();
            for(int ilb_22 = 1; ilb_22 <= (new Integer((new Integer(fields.size())).intValue() - (new Integer(1)).intValue())).intValue(); ilb_22++)
            {
                Integer i = new Integer(ilb_22);
                String rhs_23 = null;
                rhs_23 = res.concat(nl());
                res = UTIL.ConvertToString(UTIL.clone(rhs_23));
                AstField obj_27 = null;
                if(1 <= i.intValue() && i.intValue() <= fields.size())
                    obj_27 = (AstField)fields.get(i.intValue() - 1);
                else
                    UTIL.RunTime("Run-Time Error:Illegal index");
                obj_27.accept(this);
                String rhs_31 = null;
                rhs_31 = res.concat(new String(","));
                res = UTIL.ConvertToString(UTIL.clone(rhs_31));
            }

            String rhs_34 = null;
            rhs_34 = res.concat(nl());
            res = UTIL.ConvertToString(UTIL.clone(rhs_34));
            AstField obj_38 = null;
            if(1 <= (new Integer(fields.size())).intValue() && (new Integer(fields.size())).intValue() <= fields.size())
                obj_38 = (AstField)fields.get((new Integer(fields.size())).intValue() - 1);
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
            obj_38.accept(this);
            popLevel();
            String rhs_44 = null;
            rhs_44 = res.concat(nl());
            res = UTIL.ConvertToString(UTIL.clone(rhs_44));
        }
        String rhs_47 = null;
        rhs_47 = res.concat(new String("])"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_47));
    }

    public void visit(AstField af)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstField("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        pushLevel();
        String rhs_6 = null;
        String var1_7 = null;
        String var1_8 = null;
        String var1_9 = null;
        var1_9 = res.concat(new String("\""));
        String var2_12 = null;
        var2_12 = af.getName();
        var1_8 = var1_9.concat(var2_12);
        var1_7 = var1_8.concat(new String("\","));
        rhs_6 = var1_7.concat(nl());
        res = UTIL.ConvertToString(UTIL.clone(rhs_6));
        AstType obj_16 = null;
        obj_16 = af.getType();
        obj_16.accept(this);
        String rhs_18 = null;
        rhs_18 = res.concat(new String(")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_18));
        popLevel();
    }

    public void visit(AstTypeName at)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstTypeName("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        String rhs_5 = null;
        String var1_6 = null;
        String var1_7 = null;
        var1_7 = res.concat(new String("\""));
        String var2_10 = null;
        var2_10 = at.getName();
        var1_6 = var1_7.concat(var2_10);
        rhs_5 = var1_6.concat(new String("\")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_5));
    }

    public void visit(AstQuotedType aqt)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstQuotedType("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        String rhs_5 = null;
        String var1_6 = null;
        String var1_7 = null;
        var1_7 = res.concat(new String("\""));
        String var2_10 = null;
        var2_10 = aqt.getName();
        var1_6 = var1_7.concat(var2_10);
        rhs_5 = var1_6.concat(new String("\")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_5));
    }

    public void visit(AstUnionType aut)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstUnionType("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        pushLevel();
        String rhs_6 = null;
        rhs_6 = res.concat(nl());
        res = UTIL.ConvertToString(UTIL.clone(rhs_6));
        AstType obj_10 = null;
        obj_10 = aut.getLhs();
        obj_10.accept(this);
        String rhs_12 = null;
        String var1_13 = null;
        var1_13 = res.concat(new String(","));
        rhs_12 = var1_13.concat(nl());
        res = UTIL.ConvertToString(UTIL.clone(rhs_12));
        AstType obj_18 = null;
        obj_18 = aut.getRhs();
        obj_18.accept(this);
        String rhs_20 = null;
        rhs_20 = res.concat(new String(")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_20));
        popLevel();
    }

    public void visit(AstSeqOfType asot)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstSeqOfType("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        AstType obj_6 = null;
        obj_6 = asot.getType();
        obj_6.accept(this);
        String rhs_8 = null;
        rhs_8 = res.concat(new String(")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_8));
    }

    public void visit(AstSetOfType asot)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstSetOfType("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        AstType obj_6 = null;
        obj_6 = asot.getType();
        obj_6.accept(this);
        String rhs_8 = null;
        rhs_8 = res.concat(new String(")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_8));
    }

    public void visit(AstOptionalType aopt)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstOptionalType("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        AstType obj_6 = null;
        obj_6 = aopt.getType();
        obj_6.accept(this);
        String rhs_8 = null;
        rhs_8 = res.concat(new String(")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_8));
    }

    public void visit(AstMapType amt)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = res.concat(new String("mk_ AstMapType("));
        res = UTIL.ConvertToString(UTIL.clone(rhs_2));
        AstType obj_6 = null;
        obj_6 = amt.getDomType();
        obj_6.accept(this);
        String rhs_8 = null;
        rhs_8 = res.concat(new String(","));
        res = UTIL.ConvertToString(UTIL.clone(rhs_8));
        AstType obj_12 = null;
        obj_12 = amt.getRngType();
        obj_12.accept(this);
        String rhs_14 = null;
        rhs_14 = res.concat(new String(")"));
        res = UTIL.ConvertToString(UTIL.clone(rhs_14));
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private static Integer level = new Integer(0);
    public String res;

}