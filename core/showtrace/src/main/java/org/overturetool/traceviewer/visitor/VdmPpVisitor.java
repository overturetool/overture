// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 31-07-2009 16:16:01
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   VdmPpVisitor.java

package org.overturetool.traceviewer.visitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import jp.co.csk.vdm.toolbox.VDM.UTIL;

import org.overturetool.traceviewer.ast.itf.IOmlBUSdecl;
import org.overturetool.traceviewer.ast.itf.IOmlCPUdecl;
import org.overturetool.traceviewer.ast.itf.IOmlDelayedThreadSwapIn;
import org.overturetool.traceviewer.ast.itf.IOmlDeployObj;
import org.overturetool.traceviewer.ast.itf.IOmlDocument;
import org.overturetool.traceviewer.ast.itf.IOmlInstVarChange;
import org.overturetool.traceviewer.ast.itf.IOmlLexem;
import org.overturetool.traceviewer.ast.itf.IOmlMessageActivate;
import org.overturetool.traceviewer.ast.itf.IOmlMessageCompleted;
import org.overturetool.traceviewer.ast.itf.IOmlMessageRequest;
import org.overturetool.traceviewer.ast.itf.IOmlNode;
import org.overturetool.traceviewer.ast.itf.IOmlOpActivate;
import org.overturetool.traceviewer.ast.itf.IOmlOpCompleted;
import org.overturetool.traceviewer.ast.itf.IOmlOpRequest;
import org.overturetool.traceviewer.ast.itf.IOmlReplyRequest;
import org.overturetool.traceviewer.ast.itf.IOmlThreadCreate;
import org.overturetool.traceviewer.ast.itf.IOmlThreadKill;
import org.overturetool.traceviewer.ast.itf.IOmlThreadSwapIn;
import org.overturetool.traceviewer.ast.itf.IOmlThreadSwapOut;
import org.overturetool.traceviewer.ast.itf.IOmlTraceEvent;
import org.overturetool.traceviewer.ast.itf.IOmlTraceFile;

// Referenced classes of package org.overturetool.tracefile.visitor:
//            OmlVisitor

public class VdmPpVisitor extends OmlVisitor
{

    public VdmPpVisitor()
        throws CGException
    {
        result = null;
        lvl = null;
        try
        {
            result = UTIL.ConvertToString(new String());
            lvl = new Long(0L);
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    private void pushNL()
        throws CGException
    {
        lvl = UTIL.NumberToLong(UTIL.clone(new Long(lvl.intValue() + (new Long(2L)).intValue())));
    }

    private void popNL()
        throws CGException
    {
        lvl = UTIL.NumberToLong(UTIL.clone(new Long(lvl.intValue() - (new Long(2L)).intValue())));
    }

    private String getNL()
        throws CGException
    {
        String res = nl;
        for(Long cnt = lvl; (new Boolean(cnt.intValue() > (new Long(0L)).intValue())).booleanValue(); cnt = UTIL.NumberToLong(UTIL.clone(new Long(cnt.intValue() - (new Long(1L)).intValue()))))
        {
            String rhs_4 = null;
            rhs_4 = res.concat(new String(" "));
            res = UTIL.ConvertToString(UTIL.clone(rhs_4));
        }

        return res;
    }

    private void printBoolField(Boolean pval)
        throws CGException
    {
        String rhs_2 = null;
        if(pval.booleanValue())
            rhs_2 = new String("true");
        else
            rhs_2 = new String("false");
        result = UTIL.ConvertToString(UTIL.clone(rhs_2));
    }

    private void printCharField(Character pval)
        throws CGException
    {
        String rhs_2 = null;
        rhs_2 = new String();
        rhs_2 = (new StringBuilder(String.valueOf(rhs_2))).append(new Character('\'')).toString();
        rhs_2 = (new StringBuilder(String.valueOf(rhs_2))).append(pval).toString();
        rhs_2 = (new StringBuilder(String.valueOf(rhs_2))).append(new Character('\'')).toString();
        result = UTIL.ConvertToString(UTIL.clone(rhs_2));
    }

    private void printNatField(Long var_1_1)
        throws CGException
    {
        result = var_1_1.toString();
    }

    private void printRealField(Double var_1_1)
        throws CGException
    {
        result = var_1_1.toString();
    }

    private void printNodeField(IOmlNode pNode)
        throws CGException
    {
        pNode.accept(this);
    }

    private void printStringField(String str)
        throws CGException
    {
        String rhs_2 = null;
        String var1_3 = null;
        var1_3 = (new String("\"")).concat(str);
        rhs_2 = var1_3.concat(new String("\""));
        result = UTIL.ConvertToString(UTIL.clone(rhs_2));
    }

    private void printSeqofField(Vector pval)
        throws CGException
    {
        String str = new String("[");
        Long cnt = new Long(pval.size());
        pushNL();
        String rhs_4 = null;
        rhs_4 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_4));
        for(; (new Boolean(cnt.intValue() > (new Long(0L)).intValue())).booleanValue(); cnt = UTIL.NumberToLong(UTIL.clone(new Long(cnt.intValue() - (new Long(1L)).intValue()))))
        {
            Object tmpArg_v_11 = null;
            if(1 <= (new Long((new Long((new Long(pval.size())).intValue() - cnt.intValue())).intValue() + (new Long(1L)).intValue())).intValue() && (new Long((new Long((new Long(pval.size())).intValue() - cnt.intValue())).intValue() + (new Long(1L)).intValue())).intValue() <= pval.size())
                tmpArg_v_11 = pval.get((new Long((new Long((new Long(pval.size())).intValue() - cnt.intValue())).intValue() + (new Long(1L)).intValue())).intValue() - 1);
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
            printField(tmpArg_v_11);
            String rhs_19 = null;
            rhs_19 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_19));
            if((new Boolean(cnt.intValue() > (new Long(1L)).intValue())).booleanValue())
            {
                String rhs_25 = null;
                String var1_26 = null;
                var1_26 = str.concat(new String(","));
                rhs_25 = var1_26.concat(getNL());
                str = UTIL.ConvertToString(UTIL.clone(rhs_25));
            }
        }

        popNL();
        String rhs_34 = null;
        String var1_35 = null;
        var1_35 = str.concat(getNL());
        rhs_34 = var1_35.concat(new String("]"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_34));
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    private void printSetofField(HashSet pval)
        throws CGException
    {
        String str = new String("{");
        Long cnt = new Long(pval.size());
        HashSet pvs = pval;
        pushNL();
        String rhs_4 = null;
        rhs_4 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_4));
        for(; (new Boolean(cnt.intValue() > (new Long(0L)).intValue())).booleanValue(); cnt = UTIL.NumberToLong(UTIL.clone(new Long(cnt.intValue() - (new Long(1L)).intValue()))))
        {
            HashSet tmpSet_10 = new HashSet();
            tmpSet_10 = pvs;
            Object pv = null;
            boolean succ_12 = false;
            for(Iterator enm_14 = tmpSet_10.iterator(); enm_14.hasNext() && !succ_12;)
            {
                Object tmpElem_13 = enm_14.next();
                succ_12 = true;
                pv = tmpElem_13;
            }

            if(!succ_12)
                UTIL.RunTime("Run-Time Error:The binding environment was empty");
            printField(pv);
            String rhs_17 = null;
            rhs_17 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_17));
            HashSet rhs_20 = new HashSet();
            HashSet var2_22 = new HashSet();
            var2_22 = new HashSet();
            var2_22.add(pv);
            rhs_20 = (HashSet)pvs.clone();
            rhs_20.removeAll(var2_22);
            pvs = (HashSet)UTIL.clone(rhs_20);
            if((new Boolean(cnt.intValue() > (new Long(1L)).intValue())).booleanValue())
            {
                String rhs_27 = null;
                String var1_28 = null;
                var1_28 = str.concat(new String(","));
                rhs_27 = var1_28.concat(getNL());
                str = UTIL.ConvertToString(UTIL.clone(rhs_27));
            }
        }

        popNL();
        String rhs_36 = null;
        String var1_37 = null;
        var1_37 = str.concat(getNL());
        rhs_36 = var1_37.concat(new String("}"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_36));
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    private void printMapField(HashMap pval)
        throws CGException
    {
        String str = new String("{");
        Long cnt = null;
        HashSet unArg_2 = new HashSet();
        unArg_2.clear();
        unArg_2.addAll((Collection)pval.keySet());
        cnt = new Long(unArg_2.size());
        HashSet pvs = new HashSet();
        pvs.clear();
        pvs.addAll((Collection)pval.keySet());
        pushNL();
        String rhs_6 = null;
        rhs_6 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_6));
        for(; (new Boolean(cnt.intValue() > (new Long(0L)).intValue())).booleanValue(); cnt = UTIL.NumberToLong(UTIL.clone(new Long(cnt.intValue() - (new Long(1L)).intValue()))))
        {
            HashSet tmpSet_12 = new HashSet();
            tmpSet_12 = pvs;
            Object pv = null;
            boolean succ_14 = false;
            for(Iterator enm_16 = tmpSet_12.iterator(); enm_16.hasNext() && !succ_14;)
            {
                Object tmpElem_15 = enm_16.next();
                succ_14 = true;
                pv = tmpElem_15;
            }

            if(!succ_14)
                UTIL.RunTime("Run-Time Error:The binding environment was empty");
            printField(pv);
            String rhs_19 = null;
            String var1_20 = null;
            var1_20 = str.concat(result);
            rhs_19 = var1_20.concat(new String(" |-> "));
            str = UTIL.ConvertToString(UTIL.clone(rhs_19));
            printField(pval.get(pv));
            String rhs_28 = null;
            rhs_28 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_28));
            HashSet rhs_31 = new HashSet();
            HashSet var2_33 = new HashSet();
            var2_33 = new HashSet();
            var2_33.add(pv);
            rhs_31 = (HashSet)pvs.clone();
            rhs_31.removeAll(var2_33);
            pvs = (HashSet)UTIL.clone(rhs_31);
            if((new Boolean(cnt.intValue() > (new Long(1L)).intValue())).booleanValue())
            {
                String rhs_38 = null;
                String var1_39 = null;
                var1_39 = str.concat(new String(","));
                rhs_38 = var1_39.concat(getNL());
                str = UTIL.ConvertToString(UTIL.clone(rhs_38));
            }
        }

        popNL();
        String rhs_47 = null;
        String var1_48 = null;
        var1_48 = str.concat(getNL());
        rhs_47 = var1_48.concat(new String("}"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_47));
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    private void printField(Object fld)
        throws CGException
    {
        if((new Boolean(fld instanceof Boolean)).booleanValue())
            printBoolField((Boolean)fld);
        else
        if((new Boolean(fld instanceof Character)).booleanValue())
            printCharField((Character)fld);
        else
        if((new Boolean(UTIL.IsInteger(fld) && ((Number)fld).intValue() >= 0)).booleanValue())
            printNatField(UTIL.NumberToLong(fld));
        else
        if((new Boolean(UTIL.IsReal(fld))).booleanValue())
        {
            printRealField(UTIL.NumberToReal(fld));
        } else
        {
            Boolean cond_6 = null;
            cond_6 = new Boolean(fld instanceof IOmlNode);
            if(cond_6.booleanValue())
                printNodeField((IOmlNode)fld);
            else
                printStringField(UTIL.ConvertToString(fld));
        }
    }

    private void printPositionInfo(IOmlNode pNode)
        throws CGException
    {
        String str = new String(".setLexemIndex(");
        Long tmpArg_v_3 = null;
        tmpArg_v_3 = pNode.getLeftMostLexemIndex();
        printNatField(tmpArg_v_3);
        String rhs_4 = null;
        String var1_5 = null;
        var1_5 = str.concat(result);
        rhs_4 = var1_5.concat(new String(").setLexemIndex("));
        str = UTIL.ConvertToString(UTIL.clone(rhs_4));
        Long tmpArg_v_10 = null;
        tmpArg_v_10 = pNode.getRightMostLexemIndex();
        printNatField(tmpArg_v_10);
        String rhs_11 = null;
        String var1_12 = null;
        var1_12 = str.concat(result);
        rhs_11 = var1_12.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_11));
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitNode(IOmlNode pNode)
        throws CGException
    {
        pNode.accept(this);
    }

    public void visitDocument(IOmlDocument pcmp)
        throws CGException
    {
        String str = new String("new OmlDocument(");
        pushNL();
        String tmpArg_v_4 = null;
        tmpArg_v_4 = pcmp.getFilename();
        printStringField(tmpArg_v_4);
        String rhs_5 = null;
        String var1_6 = null;
        String var1_7 = null;
        var1_7 = str.concat(result);
        var1_6 = var1_7.concat(new String(","));
        rhs_5 = var1_6.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_5));
        Boolean cond_12 = null;
        cond_12 = pcmp.hasTraceFile();
        if(cond_12.booleanValue())
        {
            IOmlTraceFile tmpArg_v_14 = null;
            tmpArg_v_14 = pcmp.getTraceFile();
            visitTraceFile(tmpArg_v_14);
        }
        String rhs_15 = null;
        String var1_16 = null;
        String var1_17 = null;
        var1_17 = str.concat(result);
        var1_16 = var1_17.concat(new String(","));
        rhs_15 = var1_16.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_15));
        String rhs_22 = null;
        rhs_22 = str.concat(new String("["));
        str = UTIL.ConvertToString(UTIL.clone(rhs_22));
        pushNL();
        Vector lexems = null;
        lexems = pcmp.getLexems();
        if((new Boolean((new Long(lexems.size())).intValue() > (new Long(0L)).intValue())).booleanValue())
        {
            String rhs_32 = null;
            rhs_32 = str.concat(getNL());
            str = UTIL.ConvertToString(UTIL.clone(rhs_32));
            for(int ilb_40 = 1; ilb_40 <= (new Long((new Long(lexems.size())).intValue() - (new Long(1L)).intValue())).intValue(); ilb_40++)
            {
                Long idx = new Long(ilb_40);
                IOmlLexem tmpArg_v_42 = null;
                if(1 <= idx.intValue() && idx.intValue() <= lexems.size())
                    tmpArg_v_42 = (IOmlLexem)lexems.get(idx.intValue() - 1);
                else
                    UTIL.RunTime("Run-Time Error:Illegal index");
                visitLexem(tmpArg_v_42);
                String rhs_45 = null;
                String var1_46 = null;
                String var1_47 = null;
                var1_47 = str.concat(result);
                var1_46 = var1_47.concat(new String(","));
                rhs_45 = var1_46.concat(getNL());
                str = UTIL.ConvertToString(UTIL.clone(rhs_45));
            }

            IOmlLexem tmpArg_v_53 = null;
            if(1 <= (new Long(lexems.size())).intValue() && (new Long(lexems.size())).intValue() <= lexems.size())
                tmpArg_v_53 = (IOmlLexem)lexems.get((new Long(lexems.size())).intValue() - 1);
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
            visitLexem(tmpArg_v_53);
            String rhs_57 = null;
            rhs_57 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_57));
        }
        popNL();
        String rhs_61 = null;
        String var1_62 = null;
        var1_62 = str.concat(getNL());
        rhs_61 = var1_62.concat(new String("]"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_61));
        popNL();
        String rhs_67 = null;
        String var1_68 = null;
        var1_68 = str.concat(getNL());
        rhs_67 = var1_68.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_67));
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitLexem(IOmlLexem pcmp)
        throws CGException
    {
        String str = new String("new OmlLexem(");
        Long tmpArg_v_3 = null;
        tmpArg_v_3 = pcmp.getLine();
        printNatField(tmpArg_v_3);
        String rhs_4 = null;
        String var1_5 = null;
        var1_5 = str.concat(result);
        rhs_4 = var1_5.concat(new String(","));
        str = UTIL.ConvertToString(UTIL.clone(rhs_4));
        Long tmpArg_v_10 = null;
        tmpArg_v_10 = pcmp.getColumn();
        printNatField(tmpArg_v_10);
        String rhs_11 = null;
        String var1_12 = null;
        var1_12 = str.concat(result);
        rhs_11 = var1_12.concat(new String(","));
        str = UTIL.ConvertToString(UTIL.clone(rhs_11));
        Long tmpArg_v_17 = null;
        tmpArg_v_17 = pcmp.getLexval();
        printNatField(tmpArg_v_17);
        String rhs_18 = null;
        String var1_19 = null;
        var1_19 = str.concat(result);
        rhs_18 = var1_19.concat(new String(","));
        str = UTIL.ConvertToString(UTIL.clone(rhs_18));
        String tmpArg_v_24 = null;
        tmpArg_v_24 = pcmp.getText();
        printStringField(tmpArg_v_24);
        String rhs_25 = null;
        String var1_26 = null;
        var1_26 = str.concat(result);
        rhs_25 = var1_26.concat(new String(","));
        str = UTIL.ConvertToString(UTIL.clone(rhs_25));
        Long tmpArg_v_31 = null;
        tmpArg_v_31 = pcmp.getIndex();
        printNatField(tmpArg_v_31);
        String rhs_32 = null;
        String var1_33 = null;
        var1_33 = str.concat(result);
        rhs_32 = var1_33.concat(new String(","));
        str = UTIL.ConvertToString(UTIL.clone(rhs_32));
        Long tmpArg_v_38 = null;
        tmpArg_v_38 = pcmp.getType();
        printNatField(tmpArg_v_38);
        String rhs_39 = null;
        rhs_39 = str.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_39));
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitDeployObj(IOmlDeployObj pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getObjref();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Long tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getCpunm();
        printNatField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        String tmpArg_v_29 = null;
        tmpArg_v_29 = pcmp.getClnm();
        printStringField(tmpArg_v_29);
        String rhs_30 = null;
        String var1_31 = null;
        String var1_32 = null;
        var1_32 = str.concat(result);
        var1_31 = var1_32.concat(new String(","));
        rhs_30 = var1_31.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_30));
        Long tmpArg_v_38 = null;
        tmpArg_v_38 = pcmp.getObstime();
        printNatField(tmpArg_v_38);
        String rhs_39 = null;
        rhs_39 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_39));
        popNL();
        String rhs_43 = null;
        String var1_44 = null;
        var1_44 = str.concat(getNL());
        rhs_43 = var1_44.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_43));
        Boolean cond_48 = null;
        cond_48 = pcmp.hasPositionInfo();
        if(cond_48.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_51 = null;
            rhs_51 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_51));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitInstVarChange(IOmlInstVarChange pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        String tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getInstnm();
        printStringField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        String tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getVal();
        printStringField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        Long tmpArg_v_29 = null;
        tmpArg_v_29 = pcmp.getObjref();
        printNatField(tmpArg_v_29);
        String rhs_30 = null;
        String var1_31 = null;
        String var1_32 = null;
        var1_32 = str.concat(result);
        var1_31 = var1_32.concat(new String(","));
        rhs_30 = var1_31.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_30));
        Long tmpArg_v_38 = null;
        tmpArg_v_38 = pcmp.getObstime();
        printNatField(tmpArg_v_38);
        String rhs_39 = null;
        rhs_39 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_39));
        popNL();
        String rhs_43 = null;
        String var1_44 = null;
        var1_44 = str.concat(getNL());
        rhs_43 = var1_44.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_43));
        Boolean cond_48 = null;
        cond_48 = pcmp.hasPositionInfo();
        if(cond_48.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_51 = null;
            rhs_51 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_51));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitThreadSwapOut(IOmlThreadSwapOut pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Boolean cond_19 = null;
        cond_19 = pcmp.hasObjref();
        if(cond_19.booleanValue())
        {
            Long tmpArg_v_22 = null;
            tmpArg_v_22 = pcmp.getObjref();
            printNatField(tmpArg_v_22);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_23 = null;
        String var1_24 = null;
        String var1_25 = null;
        var1_25 = str.concat(result);
        var1_24 = var1_25.concat(new String(","));
        rhs_23 = var1_24.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_23));
        Boolean cond_30 = null;
        cond_30 = pcmp.hasClnm();
        if(cond_30.booleanValue())
        {
            String tmpArg_v_33 = null;
            tmpArg_v_33 = pcmp.getClnm();
            printStringField(tmpArg_v_33);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_34 = null;
        String var1_35 = null;
        String var1_36 = null;
        var1_36 = str.concat(result);
        var1_35 = var1_36.concat(new String(","));
        rhs_34 = var1_35.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_34));
        Long tmpArg_v_42 = null;
        tmpArg_v_42 = pcmp.getCpunm();
        printNatField(tmpArg_v_42);
        String rhs_43 = null;
        String var1_44 = null;
        String var1_45 = null;
        var1_45 = str.concat(result);
        var1_44 = var1_45.concat(new String(","));
        rhs_43 = var1_44.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_43));
        Long tmpArg_v_51 = null;
        tmpArg_v_51 = pcmp.getOverhead();
        printNatField(tmpArg_v_51);
        String rhs_52 = null;
        String var1_53 = null;
        String var1_54 = null;
        var1_54 = str.concat(result);
        var1_53 = var1_54.concat(new String(","));
        rhs_52 = var1_53.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_52));
        Long tmpArg_v_60 = null;
        tmpArg_v_60 = pcmp.getObstime();
        printNatField(tmpArg_v_60);
        String rhs_61 = null;
        rhs_61 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_61));
        popNL();
        String rhs_65 = null;
        String var1_66 = null;
        var1_66 = str.concat(getNL());
        rhs_65 = var1_66.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_65));
        Boolean cond_70 = null;
        cond_70 = pcmp.hasPositionInfo();
        if(cond_70.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_73 = null;
            rhs_73 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_73));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitThreadSwapIn(IOmlThreadSwapIn pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Boolean cond_19 = null;
        cond_19 = pcmp.hasObjref();
        if(cond_19.booleanValue())
        {
            Long tmpArg_v_22 = null;
            tmpArg_v_22 = pcmp.getObjref();
            printNatField(tmpArg_v_22);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_23 = null;
        String var1_24 = null;
        String var1_25 = null;
        var1_25 = str.concat(result);
        var1_24 = var1_25.concat(new String(","));
        rhs_23 = var1_24.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_23));
        Boolean cond_30 = null;
        cond_30 = pcmp.hasClnm();
        if(cond_30.booleanValue())
        {
            String tmpArg_v_33 = null;
            tmpArg_v_33 = pcmp.getClnm();
            printStringField(tmpArg_v_33);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_34 = null;
        String var1_35 = null;
        String var1_36 = null;
        var1_36 = str.concat(result);
        var1_35 = var1_36.concat(new String(","));
        rhs_34 = var1_35.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_34));
        Long tmpArg_v_42 = null;
        tmpArg_v_42 = pcmp.getCpunm();
        printNatField(tmpArg_v_42);
        String rhs_43 = null;
        String var1_44 = null;
        String var1_45 = null;
        var1_45 = str.concat(result);
        var1_44 = var1_45.concat(new String(","));
        rhs_43 = var1_44.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_43));
        Long tmpArg_v_51 = null;
        tmpArg_v_51 = pcmp.getOverhead();
        printNatField(tmpArg_v_51);
        String rhs_52 = null;
        String var1_53 = null;
        String var1_54 = null;
        var1_54 = str.concat(result);
        var1_53 = var1_54.concat(new String(","));
        rhs_52 = var1_53.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_52));
        Long tmpArg_v_60 = null;
        tmpArg_v_60 = pcmp.getObstime();
        printNatField(tmpArg_v_60);
        String rhs_61 = null;
        rhs_61 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_61));
        popNL();
        String rhs_65 = null;
        String var1_66 = null;
        var1_66 = str.concat(getNL());
        rhs_65 = var1_66.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_65));
        Boolean cond_70 = null;
        cond_70 = pcmp.hasPositionInfo();
        if(cond_70.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_73 = null;
            rhs_73 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_73));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitDelayedThreadSwapIn(IOmlDelayedThreadSwapIn pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Boolean cond_19 = null;
        cond_19 = pcmp.hasObjref();
        if(cond_19.booleanValue())
        {
            Long tmpArg_v_22 = null;
            tmpArg_v_22 = pcmp.getObjref();
            printNatField(tmpArg_v_22);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_23 = null;
        String var1_24 = null;
        String var1_25 = null;
        var1_25 = str.concat(result);
        var1_24 = var1_25.concat(new String(","));
        rhs_23 = var1_24.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_23));
        Boolean cond_30 = null;
        cond_30 = pcmp.hasClnm();
        if(cond_30.booleanValue())
        {
            String tmpArg_v_33 = null;
            tmpArg_v_33 = pcmp.getClnm();
            printStringField(tmpArg_v_33);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_34 = null;
        String var1_35 = null;
        String var1_36 = null;
        var1_36 = str.concat(result);
        var1_35 = var1_36.concat(new String(","));
        rhs_34 = var1_35.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_34));
        Long tmpArg_v_42 = null;
        tmpArg_v_42 = pcmp.getCpunm();
        printNatField(tmpArg_v_42);
        String rhs_43 = null;
        String var1_44 = null;
        String var1_45 = null;
        var1_45 = str.concat(result);
        var1_44 = var1_45.concat(new String(","));
        rhs_43 = var1_44.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_43));
        Long tmpArg_v_51 = null;
        tmpArg_v_51 = pcmp.getDelay();
        printNatField(tmpArg_v_51);
        String rhs_52 = null;
        String var1_53 = null;
        String var1_54 = null;
        var1_54 = str.concat(result);
        var1_53 = var1_54.concat(new String(","));
        rhs_52 = var1_53.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_52));
        Long tmpArg_v_60 = null;
        tmpArg_v_60 = pcmp.getOverhead();
        printNatField(tmpArg_v_60);
        String rhs_61 = null;
        String var1_62 = null;
        String var1_63 = null;
        var1_63 = str.concat(result);
        var1_62 = var1_63.concat(new String(","));
        rhs_61 = var1_62.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_61));
        Long tmpArg_v_69 = null;
        tmpArg_v_69 = pcmp.getObstime();
        printNatField(tmpArg_v_69);
        String rhs_70 = null;
        rhs_70 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_70));
        popNL();
        String rhs_74 = null;
        String var1_75 = null;
        var1_75 = str.concat(getNL());
        rhs_74 = var1_75.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_74));
        Boolean cond_79 = null;
        cond_79 = pcmp.hasPositionInfo();
        if(cond_79.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_82 = null;
            rhs_82 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_82));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitReplyRequest(IOmlReplyRequest pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getBusid();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Long tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getFromcpu();
        printNatField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        Long tmpArg_v_29 = null;
        tmpArg_v_29 = pcmp.getTocpu();
        printNatField(tmpArg_v_29);
        String rhs_30 = null;
        String var1_31 = null;
        String var1_32 = null;
        var1_32 = str.concat(result);
        var1_31 = var1_32.concat(new String(","));
        rhs_30 = var1_31.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_30));
        Long tmpArg_v_38 = null;
        tmpArg_v_38 = pcmp.getMsgid();
        printNatField(tmpArg_v_38);
        String rhs_39 = null;
        String var1_40 = null;
        String var1_41 = null;
        var1_41 = str.concat(result);
        var1_40 = var1_41.concat(new String(","));
        rhs_39 = var1_40.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_39));
        Long tmpArg_v_47 = null;
        tmpArg_v_47 = pcmp.getOrigmsgid();
        printNatField(tmpArg_v_47);
        String rhs_48 = null;
        String var1_49 = null;
        String var1_50 = null;
        var1_50 = str.concat(result);
        var1_49 = var1_50.concat(new String(","));
        rhs_48 = var1_49.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_48));
        Long tmpArg_v_56 = null;
        tmpArg_v_56 = pcmp.getCallthr();
        printNatField(tmpArg_v_56);
        String rhs_57 = null;
        String var1_58 = null;
        String var1_59 = null;
        var1_59 = str.concat(result);
        var1_58 = var1_59.concat(new String(","));
        rhs_57 = var1_58.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_57));
        Long tmpArg_v_65 = null;
        tmpArg_v_65 = pcmp.getCalleethr();
        printNatField(tmpArg_v_65);
        String rhs_66 = null;
        String var1_67 = null;
        String var1_68 = null;
        var1_68 = str.concat(result);
        var1_67 = var1_68.concat(new String(","));
        rhs_66 = var1_67.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_66));
        Long tmpArg_v_74 = null;
        tmpArg_v_74 = pcmp.getSize();
        printNatField(tmpArg_v_74);
        String rhs_75 = null;
        String var1_76 = null;
        String var1_77 = null;
        var1_77 = str.concat(result);
        var1_76 = var1_77.concat(new String(","));
        rhs_75 = var1_76.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_75));
        Long tmpArg_v_83 = null;
        tmpArg_v_83 = pcmp.getObstime();
        printNatField(tmpArg_v_83);
        String rhs_84 = null;
        rhs_84 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_84));
        popNL();
        String rhs_88 = null;
        String var1_89 = null;
        var1_89 = str.concat(getNL());
        rhs_88 = var1_89.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_88));
        Boolean cond_93 = null;
        cond_93 = pcmp.hasPositionInfo();
        if(cond_93.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_96 = null;
            rhs_96 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_96));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitOpRequest(IOmlOpRequest pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        String tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getOpname();
        printStringField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        Boolean cond_28 = null;
        cond_28 = pcmp.hasObjref();
        if(cond_28.booleanValue())
        {
            Long tmpArg_v_31 = null;
            tmpArg_v_31 = pcmp.getObjref();
            printNatField(tmpArg_v_31);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_32 = null;
        String var1_33 = null;
        String var1_34 = null;
        var1_34 = str.concat(result);
        var1_33 = var1_34.concat(new String(","));
        rhs_32 = var1_33.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_32));
        String tmpArg_v_40 = null;
        tmpArg_v_40 = pcmp.getClnm();
        printStringField(tmpArg_v_40);
        String rhs_41 = null;
        String var1_42 = null;
        String var1_43 = null;
        var1_43 = str.concat(result);
        var1_42 = var1_43.concat(new String(","));
        rhs_41 = var1_42.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_41));
        Long tmpArg_v_49 = null;
        tmpArg_v_49 = pcmp.getCpunm();
        printNatField(tmpArg_v_49);
        String rhs_50 = null;
        String var1_51 = null;
        String var1_52 = null;
        var1_52 = str.concat(result);
        var1_51 = var1_52.concat(new String(","));
        rhs_50 = var1_51.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_50));
        Boolean tmpArg_v_58 = null;
        tmpArg_v_58 = pcmp.getAsynchronous();
        printBoolField(tmpArg_v_58);
        String rhs_59 = null;
        String var1_60 = null;
        String var1_61 = null;
        var1_61 = str.concat(result);
        var1_60 = var1_61.concat(new String(","));
        rhs_59 = var1_60.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_59));
        Boolean cond_66 = null;
        cond_66 = pcmp.hasArgs();
        if(cond_66.booleanValue())
        {
            String tmpArg_v_69 = null;
            tmpArg_v_69 = pcmp.getArgs();
            printStringField(tmpArg_v_69);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_70 = null;
        String var1_71 = null;
        String var1_72 = null;
        var1_72 = str.concat(result);
        var1_71 = var1_72.concat(new String(","));
        rhs_70 = var1_71.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_70));
        Long tmpArg_v_78 = null;
        tmpArg_v_78 = pcmp.getObstime();
        printNatField(tmpArg_v_78);
        String rhs_79 = null;
        rhs_79 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_79));
        popNL();
        String rhs_83 = null;
        String var1_84 = null;
        var1_84 = str.concat(getNL());
        rhs_83 = var1_84.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_83));
        Boolean cond_88 = null;
        cond_88 = pcmp.hasPositionInfo();
        if(cond_88.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_91 = null;
            rhs_91 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_91));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitMessageActivate(IOmlMessageActivate pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getMsgid();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Long tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getObstime();
        printNatField(tmpArg_v_20);
        String rhs_21 = null;
        rhs_21 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        popNL();
        String rhs_25 = null;
        String var1_26 = null;
        var1_26 = str.concat(getNL());
        rhs_25 = var1_26.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_25));
        Boolean cond_30 = null;
        cond_30 = pcmp.hasPositionInfo();
        if(cond_30.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_33 = null;
            rhs_33 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_33));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitCPUdecl(IOmlCPUdecl pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Boolean tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getExpl();
        printBoolField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        String tmpArg_v_29 = null;
        tmpArg_v_29 = pcmp.getSys();
        printStringField(tmpArg_v_29);
        String rhs_30 = null;
        String var1_31 = null;
        String var1_32 = null;
        var1_32 = str.concat(result);
        var1_31 = var1_32.concat(new String(","));
        rhs_30 = var1_31.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_30));
        String tmpArg_v_38 = null;
        tmpArg_v_38 = pcmp.getName();
        printStringField(tmpArg_v_38);
        String rhs_39 = null;
        rhs_39 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_39));
        popNL();
        String rhs_43 = null;
        String var1_44 = null;
        var1_44 = str.concat(getNL());
        rhs_43 = var1_44.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_43));
        Boolean cond_48 = null;
        cond_48 = pcmp.hasPositionInfo();
        if(cond_48.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_51 = null;
            rhs_51 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_51));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitOpCompleted(IOmlOpCompleted pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        String tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getOpname();
        printStringField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        Boolean cond_28 = null;
        cond_28 = pcmp.hasObjref();
        if(cond_28.booleanValue())
        {
            Long tmpArg_v_31 = null;
            tmpArg_v_31 = pcmp.getObjref();
            printNatField(tmpArg_v_31);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_32 = null;
        String var1_33 = null;
        String var1_34 = null;
        var1_34 = str.concat(result);
        var1_33 = var1_34.concat(new String(","));
        rhs_32 = var1_33.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_32));
        String tmpArg_v_40 = null;
        tmpArg_v_40 = pcmp.getClnm();
        printStringField(tmpArg_v_40);
        String rhs_41 = null;
        String var1_42 = null;
        String var1_43 = null;
        var1_43 = str.concat(result);
        var1_42 = var1_43.concat(new String(","));
        rhs_41 = var1_42.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_41));
        Long tmpArg_v_49 = null;
        tmpArg_v_49 = pcmp.getCpunm();
        printNatField(tmpArg_v_49);
        String rhs_50 = null;
        String var1_51 = null;
        String var1_52 = null;
        var1_52 = str.concat(result);
        var1_51 = var1_52.concat(new String(","));
        rhs_50 = var1_51.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_50));
        Boolean tmpArg_v_58 = null;
        tmpArg_v_58 = pcmp.getAsynchronous();
        printBoolField(tmpArg_v_58);
        String rhs_59 = null;
        String var1_60 = null;
        String var1_61 = null;
        var1_61 = str.concat(result);
        var1_60 = var1_61.concat(new String(","));
        rhs_59 = var1_60.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_59));
        Boolean cond_66 = null;
        cond_66 = pcmp.hasRes();
        if(cond_66.booleanValue())
        {
            String tmpArg_v_69 = null;
            tmpArg_v_69 = pcmp.getRes();
            printStringField(tmpArg_v_69);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_70 = null;
        String var1_71 = null;
        String var1_72 = null;
        var1_72 = str.concat(result);
        var1_71 = var1_72.concat(new String(","));
        rhs_70 = var1_71.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_70));
        Long tmpArg_v_78 = null;
        tmpArg_v_78 = pcmp.getObstime();
        printNatField(tmpArg_v_78);
        String rhs_79 = null;
        rhs_79 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_79));
        popNL();
        String rhs_83 = null;
        String var1_84 = null;
        var1_84 = str.concat(getNL());
        rhs_83 = var1_84.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_83));
        Boolean cond_88 = null;
        cond_88 = pcmp.hasPositionInfo();
        if(cond_88.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_91 = null;
            rhs_91 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_91));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitTraceFile(IOmlTraceFile pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        Vector tmpArg_v_7 = null;
        tmpArg_v_7 = pcmp.getTrace();
        printSeqofField(tmpArg_v_7);
        String rhs_8 = null;
        rhs_8 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_8));
        String rhs_11 = null;
        rhs_11 = str.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_11));
        Boolean cond_14 = null;
        cond_14 = pcmp.hasPositionInfo();
        if(cond_14.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_17 = null;
            rhs_17 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_17));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitThreadCreate(IOmlThreadCreate pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Boolean tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getPeriod();
        printBoolField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        Boolean cond_28 = null;
        cond_28 = pcmp.hasObjref();
        if(cond_28.booleanValue())
        {
            Long tmpArg_v_31 = null;
            tmpArg_v_31 = pcmp.getObjref();
            printNatField(tmpArg_v_31);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_32 = null;
        String var1_33 = null;
        String var1_34 = null;
        var1_34 = str.concat(result);
        var1_33 = var1_34.concat(new String(","));
        rhs_32 = var1_33.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_32));
        Boolean cond_39 = null;
        cond_39 = pcmp.hasClnm();
        if(cond_39.booleanValue())
        {
            String tmpArg_v_42 = null;
            tmpArg_v_42 = pcmp.getClnm();
            printStringField(tmpArg_v_42);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_43 = null;
        String var1_44 = null;
        String var1_45 = null;
        var1_45 = str.concat(result);
        var1_44 = var1_45.concat(new String(","));
        rhs_43 = var1_44.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_43));
        Long tmpArg_v_51 = null;
        tmpArg_v_51 = pcmp.getCpunm();
        printNatField(tmpArg_v_51);
        String rhs_52 = null;
        String var1_53 = null;
        String var1_54 = null;
        var1_54 = str.concat(result);
        var1_53 = var1_54.concat(new String(","));
        rhs_52 = var1_53.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_52));
        Long tmpArg_v_60 = null;
        tmpArg_v_60 = pcmp.getObstime();
        printNatField(tmpArg_v_60);
        String rhs_61 = null;
        rhs_61 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_61));
        popNL();
        String rhs_65 = null;
        String var1_66 = null;
        var1_66 = str.concat(getNL());
        rhs_65 = var1_66.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_65));
        Boolean cond_70 = null;
        cond_70 = pcmp.hasPositionInfo();
        if(cond_70.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_73 = null;
            rhs_73 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_73));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitMessageCompleted(IOmlMessageCompleted pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getMsgid();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Long tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getObstime();
        printNatField(tmpArg_v_20);
        String rhs_21 = null;
        rhs_21 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        popNL();
        String rhs_25 = null;
        String var1_26 = null;
        var1_26 = str.concat(getNL());
        rhs_25 = var1_26.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_25));
        Boolean cond_30 = null;
        cond_30 = pcmp.hasPositionInfo();
        if(cond_30.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_33 = null;
            rhs_33 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_33));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitMessageRequest(IOmlMessageRequest pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getBusid();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Long tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getFromcpu();
        printNatField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        Long tmpArg_v_29 = null;
        tmpArg_v_29 = pcmp.getTocpu();
        printNatField(tmpArg_v_29);
        String rhs_30 = null;
        String var1_31 = null;
        String var1_32 = null;
        var1_32 = str.concat(result);
        var1_31 = var1_32.concat(new String(","));
        rhs_30 = var1_31.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_30));
        Long tmpArg_v_38 = null;
        tmpArg_v_38 = pcmp.getMsgid();
        printNatField(tmpArg_v_38);
        String rhs_39 = null;
        String var1_40 = null;
        String var1_41 = null;
        var1_41 = str.concat(result);
        var1_40 = var1_41.concat(new String(","));
        rhs_39 = var1_40.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_39));
        Long tmpArg_v_47 = null;
        tmpArg_v_47 = pcmp.getCallthr();
        printNatField(tmpArg_v_47);
        String rhs_48 = null;
        String var1_49 = null;
        String var1_50 = null;
        var1_50 = str.concat(result);
        var1_49 = var1_50.concat(new String(","));
        rhs_48 = var1_49.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_48));
        String tmpArg_v_56 = null;
        tmpArg_v_56 = pcmp.getOpname();
        printStringField(tmpArg_v_56);
        String rhs_57 = null;
        String var1_58 = null;
        String var1_59 = null;
        var1_59 = str.concat(result);
        var1_58 = var1_59.concat(new String(","));
        rhs_57 = var1_58.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_57));
        Boolean cond_64 = null;
        cond_64 = pcmp.hasObjref();
        if(cond_64.booleanValue())
        {
            Long tmpArg_v_67 = null;
            tmpArg_v_67 = pcmp.getObjref();
            printNatField(tmpArg_v_67);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_68 = null;
        String var1_69 = null;
        String var1_70 = null;
        var1_70 = str.concat(result);
        var1_69 = var1_70.concat(new String(","));
        rhs_68 = var1_69.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_68));
        Long tmpArg_v_76 = null;
        tmpArg_v_76 = pcmp.getSize();
        printNatField(tmpArg_v_76);
        String rhs_77 = null;
        String var1_78 = null;
        String var1_79 = null;
        var1_79 = str.concat(result);
        var1_78 = var1_79.concat(new String(","));
        rhs_77 = var1_78.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_77));
        Long tmpArg_v_85 = null;
        tmpArg_v_85 = pcmp.getObstime();
        printNatField(tmpArg_v_85);
        String rhs_86 = null;
        rhs_86 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_86));
        popNL();
        String rhs_90 = null;
        String var1_91 = null;
        var1_91 = str.concat(getNL());
        rhs_90 = var1_91.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_90));
        Boolean cond_95 = null;
        cond_95 = pcmp.hasPositionInfo();
        if(cond_95.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_98 = null;
            rhs_98 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_98));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitBUSdecl(IOmlBUSdecl pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        String tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getName();
        printStringField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        HashSet tmpArg_v_29 = new HashSet();
        tmpArg_v_29 = pcmp.getTopo();
        printSetofField(tmpArg_v_29);
        String rhs_30 = null;
        rhs_30 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_30));
        popNL();
        String rhs_34 = null;
        String var1_35 = null;
        var1_35 = str.concat(getNL());
        rhs_34 = var1_35.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_34));
        Boolean cond_39 = null;
        cond_39 = pcmp.hasPositionInfo();
        if(cond_39.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_42 = null;
            rhs_42 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_42));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitThreadKill(IOmlThreadKill pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        Long tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getCpunm();
        printNatField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        Long tmpArg_v_29 = null;
        tmpArg_v_29 = pcmp.getObstime();
        printNatField(tmpArg_v_29);
        String rhs_30 = null;
        rhs_30 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_30));
        popNL();
        String rhs_34 = null;
        String var1_35 = null;
        var1_35 = str.concat(getNL());
        rhs_34 = var1_35.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_34));
        Boolean cond_39 = null;
        cond_39 = pcmp.hasPositionInfo();
        if(cond_39.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_42 = null;
            rhs_42 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_42));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitOpActivate(IOmlOpActivate pcmp)
        throws CGException
    {
        String str = null;
        String var1_2 = null;
        String var2_4 = null;
        var2_4 = pcmp.identity();
        var1_2 = prefix.concat(var2_4);
        str = var1_2.concat(new String("("));
        pushNL();
        String rhs_7 = null;
        rhs_7 = str.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_7));
        Long tmpArg_v_11 = null;
        tmpArg_v_11 = pcmp.getId();
        printNatField(tmpArg_v_11);
        String rhs_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = str.concat(result);
        var1_13 = var1_14.concat(new String(","));
        rhs_12 = var1_13.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_12));
        String tmpArg_v_20 = null;
        tmpArg_v_20 = pcmp.getOpname();
        printStringField(tmpArg_v_20);
        String rhs_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        var1_23 = str.concat(result);
        var1_22 = var1_23.concat(new String(","));
        rhs_21 = var1_22.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_21));
        Boolean cond_28 = null;
        cond_28 = pcmp.hasObjref();
        if(cond_28.booleanValue())
        {
            Long tmpArg_v_31 = null;
            tmpArg_v_31 = pcmp.getObjref();
            printNatField(tmpArg_v_31);
        } else
        {
            result = UTIL.ConvertToString(UTIL.clone(new String("nil")));
        }
        String rhs_32 = null;
        String var1_33 = null;
        String var1_34 = null;
        var1_34 = str.concat(result);
        var1_33 = var1_34.concat(new String(","));
        rhs_32 = var1_33.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_32));
        String tmpArg_v_40 = null;
        tmpArg_v_40 = pcmp.getClnm();
        printStringField(tmpArg_v_40);
        String rhs_41 = null;
        String var1_42 = null;
        String var1_43 = null;
        var1_43 = str.concat(result);
        var1_42 = var1_43.concat(new String(","));
        rhs_41 = var1_42.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_41));
        Long tmpArg_v_49 = null;
        tmpArg_v_49 = pcmp.getCpunm();
        printNatField(tmpArg_v_49);
        String rhs_50 = null;
        String var1_51 = null;
        String var1_52 = null;
        var1_52 = str.concat(result);
        var1_51 = var1_52.concat(new String(","));
        rhs_50 = var1_51.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_50));
        Boolean tmpArg_v_58 = null;
        tmpArg_v_58 = pcmp.getAsynchronous();
        printBoolField(tmpArg_v_58);
        String rhs_59 = null;
        String var1_60 = null;
        String var1_61 = null;
        var1_61 = str.concat(result);
        var1_60 = var1_61.concat(new String(","));
        rhs_59 = var1_60.concat(getNL());
        str = UTIL.ConvertToString(UTIL.clone(rhs_59));
        Long tmpArg_v_67 = null;
        tmpArg_v_67 = pcmp.getObstime();
        printNatField(tmpArg_v_67);
        String rhs_68 = null;
        rhs_68 = str.concat(result);
        str = UTIL.ConvertToString(UTIL.clone(rhs_68));
        popNL();
        String rhs_72 = null;
        String var1_73 = null;
        var1_73 = str.concat(getNL());
        rhs_72 = var1_73.concat(new String(")"));
        str = UTIL.ConvertToString(UTIL.clone(rhs_72));
        Boolean cond_77 = null;
        cond_77 = pcmp.hasPositionInfo();
        if(cond_77.booleanValue())
        {
            printPositionInfo(pcmp);
            String rhs_80 = null;
            rhs_80 = str.concat(result);
            str = UTIL.ConvertToString(UTIL.clone(rhs_80));
        }
        result = UTIL.ConvertToString(UTIL.clone(str));
    }

    public void visitTraceEvent(IOmlTraceEvent pNode)
        throws CGException
    {
        pNode.accept(this);
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    public String result;
    private Long lvl;
    private static final String nl = new String("\r\n");
    private static final String prefix = new String("new Oml");

}