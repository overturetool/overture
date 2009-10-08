// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:52
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   codegenVisitor.java

package org.overturetool.tools.astgen.vdm;

import java.io.*;
import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstVisitor, AstDefinitions, AstShorthand, AstType, 
//            AstComposite, AstField, AstTypeName, AstOptionalType, 
//            AstSeqOfType, AstSetOfType, AstMapType, AstQuotedType, 
//            AstUnionType

public class codegenVisitor extends AstVisitor
{

    public codegenVisitor()
        throws CGException
    {
        files = new HashMap();
        IntegerStr = null;
        try
        {
            files = new HashMap();
            IntegerStr = new String("Integer");
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public String nat2str(Integer pval)
        throws CGException
    {
        if((new Boolean(pval.intValue() <= (new Integer(9)).intValue())).booleanValue())
        {
            String rexpr_14 = null;
            Character e_seq_15 = null;
            if(1 <= (new Integer(pval.intValue() + (new Integer(1)).intValue())).intValue() && (new Integer(pval.intValue() + (new Integer(1)).intValue())).intValue() <= digits.length())
                e_seq_15 = new Character(digits.charAt((new Integer(pval.intValue() + (new Integer(1)).intValue())).intValue() - 1));
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
            rexpr_14 = new String();
            rexpr_14 = (new StringBuilder(String.valueOf(rexpr_14))).append(e_seq_15).toString();
            return rexpr_14;
        } else
        {
            String rexpr_5 = null;
            rexpr_5 = nat2str(new Integer(pval.intValue() / (new Integer(10)).intValue())).concat(nat2str(new Integer((int)(pval.doubleValue() - (new Integer(10)).doubleValue() * Math.floor(pval.doubleValue() / (new Integer(10)).doubleValue())))));
            return rexpr_5;
        }
    }

    public void setLong()
        throws CGException
    {
        IntegerStr = UTIL.ConvertToString(UTIL.clone(new String("Long")));
    }

    private Integer createFile(Vector var_1_1)
        throws CGException
    {
        String path = "";
        for(int cnt = 0; cnt < var_1_1.size(); cnt++)
        {
            if(cnt > 0)
                path = (new StringBuilder(String.valueOf(path))).append(File.separator).toString();
            path = (new StringBuilder(String.valueOf(path))).append((String)var_1_1.elementAt(cnt)).toString();
        }

        File vnd = new File(path);
        if(vnd.exists())
            System.out.println((new StringBuilder("File \"")).append(path).append("\" does already exist!").toString());
        else
            try
            {
                if(vnd.createNewFile())
                {
                    System.out.println((new StringBuilder("Created new file \"")).append(path).append("\"").toString());
                    Integer key = new Integer(files.size());
                    FileWriter fwr = new FileWriter(vnd);
                    files.put(key, fwr);
                } else
                {
                    System.out.println((new StringBuilder("Creating new file \"")).append(path).append("\" failed!").toString());
                }
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        return new Integer(files.size() - 1);
    }

    private void printFile(Integer var_1_1, String var_2_2)
        throws CGException
    {
        if(files.containsKey(var_1_1))
        {
            FileWriter fwr = (FileWriter)files.get(var_1_1);
            try
            {
                fwr.write(var_2_2);
            }
            catch(IOException ioe)
            {
                ioe.printStackTrace();
            }
        } else
        {
            System.out.println((new StringBuilder("Could not find file identifier ")).append(UTIL.ConvertToString(var_1_1)).toString());
        }
    }

    private void closeFiles()
        throws CGException
    {
        for(int cnt = 0; cnt < files.size(); cnt++)
        {
            Integer key = new Integer(cnt);
            if(files.containsKey(key))
            {
                FileWriter fwr = (FileWriter)files.get(key);
                try
                {
                    fwr.close();
                }
                catch(IOException ioe)
                {
                    ioe.printStackTrace();
                }
            } else
            {
                System.out.println((new StringBuilder("Could not find file identifier ")).append(UTIL.ConvertToString(key)).toString());
            }
        }

    }

    private Boolean createDirectory(String var_1_1, Vector var_2_2)
        throws CGException
    {
        boolean res = true;
        String path;
        if(var_1_1.length() > 0)
        {
            path = var_1_1;
            File dir = new File(path);
            res = dir.exists();
            if(res)
            {
                res = dir.isDirectory();
                if(!res)
                    System.out.println((new StringBuilder("\"")).append(path).append("\" is not a directory!").toString());
            } else
            {
                System.out.println((new StringBuilder("Root directory \"")).append(path).append("\" does not exist!").toString());
            }
        } else
        {
            path = ".";
        }
        for(int cnt = 0; cnt < var_2_2.size() && res; cnt++)
        {
            path = (new StringBuilder(String.valueOf(path))).append(File.separator).append((String)var_2_2.elementAt(cnt)).toString();
            File vnd = new File(path);
            if(vnd.exists())
            {
                res = vnd.isDirectory();
            } else
            {
                res = vnd.mkdir();
                if(res)
                    System.out.println((new StringBuilder("Created directory ")).append(path).toString());
                else
                    System.out.println((new StringBuilder("Failed to create directory ")).append(path).toString());
            }
        }

        return new Boolean(true);
    }

    private void createInterfaces(AstDefinitions ad)
        throws CGException
    {
        print(new String("Creating interfaces..."));
        String tmpVal_5 = null;
        tmpVal_5 = ad.getDirectory();
        String base = null;
        base = tmpVal_5;
        Vector root = null;
        Vector var1_8 = null;
        Vector var1_9 = null;
        var1_9 = new Vector();
        var1_9.add(new String("src"));
        Vector var2_11 = null;
        var2_11 = ad.getRawPackage();
        var1_8 = (Vector)var1_9.clone();
        var1_8.addAll(var2_11);
        Vector var2_12 = null;
        var2_12 = new Vector();
        var2_12.add(new String("itf"));
        root = (Vector)var1_8.clone();
        root.addAll(var2_12);
        if(createDirectory(base, root).booleanValue())
        {
            Vector tmpArg_v_19 = null;
            Vector var1_20 = null;
            var1_20 = new Vector();
            var1_20.add(base);
            tmpArg_v_19 = (Vector)var1_20.clone();
            tmpArg_v_19.addAll(root);
            createInterface(ad, tmpArg_v_19);
        }
    }

    private void createInterface(AstDefinitions ad, Vector path)
        throws CGException
    {
        String docnm = null;
        String var1_5 = null;
        String var2_7 = null;
        var2_7 = ad.getPrefix();
        var1_5 = (new String("I")).concat(var2_7);
        docnm = var1_5.concat(new String("Document"));
        String tmpVal_10 = null;
        String var1_11 = null;
        String var2_13 = null;
        var2_13 = ad.getPrefix();
        var1_11 = (new String("I")).concat(var2_13);
        tmpVal_10 = var1_11.concat(new String("Lexem"));
        String lexnm = null;
        lexnm = tmpVal_10;
        String tmpVal_16 = null;
        String var1_17 = null;
        String var2_19 = null;
        var2_19 = ad.getPrefix();
        var1_17 = (new String("I")).concat(var2_19);
        tmpVal_16 = var1_17.concat(new String("Node"));
        String basenm = null;
        basenm = tmpVal_16;
        String basevisit = null;
        String var1_23 = null;
        String var2_25 = null;
        var2_25 = ad.getPrefix();
        var1_23 = (new String("I")).concat(var2_25);
        basevisit = var1_23.concat(new String("Visitor"));
        String baseinfo = null;
        String var1_29 = null;
        String var2_31 = null;
        var2_31 = ad.getPrefix();
        var1_29 = (new String("I")).concat(var2_31);
        baseinfo = var1_29.concat(new String("ContextInfo"));
        Integer fid = null;
        Vector par_33 = null;
        Vector var2_35 = null;
        String e_seq_36 = null;
        e_seq_36 = docnm.concat(new String(".java"));
        var2_35 = new Vector();
        var2_35.add(e_seq_36);
        par_33 = (Vector)path.clone();
        par_33.addAll(var2_35);
        fid = createFile(par_33);
        print(new String("Creating document, lexem, base class and visitor interfaces..."));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createDocumentInterface(ad, fid, docnm);
        Integer rhs_48 = null;
        Vector par_49 = null;
        Vector var2_51 = null;
        String e_seq_52 = null;
        e_seq_52 = basenm.concat(new String(".java"));
        var2_51 = new Vector();
        var2_51.add(e_seq_52);
        par_49 = (Vector)path.clone();
        par_49.addAll(var2_51);
        rhs_48 = createFile(par_49);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_48));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createBaseNode(ad, fid, basenm);
        Integer rhs_62 = null;
        Vector par_63 = null;
        Vector var2_65 = null;
        String e_seq_66 = null;
        e_seq_66 = basevisit.concat(new String(".java"));
        var2_65 = new Vector();
        var2_65.add(e_seq_66);
        par_63 = (Vector)path.clone();
        par_63.addAll(var2_65);
        rhs_62 = createFile(par_63);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_62));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createBaseVisit(ad, fid, basevisit);
        Integer rhs_76 = null;
        Vector par_77 = null;
        Vector var2_79 = null;
        String e_seq_80 = null;
        e_seq_80 = baseinfo.concat(new String(".java"));
        var2_79 = new Vector();
        var2_79.add(e_seq_80);
        par_77 = (Vector)path.clone();
        par_77.addAll(var2_79);
        rhs_76 = createFile(par_77);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_76));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createContextInfo(ad, fid, baseinfo);
        Integer rhs_90 = null;
        Vector par_91 = null;
        Vector var2_93 = null;
        String e_seq_94 = null;
        e_seq_94 = lexnm.concat(new String(".java"));
        var2_93 = new Vector();
        var2_93.add(e_seq_94);
        par_91 = (Vector)path.clone();
        par_91.addAll(var2_93);
        rhs_90 = createFile(par_91);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_90));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createLexemInterface(ad, fid, lexnm);
        print(new String("Creating composites..."));
        HashSet iset_106 = new HashSet();
        iset_106 = ad.getComposites();
        String composite = null;
        for(Iterator enm_131 = iset_106.iterator(); enm_131.hasNext();)
        {
            String elem_107 = UTIL.ConvertToString(enm_131.next());
            composite = elem_107;
            String tmpVal_111 = null;
            String var1_112 = null;
            String var2_114 = null;
            var2_114 = ad.getPrefix();
            var1_112 = (new String("I")).concat(var2_114);
            tmpVal_111 = var1_112.concat(composite);
            String clnm = null;
            clnm = tmpVal_111;
            Integer rhs_116 = null;
            Vector par_117 = null;
            Vector var2_119 = null;
            String e_seq_120 = null;
            e_seq_120 = clnm.concat(new String(".java"));
            var2_119 = new Vector();
            var2_119.add(e_seq_120);
            par_117 = (Vector)path.clone();
            par_117.addAll(var2_119);
            rhs_116 = createFile(par_117);
            fid = UTIL.NumberToInt(UTIL.clone(rhs_116));
            if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            {
                AstComposite tmpArg_v_128 = null;
                tmpArg_v_128 = ad.getComposite(composite);
                createDefInt(ad, tmpArg_v_128, fid);
            }
        }

        print(new String("Creating shorthands..."));
        HashSet iset_134 = new HashSet();
        iset_134 = ad.getShorthands();
        String shorthand = null;
        for(Iterator enm_164 = iset_134.iterator(); enm_164.hasNext();)
        {
            String elem_135 = UTIL.ConvertToString(enm_164.next());
            shorthand = elem_135;
            Boolean cond_138 = null;
            AstType obj_139 = null;
            AstShorthand obj_140 = null;
            obj_140 = ad.getShorthand(shorthand);
            obj_139 = obj_140.getType();
            cond_138 = obj_139.isUnionType();
            if(cond_138.booleanValue())
            {
                String tmpVal_143 = null;
                String var1_144 = null;
                String var2_146 = null;
                var2_146 = ad.getPrefix();
                var1_144 = (new String("I")).concat(var2_146);
                tmpVal_143 = var1_144.concat(shorthand);
                String clnm = null;
                clnm = tmpVal_143;
                Integer rhs_148 = null;
                Vector par_149 = null;
                Vector var2_151 = null;
                String e_seq_152 = null;
                e_seq_152 = clnm.concat(new String(".java"));
                var2_151 = new Vector();
                var2_151.add(e_seq_152);
                par_149 = (Vector)path.clone();
                par_149.addAll(var2_151);
                rhs_148 = createFile(par_149);
                fid = UTIL.NumberToInt(UTIL.clone(rhs_148));
                if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
                {
                    AstShorthand tmpArg_v_160 = null;
                    tmpArg_v_160 = ad.getShorthand(shorthand);
                    createShInt(ad, tmpArg_v_160, path, fid);
                }
            }
        }

    }

    private void createLexemInterface(AstDefinitions ad, Integer fid, String lexnm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = createPackage(ad, new String("itf")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_13 = null;
        String var1_14 = null;
        var1_14 = (new String("import jp.co.csk.vdm.toolbox.VDM.*;")).concat(nl);
        tmpArg_v_13 = var1_14.concat(nl);
        printFile(fid, tmpArg_v_13);
        String tmpArg_v_20 = null;
        String var1_21 = null;
        var1_21 = (new String("public abstract interface ")).concat(lexnm);
        tmpArg_v_20 = var1_21.concat(nl);
        printFile(fid, tmpArg_v_20);
        String tmpArg_v_27 = null;
        tmpArg_v_27 = (new String("{")).concat(nl);
        printFile(fid, tmpArg_v_27);
        String tmpArg_v_32 = null;
        tmpArg_v_32 = (new String("\tLong ILEXEMUNKNOWN      = new Long(0);")).concat(nl);
        printFile(fid, tmpArg_v_32);
        String tmpArg_v_37 = null;
        tmpArg_v_37 = (new String("\tLong ILEXEMKEYWORD      = new Long(1);")).concat(nl);
        printFile(fid, tmpArg_v_37);
        String tmpArg_v_42 = null;
        tmpArg_v_42 = (new String("\tLong ILEXEMIDENTIFIER   = new Long(2);")).concat(nl);
        printFile(fid, tmpArg_v_42);
        String tmpArg_v_47 = null;
        tmpArg_v_47 = (new String("\tLong ILEXEMLINECOMMENT  = new Long(3);")).concat(nl);
        printFile(fid, tmpArg_v_47);
        String tmpArg_v_52 = null;
        tmpArg_v_52 = (new String("\tLong ILEXEMBLOCKCOMMENT = new Long(4);")).concat(nl);
        printFile(fid, tmpArg_v_52);
        String tmpArg_v_57 = null;
        String var1_58 = null;
        String var1_59 = null;
        String var2_61 = null;
        var2_61 = ad.getPrefix();
        var1_59 = (new String("\tabstract void accept(I")).concat(var2_61);
        var1_58 = var1_59.concat(new String("Visitor theVisitor) throws CGException;"));
        tmpArg_v_57 = var1_58.concat(nl);
        printFile(fid, tmpArg_v_57);
        String tmpArg_v_66 = null;
        tmpArg_v_66 = (new String("\tabstract Long getLine() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_66);
        String tmpArg_v_71 = null;
        tmpArg_v_71 = (new String("\tabstract Long getColumn() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_71);
        String tmpArg_v_76 = null;
        tmpArg_v_76 = (new String("\tabstract Long getLexval() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_76);
        String tmpArg_v_81 = null;
        tmpArg_v_81 = (new String("\tabstract String getText() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_81);
        String tmpArg_v_86 = null;
        tmpArg_v_86 = (new String("\tabstract Long getType() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_86);
        String tmpArg_v_91 = null;
        tmpArg_v_91 = (new String("\tabstract Boolean isKeyword() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_91);
        String tmpArg_v_96 = null;
        tmpArg_v_96 = (new String("\tabstract Boolean isIdentifier() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_96);
        String tmpArg_v_101 = null;
        tmpArg_v_101 = (new String("\tabstract Boolean isComment() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_101);
        String tmpArg_v_106 = null;
        tmpArg_v_106 = (new String("\tabstract Boolean isLineComment() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_106);
        String tmpArg_v_111 = null;
        tmpArg_v_111 = (new String("\tabstract Boolean isBlockComment() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_111);
        String tmpArg_v_116 = null;
        String var1_117 = null;
        var1_117 = (new String("}")).concat(nl);
        tmpArg_v_116 = var1_117.concat(nl);
        printFile(fid, tmpArg_v_116);
    }

    private void createDocumentInterface(AstDefinitions ad, Integer fid, String docnm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = createPackage(ad, new String("itf")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_13 = null;
        tmpArg_v_13 = (new String("import java.util.*;")).concat(nl);
        printFile(fid, tmpArg_v_13);
        String tmpArg_v_18 = null;
        String var1_19 = null;
        var1_19 = (new String("import jp.co.csk.vdm.toolbox.VDM.*;")).concat(nl);
        tmpArg_v_18 = var1_19.concat(nl);
        printFile(fid, tmpArg_v_18);
        String tmpArg_v_25 = null;
        String var1_26 = null;
        var1_26 = (new String("public abstract interface ")).concat(docnm);
        tmpArg_v_25 = var1_26.concat(nl);
        printFile(fid, tmpArg_v_25);
        String tmpArg_v_32 = null;
        tmpArg_v_32 = (new String("{")).concat(nl);
        printFile(fid, tmpArg_v_32);
        String tmpArg_v_37 = null;
        tmpArg_v_37 = (new String("\tabstract String getFilename() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_37);
        Vector sq_40 = null;
        sq_40 = ad.getTop();
        String name = null;
        String tmpArg_v_59;
        for(Iterator enm_71 = sq_40.iterator(); enm_71.hasNext(); printFile(fid, tmpArg_v_59))
        {
            String elem_41 = UTIL.ConvertToString(enm_71.next());
            name = elem_41;
            String pname = null;
            String var1_46 = null;
            var1_46 = ad.getPrefix();
            pname = var1_46.concat(name);
            String tmpArg_v_50 = null;
            String var1_51 = null;
            String var1_52 = null;
            var1_52 = (new String("\tabstract Boolean has")).concat(name);
            var1_51 = var1_52.concat(new String("() throws CGException;"));
            tmpArg_v_50 = var1_51.concat(nl);
            printFile(fid, tmpArg_v_50);
            tmpArg_v_59 = null;
            String var1_60 = null;
            String var1_61 = null;
            String var1_62 = null;
            String var1_63 = null;
            var1_63 = (new String("\tabstract I")).concat(pname);
            var1_62 = var1_63.concat(new String(" get"));
            var1_61 = var1_62.concat(name);
            var1_60 = var1_61.concat(new String("() throws CGException;"));
            tmpArg_v_59 = var1_60.concat(nl);
        }

        String tmpArg_v_74 = null;
        tmpArg_v_74 = (new String("\tabstract Vector getLexems() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_74);
        String tmpArg_v_79 = null;
        tmpArg_v_79 = (new String("\tabstract String toVdmSlValue() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_79);
        String tmpArg_v_84 = null;
        tmpArg_v_84 = (new String("\tabstract String toVdmPpValue() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_84);
        String tmpArg_v_89 = null;
        String var1_90 = null;
        String var1_91 = null;
        String var2_93 = null;
        var2_93 = ad.getPrefix();
        var1_91 = (new String("\tabstract void accept(I")).concat(var2_93);
        var1_90 = var1_91.concat(new String("Visitor theVisitor) throws CGException;"));
        tmpArg_v_89 = var1_90.concat(nl);
        printFile(fid, tmpArg_v_89);
        String tmpArg_v_98 = null;
        String var1_99 = null;
        var1_99 = (new String("}")).concat(nl);
        tmpArg_v_98 = var1_99.concat(nl);
        printFile(fid, tmpArg_v_98);
    }

    private String createPackage(AstDefinitions ad, String str)
        throws CGException
    {
        String pack = null;
        pack = ad.getPackage();
        String name = null;
        if((new Boolean(UTIL.equals(pack, new Vector()))).booleanValue())
        {
            name = str;
        } else
        {
            String var1_11 = null;
            var1_11 = pack.concat(new String("."));
            name = var1_11.concat(str);
        }
        String rule = null;
        String var1_17 = null;
        String var1_18 = null;
        var1_18 = (new String("package ")).concat(name);
        var1_17 = var1_18.concat(new String(";"));
        rule = var1_17.concat(nl);
        return rule;
    }

    private void createBaseNode(AstDefinitions ad, Integer fid, String basenm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = createPackage(ad, new String("itf")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_13 = null;
        String var1_14 = null;
        var1_14 = (new String("import jp.co.csk.vdm.toolbox.VDM.*;")).concat(nl);
        tmpArg_v_13 = var1_14.concat(nl);
        printFile(fid, tmpArg_v_13);
        String tmpArg_v_20 = null;
        String var1_21 = null;
        var1_21 = (new String("public abstract interface ")).concat(basenm);
        tmpArg_v_20 = var1_21.concat(nl);
        printFile(fid, tmpArg_v_20);
        String tmpArg_v_27 = null;
        tmpArg_v_27 = (new String("{")).concat(nl);
        printFile(fid, tmpArg_v_27);
        String tmpArg_v_32 = null;
        tmpArg_v_32 = (new String("\tabstract String identity() throws CGException;")).concat(nl);
        printFile(fid, tmpArg_v_32);
        String tmpArg_v_37 = null;
        String var1_38 = null;
        String var1_39 = null;
        String var2_41 = null;
        var2_41 = ad.getPrefix();
        var1_39 = (new String("\tabstract void accept(I")).concat(var2_41);
        var1_38 = var1_39.concat(new String("Visitor theVisitor) throws CGException;"));
        tmpArg_v_37 = var1_38.concat(nl);
        printFile(fid, tmpArg_v_37);
        String tmpArg_v_46 = null;
        String var1_47 = null;
        String var1_48 = null;
        var1_48 = (new String("\tabstract ")).concat(IntegerStr);
        var1_47 = var1_48.concat(new String(" getLine () throws CGException;"));
        tmpArg_v_46 = var1_47.concat(nl);
        printFile(fid, tmpArg_v_46);
        String tmpArg_v_55 = null;
        String var1_56 = null;
        String var1_57 = null;
        var1_57 = (new String("\tabstract ")).concat(IntegerStr);
        var1_56 = var1_57.concat(new String(" getColumn () throws CGException;"));
        tmpArg_v_55 = var1_56.concat(nl);
        printFile(fid, tmpArg_v_55);
        String tmpArg_v_64 = null;
        String var1_65 = null;
        String var1_66 = null;
        String var1_67 = null;
        String var1_68 = null;
        var1_68 = (new String("\tabstract void setPosition(")).concat(IntegerStr);
        var1_67 = var1_68.concat(new String(" iLine, "));
        var1_66 = var1_67.concat(IntegerStr);
        var1_65 = var1_66.concat(new String(" iColumn) throws CGException;"));
        tmpArg_v_64 = var1_65.concat(nl);
        printFile(fid, tmpArg_v_64);
        String tmpArg_v_77 = null;
        String var1_78 = null;
        String var1_79 = null;
        String var2_81 = null;
        var2_81 = ad.getPrefix();
        var1_79 = (new String("\tabstract void setPosLexem(I")).concat(var2_81);
        var1_78 = var1_79.concat(new String("Lexem iLexem) throws CGException;"));
        tmpArg_v_77 = var1_78.concat(nl);
        printFile(fid, tmpArg_v_77);
        String tmpArg_v_86 = null;
        String var1_87 = null;
        String var1_88 = null;
        String var2_90 = null;
        var2_90 = ad.getPrefix();
        var1_88 = (new String("\tabstract void setPosNode(I")).concat(var2_90);
        var1_87 = var1_88.concat(new String("Node iNode) throws CGException;"));
        tmpArg_v_86 = var1_87.concat(nl);
        printFile(fid, tmpArg_v_86);
        String tmpArg_v_95 = null;
        String var1_96 = null;
        var1_96 = (new String("}")).concat(nl);
        tmpArg_v_95 = var1_96.concat(nl);
        printFile(fid, tmpArg_v_95);
    }

    private void createContextInfo(AstDefinitions ad, Integer fid, String basenm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = createPackage(ad, new String("itf")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_13 = null;
        String var1_14 = null;
        var1_14 = (new String("import jp.co.csk.vdm.toolbox.VDM.*;")).concat(nl);
        tmpArg_v_13 = var1_14.concat(nl);
        printFile(fid, tmpArg_v_13);
        String tmpArg_v_20 = null;
        String var1_21 = null;
        var1_21 = (new String("public abstract interface ")).concat(basenm);
        tmpArg_v_20 = var1_21.concat(nl);
        printFile(fid, tmpArg_v_20);
        String tmpArg_v_27 = null;
        tmpArg_v_27 = (new String("{")).concat(nl);
        printFile(fid, tmpArg_v_27);
        String tmpArg_v_32 = null;
        String var1_33 = null;
        String var1_34 = null;
        String var2_36 = null;
        var2_36 = ad.getPrefix();
        var1_34 = (new String("\tabstract void accept(I")).concat(var2_36);
        var1_33 = var1_34.concat(new String("Visitor theVisitor) throws CGException;"));
        tmpArg_v_32 = var1_33.concat(nl);
        printFile(fid, tmpArg_v_32);
        String tmpArg_v_41 = null;
        String var1_42 = null;
        var1_42 = (new String("}")).concat(nl);
        tmpArg_v_41 = var1_42.concat(nl);
        printFile(fid, tmpArg_v_41);
    }

    private void createBaseVisit(AstDefinitions ad, Integer fid, String basenm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = createPackage(ad, new String("itf")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_13 = null;
        String var1_14 = null;
        var1_14 = (new String("import jp.co.csk.vdm.toolbox.VDM.*;")).concat(nl);
        tmpArg_v_13 = var1_14.concat(nl);
        printFile(fid, tmpArg_v_13);
        String tmpArg_v_20 = null;
        String var1_21 = null;
        var1_21 = (new String("public abstract interface ")).concat(basenm);
        tmpArg_v_20 = var1_21.concat(nl);
        printFile(fid, tmpArg_v_20);
        String tmpArg_v_27 = null;
        tmpArg_v_27 = (new String("{")).concat(nl);
        printFile(fid, tmpArg_v_27);
        String tmpArg_v_32 = null;
        String var1_33 = null;
        String var1_34 = null;
        String var2_36 = null;
        var2_36 = ad.getPrefix();
        var1_34 = (new String("\tabstract void visitContextInfo(I")).concat(var2_36);
        var1_33 = var1_34.concat(new String("ContextInfo theInfo) throws CGException;"));
        tmpArg_v_32 = var1_33.concat(nl);
        printFile(fid, tmpArg_v_32);
        String tmpArg_v_41 = null;
        String var1_42 = null;
        String var1_43 = null;
        String var2_45 = null;
        var2_45 = ad.getPrefix();
        var1_43 = (new String("\tabstract void visitLexem(I")).concat(var2_45);
        var1_42 = var1_43.concat(new String("Lexem theLexem) throws CGException;"));
        tmpArg_v_41 = var1_42.concat(nl);
        printFile(fid, tmpArg_v_41);
        String tmpArg_v_50 = null;
        String var1_51 = null;
        String var1_52 = null;
        String var2_54 = null;
        var2_54 = ad.getPrefix();
        var1_52 = (new String("\tabstract void visitNode(I")).concat(var2_54);
        var1_51 = var1_52.concat(new String("Node theNode) throws CGException;"));
        tmpArg_v_50 = var1_51.concat(nl);
        printFile(fid, tmpArg_v_50);
        String tmpArg_v_59 = null;
        String var1_60 = null;
        String var1_61 = null;
        String var2_63 = null;
        var2_63 = ad.getPrefix();
        var1_61 = (new String("\tabstract void visitDocument(I")).concat(var2_63);
        var1_60 = var1_61.concat(new String("Document theDocument) throws CGException;"));
        tmpArg_v_59 = var1_60.concat(nl);
        printFile(fid, tmpArg_v_59);
        HashSet iset_66 = new HashSet();
        iset_66 = ad.getComposites();
        String id = null;
        String tmpArg_v_72;
        for(Iterator enm_85 = iset_66.iterator(); enm_85.hasNext(); printFile(fid, tmpArg_v_72))
        {
            String elem_67 = UTIL.ConvertToString(enm_85.next());
            id = elem_67;
            tmpArg_v_72 = null;
            String var1_73 = null;
            String var1_74 = null;
            String var1_75 = null;
            String var1_76 = null;
            String var1_77 = null;
            var1_77 = (new String("\tabstract void visit")).concat(id);
            var1_76 = var1_77.concat(new String("(I"));
            String var2_81 = null;
            var2_81 = ad.getPrefix();
            var1_75 = var1_76.concat(var2_81);
            var1_74 = var1_75.concat(id);
            var1_73 = var1_74.concat(new String(" theNode) throws CGException;"));
            tmpArg_v_72 = var1_73.concat(nl);
        }

        HashSet iset_86 = new HashSet();
        iset_86 = ad.getShorthands();
        id = null;
        for(Iterator enm_109 = iset_86.iterator(); enm_109.hasNext();)
        {
            String elem_87 = UTIL.ConvertToString(enm_109.next());
            id = elem_87;
            Boolean cond_90 = null;
            AstType obj_91 = null;
            AstShorthand obj_92 = null;
            obj_92 = ad.getShorthand(id);
            obj_91 = obj_92.getType();
            cond_90 = obj_91.isUnionType();
            if(cond_90.booleanValue())
            {
                String tmpArg_v_96 = null;
                String var1_97 = null;
                String var1_98 = null;
                String var1_99 = null;
                String var1_100 = null;
                String var1_101 = null;
                var1_101 = (new String("\tabstract void visit")).concat(id);
                var1_100 = var1_101.concat(new String("(I"));
                String var2_105 = null;
                var2_105 = ad.getPrefix();
                var1_99 = var1_100.concat(var2_105);
                var1_98 = var1_99.concat(id);
                var1_97 = var1_98.concat(new String(" theNode) throws CGException;"));
                tmpArg_v_96 = var1_97.concat(nl);
                printFile(fid, tmpArg_v_96);
            }
        }

        String tmpArg_v_112 = null;
        String var1_113 = null;
        var1_113 = (new String("}")).concat(nl);
        tmpArg_v_112 = var1_113.concat(nl);
        printFile(fid, tmpArg_v_112);
    }

    private void createDefInt(AstDefinitions ad, AstComposite composite, Integer fid)
        throws CGException
    {
        String tmpVal_5 = null;
        String var1_6 = null;
        String var2_8 = null;
        var2_8 = ad.getPrefix();
        var1_6 = (new String("I")).concat(var2_8);
        String var2_9 = null;
        var2_9 = composite.getName();
        tmpVal_5 = var1_6.concat(var2_9);
        String clnm = null;
        clnm = tmpVal_5;
        String tmpArg_v_12 = null;
        tmpArg_v_12 = createPackage(ad, new String("itf")).concat(nl);
        printFile(fid, tmpArg_v_12);
        Boolean cond_17 = null;
        cond_17 = composite.hasAdts();
        if(cond_17.booleanValue())
        {
            String tmpArg_v_20 = null;
            tmpArg_v_20 = (new String("import java.util.*;")).concat(nl);
            printFile(fid, tmpArg_v_20);
        }
        Boolean cond_23 = null;
        Integer var1_24 = null;
        Vector unArg_25 = null;
        unArg_25 = composite.getFields();
        var1_24 = new Integer(unArg_25.size());
        cond_23 = new Boolean(var1_24.intValue() > (new Integer(0)).intValue());
        if(cond_23.booleanValue())
        {
            String tmpArg_v_29 = null;
            String var1_30 = null;
            var1_30 = (new String("import jp.co.csk.vdm.toolbox.VDM.*;")).concat(nl);
            tmpArg_v_29 = var1_30.concat(nl);
            printFile(fid, tmpArg_v_29);
        }
        String tmpArg_v_36 = null;
        tmpArg_v_36 = (new String("public abstract interface ")).concat(clnm);
        printFile(fid, tmpArg_v_36);
        String tmpVal_40 = null;
        String var1_41 = null;
        String var2_43 = null;
        var2_43 = ad.getPrefix();
        var1_41 = (new String("I")).concat(var2_43);
        String var2_44 = null;
        Boolean cond_46 = null;
        String par_47 = null;
        par_47 = composite.getName();
        cond_46 = ad.hasInherit(par_47);
        if(cond_46.booleanValue())
        {
            String par_48 = null;
            par_48 = composite.getName();
            var2_44 = ad.getInherit(par_48);
        } else
        {
            var2_44 = new String("Node");
        }
        tmpVal_40 = var1_41.concat(var2_44);
        String base = null;
        base = tmpVal_40;
        String tmpArg_v_51 = null;
        String var1_52 = null;
        var1_52 = (new String(" extends ")).concat(base);
        tmpArg_v_51 = var1_52.concat(nl);
        printFile(fid, tmpArg_v_51);
        String tmpArg_v_58 = null;
        tmpArg_v_58 = (new String("{")).concat(nl);
        printFile(fid, tmpArg_v_58);
        Vector sq_61 = null;
        sq_61 = composite.getFields();
        AstField field = null;
        for(Iterator enm_92 = sq_61.iterator(); enm_92.hasNext();)
        {
            AstField elem_62 = (AstField)enm_92.next();
            field = elem_62;
            String tp = null;
            String par_65 = null;
            par_65 = ad.getPrefix();
            AstType par_66 = null;
            par_66 = field.getType();
            tp = getJavaType(par_65, par_66);
            String tmpArg_v_69 = null;
            String var1_70 = null;
            String var1_71 = null;
            String var1_72 = null;
            String var1_73 = null;
            var1_73 = (new String("\tabstract ")).concat(tp);
            var1_72 = var1_73.concat(new String(" get"));
            String var2_77 = null;
            var2_77 = field.getName();
            var1_71 = var1_72.concat(var2_77);
            var1_70 = var1_71.concat(new String("() throws CGException;"));
            tmpArg_v_69 = var1_70.concat(nl);
            printFile(fid, tmpArg_v_69);
            Boolean cond_80 = null;
            AstType obj_81 = null;
            obj_81 = field.getType();
            cond_80 = obj_81.isOptionalType();
            if(cond_80.booleanValue())
            {
                String tmpArg_v_84 = null;
                String var1_85 = null;
                String var1_86 = null;
                String var2_88 = null;
                var2_88 = field.getName();
                var1_86 = (new String("\tabstract Boolean has")).concat(var2_88);
                var1_85 = var1_86.concat(new String("() throws CGException;"));
                tmpArg_v_84 = var1_85.concat(nl);
                printFile(fid, tmpArg_v_84);
            }
        }

        String tmpArg_v_95 = null;
        String var1_96 = null;
        var1_96 = (new String("}")).concat(nl);
        tmpArg_v_95 = var1_96.concat(nl);
        printFile(fid, tmpArg_v_95);
    }

    private void createShInt(AstDefinitions ad, AstShorthand shorthand, Vector path, Integer fid)
        throws CGException
    {
        String tmpVal_6 = null;
        String var1_7 = null;
        String var2_9 = null;
        var2_9 = ad.getPrefix();
        var1_7 = (new String("I")).concat(var2_9);
        String var2_10 = null;
        var2_10 = shorthand.getName();
        tmpVal_6 = var1_7.concat(var2_10);
        String clnm = null;
        clnm = tmpVal_6;
        Tuple tmpVal_12 = new Tuple(2);
        AstType obj_13 = null;
        obj_13 = shorthand.getType();
        tmpVal_12 = obj_13.isQuotedTypeUnion();
        Boolean rb = null;
        HashSet rs = new HashSet();
        boolean succ_11 = true;
        Vector e_l_14 = new Vector();
        for(int i_15 = 1; i_15 <= tmpVal_12.Length(); i_15++)
            e_l_14.add(tmpVal_12.GetField(i_15));

        if(succ_11 = 2 == e_l_14.size())
        {
            rb = (Boolean)e_l_14.get(0);
            rs = (HashSet)(HashSet)e_l_14.get(1);
        }
        if(!succ_11)
            UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
        String tmpArg_v_18 = null;
        tmpArg_v_18 = createPackage(ad, new String("itf")).concat(nl);
        printFile(fid, tmpArg_v_18);
        if(rb.booleanValue())
        {
            String tmpArg_v_26 = null;
            String var1_27 = null;
            var1_27 = (new String("import jp.co.csk.vdm.toolbox.VDM.*;")).concat(nl);
            tmpArg_v_26 = var1_27.concat(nl);
            printFile(fid, tmpArg_v_26);
        }
        String tmpArg_v_33 = null;
        tmpArg_v_33 = (new String("public abstract interface ")).concat(clnm);
        printFile(fid, tmpArg_v_33);
        String tmpVal_37 = null;
        String var1_38 = null;
        String var2_40 = null;
        var2_40 = ad.getPrefix();
        var1_38 = (new String("I")).concat(var2_40);
        String var2_41 = null;
        Boolean cond_43 = null;
        String par_44 = null;
        par_44 = shorthand.getName();
        cond_43 = ad.hasInherit(par_44);
        if(cond_43.booleanValue())
        {
            String par_45 = null;
            par_45 = shorthand.getName();
            var2_41 = ad.getInherit(par_45);
        } else
        {
            var2_41 = new String("Node");
        }
        tmpVal_37 = var1_38.concat(var2_41);
        String base = null;
        base = tmpVal_37;
        String tmpArg_v_48 = null;
        String var1_49 = null;
        var1_49 = (new String(" extends ")).concat(base);
        tmpArg_v_48 = var1_49.concat(nl);
        printFile(fid, tmpArg_v_48);
        String tmpArg_v_55 = null;
        tmpArg_v_55 = (new String("{")).concat(nl);
        printFile(fid, tmpArg_v_55);
        if(rb.booleanValue())
        {
            createShQuotedTypeInt(ad, clnm, path, rs);
            String tmpArg_v_66 = null;
            String var1_67 = null;
            String var1_68 = null;
            var1_68 = (new String("\tabstract void setValue(")).concat(IntegerStr);
            var1_67 = var1_68.concat(new String(" val) throws CGException;"));
            tmpArg_v_66 = var1_67.concat(nl);
            printFile(fid, tmpArg_v_66);
            String tmpArg_v_75 = null;
            String var1_76 = null;
            String var1_77 = null;
            var1_77 = (new String("\tabstract ")).concat(IntegerStr);
            var1_76 = var1_77.concat(new String(" getValue() throws CGException;"));
            tmpArg_v_75 = var1_76.concat(nl);
            printFile(fid, tmpArg_v_75);
            String tmpArg_v_84 = null;
            tmpArg_v_84 = (new String("\tabstract String getStringValue() throws CGException;")).concat(nl);
            printFile(fid, tmpArg_v_84);
        }
        String tmpArg_v_89 = null;
        String var1_90 = null;
        var1_90 = (new String("}")).concat(nl);
        tmpArg_v_89 = var1_90.concat(nl);
        printFile(fid, tmpArg_v_89);
    }

    private void createShQuotedTypeInt(AstDefinitions ad, String shnm, Vector path, HashSet qtnms)
        throws CGException
    {
        String tmpVal_6 = null;
        tmpVal_6 = shnm.concat(new String("Quotes"));
        String clnm = null;
        clnm = tmpVal_6;
        Integer fid = null;
        Vector par_9 = null;
        Vector var2_11 = null;
        String e_seq_12 = null;
        e_seq_12 = clnm.concat(new String(".java"));
        var2_11 = new Vector();
        var2_11.add(e_seq_12);
        par_9 = (Vector)path.clone();
        par_9.addAll(var2_11);
        fid = createFile(par_9);
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
        {
            Integer cnt = new Integer(0);
            String tmpArg_v_20 = null;
            String var1_21 = null;
            var1_21 = createPackage(ad, new String("itf")).concat(nl);
            tmpArg_v_20 = var1_21.concat(nl);
            printFile(fid, tmpArg_v_20);
            String tmpArg_v_29 = null;
            String var1_30 = null;
            String var1_31 = null;
            var1_31 = (new String("public interface ")).concat(clnm);
            var1_30 = var1_31.concat(new String("{"));
            tmpArg_v_29 = var1_30.concat(nl);
            printFile(fid, tmpArg_v_29);
            String id = null;
            for(Iterator enm_65 = qtnms.iterator(); enm_65.hasNext();)
            {
                String elem_37 = UTIL.ConvertToString(enm_65.next());
                id = elem_37;
                String tmpArg_v_42 = null;
                String var1_43 = null;
                String var1_44 = null;
                String var1_45 = null;
                String var1_46 = null;
                String var1_47 = null;
                String var1_48 = null;
                String var1_49 = null;
                String var1_50 = null;
                var1_50 = (new String("  public final ")).concat(IntegerStr);
                var1_49 = var1_50.concat(new String(" IQ"));
                var1_48 = var1_49.concat(id);
                var1_47 = var1_48.concat(new String(" = new "));
                var1_46 = var1_47.concat(IntegerStr);
                var1_45 = var1_46.concat(new String("("));
                var1_44 = var1_45.concat(nat2str(cnt));
                var1_43 = var1_44.concat(new String(");"));
                tmpArg_v_42 = var1_43.concat(nl);
                printFile(fid, tmpArg_v_42);
                cnt = UTIL.NumberToInt(UTIL.clone(new Integer(cnt.intValue() + (new Integer(1)).intValue())));
            }

            String tmpArg_v_68 = null;
            tmpArg_v_68 = (new String("}")).concat(nl);
            printFile(fid, tmpArg_v_68);
        }
    }

    private String getJavaType(String prefix, AstType atp)
        throws CGException
    {
        Boolean cond_3 = null;
        cond_3 = atp.isCharType();
        if(cond_3.booleanValue())
            return new String("Character");
        Boolean cond_26 = null;
        cond_26 = atp.isBoolType();
        if(cond_26.booleanValue())
            return new String("Boolean");
        Boolean cond_24 = null;
        cond_24 = atp.isNatType();
        if(cond_24.booleanValue())
            return IntegerStr;
        Boolean cond_22 = null;
        cond_22 = atp.isRealType();
        if(cond_22.booleanValue())
            return new String("Double");
        Boolean cond_20 = null;
        cond_20 = atp.isStringType();
        if(cond_20.booleanValue())
            return new String("String");
        Boolean cond_14 = null;
        cond_14 = atp.isTypeName();
        if(cond_14.booleanValue())
        {
            AstTypeName atpnm = (AstTypeName)atp;
            String rexpr_15 = null;
            String var1_16 = null;
            var1_16 = (new String("I")).concat(prefix);
            String var2_19 = null;
            var2_19 = atpnm.getName();
            rexpr_15 = var1_16.concat(var2_19);
            return rexpr_15;
        }
        Boolean cond_12 = null;
        cond_12 = atp.isSeqType();
        if(cond_12.booleanValue())
            return new String("Vector");
        Boolean cond_10 = null;
        cond_10 = atp.isSetType();
        if(cond_10.booleanValue())
            return new String("HashSet");
        Boolean cond_6 = null;
        cond_6 = atp.isOptionalType();
        if(cond_6.booleanValue())
        {
            AstOptionalType aotp = (AstOptionalType)atp;
            String rexpr_7 = null;
            AstType par_9 = null;
            par_9 = aotp.getType();
            rexpr_7 = getJavaType(prefix, par_9);
            return rexpr_7;
        }
        Boolean cond_4 = null;
        cond_4 = atp.isMapType();
        if(cond_4.booleanValue())
        {
            return new String("HashMap");
        } else
        {
            UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
            return new String();
        }
    }

    private String getVdmType(String prefix, AstType atp)
        throws CGException
    {
        Boolean cond_3 = null;
        cond_3 = atp.isCharType();
        if(cond_3.booleanValue())
            return new String("char");
        Boolean cond_44 = null;
        cond_44 = atp.isBoolType();
        if(cond_44.booleanValue())
            return new String("bool");
        Boolean cond_42 = null;
        cond_42 = atp.isNatType();
        if(cond_42.booleanValue())
            return new String("nat");
        Boolean cond_40 = null;
        cond_40 = atp.isRealType();
        if(cond_40.booleanValue())
            return new String("real");
        Boolean cond_38 = null;
        cond_38 = atp.isStringType();
        if(cond_38.booleanValue())
            return new String("seq of char");
        Boolean cond_32 = null;
        cond_32 = atp.isTypeName();
        if(cond_32.booleanValue())
        {
            AstTypeName atpnm = (AstTypeName)atp;
            String rexpr_33 = null;
            String var1_34 = null;
            var1_34 = (new String("I")).concat(prefix);
            String var2_37 = null;
            var2_37 = atpnm.getName();
            rexpr_33 = var1_34.concat(var2_37);
            return rexpr_33;
        }
        Boolean cond_26 = null;
        cond_26 = atp.isSeqType();
        if(cond_26.booleanValue())
        {
            AstSeqOfType astp = (AstSeqOfType)atp;
            String rexpr_27 = null;
            String var2_29 = null;
            AstType par_31 = null;
            par_31 = astp.getType();
            var2_29 = getVdmType(prefix, par_31);
            rexpr_27 = (new String("seq of ")).concat(var2_29);
            return rexpr_27;
        }
        Boolean cond_20 = null;
        cond_20 = atp.isSetType();
        if(cond_20.booleanValue())
        {
            AstSetOfType astp = (AstSetOfType)atp;
            String rexpr_21 = null;
            String var2_23 = null;
            AstType par_25 = null;
            par_25 = astp.getType();
            var2_23 = getVdmType(prefix, par_25);
            rexpr_21 = (new String("set of ")).concat(var2_23);
            return rexpr_21;
        }
        Boolean cond_16 = null;
        cond_16 = atp.isOptionalType();
        if(cond_16.booleanValue())
        {
            AstOptionalType aotp = (AstOptionalType)atp;
            String rexpr_17 = null;
            AstType par_19 = null;
            par_19 = aotp.getType();
            rexpr_17 = getVdmType(prefix, par_19);
            return rexpr_17;
        }
        Boolean cond_4 = null;
        cond_4 = atp.isMapType();
        if(cond_4.booleanValue())
        {
            AstMapType amtp = (AstMapType)atp;
            String rexpr_5 = null;
            String var1_6 = null;
            String var1_7 = null;
            String var2_9 = null;
            AstType par_11 = null;
            par_11 = amtp.getDomType();
            var2_9 = getVdmType(prefix, par_11);
            var1_7 = (new String("map ")).concat(var2_9);
            var1_6 = var1_7.concat(new String(" to "));
            String var2_13 = null;
            AstType par_15 = null;
            par_15 = amtp.getRngType();
            var2_13 = getVdmType(prefix, par_15);
            rexpr_5 = var1_6.concat(var2_13);
            return rexpr_5;
        } else
        {
            UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
            return new String();
        }
    }

    private void createSpecifications(AstDefinitions ad)
        throws CGException
    {
        String tmpVal_3 = null;
        tmpVal_3 = ad.getDirectory();
        String base = null;
        base = tmpVal_3;
        Vector root = null;
        root = new Vector();
        root.add(new String("src"));
        if(createDirectory(base, root).booleanValue())
        {
            Vector tmpArg_v_12 = null;
            Vector var1_13 = null;
            var1_13 = new Vector();
            var1_13.add(base);
            tmpArg_v_12 = (Vector)var1_13.clone();
            tmpArg_v_12.addAll(root);
            createSpecification(ad, tmpArg_v_12);
        }
    }

    private void createSpecification(AstDefinitions ad, Vector path)
        throws CGException
    {
        String tmpVal_4 = null;
        String var1_5 = null;
        var1_5 = ad.getPrefix();
        tmpVal_4 = var1_5.concat(new String("Document"));
        String docnm = null;
        docnm = tmpVal_4;
        String tmpVal_8 = null;
        String var1_9 = null;
        var1_9 = ad.getPrefix();
        tmpVal_8 = var1_9.concat(new String("Node"));
        String basenm = null;
        basenm = tmpVal_8;
        String tmpVal_12 = null;
        String var1_13 = null;
        var1_13 = ad.getPrefix();
        tmpVal_12 = var1_13.concat(new String("Visitor"));
        String basevisit = null;
        basevisit = tmpVal_12;
        String tmpVal_16 = null;
        String var1_17 = null;
        var1_17 = ad.getPrefix();
        tmpVal_16 = var1_17.concat(new String("Lexem"));
        String lexnm = null;
        lexnm = tmpVal_16;
        Integer fid = null;
        Vector par_19 = null;
        Vector var2_21 = null;
        String e_seq_22 = null;
        String var1_23 = null;
        var1_23 = (new String("I")).concat(docnm);
        e_seq_22 = var1_23.concat(new String(".tex"));
        var2_21 = new Vector();
        var2_21.add(e_seq_22);
        par_19 = (Vector)path.clone();
        par_19.addAll(var2_21);
        fid = createFile(par_19);
        print(new String("Creating document, lexem, base class and visitor interfaces..."));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVppIDocument(ad, fid, docnm);
        Integer rhs_36 = null;
        Vector par_37 = null;
        Vector var2_39 = null;
        String e_seq_40 = null;
        e_seq_40 = docnm.concat(new String(".tex"));
        var2_39 = new Vector();
        var2_39.add(e_seq_40);
        par_37 = (Vector)path.clone();
        par_37.addAll(var2_39);
        rhs_36 = createFile(par_37);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_36));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVppDocument(ad, fid, docnm);
        Integer rhs_50 = null;
        Vector par_51 = null;
        Vector var2_53 = null;
        String e_seq_54 = null;
        String var1_55 = null;
        var1_55 = (new String("I")).concat(lexnm);
        e_seq_54 = var1_55.concat(new String(".tex"));
        var2_53 = new Vector();
        var2_53.add(e_seq_54);
        par_51 = (Vector)path.clone();
        par_51.addAll(var2_53);
        rhs_50 = createFile(par_51);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_50));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVppILexem(ad, fid, lexnm);
        Integer rhs_66 = null;
        Vector par_67 = null;
        Vector var2_69 = null;
        String e_seq_70 = null;
        e_seq_70 = lexnm.concat(new String(".tex"));
        var2_69 = new Vector();
        var2_69.add(e_seq_70);
        par_67 = (Vector)path.clone();
        par_67.addAll(var2_69);
        rhs_66 = createFile(par_67);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_66));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVppLexem(ad, fid, lexnm);
        Integer rhs_80 = null;
        Vector par_81 = null;
        Vector var2_83 = null;
        String e_seq_84 = null;
        e_seq_84 = basenm.concat(new String(".tex"));
        var2_83 = new Vector();
        var2_83.add(e_seq_84);
        par_81 = (Vector)path.clone();
        par_81.addAll(var2_83);
        rhs_80 = createFile(par_81);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_80));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVppBaseNode(ad, fid, basenm);
        Integer rhs_94 = null;
        Vector par_95 = null;
        Vector var2_97 = null;
        String e_seq_98 = null;
        String var1_99 = null;
        var1_99 = (new String("I")).concat(basevisit);
        e_seq_98 = var1_99.concat(new String(".tex"));
        var2_97 = new Vector();
        var2_97.add(e_seq_98);
        par_95 = (Vector)path.clone();
        par_95.addAll(var2_97);
        rhs_94 = createFile(par_95);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_94));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVppIBaseVisit(ad, fid, basevisit);
        Integer rhs_110 = null;
        Vector par_111 = null;
        Vector var2_113 = null;
        String e_seq_114 = null;
        e_seq_114 = basevisit.concat(new String(".tex"));
        var2_113 = new Vector();
        var2_113.add(e_seq_114);
        par_111 = (Vector)path.clone();
        par_111.addAll(var2_113);
        rhs_110 = createFile(par_111);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_110));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVppBaseVisit(ad, fid, basevisit);
        print(new String("Creating specifications..."));
        HashSet iset_126 = new HashSet();
        iset_126 = ad.getComposites();
        String composite = null;
        for(Iterator enm_168 = iset_126.iterator(); enm_168.hasNext();)
        {
            String elem_127 = UTIL.ConvertToString(enm_168.next());
            composite = elem_127;
            String tmpVal_131 = null;
            String var1_132 = null;
            var1_132 = ad.getPrefix();
            tmpVal_131 = var1_132.concat(composite);
            String clnm = null;
            clnm = tmpVal_131;
            Integer rhs_134 = null;
            Vector par_135 = null;
            Vector var2_137 = null;
            String e_seq_138 = null;
            String var1_139 = null;
            var1_139 = (new String("I")).concat(clnm);
            e_seq_138 = var1_139.concat(new String(".tex"));
            var2_137 = new Vector();
            var2_137.add(e_seq_138);
            par_135 = (Vector)path.clone();
            par_135.addAll(var2_137);
            rhs_134 = createFile(par_135);
            fid = UTIL.NumberToInt(UTIL.clone(rhs_134));
            if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            {
                AstComposite tmpArg_v_148 = null;
                tmpArg_v_148 = ad.getComposite(composite);
                createDefISpec(ad, tmpArg_v_148, clnm, fid);
            }
            Integer rhs_152 = null;
            Vector par_153 = null;
            Vector var2_155 = null;
            String e_seq_156 = null;
            e_seq_156 = clnm.concat(new String(".tex"));
            var2_155 = new Vector();
            var2_155.add(e_seq_156);
            par_153 = (Vector)path.clone();
            par_153.addAll(var2_155);
            rhs_152 = createFile(par_153);
            fid = UTIL.NumberToInt(UTIL.clone(rhs_152));
            if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            {
                AstComposite tmpArg_v_164 = null;
                tmpArg_v_164 = ad.getComposite(composite);
                createDefSpec(ad, tmpArg_v_164, clnm, fid);
            }
        }

        print(new String("Creating shorthands..."));
        HashSet iset_171 = new HashSet();
        iset_171 = ad.getShorthands();
        String shorthand = null;
        for(Iterator enm_219 = iset_171.iterator(); enm_219.hasNext();)
        {
            String elem_172 = UTIL.ConvertToString(enm_219.next());
            shorthand = elem_172;
            Boolean cond_175 = null;
            AstType obj_176 = null;
            AstShorthand obj_177 = null;
            obj_177 = ad.getShorthand(shorthand);
            obj_176 = obj_177.getType();
            cond_175 = obj_176.isUnionType();
            if(cond_175.booleanValue())
            {
                String tmpVal_180 = null;
                String var1_181 = null;
                var1_181 = ad.getPrefix();
                tmpVal_180 = var1_181.concat(shorthand);
                String clnm = null;
                clnm = tmpVal_180;
                Integer rhs_183 = null;
                Vector par_184 = null;
                Vector var2_186 = null;
                String e_seq_187 = null;
                String var1_188 = null;
                var1_188 = (new String("I")).concat(clnm);
                e_seq_187 = var1_188.concat(new String(".tex"));
                var2_186 = new Vector();
                var2_186.add(e_seq_187);
                par_184 = (Vector)path.clone();
                par_184.addAll(var2_186);
                rhs_183 = createFile(par_184);
                fid = UTIL.NumberToInt(UTIL.clone(rhs_183));
                if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
                {
                    AstShorthand tmpArg_v_197 = null;
                    tmpArg_v_197 = ad.getShorthand(shorthand);
                    createShISpec(ad, tmpArg_v_197, clnm, path, fid);
                }
                Integer rhs_202 = null;
                Vector par_203 = null;
                Vector var2_205 = null;
                String e_seq_206 = null;
                e_seq_206 = clnm.concat(new String(".tex"));
                var2_205 = new Vector();
                var2_205.add(e_seq_206);
                par_203 = (Vector)path.clone();
                par_203.addAll(var2_205);
                rhs_202 = createFile(par_203);
                fid = UTIL.NumberToInt(UTIL.clone(rhs_202));
                if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
                {
                    AstShorthand tmpArg_v_214 = null;
                    tmpArg_v_214 = ad.getShorthand(shorthand);
                    createShSpec(ad, tmpArg_v_214, clnm, path, fid);
                }
            }
        }

        print(new String("Creating visitors..."));
        Integer rhs_222 = null;
        Vector par_223 = null;
        Vector var2_225 = null;
        var2_225 = new Vector();
        var2_225.add(new String("VdmSlVisitor.tex"));
        par_223 = (Vector)path.clone();
        par_223.addAll(var2_225);
        rhs_222 = createFile(par_223);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_222));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVdmVisitor(ad, fid, new Boolean(false));
        Integer rhs_234 = null;
        Vector par_235 = null;
        Vector var2_237 = null;
        var2_237 = new Vector();
        var2_237.add(new String("VdmPpVisitor.tex"));
        par_235 = (Vector)path.clone();
        par_235.addAll(var2_237);
        rhs_234 = createFile(par_235);
        fid = UTIL.NumberToInt(UTIL.clone(rhs_234));
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
            createVdmVisitor(ad, fid, new Boolean(true));
    }

    private void createVppIDocument(AstDefinitions ad, Integer fid, String docnm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_11 = null;
        String var1_12 = null;
        String var1_13 = null;
        var1_13 = (new String("class I")).concat(docnm);
        var1_12 = var1_13.concat(nl);
        tmpArg_v_11 = var1_12.concat(nl);
        printFile(fid, tmpArg_v_11);
        String tmpArg_v_20 = null;
        tmpArg_v_20 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_20);
        String tmpArg_v_25 = null;
        tmpArg_v_25 = (new String("  public getFilename: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_25);
        String tmpArg_v_30 = null;
        String var1_31 = null;
        var1_31 = (new String("  getFilename () == is subclass responsibility;")).concat(nl);
        tmpArg_v_30 = var1_31.concat(nl);
        printFile(fid, tmpArg_v_30);
        Vector sq_35 = null;
        sq_35 = ad.getTop();
        String name = null;
        String tmpArg_v_74;
        for(Iterator enm_84 = sq_35.iterator(); enm_84.hasNext(); printFile(fid, tmpArg_v_74))
        {
            String elem_36 = UTIL.ConvertToString(enm_84.next());
            name = elem_36;
            String tmpArg_v_41 = null;
            String var1_42 = null;
            String var1_43 = null;
            var1_43 = (new String("  public has")).concat(name);
            var1_42 = var1_43.concat(new String(": () ==> bool"));
            tmpArg_v_41 = var1_42.concat(nl);
            printFile(fid, tmpArg_v_41);
            String tmpArg_v_50 = null;
            String var1_51 = null;
            String var1_52 = null;
            String var1_53 = null;
            var1_53 = (new String("  has")).concat(name);
            var1_52 = var1_53.concat(new String(" () == is subclass responsibility;"));
            var1_51 = var1_52.concat(nl);
            tmpArg_v_50 = var1_51.concat(nl);
            printFile(fid, tmpArg_v_50);
            String tmpArg_v_61 = null;
            String var1_62 = null;
            String var1_63 = null;
            String var1_64 = null;
            String var1_65 = null;
            var1_65 = (new String("  public get")).concat(name);
            var1_64 = var1_65.concat(new String(": () ==> I"));
            String var2_69 = null;
            var2_69 = ad.getPrefix();
            var1_63 = var1_64.concat(var2_69);
            var1_62 = var1_63.concat(name);
            tmpArg_v_61 = var1_62.concat(nl);
            printFile(fid, tmpArg_v_61);
            tmpArg_v_74 = null;
            String var1_75 = null;
            String var1_76 = null;
            String var1_77 = null;
            var1_77 = (new String("  get")).concat(name);
            var1_76 = var1_77.concat(new String(" () == is subclass responsibility;"));
            var1_75 = var1_76.concat(nl);
            tmpArg_v_74 = var1_75.concat(nl);
        }

        String tmpArg_v_87 = null;
        String var1_88 = null;
        String var1_89 = null;
        String var2_91 = null;
        var2_91 = ad.getPrefix();
        var1_89 = (new String("  public getLexems: () ==> seq of I")).concat(var2_91);
        var1_88 = var1_89.concat(new String("Lexem"));
        tmpArg_v_87 = var1_88.concat(nl);
        printFile(fid, tmpArg_v_87);
        String tmpArg_v_96 = null;
        String var1_97 = null;
        var1_97 = (new String("  getLexems () == is subclass responsibility;")).concat(nl);
        tmpArg_v_96 = var1_97.concat(nl);
        printFile(fid, tmpArg_v_96);
        String tmpArg_v_103 = null;
        tmpArg_v_103 = (new String("  public toVdmSlValue: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_103);
        String tmpArg_v_108 = null;
        String var1_109 = null;
        var1_109 = (new String("  toVdmSlValue () == is subclass responsibility;")).concat(nl);
        tmpArg_v_108 = var1_109.concat(nl);
        printFile(fid, tmpArg_v_108);
        String tmpArg_v_115 = null;
        tmpArg_v_115 = (new String("  public toVdmPpValue: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_115);
        String tmpArg_v_120 = null;
        String var1_121 = null;
        var1_121 = (new String("  toVdmPpValue () == is subclass responsibility;")).concat(nl);
        tmpArg_v_120 = var1_121.concat(nl);
        printFile(fid, tmpArg_v_120);
        String tmpArg_v_127 = null;
        String var1_128 = null;
        String var1_129 = null;
        String var2_131 = null;
        var2_131 = ad.getPrefix();
        var1_129 = (new String("  public accept: I")).concat(var2_131);
        var1_128 = var1_129.concat(new String("Visitor ==> ()"));
        tmpArg_v_127 = var1_128.concat(nl);
        printFile(fid, tmpArg_v_127);
        String tmpArg_v_136 = null;
        String var1_137 = null;
        var1_137 = (new String("  accept (-) == is subclass responsibility")).concat(nl);
        tmpArg_v_136 = var1_137.concat(nl);
        printFile(fid, tmpArg_v_136);
        String tmpArg_v_143 = null;
        String var1_144 = null;
        var1_144 = (new String("end I")).concat(docnm);
        tmpArg_v_143 = var1_144.concat(nl);
        printFile(fid, tmpArg_v_143);
        String tmpArg_v_150 = null;
        tmpArg_v_150 = (new String("\\end{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_150);
    }

    private void createVppDocument(AstDefinitions ad, Integer fid, String docnm)
        throws CGException
    {
        String pexpr = UTIL.ConvertToString(new String());
        String tmpArg_v_6 = null;
        tmpArg_v_6 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_11 = null;
        String var1_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        String var1_15 = null;
        var1_15 = (new String("class ")).concat(docnm);
        var1_14 = var1_15.concat(new String(" is subclass of I"));
        var1_13 = var1_14.concat(docnm);
        var1_12 = var1_13.concat(nl);
        tmpArg_v_11 = var1_12.concat(nl);
        printFile(fid, tmpArg_v_11);
        String tmpArg_v_24 = null;
        tmpArg_v_24 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_24);
        String tmpArg_v_29 = null;
        String var1_30 = null;
        var1_30 = (new String("  private ivFilename : seq of char := []")).concat(nl);
        tmpArg_v_29 = var1_30.concat(nl);
        printFile(fid, tmpArg_v_29);
        String tmpArg_v_36 = null;
        tmpArg_v_36 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_36);
        String tmpArg_v_41 = null;
        tmpArg_v_41 = (new String("  public getFilename: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_41);
        String tmpArg_v_46 = null;
        String var1_47 = null;
        var1_47 = (new String("  getFilename () == return ivFilename;")).concat(nl);
        tmpArg_v_46 = var1_47.concat(nl);
        printFile(fid, tmpArg_v_46);
        String tmpArg_v_53 = null;
        tmpArg_v_53 = (new String("  public setFilename: seq of char ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_53);
        String tmpArg_v_58 = null;
        String var1_59 = null;
        var1_59 = (new String("  setFilename (pfilename) == ivFilename := pfilename;")).concat(nl);
        tmpArg_v_58 = var1_59.concat(nl);
        printFile(fid, tmpArg_v_58);
        String tmpArg_v_65 = null;
        tmpArg_v_65 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_65);
        String tmpArg_v_70 = null;
        String var1_71 = null;
        String var1_72 = null;
        String var1_73 = null;
        String var2_75 = null;
        var2_75 = ad.getPrefix();
        var1_73 = (new String("  private ivTopNode : [ I")).concat(var2_75);
        var1_72 = var1_73.concat(new String("Node ] := nil"));
        var1_71 = var1_72.concat(nl);
        tmpArg_v_70 = var1_71.concat(nl);
        printFile(fid, tmpArg_v_70);
        String tmpArg_v_81 = null;
        tmpArg_v_81 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_81);
        Vector sq_84 = null;
        sq_84 = ad.getTop();
        String name = null;
        for(Iterator enm_196 = sq_84.iterator(); enm_196.hasNext();)
        {
            String elem_85 = UTIL.ConvertToString(enm_196.next());
            name = elem_85;
            String tmpArg_v_90 = null;
            String var1_91 = null;
            String var1_92 = null;
            var1_92 = (new String("  public has")).concat(name);
            var1_91 = var1_92.concat(new String(": () ==> bool"));
            tmpArg_v_90 = var1_91.concat(nl);
            printFile(fid, tmpArg_v_90);
            String tmpArg_v_99 = null;
            String var1_100 = null;
            String var1_101 = null;
            String var1_102 = null;
            String var1_103 = null;
            String var1_104 = null;
            String var1_105 = null;
            var1_105 = (new String("  has")).concat(name);
            var1_104 = var1_105.concat(new String(" () == return isofclass(I"));
            String var2_109 = null;
            var2_109 = ad.getPrefix();
            var1_103 = var1_104.concat(var2_109);
            var1_102 = var1_103.concat(name);
            var1_101 = var1_102.concat(new String(",ivTopNode);"));
            var1_100 = var1_101.concat(nl);
            tmpArg_v_99 = var1_100.concat(nl);
            printFile(fid, tmpArg_v_99);
            String tmpArg_v_116 = null;
            String var1_117 = null;
            String var1_118 = null;
            String var1_119 = null;
            String var1_120 = null;
            var1_120 = (new String("  public get")).concat(name);
            var1_119 = var1_120.concat(new String(": () ==> I"));
            String var2_124 = null;
            var2_124 = ad.getPrefix();
            var1_118 = var1_119.concat(var2_124);
            var1_117 = var1_118.concat(name);
            tmpArg_v_116 = var1_117.concat(nl);
            printFile(fid, tmpArg_v_116);
            String tmpArg_v_129 = null;
            String var1_130 = null;
            String var1_131 = null;
            var1_131 = (new String("  get")).concat(name);
            var1_130 = var1_131.concat(new String(" () == return ivTopNode"));
            tmpArg_v_129 = var1_130.concat(nl);
            printFile(fid, tmpArg_v_129);
            String tmpArg_v_138 = null;
            String var1_139 = null;
            String var1_140 = null;
            String var1_141 = null;
            var1_141 = (new String("    pre has")).concat(name);
            var1_140 = var1_141.concat(new String("();"));
            var1_139 = var1_140.concat(nl);
            tmpArg_v_138 = var1_139.concat(nl);
            printFile(fid, tmpArg_v_138);
            String tmpArg_v_149 = null;
            String var1_150 = null;
            String var1_151 = null;
            String var1_152 = null;
            String var1_153 = null;
            String var1_154 = null;
            var1_154 = (new String("  public set")).concat(name);
            var1_153 = var1_154.concat(new String(": I"));
            String var2_158 = null;
            var2_158 = ad.getPrefix();
            var1_152 = var1_153.concat(var2_158);
            var1_151 = var1_152.concat(name);
            var1_150 = var1_151.concat(new String(" ==> ()"));
            tmpArg_v_149 = var1_150.concat(nl);
            printFile(fid, tmpArg_v_149);
            String tmpArg_v_164 = null;
            String var1_165 = null;
            String var1_166 = null;
            var1_166 = (new String("  set")).concat(name);
            var1_165 = var1_166.concat(new String(" (pNode) == ivTopNode := pNode"));
            tmpArg_v_164 = var1_165.concat(nl);
            printFile(fid, tmpArg_v_164);
            String tmpArg_v_173 = null;
            String var1_174 = null;
            var1_174 = (new String("    pre ivTopNode = nil;")).concat(nl);
            tmpArg_v_173 = var1_174.concat(nl);
            printFile(fid, tmpArg_v_173);
            String tmpVal_179 = null;
            String var1_180 = null;
            String var1_181 = null;
            String var2_183 = null;
            var2_183 = ad.getPrefix();
            var1_181 = (new String("isofclass(I")).concat(var2_183);
            var1_180 = var1_181.concat(name);
            tmpVal_179 = var1_180.concat(new String(",pnode)"));
            String str = null;
            str = tmpVal_179;
            if((new Boolean(UTIL.equals(pexpr, new Vector()))).booleanValue())
            {
                pexpr = UTIL.ConvertToString(UTIL.clone(str));
            } else
            {
                String rhs_189 = null;
                String var1_190 = null;
                var1_190 = pexpr.concat(new String(" or "));
                rhs_189 = var1_190.concat(str);
                pexpr = UTIL.ConvertToString(UTIL.clone(rhs_189));
            }
        }

        String tmpArg_v_199 = null;
        tmpArg_v_199 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_199);
        String tmpArg_v_204 = null;
        String var1_205 = null;
        String var1_206 = null;
        String var1_207 = null;
        String var2_209 = null;
        var2_209 = ad.getPrefix();
        var1_207 = (new String("  private ivLexems : seq of I")).concat(var2_209);
        var1_206 = var1_207.concat(new String("Lexem := []"));
        var1_205 = var1_206.concat(nl);
        tmpArg_v_204 = var1_205.concat(nl);
        printFile(fid, tmpArg_v_204);
        String tmpArg_v_215 = null;
        tmpArg_v_215 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_215);
        String tmpArg_v_220 = null;
        String var1_221 = null;
        String var1_222 = null;
        String var2_224 = null;
        var2_224 = ad.getPrefix();
        var1_222 = (new String("  public getLexems : () ==> seq of I")).concat(var2_224);
        var1_221 = var1_222.concat(new String("Lexem"));
        tmpArg_v_220 = var1_221.concat(nl);
        printFile(fid, tmpArg_v_220);
        String tmpArg_v_229 = null;
        String var1_230 = null;
        var1_230 = (new String("  getLexems () == return ivLexems;")).concat(nl);
        tmpArg_v_229 = var1_230.concat(nl);
        printFile(fid, tmpArg_v_229);
        String tmpArg_v_236 = null;
        String var1_237 = null;
        String var1_238 = null;
        String var2_240 = null;
        var2_240 = ad.getPrefix();
        var1_238 = (new String("  public setLexems : seq of I")).concat(var2_240);
        var1_237 = var1_238.concat(new String("Lexem ==> ()"));
        tmpArg_v_236 = var1_237.concat(nl);
        printFile(fid, tmpArg_v_236);
        String tmpArg_v_245 = null;
        String var1_246 = null;
        var1_246 = (new String("  setLexems (plexems) == ivLexems := plexems;")).concat(nl);
        tmpArg_v_245 = var1_246.concat(nl);
        printFile(fid, tmpArg_v_245);
        String tmpArg_v_252 = null;
        String var1_253 = null;
        String var1_254 = null;
        String var2_256 = null;
        var2_256 = ad.getPrefix();
        var1_254 = (new String("  public accept: I")).concat(var2_256);
        var1_253 = var1_254.concat(new String("Visitor ==> ()"));
        tmpArg_v_252 = var1_253.concat(nl);
        printFile(fid, tmpArg_v_252);
        String tmpArg_v_261 = null;
        String var1_262 = null;
        var1_262 = (new String("  accept (pVisitor) == pVisitor.visitDocument(self);")).concat(nl);
        tmpArg_v_261 = var1_262.concat(nl);
        printFile(fid, tmpArg_v_261);
        String tmpArg_v_268 = null;
        tmpArg_v_268 = (new String("  public toVdmSlValue: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_268);
        String tmpArg_v_273 = null;
        tmpArg_v_273 = (new String("  toVdmSlValue () ==")).concat(nl);
        printFile(fid, tmpArg_v_273);
        String tmpArg_v_278 = null;
        tmpArg_v_278 = (new String("    ( dcl visitor : VdmSlVisitor := new VdmSlVisitor();")).concat(nl);
        printFile(fid, tmpArg_v_278);
        String tmpArg_v_283 = null;
        tmpArg_v_283 = (new String("      accept(visitor);")).concat(nl);
        printFile(fid, tmpArg_v_283);
        String tmpArg_v_288 = null;
        String var1_289 = null;
        var1_289 = (new String("      return visitor.result );")).concat(nl);
        tmpArg_v_288 = var1_289.concat(nl);
        printFile(fid, tmpArg_v_288);
        String tmpArg_v_295 = null;
        tmpArg_v_295 = (new String("  public toVdmPpValue: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_295);
        String tmpArg_v_300 = null;
        tmpArg_v_300 = (new String("  toVdmPpValue () ==")).concat(nl);
        printFile(fid, tmpArg_v_300);
        String tmpArg_v_305 = null;
        tmpArg_v_305 = (new String("    ( dcl visitor : VdmPpVisitor := new VdmPpVisitor();")).concat(nl);
        printFile(fid, tmpArg_v_305);
        String tmpArg_v_310 = null;
        tmpArg_v_310 = (new String("      accept(visitor);")).concat(nl);
        printFile(fid, tmpArg_v_310);
        String tmpArg_v_315 = null;
        String var1_316 = null;
        var1_316 = (new String("      return visitor.result );")).concat(nl);
        tmpArg_v_315 = var1_316.concat(nl);
        printFile(fid, tmpArg_v_315);
        String pfx = null;
        pfx = ad.getPrefix();
        String tmpArg_v_324 = null;
        String var1_325 = null;
        String var1_326 = null;
        String var1_327 = null;
        String var1_328 = null;
        String var1_329 = null;
        String var1_330 = null;
        String var1_331 = null;
        var1_331 = (new String("  public ")).concat(docnm);
        var1_330 = var1_331.concat(new String(": seq of char * I"));
        var1_329 = var1_330.concat(pfx);
        var1_328 = var1_329.concat(new String("Node * seq of I"));
        String var2_337 = null;
        var2_337 = ad.getPrefix();
        var1_327 = var1_328.concat(var2_337);
        var1_326 = var1_327.concat(new String("Lexem ==> "));
        var1_325 = var1_326.concat(docnm);
        tmpArg_v_324 = var1_325.concat(nl);
        printFile(fid, tmpArg_v_324);
        String tmpArg_v_343 = null;
        String var1_344 = null;
        String var1_345 = null;
        var1_345 = (new String("  ")).concat(docnm);
        var1_344 = var1_345.concat(new String(" (pfilename, pnode, plexems) =="));
        tmpArg_v_343 = var1_344.concat(nl);
        printFile(fid, tmpArg_v_343);
        String tmpArg_v_352 = null;
        tmpArg_v_352 = (new String("    ( setFilename(pfilename);")).concat(nl);
        printFile(fid, tmpArg_v_352);
        String tmpArg_v_357 = null;
        tmpArg_v_357 = (new String("      ivTopNode := pnode;")).concat(nl);
        printFile(fid, tmpArg_v_357);
        String tmpArg_v_362 = null;
        tmpArg_v_362 = (new String("      setLexems(plexems) )")).concat(nl);
        printFile(fid, tmpArg_v_362);
        String tmpArg_v_367 = null;
        String var1_368 = null;
        String var1_369 = null;
        var1_369 = (new String("    pre ")).concat(pexpr);
        var1_368 = var1_369.concat(nl);
        tmpArg_v_367 = var1_368.concat(nl);
        printFile(fid, tmpArg_v_367);
        String tmpArg_v_376 = null;
        String var1_377 = null;
        var1_377 = (new String("end ")).concat(docnm);
        tmpArg_v_376 = var1_377.concat(nl);
        printFile(fid, tmpArg_v_376);
        String tmpArg_v_383 = null;
        tmpArg_v_383 = (new String("\\end{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_383);
    }

    private void createVppILexem(AstDefinitions ad, Integer fid, String lexnm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_11 = null;
        String var1_12 = null;
        String var1_13 = null;
        var1_13 = (new String("class I")).concat(lexnm);
        var1_12 = var1_13.concat(nl);
        tmpArg_v_11 = var1_12.concat(nl);
        printFile(fid, tmpArg_v_11);
        String tmpArg_v_20 = null;
        tmpArg_v_20 = (new String("values")).concat(nl);
        printFile(fid, tmpArg_v_20);
        String tmpArg_v_25 = null;
        tmpArg_v_25 = (new String("  static public ILEXEMUNKNOWN      : nat = 0;")).concat(nl);
        printFile(fid, tmpArg_v_25);
        String tmpArg_v_30 = null;
        tmpArg_v_30 = (new String("  static public ILEXEMKEYWORD      : nat = 1;")).concat(nl);
        printFile(fid, tmpArg_v_30);
        String tmpArg_v_35 = null;
        tmpArg_v_35 = (new String("  static public ILEXEMIDENTIFIER   : nat = 2;")).concat(nl);
        printFile(fid, tmpArg_v_35);
        String tmpArg_v_40 = null;
        tmpArg_v_40 = (new String("  static public ILEXEMLINECOMMENT  : nat = 3;")).concat(nl);
        printFile(fid, tmpArg_v_40);
        String tmpArg_v_45 = null;
        String var1_46 = null;
        var1_46 = (new String("  static public ILEXEMBLOCKCOMMENT : nat = 4")).concat(nl);
        tmpArg_v_45 = var1_46.concat(nl);
        printFile(fid, tmpArg_v_45);
        String tmpArg_v_52 = null;
        tmpArg_v_52 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_52);
        String tmpArg_v_57 = null;
        String var1_58 = null;
        String var1_59 = null;
        String var2_61 = null;
        var2_61 = ad.getPrefix();
        var1_59 = (new String("  public accept: I")).concat(var2_61);
        var1_58 = var1_59.concat(new String("Visitor ==> ()"));
        tmpArg_v_57 = var1_58.concat(nl);
        printFile(fid, tmpArg_v_57);
        String tmpArg_v_66 = null;
        String var1_67 = null;
        var1_67 = (new String("  accept (-) == is subclass responsibility;")).concat(nl);
        tmpArg_v_66 = var1_67.concat(nl);
        printFile(fid, tmpArg_v_66);
        String tmpArg_v_73 = null;
        tmpArg_v_73 = (new String("  public getLine: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_73);
        String tmpArg_v_78 = null;
        String var1_79 = null;
        var1_79 = (new String("  getLine () == is subclass responsibility;")).concat(nl);
        tmpArg_v_78 = var1_79.concat(nl);
        printFile(fid, tmpArg_v_78);
        String tmpArg_v_85 = null;
        tmpArg_v_85 = (new String("  public getColumn: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_85);
        String tmpArg_v_90 = null;
        String var1_91 = null;
        var1_91 = (new String("  getColumn () == is subclass responsibility;")).concat(nl);
        tmpArg_v_90 = var1_91.concat(nl);
        printFile(fid, tmpArg_v_90);
        String tmpArg_v_97 = null;
        tmpArg_v_97 = (new String("  public getLexval: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_97);
        String tmpArg_v_102 = null;
        String var1_103 = null;
        var1_103 = (new String("  getLexval () == is subclass responsibility;")).concat(nl);
        tmpArg_v_102 = var1_103.concat(nl);
        printFile(fid, tmpArg_v_102);
        String tmpArg_v_109 = null;
        tmpArg_v_109 = (new String("  public getText: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_109);
        String tmpArg_v_114 = null;
        String var1_115 = null;
        var1_115 = (new String("  getText () == is subclass responsibility;")).concat(nl);
        tmpArg_v_114 = var1_115.concat(nl);
        printFile(fid, tmpArg_v_114);
        String tmpArg_v_121 = null;
        tmpArg_v_121 = (new String("  public getType: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_121);
        String tmpArg_v_126 = null;
        String var1_127 = null;
        var1_127 = (new String("  getType () == is subclass responsibility;")).concat(nl);
        tmpArg_v_126 = var1_127.concat(nl);
        printFile(fid, tmpArg_v_126);
        String tmpArg_v_133 = null;
        tmpArg_v_133 = (new String("  public isKeyword: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_133);
        String tmpArg_v_138 = null;
        String var1_139 = null;
        var1_139 = (new String("  isKeyword () == is subclass responsibility;")).concat(nl);
        tmpArg_v_138 = var1_139.concat(nl);
        printFile(fid, tmpArg_v_138);
        String tmpArg_v_145 = null;
        tmpArg_v_145 = (new String("  public isIdentifier: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_145);
        String tmpArg_v_150 = null;
        String var1_151 = null;
        var1_151 = (new String("  isIdentifier () == is subclass responsibility;")).concat(nl);
        tmpArg_v_150 = var1_151.concat(nl);
        printFile(fid, tmpArg_v_150);
        String tmpArg_v_157 = null;
        tmpArg_v_157 = (new String("  public isComment: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_157);
        String tmpArg_v_162 = null;
        String var1_163 = null;
        var1_163 = (new String("  isComment () == is subclass responsibility;")).concat(nl);
        tmpArg_v_162 = var1_163.concat(nl);
        printFile(fid, tmpArg_v_162);
        String tmpArg_v_169 = null;
        tmpArg_v_169 = (new String("  public isLineComment: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_169);
        String tmpArg_v_174 = null;
        String var1_175 = null;
        var1_175 = (new String("  isLineComment () == is subclass responsibility;")).concat(nl);
        tmpArg_v_174 = var1_175.concat(nl);
        printFile(fid, tmpArg_v_174);
        String tmpArg_v_181 = null;
        tmpArg_v_181 = (new String("  public isBlockComment: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_181);
        String tmpArg_v_186 = null;
        String var1_187 = null;
        var1_187 = (new String("  isBlockComment () == is subclass responsibility;")).concat(nl);
        tmpArg_v_186 = var1_187.concat(nl);
        printFile(fid, tmpArg_v_186);
        String tmpArg_v_193 = null;
        String var1_194 = null;
        var1_194 = (new String("end I")).concat(lexnm);
        tmpArg_v_193 = var1_194.concat(nl);
        printFile(fid, tmpArg_v_193);
        String tmpArg_v_200 = null;
        tmpArg_v_200 = (new String("\\end{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_200);
    }

    private void createVppLexem(AstDefinitions ad, Integer fid, String lexnm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_11 = null;
        String var1_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        String var1_15 = null;
        var1_15 = (new String("class ")).concat(lexnm);
        var1_14 = var1_15.concat(new String(" is subclass of I"));
        var1_13 = var1_14.concat(lexnm);
        var1_12 = var1_13.concat(nl);
        tmpArg_v_11 = var1_12.concat(nl);
        printFile(fid, tmpArg_v_11);
        String tmpArg_v_24 = null;
        tmpArg_v_24 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_24);
        String tmpArg_v_29 = null;
        String var1_30 = null;
        var1_30 = (new String("  private ivLine : nat := 0")).concat(nl);
        tmpArg_v_29 = var1_30.concat(nl);
        printFile(fid, tmpArg_v_29);
        String tmpArg_v_36 = null;
        tmpArg_v_36 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_36);
        String tmpArg_v_41 = null;
        String var1_42 = null;
        String var1_43 = null;
        String var2_45 = null;
        var2_45 = ad.getPrefix();
        var1_43 = (new String("  public accept: I")).concat(var2_45);
        var1_42 = var1_43.concat(new String("Visitor ==> ()"));
        tmpArg_v_41 = var1_42.concat(nl);
        printFile(fid, tmpArg_v_41);
        String tmpArg_v_50 = null;
        String var1_51 = null;
        var1_51 = (new String("  accept (pVisitor) == pVisitor.visitLexem(self);")).concat(nl);
        tmpArg_v_50 = var1_51.concat(nl);
        printFile(fid, tmpArg_v_50);
        String tmpArg_v_57 = null;
        tmpArg_v_57 = (new String("  public getLine: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_57);
        String tmpArg_v_62 = null;
        String var1_63 = null;
        var1_63 = (new String("  getLine () == return ivLine;")).concat(nl);
        tmpArg_v_62 = var1_63.concat(nl);
        printFile(fid, tmpArg_v_62);
        String tmpArg_v_69 = null;
        tmpArg_v_69 = (new String("  public setLine: nat ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_69);
        String tmpArg_v_74 = null;
        String var1_75 = null;
        var1_75 = (new String("  setLine (pline) == ivLine := pline")).concat(nl);
        tmpArg_v_74 = var1_75.concat(nl);
        printFile(fid, tmpArg_v_74);
        String tmpArg_v_81 = null;
        tmpArg_v_81 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_81);
        String tmpArg_v_86 = null;
        String var1_87 = null;
        var1_87 = (new String("  private ivColumn : nat := 0")).concat(nl);
        tmpArg_v_86 = var1_87.concat(nl);
        printFile(fid, tmpArg_v_86);
        String tmpArg_v_93 = null;
        tmpArg_v_93 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_93);
        String tmpArg_v_98 = null;
        tmpArg_v_98 = (new String("  public getColumn: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_98);
        String tmpArg_v_103 = null;
        String var1_104 = null;
        var1_104 = (new String("  getColumn () == return ivColumn;")).concat(nl);
        tmpArg_v_103 = var1_104.concat(nl);
        printFile(fid, tmpArg_v_103);
        String tmpArg_v_110 = null;
        tmpArg_v_110 = (new String("  public setColumn: nat ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_110);
        String tmpArg_v_115 = null;
        String var1_116 = null;
        var1_116 = (new String("  setColumn (pcolumn) == ivColumn := pcolumn")).concat(nl);
        tmpArg_v_115 = var1_116.concat(nl);
        printFile(fid, tmpArg_v_115);
        String tmpArg_v_122 = null;
        tmpArg_v_122 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_122);
        String tmpArg_v_127 = null;
        String var1_128 = null;
        var1_128 = (new String("  private ivLexval : nat := 0")).concat(nl);
        tmpArg_v_127 = var1_128.concat(nl);
        printFile(fid, tmpArg_v_127);
        String tmpArg_v_134 = null;
        tmpArg_v_134 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_134);
        String tmpArg_v_139 = null;
        tmpArg_v_139 = (new String("  public getLexval: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_139);
        String tmpArg_v_144 = null;
        String var1_145 = null;
        var1_145 = (new String("  getLexval () == return ivLexval;")).concat(nl);
        tmpArg_v_144 = var1_145.concat(nl);
        printFile(fid, tmpArg_v_144);
        String tmpArg_v_151 = null;
        tmpArg_v_151 = (new String("  public setLexval: nat ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_151);
        String tmpArg_v_156 = null;
        String var1_157 = null;
        var1_157 = (new String("  setLexval (plexval) == ivLexval := plexval")).concat(nl);
        tmpArg_v_156 = var1_157.concat(nl);
        printFile(fid, tmpArg_v_156);
        String tmpArg_v_163 = null;
        tmpArg_v_163 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_163);
        String tmpArg_v_168 = null;
        String var1_169 = null;
        var1_169 = (new String("  private ivText : seq of char := []")).concat(nl);
        tmpArg_v_168 = var1_169.concat(nl);
        printFile(fid, tmpArg_v_168);
        String tmpArg_v_175 = null;
        tmpArg_v_175 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_175);
        String tmpArg_v_180 = null;
        tmpArg_v_180 = (new String("  public getText: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_180);
        String tmpArg_v_185 = null;
        String var1_186 = null;
        var1_186 = (new String("  getText () == return ivText;")).concat(nl);
        tmpArg_v_185 = var1_186.concat(nl);
        printFile(fid, tmpArg_v_185);
        String tmpArg_v_192 = null;
        tmpArg_v_192 = (new String("  public setText: seq of char ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_192);
        String tmpArg_v_197 = null;
        String var1_198 = null;
        var1_198 = (new String("  setText (ptext) == ivText := ptext")).concat(nl);
        tmpArg_v_197 = var1_198.concat(nl);
        printFile(fid, tmpArg_v_197);
        String tmpArg_v_204 = null;
        tmpArg_v_204 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_204);
        String tmpArg_v_209 = null;
        String var1_210 = null;
        var1_210 = (new String("  private ivType : nat := ILEXEMUNKNOWN")).concat(nl);
        tmpArg_v_209 = var1_210.concat(nl);
        printFile(fid, tmpArg_v_209);
        String tmpArg_v_216 = null;
        tmpArg_v_216 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_216);
        String tmpArg_v_221 = null;
        tmpArg_v_221 = (new String("  public getType: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_221);
        String tmpArg_v_226 = null;
        String var1_227 = null;
        var1_227 = (new String("  getType () == return ivType;")).concat(nl);
        tmpArg_v_226 = var1_227.concat(nl);
        printFile(fid, tmpArg_v_226);
        String tmpArg_v_233 = null;
        tmpArg_v_233 = (new String("  public isKeyword: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_233);
        String tmpArg_v_238 = null;
        String var1_239 = null;
        var1_239 = (new String("  isKeyword () == return ivType = ILEXEMKEYWORD;")).concat(nl);
        tmpArg_v_238 = var1_239.concat(nl);
        printFile(fid, tmpArg_v_238);
        String tmpArg_v_245 = null;
        tmpArg_v_245 = (new String("  public isIdentifier: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_245);
        String tmpArg_v_250 = null;
        String var1_251 = null;
        var1_251 = (new String("  isIdentifier () == return ivType = ILEXEMIDENTIFIER;")).concat(nl);
        tmpArg_v_250 = var1_251.concat(nl);
        printFile(fid, tmpArg_v_250);
        String tmpArg_v_257 = null;
        tmpArg_v_257 = (new String("  public isComment: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_257);
        String tmpArg_v_262 = null;
        String var1_263 = null;
        var1_263 = (new String("  isComment () == return (ivType = ILEXEMLINECOMMENT) or (ivType = ILEXEMBLOCKCOMMENT);")).concat(nl);
        tmpArg_v_262 = var1_263.concat(nl);
        printFile(fid, tmpArg_v_262);
        String tmpArg_v_269 = null;
        tmpArg_v_269 = (new String("  public isLineComment: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_269);
        String tmpArg_v_274 = null;
        String var1_275 = null;
        var1_275 = (new String("  isLineComment () == return ivType = ILEXEMLINECOMMENT;")).concat(nl);
        tmpArg_v_274 = var1_275.concat(nl);
        printFile(fid, tmpArg_v_274);
        String tmpArg_v_281 = null;
        tmpArg_v_281 = (new String("  public isBlockComment: () ==> bool")).concat(nl);
        printFile(fid, tmpArg_v_281);
        String tmpArg_v_286 = null;
        String var1_287 = null;
        var1_287 = (new String("  isBlockComment () == return ivType = ILEXEMBLOCKCOMMENT;")).concat(nl);
        tmpArg_v_286 = var1_287.concat(nl);
        printFile(fid, tmpArg_v_286);
        String tmpArg_v_293 = null;
        String var1_294 = null;
        String var1_295 = null;
        String var1_296 = null;
        var1_296 = (new String("  public ")).concat(lexnm);
        var1_295 = var1_296.concat(new String(": nat * nat * nat * seq of char * nat ==> "));
        var1_294 = var1_295.concat(lexnm);
        tmpArg_v_293 = var1_294.concat(nl);
        printFile(fid, tmpArg_v_293);
        String tmpArg_v_304 = null;
        String var1_305 = null;
        String var1_306 = null;
        var1_306 = (new String("  ")).concat(lexnm);
        var1_305 = var1_306.concat(new String(" (pline, pcolumn, plexval, ptext, ptype) =="));
        tmpArg_v_304 = var1_305.concat(nl);
        printFile(fid, tmpArg_v_304);
        String tmpArg_v_313 = null;
        tmpArg_v_313 = (new String("    ( ivLine := pline;")).concat(nl);
        printFile(fid, tmpArg_v_313);
        String tmpArg_v_318 = null;
        tmpArg_v_318 = (new String("      ivColumn := pcolumn;")).concat(nl);
        printFile(fid, tmpArg_v_318);
        String tmpArg_v_323 = null;
        tmpArg_v_323 = (new String("      ivLexval := plexval;")).concat(nl);
        printFile(fid, tmpArg_v_323);
        String tmpArg_v_328 = null;
        tmpArg_v_328 = (new String("      ivText := ptext;")).concat(nl);
        printFile(fid, tmpArg_v_328);
        String tmpArg_v_333 = null;
        String var1_334 = null;
        var1_334 = (new String("      ivType := ptype )")).concat(nl);
        tmpArg_v_333 = var1_334.concat(nl);
        printFile(fid, tmpArg_v_333);
        String tmpArg_v_340 = null;
        String var1_341 = null;
        var1_341 = (new String("end ")).concat(lexnm);
        tmpArg_v_340 = var1_341.concat(nl);
        printFile(fid, tmpArg_v_340);
        String tmpArg_v_347 = null;
        tmpArg_v_347 = (new String("\\end{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_347);
    }

    private void createVdmVisitor(AstDefinitions ad, Integer fid, Boolean vpp)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_6);
        if((new Boolean(!vpp.booleanValue())).booleanValue())
        {
            String tmpArg_v_24 = null;
            String var1_25 = null;
            String var1_26 = null;
            String var1_27 = null;
            String var2_29 = null;
            var2_29 = ad.getPrefix();
            var1_27 = (new String("class VdmSlVisitor is subclass of ")).concat(var2_29);
            var1_26 = var1_27.concat(new String("Visitor"));
            var1_25 = var1_26.concat(nl);
            tmpArg_v_24 = var1_25.concat(nl);
            printFile(fid, tmpArg_v_24);
        } else
        {
            String tmpArg_v_13 = null;
            String var1_14 = null;
            String var1_15 = null;
            String var1_16 = null;
            String var2_18 = null;
            var2_18 = ad.getPrefix();
            var1_16 = (new String("class VdmPpVisitor is subclass of ")).concat(var2_18);
            var1_15 = var1_16.concat(new String("Visitor"));
            var1_14 = var1_15.concat(nl);
            tmpArg_v_13 = var1_14.concat(nl);
            printFile(fid, tmpArg_v_13);
        }
        String tmpArg_v_35 = null;
        tmpArg_v_35 = (new String("values")).concat(nl);
        printFile(fid, tmpArg_v_35);
        String tmpArg_v_40 = null;
        tmpArg_v_40 = (new String("  private nl : seq of char = \"\\r\\n\";")).concat(nl);
        printFile(fid, tmpArg_v_40);
        if((new Boolean(!vpp.booleanValue())).booleanValue())
        {
            String tmpArg_v_58 = null;
            String var1_59 = null;
            var1_59 = (new String("  private prefix : seq of char = \"mk_ \"")).concat(nl);
            tmpArg_v_58 = var1_59.concat(nl);
            printFile(fid, tmpArg_v_58);
        } else
        {
            String tmpArg_v_47 = null;
            String var1_48 = null;
            String var1_49 = null;
            String var1_50 = null;
            String var2_52 = null;
            var2_52 = ad.getPrefix();
            var1_50 = (new String("  private prefix : seq of char = \"new ")).concat(var2_52);
            var1_49 = var1_50.concat(new String("\""));
            var1_48 = var1_49.concat(nl);
            tmpArg_v_47 = var1_48.concat(nl);
            printFile(fid, tmpArg_v_47);
        }
        String tmpArg_v_65 = null;
        tmpArg_v_65 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_65);
        String tmpArg_v_70 = null;
        tmpArg_v_70 = (new String("  public result : seq of char := [];")).concat(nl);
        printFile(fid, tmpArg_v_70);
        String tmpArg_v_75 = null;
        String var1_76 = null;
        var1_76 = (new String("  private lvl : nat := 0")).concat(nl);
        tmpArg_v_75 = var1_76.concat(nl);
        printFile(fid, tmpArg_v_75);
        String tmpArg_v_82 = null;
        tmpArg_v_82 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_82);
        String tmpArg_v_87 = null;
        tmpArg_v_87 = (new String("  private pushNL: () ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_87);
        String tmpArg_v_92 = null;
        String var1_93 = null;
        var1_93 = (new String("  pushNL () == lvl := lvl + 2;")).concat(nl);
        tmpArg_v_92 = var1_93.concat(nl);
        printFile(fid, tmpArg_v_92);
        String tmpArg_v_99 = null;
        tmpArg_v_99 = (new String("  private popNL: () ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_99);
        String tmpArg_v_104 = null;
        String var1_105 = null;
        var1_105 = (new String("  popNL () == lvl := lvl - 2;")).concat(nl);
        tmpArg_v_104 = var1_105.concat(nl);
        printFile(fid, tmpArg_v_104);
        String tmpArg_v_111 = null;
        tmpArg_v_111 = (new String("  private getNL: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_111);
        String tmpArg_v_116 = null;
        tmpArg_v_116 = (new String("  getNL () ==")).concat(nl);
        printFile(fid, tmpArg_v_116);
        String tmpArg_v_121 = null;
        tmpArg_v_121 = (new String("    ( dcl res : seq of char := nl, cnt : nat := lvl;")).concat(nl);
        printFile(fid, tmpArg_v_121);
        String tmpArg_v_126 = null;
        tmpArg_v_126 = (new String("      while cnt > 0 do ( res := res ^\" \"; cnt := cnt - 1 );")).concat(nl);
        printFile(fid, tmpArg_v_126);
        String tmpArg_v_131 = null;
        String var1_132 = null;
        var1_132 = (new String("      return res );")).concat(nl);
        tmpArg_v_131 = var1_132.concat(nl);
        printFile(fid, tmpArg_v_131);
        createVdmSlFieldVisitors(ad, fid, vpp);
        String tmpArg_v_142 = null;
        String var1_143 = null;
        String var1_144 = null;
        String var2_146 = null;
        var2_146 = ad.getPrefix();
        var1_144 = (new String("  public visitNode: I")).concat(var2_146);
        var1_143 = var1_144.concat(new String("Node ==> ()"));
        tmpArg_v_142 = var1_143.concat(nl);
        printFile(fid, tmpArg_v_142);
        String tmpArg_v_151 = null;
        String var1_152 = null;
        var1_152 = (new String("  visitNode (pNode) == pNode.accept(self);")).concat(nl);
        tmpArg_v_151 = var1_152.concat(nl);
        printFile(fid, tmpArg_v_151);
        createVdmDocLexemVisitors(ad, fid, vpp);
        HashSet iset_160 = new HashSet();
        iset_160 = ad.getComposites();
        String composite = null;
        String tmpArg_v_344;
        for(Iterator enm_349 = iset_160.iterator(); enm_349.hasNext(); printFile(fid, tmpArg_v_344))
        {
            String elem_161 = UTIL.ConvertToString(enm_349.next());
            composite = elem_161;
            String tmpVal_165 = null;
            String var1_166 = null;
            var1_166 = ad.getPrefix();
            tmpVal_165 = var1_166.concat(composite);
            String clnm = null;
            clnm = tmpVal_165;
            AstComposite cmp = null;
            cmp = ad.getComposite(composite);
            Integer cnt = new Integer(1);
            String tmpArg_v_171 = null;
            String var1_172 = null;
            String var1_173 = null;
            String var1_174 = null;
            String var1_175 = null;
            var1_175 = (new String("  public visit")).concat(composite);
            var1_174 = var1_175.concat(new String(": I"));
            var1_173 = var1_174.concat(clnm);
            var1_172 = var1_173.concat(new String(" ==> ()"));
            tmpArg_v_171 = var1_172.concat(nl);
            printFile(fid, tmpArg_v_171);
            String tmpArg_v_184 = null;
            String var1_185 = null;
            String var1_186 = null;
            var1_186 = (new String("  visit")).concat(composite);
            var1_185 = var1_186.concat(new String("(pcmp) =="));
            tmpArg_v_184 = var1_185.concat(nl);
            printFile(fid, tmpArg_v_184);
            String tmpArg_v_193 = null;
            tmpArg_v_193 = (new String("    ( dcl str : seq of char := prefix ^pcmp.identity() ^\"(\";")).concat(nl);
            printFile(fid, tmpArg_v_193);
            Boolean cond_196 = null;
            Integer var1_197 = null;
            Vector unArg_198 = null;
            unArg_198 = cmp.getFields();
            var1_197 = new Integer(unArg_198.size());
            cond_196 = new Boolean(var1_197.intValue() > (new Integer(1)).intValue());
            if(cond_196.booleanValue())
            {
                String tmpArg_v_202 = null;
                tmpArg_v_202 = (new String("      pushNL();")).concat(nl);
                printFile(fid, tmpArg_v_202);
                String tmpArg_v_207 = null;
                tmpArg_v_207 = (new String("      str := str ^getNL();")).concat(nl);
                printFile(fid, tmpArg_v_207);
            }
            do
            {
                Boolean whCrtl_210 = null;
                Integer var2_212 = null;
                Vector unArg_213 = null;
                unArg_213 = cmp.getFields();
                var2_212 = new Integer(unArg_213.size());
                whCrtl_210 = new Boolean(cnt.intValue() <= var2_212.intValue());
                if(!whCrtl_210.booleanValue())
                    break;
                AstField fld = null;
                Vector tmp_l_214 = null;
                tmp_l_214 = cmp.getFields();
                if(1 <= cnt.intValue() && cnt.intValue() <= tmp_l_214.size())
                    fld = (AstField)tmp_l_214.get(cnt.intValue() - 1);
                else
                    UTIL.RunTime("Run-Time Error:Illegal index");
                String ostr = null;
                AstType par_216 = null;
                par_216 = fld.getType();
                ostr = getVdmSlFieldOper(par_216);
                Boolean cond_217 = null;
                AstType obj_218 = null;
                obj_218 = fld.getType();
                cond_217 = obj_218.isOptionalType();
                if(cond_217.booleanValue())
                {
                    AstOptionalType aotp = null;
                    aotp = (AstOptionalType)fld.getType();
                    String tmpArg_v_234 = null;
                    String var1_235 = null;
                    String var1_236 = null;
                    String var2_238 = null;
                    var2_238 = fld.getName();
                    var1_236 = (new String("      if pcmp.has")).concat(var2_238);
                    var1_235 = var1_236.concat(new String("()"));
                    tmpArg_v_234 = var1_235.concat(nl);
                    printFile(fid, tmpArg_v_234);
                    String tmpArg_v_243 = null;
                    String var1_244 = null;
                    String var1_245 = null;
                    String var1_246 = null;
                    String var1_247 = null;
                    var1_247 = (new String("      then ")).concat(ostr);
                    var1_246 = var1_247.concat(new String("(pcmp.get"));
                    String var2_251 = null;
                    var2_251 = fld.getName();
                    var1_245 = var1_246.concat(var2_251);
                    var1_244 = var1_245.concat(new String("())"));
                    tmpArg_v_243 = var1_244.concat(nl);
                    printFile(fid, tmpArg_v_243);
                    String tmpArg_v_256 = null;
                    tmpArg_v_256 = (new String("      else result := \"nil\";")).concat(nl);
                    printFile(fid, tmpArg_v_256);
                } else
                {
                    String tmpArg_v_221 = null;
                    String var1_222 = null;
                    String var1_223 = null;
                    String var1_224 = null;
                    String var1_225 = null;
                    var1_225 = (new String("      ")).concat(ostr);
                    var1_224 = var1_225.concat(new String("(pcmp.get"));
                    String var2_229 = null;
                    var2_229 = fld.getName();
                    var1_223 = var1_224.concat(var2_229);
                    var1_222 = var1_223.concat(new String("());"));
                    tmpArg_v_221 = var1_222.concat(nl);
                    printFile(fid, tmpArg_v_221);
                }
                printFile(fid, new String("      str := str ^result"));
                Boolean cond_262 = null;
                Boolean var1_263 = null;
                Integer var2_265 = null;
                Vector unArg_266 = null;
                unArg_266 = cmp.getFields();
                var2_265 = new Integer(unArg_266.size());
                var1_263 = new Boolean(cnt.intValue() < var2_265.intValue());
                if(!(cond_262 = var1_263).booleanValue())
                    cond_262 = vpp;
                if(cond_262.booleanValue())
                {
                    Boolean cond_273 = null;
                    Integer var1_274 = null;
                    Vector unArg_275 = null;
                    unArg_275 = cmp.getFields();
                    var1_274 = new Integer(unArg_275.size());
                    cond_273 = new Boolean(var1_274.intValue() == (new Integer(1)).intValue());
                    if(cond_273.booleanValue())
                    {
                        String tmpArg_v_284 = null;
                        tmpArg_v_284 = (new String(" ^\",\";")).concat(nl);
                        printFile(fid, tmpArg_v_284);
                    } else
                    {
                        String tmpArg_v_279 = null;
                        tmpArg_v_279 = (new String(" ^\",\" ^getNL();")).concat(nl);
                        printFile(fid, tmpArg_v_279);
                    }
                } else
                {
                    String tmpArg_v_270 = null;
                    tmpArg_v_270 = (new String(";")).concat(nl);
                    printFile(fid, tmpArg_v_270);
                }
                cnt = UTIL.NumberToInt(UTIL.clone(new Integer(cnt.intValue() + (new Integer(1)).intValue())));
            } while(true);
            if(vpp.booleanValue())
            {
                String tmpArg_v_293 = null;
                tmpArg_v_293 = (new String("      printNatField(pcmp.getLine());")).concat(nl);
                printFile(fid, tmpArg_v_293);
                printFile(fid, new String("      str := str ^result ^\",\""));
                Boolean cond_299 = null;
                Integer var1_300 = null;
                Vector unArg_301 = null;
                unArg_301 = cmp.getFields();
                var1_300 = new Integer(unArg_301.size());
                cond_299 = new Boolean(var1_300.intValue() > (new Integer(1)).intValue());
                if(cond_299.booleanValue())
                {
                    String tmpArg_v_310 = null;
                    tmpArg_v_310 = (new String(" ^getNL();")).concat(nl);
                    printFile(fid, tmpArg_v_310);
                } else
                {
                    String tmpArg_v_305 = null;
                    tmpArg_v_305 = (new String(";")).concat(nl);
                    printFile(fid, tmpArg_v_305);
                }
                String tmpArg_v_315 = null;
                tmpArg_v_315 = (new String("      printNatField(pcmp.getColumn());")).concat(nl);
                printFile(fid, tmpArg_v_315);
                String tmpArg_v_320 = null;
                tmpArg_v_320 = (new String("      str := str ^result;")).concat(nl);
                printFile(fid, tmpArg_v_320);
            }
            Boolean cond_323 = null;
            Integer var1_324 = null;
            Vector unArg_325 = null;
            unArg_325 = cmp.getFields();
            var1_324 = new Integer(unArg_325.size());
            cond_323 = new Boolean(var1_324.intValue() > (new Integer(1)).intValue());
            if(cond_323.booleanValue())
            {
                String tmpArg_v_334 = null;
                tmpArg_v_334 = (new String("      popNL();")).concat(nl);
                printFile(fid, tmpArg_v_334);
                String tmpArg_v_339 = null;
                tmpArg_v_339 = (new String("      str := str ^getNL() ^\")\";")).concat(nl);
                printFile(fid, tmpArg_v_339);
            } else
            {
                String tmpArg_v_329 = null;
                tmpArg_v_329 = (new String("      str := str ^\")\";")).concat(nl);
                printFile(fid, tmpArg_v_329);
            }
            tmpArg_v_344 = null;
            String var1_345 = null;
            var1_345 = (new String("      result := str );")).concat(nl);
            tmpArg_v_344 = var1_345.concat(nl);
        }

        HashSet iset_350 = new HashSet();
        iset_350 = ad.getUnionShorthands();
        String shorthand = null;
        for(Iterator enm_463 = iset_350.iterator(); enm_463.hasNext();)
        {
            String elem_351 = UTIL.ConvertToString(enm_463.next());
            shorthand = elem_351;
            Tuple tmpVal_355 = new Tuple(2);
            AstType obj_356 = null;
            AstShorthand obj_357 = null;
            obj_357 = ad.getShorthand(shorthand);
            obj_356 = obj_357.getType();
            tmpVal_355 = obj_356.isTypeNameUnion();
            Boolean sb = null;
            boolean succ_354 = true;
            Vector e_l_359 = new Vector();
            for(int i_360 = 1; i_360 <= tmpVal_355.Length(); i_360++)
                e_l_359.add(tmpVal_355.GetField(i_360));

            if(succ_354 = 2 == e_l_359.size())
                sb = (Boolean)e_l_359.get(0);
            if(!succ_354)
                UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
            String shnm = null;
            String var1_363 = null;
            var1_363 = ad.getPrefix();
            shnm = var1_363.concat(shorthand);
            if(sb.booleanValue())
            {
                String tmpArg_v_441 = null;
                String var1_442 = null;
                String var1_443 = null;
                String var1_444 = null;
                String var1_445 = null;
                var1_445 = (new String("  public visit")).concat(shorthand);
                var1_444 = var1_445.concat(new String(": I"));
                var1_443 = var1_444.concat(shnm);
                var1_442 = var1_443.concat(new String(" ==> ()"));
                tmpArg_v_441 = var1_442.concat(nl);
                printFile(fid, tmpArg_v_441);
                String tmpArg_v_454 = null;
                String var1_455 = null;
                String var1_456 = null;
                String var1_457 = null;
                var1_457 = (new String("  visit")).concat(shorthand);
                var1_456 = var1_457.concat(new String("(pNode) == pNode.accept(self);"));
                var1_455 = var1_456.concat(nl);
                tmpArg_v_454 = var1_455.concat(nl);
                printFile(fid, tmpArg_v_454);
            } else
            {
                String tmpArg_v_368 = null;
                String var1_369 = null;
                String var1_370 = null;
                String var1_371 = null;
                String var1_372 = null;
                var1_372 = (new String("  public visit")).concat(shorthand);
                var1_371 = var1_372.concat(new String(": I"));
                var1_370 = var1_371.concat(shnm);
                var1_369 = var1_370.concat(new String(" ==> ()"));
                tmpArg_v_368 = var1_369.concat(nl);
                printFile(fid, tmpArg_v_368);
                String tmpArg_v_381 = null;
                String var1_382 = null;
                var1_382 = (new String("  visit")).concat(shorthand);
                tmpArg_v_381 = var1_382.concat(new String("(pNode) == "));
                printFile(fid, tmpArg_v_381);
                if((new Boolean(!vpp.booleanValue())).booleanValue())
                {
                    String tmpArg_v_434 = null;
                    String var1_435 = null;
                    var1_435 = (new String("result := pNode.getStringValue();")).concat(nl);
                    tmpArg_v_434 = var1_435.concat(nl);
                    printFile(fid, tmpArg_v_434);
                } else
                {
                    String tmpArg_v_390 = null;
                    String var1_391 = null;
                    var1_391 = nl.concat(new String("    ( dcl str : seq of char := prefix ^pNode.identity() ^\"(\";"));
                    tmpArg_v_390 = var1_391.concat(nl);
                    printFile(fid, tmpArg_v_390);
                    String tmpArg_v_397 = null;
                    tmpArg_v_397 = (new String("      printNatField(pNode.getValue());")).concat(nl);
                    printFile(fid, tmpArg_v_397);
                    String tmpArg_v_402 = null;
                    tmpArg_v_402 = (new String("      str := str ^ result ^ \",\";")).concat(nl);
                    printFile(fid, tmpArg_v_402);
                    String tmpArg_v_407 = null;
                    tmpArg_v_407 = (new String("      printNatField(pNode.getLine());")).concat(nl);
                    printFile(fid, tmpArg_v_407);
                    String tmpArg_v_412 = null;
                    tmpArg_v_412 = (new String("      str := str ^ result ^ \",\";")).concat(nl);
                    printFile(fid, tmpArg_v_412);
                    String tmpArg_v_417 = null;
                    tmpArg_v_417 = (new String("      printNatField(pNode.getColumn());")).concat(nl);
                    printFile(fid, tmpArg_v_417);
                    String tmpArg_v_422 = null;
                    tmpArg_v_422 = (new String("      str := str ^ result ^ \")\";")).concat(nl);
                    printFile(fid, tmpArg_v_422);
                    String tmpArg_v_427 = null;
                    String var1_428 = null;
                    var1_428 = (new String("      result := str );")).concat(nl);
                    tmpArg_v_427 = var1_428.concat(nl);
                    printFile(fid, tmpArg_v_427);
                }
            }
        }

        if((new Boolean(!vpp.booleanValue())).booleanValue())
        {
            String tmpArg_v_473 = null;
            tmpArg_v_473 = (new String("end VdmSlVisitor")).concat(nl);
            printFile(fid, tmpArg_v_473);
        } else
        {
            String tmpArg_v_468 = null;
            tmpArg_v_468 = (new String("end VdmPpVisitor")).concat(nl);
            printFile(fid, tmpArg_v_468);
        }
        String tmpArg_v_478 = null;
        tmpArg_v_478 = (new String("\\end{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_478);
    }

    private void createVdmSlFieldVisitors(AstDefinitions ad, Integer fid, Boolean vpp)
        throws CGException
    {
        String node = null;
        String var1_6 = null;
        String var2_8 = null;
        var2_8 = ad.getPrefix();
        var1_6 = (new String("I")).concat(var2_8);
        node = var1_6.concat(new String("Node"));
        String fval = null;
        fval = node.concat(new String("`FieldValue"));
        String tmpArg_v_16 = null;
        tmpArg_v_16 = (new String("  private printBoolField: bool ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_16);
        String tmpArg_v_21 = null;
        tmpArg_v_21 = (new String("  printBoolField (pval) ==")).concat(nl);
        printFile(fid, tmpArg_v_21);
        String tmpArg_v_26 = null;
        String var1_27 = null;
        var1_27 = (new String("    result := if pval then \"true\" else \"false\";")).concat(nl);
        tmpArg_v_26 = var1_27.concat(nl);
        printFile(fid, tmpArg_v_26);
        String tmpArg_v_33 = null;
        tmpArg_v_33 = (new String("  private printCharField: char ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_33);
        String tmpArg_v_38 = null;
        tmpArg_v_38 = (new String("  printCharField (pval) ==")).concat(nl);
        printFile(fid, tmpArg_v_38);
        String tmpArg_v_43 = null;
        String var1_44 = null;
        var1_44 = (new String("    result := [''',pval,'''];")).concat(nl);
        tmpArg_v_43 = var1_44.concat(nl);
        printFile(fid, tmpArg_v_43);
        String tmpArg_v_50 = null;
        tmpArg_v_50 = (new String("  private printNatField: nat ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_50);
        String tmpArg_v_55 = null;
        tmpArg_v_55 = (new String("  -- implement this operation by hand!")).concat(nl);
        printFile(fid, tmpArg_v_55);
        String tmpArg_v_60 = null;
        String var1_61 = null;
        var1_61 = (new String("  printNatField (-) == error;")).concat(nl);
        tmpArg_v_60 = var1_61.concat(nl);
        printFile(fid, tmpArg_v_60);
        String tmpArg_v_67 = null;
        tmpArg_v_67 = (new String("  private printRealField: real ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_67);
        String tmpArg_v_72 = null;
        tmpArg_v_72 = (new String("  -- implement this operation by hand!")).concat(nl);
        printFile(fid, tmpArg_v_72);
        String tmpArg_v_77 = null;
        String var1_78 = null;
        var1_78 = (new String("  printRealField (-) == error;")).concat(nl);
        tmpArg_v_77 = var1_78.concat(nl);
        printFile(fid, tmpArg_v_77);
        String tmpArg_v_84 = null;
        String var1_85 = null;
        String var1_86 = null;
        var1_86 = (new String("  private printNodeField: ")).concat(node);
        var1_85 = var1_86.concat(new String(" ==> ()"));
        tmpArg_v_84 = var1_85.concat(nl);
        printFile(fid, tmpArg_v_84);
        String tmpArg_v_93 = null;
        String var1_94 = null;
        var1_94 = (new String("  printNodeField (pNode) == pNode.accept(self);")).concat(nl);
        tmpArg_v_93 = var1_94.concat(nl);
        printFile(fid, tmpArg_v_93);
        String tmpArg_v_100 = null;
        tmpArg_v_100 = (new String("  private patchString: seq of char ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_100);
        String tmpArg_v_105 = null;
        tmpArg_v_105 = (new String("  patchString (str) ==")).concat(nl);
        printFile(fid, tmpArg_v_105);
        String tmpArg_v_110 = null;
        tmpArg_v_110 = (new String("   ( dcl res : seq of char := [];")).concat(nl);
        printFile(fid, tmpArg_v_110);
        String tmpArg_v_115 = null;
        tmpArg_v_115 = (new String("     for ch in str do")).concat(nl);
        printFile(fid, tmpArg_v_115);
        String tmpArg_v_120 = null;
        tmpArg_v_120 = (new String("       if ch = '\\\\' then res := res ^ \"\\\\\\\\\"")).concat(nl);
        printFile(fid, tmpArg_v_120);
        String tmpArg_v_125 = null;
        tmpArg_v_125 = (new String("       else if ch = '\\\"' then res := res ^ \"\\\\\\\"\"")).concat(nl);
        printFile(fid, tmpArg_v_125);
        String tmpArg_v_130 = null;
        tmpArg_v_130 = (new String("       else res := res ^ [ch];")).concat(nl);
        printFile(fid, tmpArg_v_130);
        String tmpArg_v_135 = null;
        String var1_136 = null;
        var1_136 = (new String("     return res );")).concat(nl);
        tmpArg_v_135 = var1_136.concat(nl);
        printFile(fid, tmpArg_v_135);
        String tmpArg_v_142 = null;
        tmpArg_v_142 = (new String("  private printStringField: seq of char ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_142);
        String tmpArg_v_147 = null;
        String var1_148 = null;
        var1_148 = (new String("  printStringField (str) == result := \"\\\"\" ^ patchString(str) ^ \"\\\"\";")).concat(nl);
        tmpArg_v_147 = var1_148.concat(nl);
        printFile(fid, tmpArg_v_147);
        String tmpArg_v_154 = null;
        String var1_155 = null;
        String var1_156 = null;
        var1_156 = (new String("  private printSeqofField: seq of ")).concat(fval);
        var1_155 = var1_156.concat(new String(" ==> ()"));
        tmpArg_v_154 = var1_155.concat(nl);
        printFile(fid, tmpArg_v_154);
        String tmpArg_v_163 = null;
        tmpArg_v_163 = (new String("  printSeqofField (pval) ==")).concat(nl);
        printFile(fid, tmpArg_v_163);
        String tmpArg_v_168 = null;
        tmpArg_v_168 = (new String("    ( dcl str : seq of char := \"[\", cnt : nat := len pval;")).concat(nl);
        printFile(fid, tmpArg_v_168);
        String tmpArg_v_173 = null;
        tmpArg_v_173 = (new String("      pushNL();")).concat(nl);
        printFile(fid, tmpArg_v_173);
        String tmpArg_v_178 = null;
        tmpArg_v_178 = (new String("      str := str ^ getNL();")).concat(nl);
        printFile(fid, tmpArg_v_178);
        String tmpArg_v_183 = null;
        tmpArg_v_183 = (new String("      while cnt > 0 do")).concat(nl);
        printFile(fid, tmpArg_v_183);
        String tmpArg_v_188 = null;
        tmpArg_v_188 = (new String("        ( printField(pval(len pval - cnt + 1));")).concat(nl);
        printFile(fid, tmpArg_v_188);
        String tmpArg_v_193 = null;
        tmpArg_v_193 = (new String("          str := str ^ result;")).concat(nl);
        printFile(fid, tmpArg_v_193);
        String tmpArg_v_198 = null;
        tmpArg_v_198 = (new String("          if cnt > 1")).concat(nl);
        printFile(fid, tmpArg_v_198);
        String tmpArg_v_203 = null;
        tmpArg_v_203 = (new String("          then str := str ^ \",\" ^ getNL();")).concat(nl);
        printFile(fid, tmpArg_v_203);
        String tmpArg_v_208 = null;
        tmpArg_v_208 = (new String("          cnt := cnt - 1 ); ")).concat(nl);
        printFile(fid, tmpArg_v_208);
        String tmpArg_v_213 = null;
        tmpArg_v_213 = (new String("      popNL();")).concat(nl);
        printFile(fid, tmpArg_v_213);
        String tmpArg_v_218 = null;
        tmpArg_v_218 = (new String("      str := str ^ getNL() ^ \"]\";")).concat(nl);
        printFile(fid, tmpArg_v_218);
        String tmpArg_v_223 = null;
        String var1_224 = null;
        var1_224 = (new String("      result := str );")).concat(nl);
        tmpArg_v_223 = var1_224.concat(nl);
        printFile(fid, tmpArg_v_223);
        String tmpArg_v_230 = null;
        String var1_231 = null;
        String var1_232 = null;
        var1_232 = (new String("  private printSetofField: set of ")).concat(fval);
        var1_231 = var1_232.concat(new String(" ==> ()"));
        tmpArg_v_230 = var1_231.concat(nl);
        printFile(fid, tmpArg_v_230);
        String tmpArg_v_239 = null;
        tmpArg_v_239 = (new String("  printSetofField (pval) ==")).concat(nl);
        printFile(fid, tmpArg_v_239);
        String tmpArg_v_244 = null;
        tmpArg_v_244 = (new String("    ( dcl str : seq of char := \"{\", cnt : nat := card pval,")).concat(nl);
        printFile(fid, tmpArg_v_244);
        String tmpArg_v_249 = null;
        String var1_250 = null;
        String var1_251 = null;
        var1_251 = (new String("          pvs : set of ")).concat(fval);
        var1_250 = var1_251.concat(new String(" := pval;"));
        tmpArg_v_249 = var1_250.concat(nl);
        printFile(fid, tmpArg_v_249);
        String tmpArg_v_258 = null;
        tmpArg_v_258 = (new String("      pushNL();")).concat(nl);
        printFile(fid, tmpArg_v_258);
        String tmpArg_v_263 = null;
        tmpArg_v_263 = (new String("      str := str ^ getNL();")).concat(nl);
        printFile(fid, tmpArg_v_263);
        String tmpArg_v_268 = null;
        tmpArg_v_268 = (new String("      while cnt > 0 do")).concat(nl);
        printFile(fid, tmpArg_v_268);
        String tmpArg_v_273 = null;
        tmpArg_v_273 = (new String("        let pv in set pvs in")).concat(nl);
        printFile(fid, tmpArg_v_273);
        String tmpArg_v_278 = null;
        tmpArg_v_278 = (new String("          ( printField(pv);")).concat(nl);
        printFile(fid, tmpArg_v_278);
        String tmpArg_v_283 = null;
        tmpArg_v_283 = (new String("            str := str ^ result;")).concat(nl);
        printFile(fid, tmpArg_v_283);
        String tmpArg_v_288 = null;
        tmpArg_v_288 = (new String("            pvs := pvs \\ {pv};")).concat(nl);
        printFile(fid, tmpArg_v_288);
        String tmpArg_v_293 = null;
        tmpArg_v_293 = (new String("            if cnt > 1")).concat(nl);
        printFile(fid, tmpArg_v_293);
        String tmpArg_v_298 = null;
        tmpArg_v_298 = (new String("            then str := str ^ \",\" ^ getNL();")).concat(nl);
        printFile(fid, tmpArg_v_298);
        String tmpArg_v_303 = null;
        tmpArg_v_303 = (new String("            cnt := cnt - 1 ); ")).concat(nl);
        printFile(fid, tmpArg_v_303);
        String tmpArg_v_308 = null;
        tmpArg_v_308 = (new String("      popNL();")).concat(nl);
        printFile(fid, tmpArg_v_308);
        String tmpArg_v_313 = null;
        tmpArg_v_313 = (new String("      str := str ^ getNL() ^ \"}\";")).concat(nl);
        printFile(fid, tmpArg_v_313);
        String tmpArg_v_318 = null;
        String var1_319 = null;
        var1_319 = (new String("      result := str );")).concat(nl);
        tmpArg_v_318 = var1_319.concat(nl);
        printFile(fid, tmpArg_v_318);
        String tmpArg_v_325 = null;
        String var1_326 = null;
        String var1_327 = null;
        String var1_328 = null;
        String var1_329 = null;
        var1_329 = (new String("  private printMapField: map ")).concat(fval);
        var1_328 = var1_329.concat(new String(" to "));
        var1_327 = var1_328.concat(fval);
        var1_326 = var1_327.concat(new String(" ==> ()"));
        tmpArg_v_325 = var1_326.concat(nl);
        printFile(fid, tmpArg_v_325);
        String tmpArg_v_338 = null;
        tmpArg_v_338 = (new String("  printMapField (pval) == ")).concat(nl);
        printFile(fid, tmpArg_v_338);
        String tmpArg_v_343 = null;
        tmpArg_v_343 = (new String("    ( dcl str : seq of char := \"{\", cnt : nat := card dom pval,")).concat(nl);
        printFile(fid, tmpArg_v_343);
        String tmpArg_v_348 = null;
        String var1_349 = null;
        String var1_350 = null;
        var1_350 = (new String("          pvs : set of ")).concat(fval);
        var1_349 = var1_350.concat(new String(" := dom pval;"));
        tmpArg_v_348 = var1_349.concat(nl);
        printFile(fid, tmpArg_v_348);
        String tmpArg_v_357 = null;
        tmpArg_v_357 = (new String("      pushNL();")).concat(nl);
        printFile(fid, tmpArg_v_357);
        String tmpArg_v_362 = null;
        tmpArg_v_362 = (new String("      str := str ^ getNL();")).concat(nl);
        printFile(fid, tmpArg_v_362);
        String tmpArg_v_367 = null;
        tmpArg_v_367 = (new String("      while cnt > 0 do")).concat(nl);
        printFile(fid, tmpArg_v_367);
        String tmpArg_v_372 = null;
        tmpArg_v_372 = (new String("        let pv in set pvs in")).concat(nl);
        printFile(fid, tmpArg_v_372);
        String tmpArg_v_377 = null;
        tmpArg_v_377 = (new String("          ( printField(pv);")).concat(nl);
        printFile(fid, tmpArg_v_377);
        String tmpArg_v_382 = null;
        tmpArg_v_382 = (new String("            str := str ^ result ^ \" |-> \";")).concat(nl);
        printFile(fid, tmpArg_v_382);
        String tmpArg_v_387 = null;
        tmpArg_v_387 = (new String("            printField(pval(pv));")).concat(nl);
        printFile(fid, tmpArg_v_387);
        String tmpArg_v_392 = null;
        tmpArg_v_392 = (new String("            str := str ^ result;")).concat(nl);
        printFile(fid, tmpArg_v_392);
        String tmpArg_v_397 = null;
        tmpArg_v_397 = (new String("            pvs := pvs \\ {pv};")).concat(nl);
        printFile(fid, tmpArg_v_397);
        String tmpArg_v_402 = null;
        tmpArg_v_402 = (new String("            if cnt > 1")).concat(nl);
        printFile(fid, tmpArg_v_402);
        String tmpArg_v_407 = null;
        tmpArg_v_407 = (new String("            then str := str ^ \",\" ^ getNL();")).concat(nl);
        printFile(fid, tmpArg_v_407);
        String tmpArg_v_412 = null;
        tmpArg_v_412 = (new String("            cnt := cnt - 1 ); ")).concat(nl);
        printFile(fid, tmpArg_v_412);
        String tmpArg_v_417 = null;
        tmpArg_v_417 = (new String("      popNL();")).concat(nl);
        printFile(fid, tmpArg_v_417);
        String tmpArg_v_422 = null;
        tmpArg_v_422 = (new String("      str := str ^ getNL() ^ \"}\";")).concat(nl);
        printFile(fid, tmpArg_v_422);
        String tmpArg_v_427 = null;
        String var1_428 = null;
        var1_428 = (new String("      result := str );")).concat(nl);
        tmpArg_v_427 = var1_428.concat(nl);
        printFile(fid, tmpArg_v_427);
        String tmpArg_v_434 = null;
        String var1_435 = null;
        String var1_436 = null;
        var1_436 = (new String("  private printField: ")).concat(fval);
        var1_435 = var1_436.concat(new String(" ==> ()"));
        tmpArg_v_434 = var1_435.concat(nl);
        printFile(fid, tmpArg_v_434);
        String tmpArg_v_443 = null;
        tmpArg_v_443 = (new String("  printField (fld) ==")).concat(nl);
        printFile(fid, tmpArg_v_443);
        String tmpArg_v_448 = null;
        tmpArg_v_448 = (new String("    if is_bool(fld) then printBoolField(fld)")).concat(nl);
        printFile(fid, tmpArg_v_448);
        String tmpArg_v_453 = null;
        tmpArg_v_453 = (new String("    elseif is_char(fld) then printCharField(fld)")).concat(nl);
        printFile(fid, tmpArg_v_453);
        String tmpArg_v_458 = null;
        tmpArg_v_458 = (new String("    elseif is_nat(fld) then printNatField(fld)")).concat(nl);
        printFile(fid, tmpArg_v_458);
        String tmpArg_v_463 = null;
        tmpArg_v_463 = (new String("    elseif is_real(fld) then printRealField(fld)")).concat(nl);
        printFile(fid, tmpArg_v_463);
        String tmpArg_v_468 = null;
        String var1_469 = null;
        String var1_470 = null;
        var1_470 = (new String("    elseif isofclass(")).concat(node);
        var1_469 = var1_470.concat(new String(",fld) then printNodeField(fld)"));
        tmpArg_v_468 = var1_469.concat(nl);
        printFile(fid, tmpArg_v_468);
        String tmpArg_v_477 = null;
        String var1_478 = null;
        var1_478 = (new String("    else printStringField(fld);")).concat(nl);
        tmpArg_v_477 = var1_478.concat(nl);
        printFile(fid, tmpArg_v_477);
    }

    private String getVdmSlFieldOper(AstType ast)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = ast.isBoolType();
        if(cond_2.booleanValue())
            return new String("printBoolField");
        Boolean cond_20 = null;
        cond_20 = ast.isCharType();
        if(cond_20.booleanValue())
            return new String("printCharField");
        Boolean cond_18 = null;
        cond_18 = ast.isNatType();
        if(cond_18.booleanValue())
            return new String("printNatField");
        Boolean cond_16 = null;
        cond_16 = ast.isRealType();
        if(cond_16.booleanValue())
            return new String("printRealField");
        Boolean cond_14 = null;
        cond_14 = ast.isTypeName();
        if(cond_14.booleanValue())
            return new String("printNodeField");
        Boolean cond_12 = null;
        cond_12 = ast.isStringType();
        if(cond_12.booleanValue())
            return new String("printStringField");
        Boolean cond_9 = null;
        cond_9 = ast.isOptionalType();
        if(cond_9.booleanValue())
        {
            AstOptionalType aotp = (AstOptionalType)ast;
            String rexpr_10 = null;
            AstType par_11 = null;
            par_11 = aotp.getType();
            rexpr_10 = getVdmSlFieldOper(par_11);
            return rexpr_10;
        }
        Boolean cond_7 = null;
        cond_7 = ast.isSeqType();
        if(cond_7.booleanValue())
            return new String("printSeqofField");
        Boolean cond_5 = null;
        cond_5 = ast.isSetType();
        if(cond_5.booleanValue())
            return new String("printSetofField");
        Boolean cond_3 = null;
        cond_3 = ast.isMapType();
        if(cond_3.booleanValue())
        {
            return new String("printMapField");
        } else
        {
            UTIL.RunTime("Run-Time Error:Can not evaluate an error statement");
            return new String();
        }
    }

    private void createVdmDocLexemVisitors(AstDefinitions ad, Integer fid, Boolean vpp)
        throws CGException
    {
        String pfx = null;
        pfx = ad.getPrefix();
        String tmpArg_v_8 = null;
        String var1_9 = null;
        String var1_10 = null;
        var1_10 = (new String("  public visitDocument: I")).concat(pfx);
        var1_9 = var1_10.concat(new String("Document ==> ()"));
        tmpArg_v_8 = var1_9.concat(nl);
        printFile(fid, tmpArg_v_8);
        String tmpArg_v_17 = null;
        tmpArg_v_17 = (new String("  visitDocument(pcmp) ==")).concat(nl);
        printFile(fid, tmpArg_v_17);
        if(vpp.booleanValue())
        {
            String tmpArg_v_28 = null;
            String var1_29 = null;
            String var1_30 = null;
            var1_30 = (new String("    ( dcl str : seq of char := \"new ")).concat(pfx);
            var1_29 = var1_30.concat(new String("Document(\","));
            tmpArg_v_28 = var1_29.concat(nl);
            printFile(fid, tmpArg_v_28);
            String tmpArg_v_37 = null;
            String var1_38 = null;
            String var1_39 = null;
            var1_39 = (new String("          lxms : seq of I")).concat(pfx);
            var1_38 = var1_39.concat(new String("Lexem := pcmp.getLexems();"));
            tmpArg_v_37 = var1_38.concat(nl);
            printFile(fid, tmpArg_v_37);
            String tmpArg_v_46 = null;
            tmpArg_v_46 = (new String("      pushNL();")).concat(nl);
            printFile(fid, tmpArg_v_46);
            String tmpArg_v_51 = null;
            tmpArg_v_51 = (new String("      printStringField(pcmp.getFilename());")).concat(nl);
            printFile(fid, tmpArg_v_51);
            String tmpArg_v_56 = null;
            tmpArg_v_56 = (new String("      str := str ^ result ^ \",\" ^ getNL();")).concat(nl);
            printFile(fid, tmpArg_v_56);
        } else
        {
            String tmpArg_v_23 = null;
            tmpArg_v_23 = (new String("    ( ")).concat(nl);
            printFile(fid, tmpArg_v_23);
        }
        Vector sq_59 = null;
        sq_59 = ad.getTop();
        String name = null;
        String tmpArg_v_65;
        for(Iterator enm_81 = sq_59.iterator(); enm_81.hasNext(); printFile(fid, tmpArg_v_65))
        {
            String elem_60 = UTIL.ConvertToString(enm_81.next());
            name = elem_60;
            tmpArg_v_65 = null;
            String var1_66 = null;
            String var1_67 = null;
            String var1_68 = null;
            String var1_69 = null;
            String var1_70 = null;
            String var1_71 = null;
            var1_71 = (new String("      if pcmp.has")).concat(name);
            var1_70 = var1_71.concat(new String("() then visit"));
            var1_69 = var1_70.concat(name);
            var1_68 = var1_69.concat(new String("(pcmp.get"));
            var1_67 = var1_68.concat(name);
            var1_66 = var1_67.concat(new String("());"));
            tmpArg_v_65 = var1_66.concat(nl);
        }

        if(vpp.booleanValue())
        {
            String tmpArg_v_85 = null;
            tmpArg_v_85 = (new String("      str := str ^ result ^ \",\" ^ getNL();")).concat(nl);
            printFile(fid, tmpArg_v_85);
        }
        if(vpp.booleanValue())
        {
            String tmpArg_v_91 = null;
            tmpArg_v_91 = (new String("      str := str ^ \"[\";")).concat(nl);
            printFile(fid, tmpArg_v_91);
            String tmpArg_v_96 = null;
            tmpArg_v_96 = (new String("      if len lxms > 0")).concat(nl);
            printFile(fid, tmpArg_v_96);
            String tmpArg_v_101 = null;
            tmpArg_v_101 = (new String("      then ( pushNL();")).concat(nl);
            printFile(fid, tmpArg_v_101);
            String tmpArg_v_106 = null;
            tmpArg_v_106 = (new String("             while len lxms > 0 do")).concat(nl);
            printFile(fid, tmpArg_v_106);
            String tmpArg_v_111 = null;
            tmpArg_v_111 = (new String("               ( visitLexem(hd lxms);")).concat(nl);
            printFile(fid, tmpArg_v_111);
            String tmpArg_v_116 = null;
            tmpArg_v_116 = (new String("                 str := str ^ getNL() ^ result;")).concat(nl);
            printFile(fid, tmpArg_v_116);
            String tmpArg_v_121 = null;
            tmpArg_v_121 = (new String("                 lxms := tl lxms;")).concat(nl);
            printFile(fid, tmpArg_v_121);
            String tmpArg_v_126 = null;
            tmpArg_v_126 = (new String("                 if len lxms > 0")).concat(nl);
            printFile(fid, tmpArg_v_126);
            String tmpArg_v_131 = null;
            tmpArg_v_131 = (new String("                 then str := str ^ \",\" );")).concat(nl);
            printFile(fid, tmpArg_v_131);
            String tmpArg_v_136 = null;
            tmpArg_v_136 = (new String("             popNL() );")).concat(nl);
            printFile(fid, tmpArg_v_136);
            String tmpArg_v_141 = null;
            tmpArg_v_141 = (new String("      str := str ^ getNL() ^ \"]\";")).concat(nl);
            printFile(fid, tmpArg_v_141);
        }
        if(vpp.booleanValue())
        {
            String tmpArg_v_154 = null;
            tmpArg_v_154 = (new String("      popNL();")).concat(nl);
            printFile(fid, tmpArg_v_154);
            String tmpArg_v_159 = null;
            tmpArg_v_159 = (new String("      str := str ^ getNL() ^ \")\";")).concat(nl);
            printFile(fid, tmpArg_v_159);
            String tmpArg_v_164 = null;
            String var1_165 = null;
            var1_165 = (new String("      result := str );")).concat(nl);
            tmpArg_v_164 = var1_165.concat(nl);
            printFile(fid, tmpArg_v_164);
        } else
        {
            String tmpArg_v_147 = null;
            String var1_148 = null;
            var1_148 = (new String("    );")).concat(nl);
            tmpArg_v_147 = var1_148.concat(nl);
            printFile(fid, tmpArg_v_147);
        }
        String tmpArg_v_171 = null;
        String var1_172 = null;
        String var1_173 = null;
        var1_173 = (new String("  public visitLexem: I")).concat(pfx);
        var1_172 = var1_173.concat(new String("Lexem ==> ()"));
        tmpArg_v_171 = var1_172.concat(nl);
        printFile(fid, tmpArg_v_171);
        String tmpArg_v_180 = null;
        tmpArg_v_180 = (new String("  visitLexem(pcmp) ==")).concat(nl);
        printFile(fid, tmpArg_v_180);
        if(vpp.booleanValue())
        {
            String tmpArg_v_195 = null;
            String var1_196 = null;
            String var1_197 = null;
            var1_197 = (new String("    ( dcl str : seq of char := \"new ")).concat(pfx);
            var1_196 = var1_197.concat(new String("Lexem(\";"));
            tmpArg_v_195 = var1_196.concat(nl);
            printFile(fid, tmpArg_v_195);
        } else
        {
            String tmpArg_v_186 = null;
            String var1_187 = null;
            String var1_188 = null;
            var1_188 = (new String("    ( dcl str : seq of char := \"mk_ ")).concat(pfx);
            var1_187 = var1_188.concat(new String("Lexem(\";"));
            tmpArg_v_186 = var1_187.concat(nl);
            printFile(fid, tmpArg_v_186);
        }
        String tmpArg_v_204 = null;
        tmpArg_v_204 = (new String("      printNatField(pcmp.getLine());")).concat(nl);
        printFile(fid, tmpArg_v_204);
        String tmpArg_v_209 = null;
        tmpArg_v_209 = (new String("      str := str ^ result ^ \",\";")).concat(nl);
        printFile(fid, tmpArg_v_209);
        String tmpArg_v_214 = null;
        tmpArg_v_214 = (new String("      printNatField(pcmp.getColumn());")).concat(nl);
        printFile(fid, tmpArg_v_214);
        String tmpArg_v_219 = null;
        tmpArg_v_219 = (new String("      str := str ^ result ^ \",\";")).concat(nl);
        printFile(fid, tmpArg_v_219);
        String tmpArg_v_224 = null;
        tmpArg_v_224 = (new String("      printNatField(pcmp.getLexval());")).concat(nl);
        printFile(fid, tmpArg_v_224);
        String tmpArg_v_229 = null;
        tmpArg_v_229 = (new String("      str := str ^ result ^ \",\";")).concat(nl);
        printFile(fid, tmpArg_v_229);
        String tmpArg_v_234 = null;
        tmpArg_v_234 = (new String("      printStringField(pcmp.getText());")).concat(nl);
        printFile(fid, tmpArg_v_234);
        String tmpArg_v_239 = null;
        tmpArg_v_239 = (new String("      str := str ^ result ^ \",\";")).concat(nl);
        printFile(fid, tmpArg_v_239);
        String tmpArg_v_244 = null;
        tmpArg_v_244 = (new String("      printNatField(pcmp.getType());")).concat(nl);
        printFile(fid, tmpArg_v_244);
        String tmpArg_v_249 = null;
        tmpArg_v_249 = (new String("      str := str ^ result ^ \")\";")).concat(nl);
        printFile(fid, tmpArg_v_249);
        String tmpArg_v_254 = null;
        String var1_255 = null;
        var1_255 = (new String("      result := str );")).concat(nl);
        tmpArg_v_254 = var1_255.concat(nl);
        printFile(fid, tmpArg_v_254);
    }

    private void createVppBaseNode(AstDefinitions ad, Integer fid, String basenm)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_11 = null;
        String var1_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        String var2_16 = null;
        var2_16 = ad.getPrefix();
        var1_14 = (new String("class I")).concat(var2_16);
        var1_13 = var1_14.concat(new String("ContextInfo"));
        var1_12 = var1_13.concat(nl);
        tmpArg_v_11 = var1_12.concat(nl);
        printFile(fid, tmpArg_v_11);
        String tmpArg_v_22 = null;
        tmpArg_v_22 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_22);
        String tmpArg_v_27 = null;
        String var1_28 = null;
        String var1_29 = null;
        String var2_31 = null;
        var2_31 = ad.getPrefix();
        var1_29 = (new String("  public accept: I")).concat(var2_31);
        var1_28 = var1_29.concat(new String("Visitor ==> ()"));
        tmpArg_v_27 = var1_28.concat(nl);
        printFile(fid, tmpArg_v_27);
        String tmpArg_v_36 = null;
        String var1_37 = null;
        var1_37 = (new String("  accept (-) == is subclass responsibility")).concat(nl);
        tmpArg_v_36 = var1_37.concat(nl);
        printFile(fid, tmpArg_v_36);
        String tmpArg_v_43 = null;
        String var1_44 = null;
        String var1_45 = null;
        String var2_47 = null;
        var2_47 = ad.getPrefix();
        var1_45 = (new String("end I")).concat(var2_47);
        var1_44 = var1_45.concat(new String("ContextInfo"));
        tmpArg_v_43 = var1_44.concat(nl);
        printFile(fid, tmpArg_v_43);
        String tmpArg_v_52 = null;
        String var1_53 = null;
        var1_53 = (new String("\\end{vdm_al}")).concat(nl);
        tmpArg_v_52 = var1_53.concat(nl);
        printFile(fid, tmpArg_v_52);
        String tmpArg_v_59 = null;
        tmpArg_v_59 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_59);
        String tmpArg_v_64 = null;
        String var1_65 = null;
        String var1_66 = null;
        String var2_68 = null;
        var2_68 = ad.getPrefix();
        var1_66 = (new String("-- important note: this class is renamed to ")).concat(var2_68);
        var1_65 = var1_66.concat(new String("Node by the patch script!"));
        tmpArg_v_64 = var1_65.concat(nl);
        printFile(fid, tmpArg_v_64);
        String tmpArg_v_73 = null;
        String var1_74 = null;
        String var1_75 = null;
        var1_75 = (new String("class I")).concat(basenm);
        var1_74 = var1_75.concat(nl);
        tmpArg_v_73 = var1_74.concat(nl);
        printFile(fid, tmpArg_v_73);
        String tmpArg_v_82 = null;
        tmpArg_v_82 = (new String("values")).concat(nl);
        printFile(fid, tmpArg_v_82);
        String tmpArg_v_87 = null;
        String var1_88 = null;
        String var1_89 = null;
        String var1_90 = null;
        String var2_92 = null;
        var2_92 = ad.getPrefix();
        var1_90 = (new String("  public static prefix : seq of char = \"")).concat(var2_92);
        var1_89 = var1_90.concat(new String("\""));
        var1_88 = var1_89.concat(nl);
        tmpArg_v_87 = var1_88.concat(nl);
        printFile(fid, tmpArg_v_87);
        String tmpArg_v_98 = null;
        tmpArg_v_98 = (new String("types")).concat(nl);
        printFile(fid, tmpArg_v_98);
        String tmpArg_v_103 = null;
        tmpArg_v_103 = (new String("  public FieldValue = ")).concat(nl);
        printFile(fid, tmpArg_v_103);
        String tmpArg_v_108 = null;
        String var1_109 = null;
        String var1_110 = null;
        String var2_112 = null;
        var2_112 = ad.getPrefix();
        var1_110 = (new String("    bool | char | nat | real | I")).concat(var2_112);
        var1_109 = var1_110.concat(new String("Node | "));
        tmpArg_v_108 = var1_109.concat(nl);
        printFile(fid, tmpArg_v_108);
        String tmpArg_v_117 = null;
        tmpArg_v_117 = (new String("    seq of FieldValue |")).concat(nl);
        printFile(fid, tmpArg_v_117);
        String tmpArg_v_122 = null;
        tmpArg_v_122 = (new String("    set of FieldValue |")).concat(nl);
        printFile(fid, tmpArg_v_122);
        String tmpArg_v_127 = null;
        String var1_128 = null;
        var1_128 = (new String("    map FieldValue to FieldValue")).concat(nl);
        tmpArg_v_127 = var1_128.concat(nl);
        printFile(fid, tmpArg_v_127);
        String tmpArg_v_134 = null;
        tmpArg_v_134 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_134);
        String tmpArg_v_139 = null;
        String var1_140 = null;
        String var1_141 = null;
        String var1_142 = null;
        String var2_144 = null;
        var2_144 = ad.getPrefix();
        var1_142 = (new String("  private ivInfo : map nat to I")).concat(var2_144);
        var1_141 = var1_142.concat(new String("ContextInfo := {|->}"));
        var1_140 = var1_141.concat(nl);
        tmpArg_v_139 = var1_140.concat(nl);
        printFile(fid, tmpArg_v_139);
        String tmpArg_v_150 = null;
        tmpArg_v_150 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_150);
        String tmpArg_v_155 = null;
        tmpArg_v_155 = (new String("  public identity: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_155);
        String tmpArg_v_160 = null;
        String var1_161 = null;
        var1_161 = (new String("  identity () == return \"Node\";")).concat(nl);
        tmpArg_v_160 = var1_161.concat(nl);
        printFile(fid, tmpArg_v_160);
        String tmpArg_v_167 = null;
        String var1_168 = null;
        String var1_169 = null;
        String var2_171 = null;
        var2_171 = ad.getPrefix();
        var1_169 = (new String("  public accept: I")).concat(var2_171);
        var1_168 = var1_169.concat(new String("Visitor ==> ()"));
        tmpArg_v_167 = var1_168.concat(nl);
        printFile(fid, tmpArg_v_167);
        String tmpArg_v_176 = null;
        String var1_177 = null;
        var1_177 = (new String("  accept (pVisitor) == pVisitor.visitNode(self);")).concat(nl);
        tmpArg_v_176 = var1_177.concat(nl);
        printFile(fid, tmpArg_v_176);
        String tmpArg_v_183 = null;
        String var1_184 = null;
        String var1_185 = null;
        String var2_187 = null;
        var2_187 = ad.getPrefix();
        var1_185 = (new String("  public getContextInfo: nat ==> I")).concat(var2_187);
        var1_184 = var1_185.concat(new String("ContextInfo"));
        tmpArg_v_183 = var1_184.concat(nl);
        printFile(fid, tmpArg_v_183);
        String tmpArg_v_192 = null;
        tmpArg_v_192 = (new String("  getContextInfo (pci) == return ivInfo(pci)")).concat(nl);
        printFile(fid, tmpArg_v_192);
        String tmpArg_v_197 = null;
        String var1_198 = null;
        var1_198 = (new String("    pre pci in set dom ivInfo;")).concat(nl);
        tmpArg_v_197 = var1_198.concat(nl);
        printFile(fid, tmpArg_v_197);
        String tmpArg_v_204 = null;
        tmpArg_v_204 = (new String("  public getContextInfoCount: () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_204);
        String tmpArg_v_209 = null;
        String var1_210 = null;
        var1_210 = (new String("  getContextInfoCount () == return card dom ivInfo;")).concat(nl);
        tmpArg_v_209 = var1_210.concat(nl);
        printFile(fid, tmpArg_v_209);
        String tmpArg_v_216 = null;
        String var1_217 = null;
        String var1_218 = null;
        String var2_220 = null;
        var2_220 = ad.getPrefix();
        var1_218 = (new String("  public addContextInfo: I")).concat(var2_220);
        var1_217 = var1_218.concat(new String("ContextInfo ==> nat"));
        tmpArg_v_216 = var1_217.concat(nl);
        printFile(fid, tmpArg_v_216);
        String tmpArg_v_225 = null;
        tmpArg_v_225 = (new String("  addContextInfo (pci) ==")).concat(nl);
        printFile(fid, tmpArg_v_225);
        String tmpArg_v_230 = null;
        tmpArg_v_230 = (new String("    ( dcl res : nat := card dom ivInfo + 1;")).concat(nl);
        printFile(fid, tmpArg_v_230);
        String tmpArg_v_235 = null;
        tmpArg_v_235 = (new String("      ivInfo := ivInfo munion {res |-> pci};")).concat(nl);
        printFile(fid, tmpArg_v_235);
        String tmpArg_v_240 = null;
        String var1_241 = null;
        var1_241 = (new String("      return res )")).concat(nl);
        tmpArg_v_240 = var1_241.concat(nl);
        printFile(fid, tmpArg_v_240);
        String tmpArg_v_247 = null;
        tmpArg_v_247 = (new String("instance variables")).concat(nl);
        printFile(fid, tmpArg_v_247);
        String tmpArg_v_252 = null;
        tmpArg_v_252 = (new String("  private ivLine : nat := 0;")).concat(nl);
        printFile(fid, tmpArg_v_252);
        String tmpArg_v_257 = null;
        String var1_258 = null;
        var1_258 = (new String("  private ivColumn : nat := 0;")).concat(nl);
        tmpArg_v_257 = var1_258.concat(nl);
        printFile(fid, tmpArg_v_257);
        String tmpArg_v_264 = null;
        tmpArg_v_264 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_264);
        String tmpArg_v_269 = null;
        tmpArg_v_269 = (new String("  public getLine : () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_269);
        String tmpArg_v_274 = null;
        String var1_275 = null;
        var1_275 = (new String("  getLine () == return ivLine;")).concat(nl);
        tmpArg_v_274 = var1_275.concat(nl);
        printFile(fid, tmpArg_v_274);
        String tmpArg_v_281 = null;
        tmpArg_v_281 = (new String("  public setLine : nat ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_281);
        String tmpArg_v_286 = null;
        String var1_287 = null;
        var1_287 = (new String("  setLine (pl) == ivLine := pl;")).concat(nl);
        tmpArg_v_286 = var1_287.concat(nl);
        printFile(fid, tmpArg_v_286);
        String tmpArg_v_293 = null;
        tmpArg_v_293 = (new String("  public getColumn : () ==> nat")).concat(nl);
        printFile(fid, tmpArg_v_293);
        String tmpArg_v_298 = null;
        String var1_299 = null;
        var1_299 = (new String("  getColumn () == return ivColumn;")).concat(nl);
        tmpArg_v_298 = var1_299.concat(nl);
        printFile(fid, tmpArg_v_298);
        String tmpArg_v_305 = null;
        tmpArg_v_305 = (new String("  public setColumn : nat ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_305);
        String tmpArg_v_310 = null;
        String var1_311 = null;
        var1_311 = (new String("  setColumn (pc) == ivColumn := pc;")).concat(nl);
        tmpArg_v_310 = var1_311.concat(nl);
        printFile(fid, tmpArg_v_310);
        String tmpArg_v_317 = null;
        tmpArg_v_317 = (new String("  public setPosition : nat * nat ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_317);
        String tmpArg_v_322 = null;
        tmpArg_v_322 = (new String("  setPosition (pl, pc) ==")).concat(nl);
        printFile(fid, tmpArg_v_322);
        String tmpArg_v_327 = null;
        String var1_328 = null;
        var1_328 = (new String("    ( setLine(pl); setColumn(pc) );")).concat(nl);
        tmpArg_v_327 = var1_328.concat(nl);
        printFile(fid, tmpArg_v_327);
        String tmpArg_v_334 = null;
        String var1_335 = null;
        String var1_336 = null;
        String var2_338 = null;
        var2_338 = ad.getPrefix();
        var1_336 = (new String("  public setPosLexem : I")).concat(var2_338);
        var1_335 = var1_336.concat(new String("Lexem ==> ()"));
        tmpArg_v_334 = var1_335.concat(nl);
        printFile(fid, tmpArg_v_334);
        String tmpArg_v_343 = null;
        tmpArg_v_343 = (new String("  setPosLexem (pol) ==")).concat(nl);
        printFile(fid, tmpArg_v_343);
        String tmpArg_v_348 = null;
        tmpArg_v_348 = (new String("    ( setLine(pol.getLine());")).concat(nl);
        printFile(fid, tmpArg_v_348);
        String tmpArg_v_353 = null;
        String var1_354 = null;
        var1_354 = (new String("      setColumn(pol.getColumn()) );")).concat(nl);
        tmpArg_v_353 = var1_354.concat(nl);
        printFile(fid, tmpArg_v_353);
        String tmpArg_v_360 = null;
        String var1_361 = null;
        String var1_362 = null;
        String var2_364 = null;
        var2_364 = ad.getPrefix();
        var1_362 = (new String("  public setPosNode : I")).concat(var2_364);
        var1_361 = var1_362.concat(new String("Node ==> ()"));
        tmpArg_v_360 = var1_361.concat(nl);
        printFile(fid, tmpArg_v_360);
        String tmpArg_v_369 = null;
        tmpArg_v_369 = (new String("  setPosNode (pnd) ==")).concat(nl);
        printFile(fid, tmpArg_v_369);
        String tmpArg_v_374 = null;
        tmpArg_v_374 = (new String("    ( setLine(pnd.getLine());")).concat(nl);
        printFile(fid, tmpArg_v_374);
        String tmpArg_v_379 = null;
        String var1_380 = null;
        var1_380 = (new String("      setColumn(pnd.getColumn()) )")).concat(nl);
        tmpArg_v_379 = var1_380.concat(nl);
        printFile(fid, tmpArg_v_379);
        String tmpArg_v_386 = null;
        String var1_387 = null;
        var1_387 = (new String("end I")).concat(basenm);
        tmpArg_v_386 = var1_387.concat(nl);
        printFile(fid, tmpArg_v_386);
        String tmpArg_v_393 = null;
        String var1_394 = null;
        var1_394 = (new String("\\end{vdm_al}")).concat(nl);
        tmpArg_v_393 = var1_394.concat(nl);
        printFile(fid, tmpArg_v_393);
    }

    private void createNormalVisitor(AstDefinitions ad, Integer fid, String id)
        throws CGException
    {
        String tmpArg_v_6 = null;
        String var1_7 = null;
        String var1_8 = null;
        String var1_9 = null;
        String var1_10 = null;
        String var1_11 = null;
        var1_11 = (new String("  public visit")).concat(id);
        var1_10 = var1_11.concat(new String(": I"));
        String var2_15 = null;
        var2_15 = ad.getPrefix();
        var1_9 = var1_10.concat(var2_15);
        var1_8 = var1_9.concat(id);
        var1_7 = var1_8.concat(new String(" ==> ()"));
        tmpArg_v_6 = var1_7.concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        String var1_24 = null;
        var1_24 = (new String("  visit")).concat(id);
        var1_23 = var1_24.concat(new String(" (-) == skip;"));
        var1_22 = var1_23.concat(nl);
        tmpArg_v_21 = var1_22.concat(nl);
        printFile(fid, tmpArg_v_21);
    }

    private void createNormalIVisitor(AstDefinitions ad, Integer fid, String id)
        throws CGException
    {
        String tmpArg_v_6 = null;
        String var1_7 = null;
        String var1_8 = null;
        String var1_9 = null;
        String var1_10 = null;
        String var1_11 = null;
        var1_11 = (new String("  public visit")).concat(id);
        var1_10 = var1_11.concat(new String(": I"));
        String var2_15 = null;
        var2_15 = ad.getPrefix();
        var1_9 = var1_10.concat(var2_15);
        var1_8 = var1_9.concat(id);
        var1_7 = var1_8.concat(new String(" ==> ()"));
        tmpArg_v_6 = var1_7.concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_21 = null;
        String var1_22 = null;
        String var1_23 = null;
        String var1_24 = null;
        var1_24 = (new String("  visit")).concat(id);
        var1_23 = var1_24.concat(new String(" (-) == is subclass responsibility;"));
        var1_22 = var1_23.concat(nl);
        tmpArg_v_21 = var1_22.concat(nl);
        printFile(fid, tmpArg_v_21);
    }

    private void createNodeIVisitor(AstDefinitions ad, Integer fid)
        throws CGException
    {
        String tmpArg_v_5 = null;
        String var1_6 = null;
        String var1_7 = null;
        String var2_9 = null;
        var2_9 = ad.getPrefix();
        var1_7 = (new String("  public visitNode: I")).concat(var2_9);
        var1_6 = var1_7.concat(new String("Node ==> ()"));
        tmpArg_v_5 = var1_6.concat(nl);
        printFile(fid, tmpArg_v_5);
        String tmpArg_v_14 = null;
        String var1_15 = null;
        var1_15 = (new String("  visitNode (-) == is subclass responsibility;")).concat(nl);
        tmpArg_v_14 = var1_15.concat(nl);
        printFile(fid, tmpArg_v_14);
    }

    private void createNodeVisitor(AstDefinitions ad, Integer fid)
        throws CGException
    {
        HashSet subcl = new HashSet();
        HashSet var1_3 = new HashSet();
        var1_3 = ad.getComposites();
        HashSet var2_4 = new HashSet();
        var2_4 = ad.getUnionShorthands();
        subcl = (HashSet)var1_3.clone();
        subcl.addAll(var2_4);
        String tmpArg_v_7 = null;
        String var1_8 = null;
        String var1_9 = null;
        String var2_11 = null;
        var2_11 = ad.getPrefix();
        var1_9 = (new String("  public visitNode: I")).concat(var2_11);
        var1_8 = var1_9.concat(new String("Node ==> ()"));
        tmpArg_v_7 = var1_8.concat(nl);
        printFile(fid, tmpArg_v_7);
        if((new Boolean(UTIL.equals(subcl, new HashSet()))).booleanValue())
        {
            String tmpArg_v_72 = null;
            String var1_73 = null;
            var1_73 = (new String("  visitNode (-) == skip;")).concat(nl);
            tmpArg_v_72 = var1_73.concat(nl);
            printFile(fid, tmpArg_v_72);
        } else
        {
            String tmpArg_v_19 = null;
            String var1_20 = null;
            var1_20 = (new String("  visitNode (pNode) ==")).concat(nl);
            tmpArg_v_19 = var1_20.concat(new String("    ( "));
            printFile(fid, tmpArg_v_19);
            while((new Boolean((new Integer(subcl.size())).intValue() > (new Integer(0)).intValue())).booleanValue()) 
            {
                HashSet tmpSet_28 = new HashSet();
                tmpSet_28 = subcl;
                String tp = null;
                boolean succ_30 = false;
                for(Iterator enm_32 = tmpSet_28.iterator(); enm_32.hasNext() && !succ_30;)
                {
                    String tmpElem_31 = UTIL.ConvertToString(enm_32.next());
                    succ_30 = true;
                    tp = tmpElem_31;
                }

                if(!succ_30)
                    UTIL.RunTime("Run-Time Error:The binding environment was empty");
                String ptp = null;
                String var1_35 = null;
                var1_35 = ad.getPrefix();
                ptp = var1_35.concat(tp);
                String tmpArg_v_39 = null;
                String var1_40 = null;
                String var1_41 = null;
                String var1_42 = null;
                var1_42 = (new String("if isofclass(I")).concat(ptp);
                var1_41 = var1_42.concat(new String(",pNode) then visit"));
                var1_40 = var1_41.concat(tp);
                tmpArg_v_39 = var1_40.concat(new String("(pNode)"));
                printFile(fid, tmpArg_v_39);
                HashSet rhs_48 = new HashSet();
                HashSet var2_50 = new HashSet();
                var2_50 = new HashSet();
                var2_50.add(tp);
                rhs_48 = (HashSet)subcl.clone();
                rhs_48.removeAll(var2_50);
                subcl = (HashSet)UTIL.clone(rhs_48);
                if((new Boolean((new Integer(subcl.size())).intValue() > (new Integer(0)).intValue())).booleanValue())
                {
                    String tmpArg_v_67 = null;
                    tmpArg_v_67 = nl.concat(new String("      else"));
                    printFile(fid, tmpArg_v_67);
                } else
                {
                    String tmpArg_v_58 = null;
                    String var1_59 = null;
                    String var1_60 = null;
                    var1_60 = nl.concat(new String("      else error );"));
                    var1_59 = var1_60.concat(nl);
                    tmpArg_v_58 = var1_59.concat(nl);
                    printFile(fid, tmpArg_v_58);
                }
            }
        }
    }

    private void createTypeNameUnionVisitor(AstDefinitions ad, Integer fid, String id, HashSet tnus)
        throws CGException
    {
        HashSet subcl = tnus;
        String tmpArg_v_7 = null;
        String var1_8 = null;
        String var1_9 = null;
        String var1_10 = null;
        String var1_11 = null;
        String var1_12 = null;
        var1_12 = (new String("  public visit")).concat(id);
        var1_11 = var1_12.concat(new String(": I"));
        String var2_16 = null;
        var2_16 = ad.getPrefix();
        var1_10 = var1_11.concat(var2_16);
        var1_9 = var1_10.concat(id);
        var1_8 = var1_9.concat(new String(" ==> ()"));
        tmpArg_v_7 = var1_8.concat(nl);
        printFile(fid, tmpArg_v_7);
        if((new Boolean(UTIL.equals(subcl, new HashSet()))).booleanValue())
        {
            String tmpArg_v_82 = null;
            String var1_83 = null;
            String var1_84 = null;
            String var1_85 = null;
            var1_85 = (new String("  visit")).concat(id);
            var1_84 = var1_85.concat(new String(" (-) == skip;"));
            var1_83 = var1_84.concat(nl);
            tmpArg_v_82 = var1_83.concat(nl);
            printFile(fid, tmpArg_v_82);
        } else
        {
            String tmpArg_v_25 = null;
            String var1_26 = null;
            String var1_27 = null;
            String var1_28 = null;
            var1_28 = (new String("  visit")).concat(id);
            var1_27 = var1_28.concat(new String(" (pNode) =="));
            var1_26 = var1_27.concat(nl);
            tmpArg_v_25 = var1_26.concat(new String("    ( "));
            printFile(fid, tmpArg_v_25);
            while((new Boolean((new Integer(subcl.size())).intValue() > (new Integer(0)).intValue())).booleanValue()) 
            {
                HashSet tmpSet_38 = new HashSet();
                tmpSet_38 = subcl;
                String tp = null;
                boolean succ_40 = false;
                for(Iterator enm_42 = tmpSet_38.iterator(); enm_42.hasNext() && !succ_40;)
                {
                    String tmpElem_41 = UTIL.ConvertToString(enm_42.next());
                    succ_40 = true;
                    tp = tmpElem_41;
                }

                if(!succ_40)
                    UTIL.RunTime("Run-Time Error:The binding environment was empty");
                String ptp = null;
                String var1_45 = null;
                var1_45 = ad.getPrefix();
                ptp = var1_45.concat(tp);
                String tmpArg_v_49 = null;
                String var1_50 = null;
                String var1_51 = null;
                String var1_52 = null;
                var1_52 = (new String("if isofclass(I")).concat(ptp);
                var1_51 = var1_52.concat(new String(",pNode) then visit"));
                var1_50 = var1_51.concat(tp);
                tmpArg_v_49 = var1_50.concat(new String("(pNode)"));
                printFile(fid, tmpArg_v_49);
                HashSet rhs_58 = new HashSet();
                HashSet var2_60 = new HashSet();
                var2_60 = new HashSet();
                var2_60.add(tp);
                rhs_58 = (HashSet)subcl.clone();
                rhs_58.removeAll(var2_60);
                subcl = (HashSet)UTIL.clone(rhs_58);
                if((new Boolean((new Integer(subcl.size())).intValue() > (new Integer(0)).intValue())).booleanValue())
                {
                    String tmpArg_v_77 = null;
                    tmpArg_v_77 = nl.concat(new String("      else"));
                    printFile(fid, tmpArg_v_77);
                } else
                {
                    String tmpArg_v_68 = null;
                    String var1_69 = null;
                    String var1_70 = null;
                    var1_70 = nl.concat(new String("      else error );"));
                    var1_69 = var1_70.concat(nl);
                    tmpArg_v_68 = var1_69.concat(nl);
                    printFile(fid, tmpArg_v_68);
                }
            }
        }
    }

    private void createVppBaseVisit(AstDefinitions ad, Integer fid, String basevisit)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_11 = null;
        String var1_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        var1_14 = (new String("class ")).concat(basevisit);
        var1_13 = var1_14.concat(new String(" is subclass of I"));
        var1_12 = var1_13.concat(basevisit);
        tmpArg_v_11 = var1_12.concat(nl);
        printFile(fid, tmpArg_v_11);
        String tmpArg_v_22 = null;
        tmpArg_v_22 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_22);
        String tmpArg_v_27 = null;
        String var1_28 = null;
        String var1_29 = null;
        String var2_31 = null;
        var2_31 = ad.getPrefix();
        var1_29 = (new String("  public visitDocument: I")).concat(var2_31);
        var1_28 = var1_29.concat(new String("Document ==> ()"));
        tmpArg_v_27 = var1_28.concat(nl);
        printFile(fid, tmpArg_v_27);
        String tmpArg_v_36 = null;
        String var1_37 = null;
        var1_37 = (new String("  visitDocument (-) == skip;")).concat(nl);
        tmpArg_v_36 = var1_37.concat(nl);
        printFile(fid, tmpArg_v_36);
        String tmpArg_v_43 = null;
        String var1_44 = null;
        String var1_45 = null;
        String var2_47 = null;
        var2_47 = ad.getPrefix();
        var1_45 = (new String("  public visitLexem: I")).concat(var2_47);
        var1_44 = var1_45.concat(new String("Lexem ==> ()"));
        tmpArg_v_43 = var1_44.concat(nl);
        printFile(fid, tmpArg_v_43);
        String tmpArg_v_52 = null;
        String var1_53 = null;
        var1_53 = (new String("  visitLexem (-) == skip;")).concat(nl);
        tmpArg_v_52 = var1_53.concat(nl);
        printFile(fid, tmpArg_v_52);
        HashSet iset_57 = new HashSet();
        iset_57 = ad.getComposites();
        String id = null;
        for(Iterator enm_65 = iset_57.iterator(); enm_65.hasNext(); createNormalVisitor(ad, fid, id))
        {
            String elem_58 = UTIL.ConvertToString(enm_65.next());
            id = elem_58;
        }

        createNormalVisitor(ad, fid, new String("ContextInfo"));
        createNodeVisitor(ad, fid);
        HashSet iset_73 = new HashSet();
        iset_73 = ad.getShorthands();
        id = null;
        for(Iterator enm_96 = iset_73.iterator(); enm_96.hasNext();)
        {
            String elem_74 = UTIL.ConvertToString(enm_96.next());
            id = elem_74;
            AstType shtp = null;
            AstShorthand obj_79 = null;
            obj_79 = ad.getShorthand(id);
            shtp = obj_79.getType();
            Boolean cond_81 = null;
            cond_81 = shtp.isUnionType();
            if(cond_81.booleanValue())
            {
                Tuple tmpVal_83 = new Tuple(2);
                tmpVal_83 = shtp.isTypeNameUnion();
                Boolean sb = null;
                HashSet tnus = new HashSet();
                boolean succ_82 = true;
                Vector e_l_84 = new Vector();
                for(int i_85 = 1; i_85 <= tmpVal_83.Length(); i_85++)
                    e_l_84.add(tmpVal_83.GetField(i_85));

                if(succ_82 = 2 == e_l_84.size())
                {
                    sb = (Boolean)e_l_84.get(0);
                    tnus = (HashSet)(HashSet)e_l_84.get(1);
                }
                if(!succ_82)
                    UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
                if(sb.booleanValue())
                    createTypeNameUnionVisitor(ad, fid, id, tnus);
                else
                    createNormalVisitor(ad, fid, id);
            }
        }

        String tmpArg_v_99 = null;
        String var1_100 = null;
        var1_100 = (new String("end ")).concat(basevisit);
        tmpArg_v_99 = var1_100.concat(nl);
        printFile(fid, tmpArg_v_99);
        String tmpArg_v_106 = null;
        tmpArg_v_106 = (new String("\\end{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_106);
    }

    private void createVppIBaseVisit(AstDefinitions ad, Integer fid, String basevisit)
        throws CGException
    {
        String tmpArg_v_6 = null;
        tmpArg_v_6 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_6);
        String tmpArg_v_11 = null;
        String var1_12 = null;
        var1_12 = (new String("class I")).concat(basevisit);
        tmpArg_v_11 = var1_12.concat(nl);
        printFile(fid, tmpArg_v_11);
        String tmpArg_v_18 = null;
        tmpArg_v_18 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_18);
        String tmpArg_v_23 = null;
        String var1_24 = null;
        String var1_25 = null;
        String var2_27 = null;
        var2_27 = ad.getPrefix();
        var1_25 = (new String("  public visitDocument: I")).concat(var2_27);
        var1_24 = var1_25.concat(new String("Document ==> ()"));
        tmpArg_v_23 = var1_24.concat(nl);
        printFile(fid, tmpArg_v_23);
        String tmpArg_v_32 = null;
        String var1_33 = null;
        var1_33 = (new String("  visitDocument (-) == is subclass responsibility;")).concat(nl);
        tmpArg_v_32 = var1_33.concat(nl);
        printFile(fid, tmpArg_v_32);
        String tmpArg_v_39 = null;
        String var1_40 = null;
        String var1_41 = null;
        String var2_43 = null;
        var2_43 = ad.getPrefix();
        var1_41 = (new String("  public visitLexem: I")).concat(var2_43);
        var1_40 = var1_41.concat(new String("Lexem ==> ()"));
        tmpArg_v_39 = var1_40.concat(nl);
        printFile(fid, tmpArg_v_39);
        String tmpArg_v_48 = null;
        String var1_49 = null;
        var1_49 = (new String("  visitLexem (-) == is subclass responsibility;")).concat(nl);
        tmpArg_v_48 = var1_49.concat(nl);
        printFile(fid, tmpArg_v_48);
        HashSet iset_53 = new HashSet();
        iset_53 = ad.getComposites();
        String id = null;
        for(Iterator enm_61 = iset_53.iterator(); enm_61.hasNext(); createNormalIVisitor(ad, fid, id))
        {
            String elem_54 = UTIL.ConvertToString(enm_61.next());
            id = elem_54;
        }

        createNormalIVisitor(ad, fid, new String("ContextInfo"));
        createNodeIVisitor(ad, fid);
        HashSet iset_69 = new HashSet();
        iset_69 = ad.getShorthands();
        id = null;
        for(Iterator enm_82 = iset_69.iterator(); enm_82.hasNext();)
        {
            String elem_70 = UTIL.ConvertToString(enm_82.next());
            id = elem_70;
            AstType shtp = null;
            AstShorthand obj_75 = null;
            obj_75 = ad.getShorthand(id);
            shtp = obj_75.getType();
            Boolean cond_77 = null;
            cond_77 = shtp.isUnionType();
            if(cond_77.booleanValue())
                createNormalIVisitor(ad, fid, id);
        }

        String tmpArg_v_85 = null;
        String var1_86 = null;
        var1_86 = (new String("end I")).concat(basevisit);
        tmpArg_v_85 = var1_86.concat(nl);
        printFile(fid, tmpArg_v_85);
        String tmpArg_v_92 = null;
        String var1_93 = null;
        var1_93 = (new String("\\end{vdm_al}")).concat(nl);
        tmpArg_v_92 = var1_93.concat(nl);
        printFile(fid, tmpArg_v_92);
    }

    private void createDefSpec(AstDefinitions ad, AstComposite acmp, String clnm, Integer fid)
        throws CGException
    {
        HashSet tps = new HashSet();
        String tmpArg_v_7 = null;
        tmpArg_v_7 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_7);
        String tmpArg_v_12 = null;
        String var1_13 = null;
        String var1_14 = null;
        String var1_15 = null;
        var1_15 = (new String("class ")).concat(clnm);
        var1_14 = var1_15.concat(new String(" is subclass of I"));
        var1_13 = var1_14.concat(clnm);
        tmpArg_v_12 = var1_13.concat(nl);
        printFile(fid, tmpArg_v_12);
        String tmpArg_v_23 = null;
        tmpArg_v_23 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_23);
        String tmpArg_v_28 = null;
        tmpArg_v_28 = (new String("  public identity: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_28);
        String tmpArg_v_33 = null;
        String var1_34 = null;
        String var1_35 = null;
        String var1_36 = null;
        String var2_38 = null;
        var2_38 = acmp.getName();
        var1_36 = (new String("  identity () == return \"")).concat(var2_38);
        var1_35 = var1_36.concat(new String("\";"));
        var1_34 = var1_35.concat(nl);
        tmpArg_v_33 = var1_34.concat(nl);
        printFile(fid, tmpArg_v_33);
        String tmpArg_v_44 = null;
        String var1_45 = null;
        String var1_46 = null;
        String var2_48 = null;
        var2_48 = ad.getPrefix();
        var1_46 = (new String("  public accept: I")).concat(var2_48);
        var1_45 = var1_46.concat(new String("Visitor ==> ()"));
        tmpArg_v_44 = var1_45.concat(nl);
        printFile(fid, tmpArg_v_44);
        String tmpArg_v_53 = null;
        String var1_54 = null;
        String var1_55 = null;
        String var1_56 = null;
        String var2_58 = null;
        var2_58 = acmp.getName();
        var1_56 = (new String("  accept (pVisitor) == pVisitor.visit")).concat(var2_58);
        var1_55 = var1_56.concat(new String("(self);"));
        var1_54 = var1_55.concat(nl);
        tmpArg_v_53 = var1_54.concat(nl);
        printFile(fid, tmpArg_v_53);
        createDefSpecConstructor(ad, acmp, fid, new Boolean(false));
        createDefSpecConstructor(ad, acmp, fid, new Boolean(true));
        createDefSpecCompInit(acmp, fid);
        Vector sq_75 = null;
        sq_75 = acmp.getFields();
        AstField field = null;
        for(Iterator enm_453 = sq_75.iterator(); enm_453.hasNext();)
        {
            AstField elem_76 = (AstField)enm_453.next();
            field = elem_76;
            String fldnm = null;
            String var2_82 = null;
            var2_82 = field.getName();
            fldnm = (new String("iv")).concat(var2_82);
            AstType fldtp = null;
            fldtp = field.getType();
            String tp = null;
            String par_85 = null;
            par_85 = ad.getPrefix();
            tp = getVdmType(par_85, fldtp);
            String tmpArg_v_89 = null;
            tmpArg_v_89 = (new String("instance variables")).concat(nl);
            printFile(fid, tmpArg_v_89);
            Boolean cond_92 = null;
            Boolean var1_93 = null;
            var1_93 = fldtp.isSeqType();
            if(!(cond_92 = var1_93).booleanValue())
            {
                Boolean var2_94 = null;
                var2_94 = fldtp.isStringType();
                cond_92 = var2_94;
            }
            if(cond_92.booleanValue())
            {
                String tmpArg_v_144 = null;
                String var1_145 = null;
                String var1_146 = null;
                String var1_147 = null;
                String var1_148 = null;
                String var1_149 = null;
                var1_149 = (new String("  private ")).concat(fldnm);
                var1_148 = var1_149.concat(new String(" : "));
                var1_147 = var1_148.concat(tp);
                var1_146 = var1_147.concat(new String(" := []"));
                var1_145 = var1_146.concat(nl);
                tmpArg_v_144 = var1_145.concat(nl);
                printFile(fid, tmpArg_v_144);
            } else
            {
                Boolean cond_95 = null;
                cond_95 = fldtp.isSetType();
                if(cond_95.booleanValue())
                {
                    String tmpArg_v_129 = null;
                    String var1_130 = null;
                    String var1_131 = null;
                    String var1_132 = null;
                    String var1_133 = null;
                    String var1_134 = null;
                    var1_134 = (new String("  private ")).concat(fldnm);
                    var1_133 = var1_134.concat(new String(" : "));
                    var1_132 = var1_133.concat(tp);
                    var1_131 = var1_132.concat(new String(" := {}"));
                    var1_130 = var1_131.concat(nl);
                    tmpArg_v_129 = var1_130.concat(nl);
                    printFile(fid, tmpArg_v_129);
                } else
                {
                    Boolean cond_96 = null;
                    cond_96 = fldtp.isMapType();
                    if(cond_96.booleanValue())
                    {
                        String tmpArg_v_114 = null;
                        String var1_115 = null;
                        String var1_116 = null;
                        String var1_117 = null;
                        String var1_118 = null;
                        String var1_119 = null;
                        var1_119 = (new String("  private ")).concat(fldnm);
                        var1_118 = var1_119.concat(new String(" : "));
                        var1_117 = var1_118.concat(tp);
                        var1_116 = var1_117.concat(new String(" := {|->}"));
                        var1_115 = var1_116.concat(nl);
                        tmpArg_v_114 = var1_115.concat(nl);
                        printFile(fid, tmpArg_v_114);
                    } else
                    {
                        String tmpArg_v_99 = null;
                        String var1_100 = null;
                        String var1_101 = null;
                        String var1_102 = null;
                        String var1_103 = null;
                        String var1_104 = null;
                        var1_104 = (new String("  private ")).concat(fldnm);
                        var1_103 = var1_104.concat(new String(" : ["));
                        var1_102 = var1_103.concat(tp);
                        var1_101 = var1_102.concat(new String("] := nil"));
                        var1_100 = var1_101.concat(nl);
                        tmpArg_v_99 = var1_100.concat(nl);
                        printFile(fid, tmpArg_v_99);
                    }
                }
            }
            String tmpArg_v_159 = null;
            tmpArg_v_159 = (new String("operations")).concat(nl);
            printFile(fid, tmpArg_v_159);
            String tmpArg_v_164 = null;
            String var1_165 = null;
            String var1_166 = null;
            String var1_167 = null;
            String var2_169 = null;
            var2_169 = field.getName();
            var1_167 = (new String("  public get")).concat(var2_169);
            var1_166 = var1_167.concat(new String(": () ==> "));
            var1_165 = var1_166.concat(tp);
            tmpArg_v_164 = var1_165.concat(nl);
            printFile(fid, tmpArg_v_164);
            String tmpArg_v_175 = null;
            String var1_176 = null;
            String var1_177 = null;
            String var2_179 = null;
            var2_179 = field.getName();
            var1_177 = (new String("  get")).concat(var2_179);
            var1_176 = var1_177.concat(new String("() == return "));
            tmpArg_v_175 = var1_176.concat(fldnm);
            printFile(fid, tmpArg_v_175);
            Boolean cond_182 = null;
            cond_182 = fldtp.isOptionalType();
            if(cond_182.booleanValue())
            {
                String tmpArg_v_192 = null;
                String var1_193 = null;
                String var1_194 = null;
                String var1_195 = null;
                String var1_196 = null;
                var1_196 = nl.concat(new String("    pre has"));
                String var2_199 = null;
                var2_199 = field.getName();
                var1_195 = var1_196.concat(var2_199);
                var1_194 = var1_195.concat(new String("();"));
                var1_193 = var1_194.concat(nl);
                tmpArg_v_192 = var1_193.concat(nl);
                printFile(fid, tmpArg_v_192);
            } else
            {
                String tmpArg_v_185 = null;
                String var1_186 = null;
                var1_186 = (new String(";")).concat(nl);
                tmpArg_v_185 = var1_186.concat(nl);
                printFile(fid, tmpArg_v_185);
            }
            Boolean cond_203 = null;
            cond_203 = fldtp.isOptionalType();
            if(cond_203.booleanValue())
            {
                String tmpArg_v_206 = null;
                String var1_207 = null;
                String var1_208 = null;
                String var2_210 = null;
                var2_210 = field.getName();
                var1_208 = (new String("  public has")).concat(var2_210);
                var1_207 = var1_208.concat(new String(": () ==> bool"));
                tmpArg_v_206 = var1_207.concat(nl);
                printFile(fid, tmpArg_v_206);
                String tmpArg_v_215 = null;
                String var1_216 = null;
                String var1_217 = null;
                String var1_218 = null;
                String var1_219 = null;
                String var1_220 = null;
                String var2_222 = null;
                var2_222 = field.getName();
                var1_220 = (new String("  has")).concat(var2_222);
                var1_219 = var1_220.concat(new String(" () == return "));
                var1_218 = var1_219.concat(fldnm);
                var1_217 = var1_218.concat(new String(" <> nil;"));
                var1_216 = var1_217.concat(nl);
                tmpArg_v_215 = var1_216.concat(nl);
                printFile(fid, tmpArg_v_215);
            }
            Boolean cond_228 = null;
            cond_228 = fldtp.isOptionalType();
            if(cond_228.booleanValue())
            {
                String tmpArg_v_244 = null;
                String var1_245 = null;
                String var1_246 = null;
                String var1_247 = null;
                String var1_248 = null;
                String var2_250 = null;
                var2_250 = field.getName();
                var1_248 = (new String("  public set")).concat(var2_250);
                var1_247 = var1_248.concat(new String(": [ "));
                var1_246 = var1_247.concat(tp);
                var1_245 = var1_246.concat(new String(" ] ==> ()"));
                tmpArg_v_244 = var1_245.concat(nl);
                printFile(fid, tmpArg_v_244);
            } else
            {
                String tmpArg_v_231 = null;
                String var1_232 = null;
                String var1_233 = null;
                String var1_234 = null;
                String var1_235 = null;
                String var2_237 = null;
                var2_237 = field.getName();
                var1_235 = (new String("  public set")).concat(var2_237);
                var1_234 = var1_235.concat(new String(": "));
                var1_233 = var1_234.concat(tp);
                var1_232 = var1_233.concat(new String(" ==> ()"));
                tmpArg_v_231 = var1_232.concat(nl);
                printFile(fid, tmpArg_v_231);
            }
            String tmpArg_v_257 = null;
            String var1_258 = null;
            String var1_259 = null;
            String var1_260 = null;
            String var1_261 = null;
            String var1_262 = null;
            String var2_264 = null;
            var2_264 = field.getName();
            var1_262 = (new String("  set")).concat(var2_264);
            var1_261 = var1_262.concat(new String("(parg) == "));
            var1_260 = var1_261.concat(fldnm);
            var1_259 = var1_260.concat(new String(" := parg;"));
            var1_258 = var1_259.concat(nl);
            tmpArg_v_257 = var1_258.concat(nl);
            printFile(fid, tmpArg_v_257);
            Boolean cond_270 = null;
            AstType obj_271 = null;
            obj_271 = field.getType();
            cond_270 = obj_271.isSeqType();
            if(cond_270.booleanValue())
            {
                AstSeqOfType stp = null;
                stp = (AstSeqOfType)field.getType();
                String ntp = null;
                String par_274 = null;
                par_274 = ad.getPrefix();
                AstType par_275 = null;
                par_275 = stp.getType();
                ntp = getVdmType(par_274, par_275);
                Boolean cond_276 = null;
                AstType obj_277 = null;
                obj_277 = stp.getType();
                cond_276 = obj_277.isTypeName();
                if(cond_276.booleanValue())
                {
                    String tmpArg_v_293 = null;
                    String var1_294 = null;
                    String var1_295 = null;
                    String var1_296 = null;
                    String var1_297 = null;
                    String var2_299 = null;
                    var2_299 = field.getName();
                    var1_297 = (new String("  public add")).concat(var2_299);
                    var1_296 = var1_297.concat(new String(": I"));
                    String var2_301 = null;
                    var2_301 = ad.getPrefix();
                    var1_295 = var1_296.concat(var2_301);
                    var1_294 = var1_295.concat(new String("Node ==> ()"));
                    tmpArg_v_293 = var1_294.concat(nl);
                    printFile(fid, tmpArg_v_293);
                } else
                {
                    String tmpArg_v_280 = null;
                    String var1_281 = null;
                    String var1_282 = null;
                    String var1_283 = null;
                    String var1_284 = null;
                    String var2_286 = null;
                    var2_286 = field.getName();
                    var1_284 = (new String("  public add")).concat(var2_286);
                    var1_283 = var1_284.concat(new String(": "));
                    var1_282 = var1_283.concat(ntp);
                    var1_281 = var1_282.concat(new String(" ==> ()"));
                    tmpArg_v_280 = var1_281.concat(nl);
                    printFile(fid, tmpArg_v_280);
                }
                String tmpArg_v_306 = null;
                String var1_307 = null;
                String var1_308 = null;
                String var1_309 = null;
                String var1_310 = null;
                String var1_311 = null;
                String var1_312 = null;
                String var1_313 = null;
                String var2_315 = null;
                var2_315 = field.getName();
                var1_313 = (new String("  add")).concat(var2_315);
                var1_312 = var1_313.concat(new String(" (parg) == iv"));
                String var2_317 = null;
                var2_317 = field.getName();
                var1_311 = var1_312.concat(var2_317);
                var1_310 = var1_311.concat(new String(" := iv"));
                String var2_319 = null;
                var2_319 = field.getName();
                var1_309 = var1_310.concat(var2_319);
                var1_308 = var1_309.concat(new String(" ^ [parg];"));
                var1_307 = var1_308.concat(nl);
                tmpArg_v_306 = var1_307.concat(nl);
                printFile(fid, tmpArg_v_306);
            }
            Boolean cond_323 = null;
            AstType obj_324 = null;
            obj_324 = field.getType();
            cond_323 = obj_324.isSetType();
            if(cond_323.booleanValue())
            {
                AstSetOfType stp = null;
                stp = (AstSetOfType)field.getType();
                String ntp = null;
                String par_327 = null;
                par_327 = ad.getPrefix();
                AstType par_328 = null;
                par_328 = stp.getType();
                ntp = getVdmType(par_327, par_328);
                Boolean cond_329 = null;
                AstType obj_330 = null;
                obj_330 = stp.getType();
                cond_329 = obj_330.isTypeName();
                if(cond_329.booleanValue())
                {
                    String tmpArg_v_346 = null;
                    String var1_347 = null;
                    String var1_348 = null;
                    String var1_349 = null;
                    String var1_350 = null;
                    String var2_352 = null;
                    var2_352 = field.getName();
                    var1_350 = (new String("  public add")).concat(var2_352);
                    var1_349 = var1_350.concat(new String(": I"));
                    String var2_354 = null;
                    var2_354 = ad.getPrefix();
                    var1_348 = var1_349.concat(var2_354);
                    var1_347 = var1_348.concat(new String("Node ==> ()"));
                    tmpArg_v_346 = var1_347.concat(nl);
                    printFile(fid, tmpArg_v_346);
                } else
                {
                    String tmpArg_v_333 = null;
                    String var1_334 = null;
                    String var1_335 = null;
                    String var1_336 = null;
                    String var1_337 = null;
                    String var2_339 = null;
                    var2_339 = field.getName();
                    var1_337 = (new String("  public add")).concat(var2_339);
                    var1_336 = var1_337.concat(new String(": "));
                    var1_335 = var1_336.concat(ntp);
                    var1_334 = var1_335.concat(new String(" ==> ()"));
                    tmpArg_v_333 = var1_334.concat(nl);
                    printFile(fid, tmpArg_v_333);
                }
                String tmpArg_v_359 = null;
                String var1_360 = null;
                String var1_361 = null;
                String var1_362 = null;
                String var1_363 = null;
                String var1_364 = null;
                String var1_365 = null;
                String var1_366 = null;
                String var2_368 = null;
                var2_368 = field.getName();
                var1_366 = (new String("  add")).concat(var2_368);
                var1_365 = var1_366.concat(new String(" (parg) == iv"));
                String var2_370 = null;
                var2_370 = field.getName();
                var1_364 = var1_365.concat(var2_370);
                var1_363 = var1_364.concat(new String(" := iv"));
                String var2_372 = null;
                var2_372 = field.getName();
                var1_362 = var1_363.concat(var2_372);
                var1_361 = var1_362.concat(new String(" union {parg};"));
                var1_360 = var1_361.concat(nl);
                tmpArg_v_359 = var1_360.concat(nl);
                printFile(fid, tmpArg_v_359);
            }
            Boolean cond_376 = null;
            AstType obj_377 = null;
            obj_377 = field.getType();
            cond_376 = obj_377.isMapType();
            if(cond_376.booleanValue())
            {
                AstMapType mtp = null;
                mtp = (AstMapType)field.getType();
                String ndtp = null;
                String par_380 = null;
                par_380 = ad.getPrefix();
                AstType par_381 = null;
                par_381 = mtp.getDomType();
                ndtp = getVdmType(par_380, par_381);
                String nrtp = null;
                String par_382 = null;
                par_382 = ad.getPrefix();
                AstType par_383 = null;
                par_383 = mtp.getRngType();
                nrtp = getVdmType(par_382, par_383);
                String dtnm = null;
                Boolean cond_387 = null;
                AstType obj_388 = null;
                obj_388 = mtp.getDomType();
                cond_387 = obj_388.isTypeName();
                if(cond_387.booleanValue())
                {
                    String var1_389 = null;
                    String var2_391 = null;
                    var2_391 = ad.getPrefix();
                    var1_389 = (new String("I")).concat(var2_391);
                    dtnm = var1_389.concat(new String("Node"));
                } else
                {
                    dtnm = ndtp;
                }
                String rtnm = null;
                Boolean cond_396 = null;
                AstType obj_397 = null;
                obj_397 = mtp.getRngType();
                cond_396 = obj_397.isTypeName();
                if(cond_396.booleanValue())
                {
                    String var1_398 = null;
                    String var2_400 = null;
                    var2_400 = ad.getPrefix();
                    var1_398 = (new String("I")).concat(var2_400);
                    rtnm = var1_398.concat(new String("Node"));
                } else
                {
                    rtnm = nrtp;
                }
                String tmpArg_v_404 = null;
                String var1_405 = null;
                String var1_406 = null;
                String var1_407 = null;
                String var1_408 = null;
                String var1_409 = null;
                String var1_410 = null;
                String var2_412 = null;
                var2_412 = field.getName();
                var1_410 = (new String("  public add")).concat(var2_412);
                var1_409 = var1_410.concat(new String(": "));
                var1_408 = var1_409.concat(dtnm);
                var1_407 = var1_408.concat(new String(" * "));
                var1_406 = var1_407.concat(rtnm);
                var1_405 = var1_406.concat(new String(" ==> ()"));
                tmpArg_v_404 = var1_405.concat(nl);
                printFile(fid, tmpArg_v_404);
                String tmpArg_v_421 = null;
                String var1_422 = null;
                String var1_423 = null;
                String var2_425 = null;
                var2_425 = field.getName();
                var1_423 = (new String("  add")).concat(var2_425);
                var1_422 = var1_423.concat(new String("(pd, pr) =="));
                tmpArg_v_421 = var1_422.concat(nl);
                printFile(fid, tmpArg_v_421);
                String tmpArg_v_430 = null;
                String var1_431 = null;
                String var1_432 = null;
                String var1_433 = null;
                String var1_434 = null;
                var1_434 = (new String("    ")).concat(fldnm);
                var1_433 = var1_434.concat(new String(" := "));
                var1_432 = var1_433.concat(fldnm);
                var1_431 = var1_432.concat(new String(" munion {pd |-> pr}"));
                tmpArg_v_430 = var1_431.concat(nl);
                printFile(fid, tmpArg_v_430);
                String tmpArg_v_443 = null;
                String var1_444 = null;
                String var1_445 = null;
                String var1_446 = null;
                var1_446 = (new String("    pre pd not in set dom ")).concat(fldnm);
                var1_445 = var1_446.concat(new String(";"));
                var1_444 = var1_445.concat(nl);
                tmpArg_v_443 = var1_444.concat(nl);
                printFile(fid, tmpArg_v_443);
            }
        }

        String tmpArg_v_456 = null;
        String var1_457 = null;
        var1_457 = (new String("end ")).concat(clnm);
        tmpArg_v_456 = var1_457.concat(nl);
        printFile(fid, tmpArg_v_456);
        String tmpArg_v_463 = null;
        String var1_464 = null;
        var1_464 = (new String("\\end{vdm_al}")).concat(nl);
        tmpArg_v_463 = var1_464.concat(nl);
        printFile(fid, tmpArg_v_463);
    }

    private void createDefISpec(AstDefinitions ad, AstComposite acmp, String clnm, Integer fid)
        throws CGException
    {
        HashSet tps = new HashSet();
        String tmpArg_v_7 = null;
        tmpArg_v_7 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_7);
        String tmpArg_v_12 = null;
        String var1_13 = null;
        var1_13 = (new String("class I")).concat(clnm);
        tmpArg_v_12 = var1_13.concat(nl);
        printFile(fid, tmpArg_v_12);
        String tmpVal_18 = null;
        String var1_19 = null;
        var1_19 = ad.getPrefix();
        String var2_20 = null;
        Boolean cond_22 = null;
        String par_23 = null;
        par_23 = acmp.getName();
        cond_22 = ad.hasInherit(par_23);
        if(cond_22.booleanValue())
        {
            String par_24 = null;
            par_24 = acmp.getName();
            var2_20 = ad.getInherit(par_24);
        } else
        {
            var2_20 = new String("Node");
        }
        tmpVal_18 = var1_19.concat(var2_20);
        String base = null;
        base = tmpVal_18;
        String tmpArg_v_27 = null;
        String var1_28 = null;
        String var1_29 = null;
        var1_29 = (new String(" is subclass of I")).concat(base);
        var1_28 = var1_29.concat(nl);
        tmpArg_v_27 = var1_28.concat(nl);
        printFile(fid, tmpArg_v_27);
        Vector sq_34 = null;
        sq_34 = acmp.getFields();
        AstField field = null;
        for(Iterator enm_95 = sq_34.iterator(); enm_95.hasNext();)
        {
            AstField elem_35 = (AstField)enm_95.next();
            field = elem_35;
            String fldnm = null;
            String var2_41 = null;
            var2_41 = field.getName();
            fldnm = (new String("iv")).concat(var2_41);
            AstType fldtp = null;
            fldtp = field.getType();
            String tp = null;
            String par_44 = null;
            par_44 = ad.getPrefix();
            tp = getVdmType(par_44, fldtp);
            String tmpArg_v_48 = null;
            tmpArg_v_48 = (new String("operations")).concat(nl);
            printFile(fid, tmpArg_v_48);
            String tmpArg_v_53 = null;
            String var1_54 = null;
            String var1_55 = null;
            String var1_56 = null;
            String var2_58 = null;
            var2_58 = field.getName();
            var1_56 = (new String("  public get")).concat(var2_58);
            var1_55 = var1_56.concat(new String(": () ==> "));
            var1_54 = var1_55.concat(tp);
            tmpArg_v_53 = var1_54.concat(nl);
            printFile(fid, tmpArg_v_53);
            String tmpArg_v_64 = null;
            String var1_65 = null;
            String var1_66 = null;
            String var1_67 = null;
            String var2_69 = null;
            var2_69 = field.getName();
            var1_67 = (new String("  get")).concat(var2_69);
            var1_66 = var1_67.concat(new String("() == is subclass responsibility;"));
            var1_65 = var1_66.concat(nl);
            tmpArg_v_64 = var1_65.concat(nl);
            printFile(fid, tmpArg_v_64);
            Boolean cond_73 = null;
            cond_73 = fldtp.isOptionalType();
            if(cond_73.booleanValue())
            {
                String tmpArg_v_76 = null;
                String var1_77 = null;
                String var1_78 = null;
                String var2_80 = null;
                var2_80 = field.getName();
                var1_78 = (new String("  public has")).concat(var2_80);
                var1_77 = var1_78.concat(new String(": () ==> bool"));
                tmpArg_v_76 = var1_77.concat(nl);
                printFile(fid, tmpArg_v_76);
                String tmpArg_v_85 = null;
                String var1_86 = null;
                String var1_87 = null;
                String var1_88 = null;
                String var2_90 = null;
                var2_90 = field.getName();
                var1_88 = (new String("  has")).concat(var2_90);
                var1_87 = var1_88.concat(new String(" () == is subclass responsibility;"));
                var1_86 = var1_87.concat(nl);
                tmpArg_v_85 = var1_86.concat(nl);
                printFile(fid, tmpArg_v_85);
            }
        }

        String tmpArg_v_98 = null;
        String var1_99 = null;
        var1_99 = (new String("end I")).concat(clnm);
        tmpArg_v_98 = var1_99.concat(nl);
        printFile(fid, tmpArg_v_98);
        String tmpArg_v_105 = null;
        String var1_106 = null;
        var1_106 = (new String("\\end{vdm_al}")).concat(nl);
        tmpArg_v_105 = var1_106.concat(nl);
        printFile(fid, tmpArg_v_105);
    }

    private void createDefSpecConstructor(AstDefinitions ad, AstComposite acmp, Integer fid, Boolean posinfo)
        throws CGException
    {
        String tmpVal_6 = null;
        String var1_7 = null;
        var1_7 = ad.getPrefix();
        String var2_8 = null;
        var2_8 = acmp.getName();
        tmpVal_6 = var1_7.concat(var2_8);
        String clnm = null;
        clnm = tmpVal_6;
        Vector fields = null;
        fields = acmp.getFields();
        String siglist = UTIL.ConvertToString(new String());
        String parmlist = UTIL.ConvertToString(new String());
        String body = UTIL.ConvertToString(new String());
        for(Integer cnt = new Integer(1); (new Boolean(cnt.intValue() <= (new Integer(fields.size())).intValue())).booleanValue(); cnt = UTIL.NumberToInt(UTIL.clone(new Integer(cnt.intValue() + (new Integer(1)).intValue()))))
        {
            String pname = null;
            pname = (new String("p")).concat(nat2str(cnt));
            String nbody = null;
            String var1_22 = null;
            String var1_23 = null;
            String var1_24 = null;
            String var2_26 = null;
            AstField obj_27 = null;
            if(1 <= cnt.intValue() && cnt.intValue() <= fields.size())
                obj_27 = (AstField)fields.get(cnt.intValue() - 1);
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
            var2_26 = obj_27.getName();
            var1_24 = (new String("set")).concat(var2_26);
            var1_23 = var1_24.concat(new String("("));
            var1_22 = var1_23.concat(pname);
            nbody = var1_22.concat(new String(")"));
            String btype = null;
            String par_35 = null;
            par_35 = ad.getPrefix();
            AstType par_36 = null;
            AstField obj_37 = null;
            if(1 <= cnt.intValue() && cnt.intValue() <= fields.size())
                obj_37 = (AstField)fields.get(cnt.intValue() - 1);
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
            par_36 = obj_37.getType();
            btype = getVdmType(par_35, par_36);
            String ftype = null;
            Boolean cond_43 = null;
            AstType obj_44 = null;
            AstField obj_45 = null;
            if(1 <= cnt.intValue() && cnt.intValue() <= fields.size())
                obj_45 = (AstField)fields.get(cnt.intValue() - 1);
            else
                UTIL.RunTime("Run-Time Error:Illegal index");
            obj_44 = obj_45.getType();
            cond_43 = obj_44.isOptionalType();
            if(cond_43.booleanValue())
            {
                String var1_52 = null;
                var1_52 = (new String("    [")).concat(btype);
                ftype = var1_52.concat(new String("]"));
            } else
            {
                String var1_48 = null;
                var1_48 = (new String("    (")).concat(btype);
                ftype = var1_48.concat(new String(")"));
            }
            String rhs_56 = null;
            rhs_56 = parmlist.concat(pname);
            parmlist = UTIL.ConvertToString(UTIL.clone(rhs_56));
            String rhs_59 = null;
            rhs_59 = siglist.concat(ftype);
            siglist = UTIL.ConvertToString(UTIL.clone(rhs_59));
            if((new Boolean(UTIL.equals(body, new Vector()))).booleanValue())
            {
                String rhs_74 = null;
                rhs_74 = (new String("    ( ")).concat(nbody);
                body = UTIL.ConvertToString(UTIL.clone(rhs_74));
            } else
            {
                String rhs_65 = null;
                String var1_66 = null;
                String var1_67 = null;
                String var1_68 = null;
                var1_68 = body.concat(new String(";"));
                var1_67 = var1_68.concat(nl);
                var1_66 = var1_67.concat(new String("      "));
                rhs_65 = var1_66.concat(nbody);
                body = UTIL.ConvertToString(UTIL.clone(rhs_65));
            }
            if((new Boolean(cnt.intValue() < (new Integer(fields.size())).intValue())).booleanValue())
            {
                String rhs_81 = null;
                rhs_81 = parmlist.concat(new String(","));
                parmlist = UTIL.ConvertToString(UTIL.clone(rhs_81));
                String rhs_84 = null;
                String var1_85 = null;
                var1_85 = siglist.concat(new String(" *"));
                rhs_84 = var1_85.concat(nl);
                siglist = UTIL.ConvertToString(UTIL.clone(rhs_84));
            }
        }

        if(posinfo.booleanValue())
        {
            if((new Boolean(UTIL.equals(fields, new Vector()))).booleanValue())
            {
                parmlist = UTIL.ConvertToString(UTIL.clone(new String("(line,column)")));
                String rhs_134 = null;
                String var1_135 = null;
                var1_135 = (new String("    nat *")).concat(nl);
                rhs_134 = var1_135.concat(new String("    nat"));
                siglist = UTIL.ConvertToString(UTIL.clone(rhs_134));
                body = UTIL.ConvertToString(UTIL.clone(new String("    setPosition(line, column);")));
            } else
            {
                String rhs_110 = null;
                String var1_111 = null;
                var1_111 = (new String("(")).concat(parmlist);
                rhs_110 = var1_111.concat(new String(",line,column)"));
                parmlist = UTIL.ConvertToString(UTIL.clone(rhs_110));
                String rhs_115 = null;
                String var1_116 = null;
                String var1_117 = null;
                String var1_118 = null;
                String var1_119 = null;
                var1_119 = siglist.concat(new String(" *"));
                var1_118 = var1_119.concat(nl);
                var1_117 = var1_118.concat(new String("    nat *"));
                var1_116 = var1_117.concat(nl);
                rhs_115 = var1_116.concat(new String("    nat"));
                siglist = UTIL.ConvertToString(UTIL.clone(rhs_115));
                String rhs_126 = null;
                String var1_127 = null;
                String var1_128 = null;
                var1_128 = body.concat(new String(";"));
                var1_127 = var1_128.concat(nl);
                rhs_126 = var1_127.concat(new String("      setPosition(line, column) );"));
                body = UTIL.ConvertToString(UTIL.clone(rhs_126));
            }
        } else
        if((new Boolean(UTIL.equals(fields, new Vector()))).booleanValue())
        {
            parmlist = UTIL.ConvertToString(UTIL.clone(new String("()")));
            siglist = UTIL.ConvertToString(UTIL.clone(new String("    ()")));
            body = UTIL.ConvertToString(UTIL.clone(new String("    skip;")));
        } else
        {
            String rhs_96 = null;
            String var1_97 = null;
            var1_97 = (new String("(")).concat(parmlist);
            rhs_96 = var1_97.concat(new String(")"));
            parmlist = UTIL.ConvertToString(UTIL.clone(rhs_96));
            String rhs_101 = null;
            rhs_101 = body.concat(new String(" );"));
            body = UTIL.ConvertToString(UTIL.clone(rhs_101));
        }
        String tmpArg_v_142 = null;
        String var1_143 = null;
        String var1_144 = null;
        String var1_145 = null;
        String var1_146 = null;
        String var1_147 = null;
        String var1_148 = null;
        var1_148 = (new String("  public ")).concat(clnm);
        var1_147 = var1_148.concat(new String(":"));
        var1_146 = var1_147.concat(nl);
        var1_145 = var1_146.concat(siglist);
        var1_144 = var1_145.concat(new String(" ==> "));
        var1_143 = var1_144.concat(clnm);
        tmpArg_v_142 = var1_143.concat(nl);
        printFile(fid, tmpArg_v_142);
        String tmpArg_v_159 = null;
        String var1_160 = null;
        String var1_161 = null;
        String var1_162 = null;
        String var1_163 = null;
        String var1_164 = null;
        String var1_165 = null;
        String var1_166 = null;
        var1_166 = (new String("  ")).concat(clnm);
        var1_165 = var1_166.concat(new String(" "));
        var1_164 = var1_165.concat(parmlist);
        var1_163 = var1_164.concat(new String(" == "));
        var1_162 = var1_163.concat(nl);
        var1_161 = var1_162.concat(body);
        var1_160 = var1_161.concat(nl);
        tmpArg_v_159 = var1_160.concat(nl);
        printFile(fid, tmpArg_v_159);
    }

    private void createDefSpecCompInit(AstComposite acmp, Integer fid)
        throws CGException
    {
        Vector fields = null;
        fields = acmp.getFields();
        Integer cnt = new Integer(1);
        String tmpArg_v_7 = null;
        tmpArg_v_7 = (new String("  public init: map seq of char to [FieldValue] ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_7);
        if((new Boolean(UTIL.equals(fields, new Vector()))).booleanValue())
        {
            String tmpArg_v_77 = null;
            String var1_78 = null;
            var1_78 = (new String("  init (-) == skip;")).concat(nl);
            tmpArg_v_77 = var1_78.concat(nl);
            printFile(fid, tmpArg_v_77);
        } else
        {
            String tmpArg_v_15 = null;
            String var1_16 = null;
            var1_16 = (new String("  init (data) ==")).concat(nl);
            tmpArg_v_15 = var1_16.concat(new String("    ( "));
            printFile(fid, tmpArg_v_15);
            for(; (new Boolean(cnt.intValue() <= (new Integer(fields.size())).intValue())).booleanValue(); cnt = UTIL.NumberToInt(UTIL.clone(new Integer(cnt.intValue() + (new Integer(1)).intValue()))))
            {
                String fldnm = null;
                AstField obj_26 = null;
                if(1 <= cnt.intValue() && cnt.intValue() <= fields.size())
                    obj_26 = (AstField)fields.get(cnt.intValue() - 1);
                else
                    UTIL.RunTime("Run-Time Error:Illegal index");
                fldnm = obj_26.getName();
                String rfldnm = null;
                AstField obj_30 = null;
                if(1 <= cnt.intValue() && cnt.intValue() <= fields.size())
                    obj_30 = (AstField)fields.get(cnt.intValue() - 1);
                else
                    UTIL.RunTime("Run-Time Error:Illegal index");
                rfldnm = obj_30.getRawName();
                String tmpArg_v_35 = null;
                String var1_36 = null;
                String var1_37 = null;
                var1_37 = (new String("let fname = \"")).concat(rfldnm);
                var1_36 = var1_37.concat(new String("\" in"));
                tmpArg_v_35 = var1_36.concat(nl);
                printFile(fid, tmpArg_v_35);
                String tmpArg_v_44 = null;
                tmpArg_v_44 = (new String("        if fname in set dom data")).concat(nl);
                printFile(fid, tmpArg_v_44);
                String tmpArg_v_49 = null;
                String var1_50 = null;
                var1_50 = (new String("        then set")).concat(fldnm);
                tmpArg_v_49 = var1_50.concat(new String("(data(fname))"));
                printFile(fid, tmpArg_v_49);
                if((new Boolean(cnt.intValue() < (new Integer(fields.size())).intValue())).booleanValue())
                {
                    String tmpArg_v_67 = null;
                    String var1_68 = null;
                    var1_68 = (new String(";")).concat(nl);
                    tmpArg_v_67 = var1_68.concat(new String("      "));
                    printFile(fid, tmpArg_v_67);
                } else
                {
                    String tmpArg_v_60 = null;
                    String var1_61 = null;
                    var1_61 = (new String(" );")).concat(nl);
                    tmpArg_v_60 = var1_61.concat(nl);
                    printFile(fid, tmpArg_v_60);
                }
            }

        }
    }

    private void createShSpec(AstDefinitions ad, AstShorthand shorthand, String clnm, Vector path, Integer fid)
        throws CGException
    {
        String tmpArg_v_8 = null;
        tmpArg_v_8 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_8);
        String tmpArg_v_13 = null;
        String var1_14 = null;
        String var1_15 = null;
        String var1_16 = null;
        var1_16 = (new String("class ")).concat(clnm);
        var1_15 = var1_16.concat(new String(" is subclass of I"));
        var1_14 = var1_15.concat(clnm);
        tmpArg_v_13 = var1_14.concat(nl);
        printFile(fid, tmpArg_v_13);
        String tmpArg_v_24 = null;
        tmpArg_v_24 = (new String("operations")).concat(nl);
        printFile(fid, tmpArg_v_24);
        String tmpArg_v_29 = null;
        tmpArg_v_29 = (new String("  public identity: () ==> seq of char")).concat(nl);
        printFile(fid, tmpArg_v_29);
        String tmpArg_v_34 = null;
        String var1_35 = null;
        String var1_36 = null;
        String var1_37 = null;
        String var2_39 = null;
        var2_39 = shorthand.getName();
        var1_37 = (new String("  identity () == return \"")).concat(var2_39);
        var1_36 = var1_37.concat(new String("\";"));
        var1_35 = var1_36.concat(nl);
        tmpArg_v_34 = var1_35.concat(nl);
        printFile(fid, tmpArg_v_34);
        String tmpArg_v_45 = null;
        String var1_46 = null;
        String var1_47 = null;
        String var2_49 = null;
        var2_49 = ad.getPrefix();
        var1_47 = (new String("  public accept: I")).concat(var2_49);
        var1_46 = var1_47.concat(new String("Visitor ==> ()"));
        tmpArg_v_45 = var1_46.concat(nl);
        printFile(fid, tmpArg_v_45);
        String tmpArg_v_54 = null;
        String var1_55 = null;
        String var1_56 = null;
        String var1_57 = null;
        String var2_59 = null;
        var2_59 = shorthand.getName();
        var1_57 = (new String("  accept (pVisitor) == pVisitor.visit")).concat(var2_59);
        var1_56 = var1_57.concat(new String("(self);"));
        var1_55 = var1_56.concat(nl);
        tmpArg_v_54 = var1_55.concat(nl);
        printFile(fid, tmpArg_v_54);
        Tuple tmpVal_64 = new Tuple(2);
        AstType obj_65 = null;
        obj_65 = shorthand.getType();
        tmpVal_64 = obj_65.isQuotedTypeUnion();
        Boolean rb = null;
        HashSet rs = new HashSet();
        boolean succ_63 = true;
        Vector e_l_66 = new Vector();
        for(int i_67 = 1; i_67 <= tmpVal_64.Length(); i_67++)
            e_l_66.add(tmpVal_64.GetField(i_67));

        if(succ_63 = 2 == e_l_66.size())
        {
            rb = (Boolean)e_l_66.get(0);
            rs = (HashSet)(HashSet)e_l_66.get(1);
        }
        if(!succ_63)
            UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
        if(rb.booleanValue())
        {
            String tmpArg_v_71 = null;
            String var1_72 = null;
            String var1_73 = null;
            String var1_74 = null;
            var1_74 = (new String("  public ")).concat(clnm);
            var1_73 = var1_74.concat(new String(": nat ==> "));
            var1_72 = var1_73.concat(clnm);
            tmpArg_v_71 = var1_72.concat(nl);
            printFile(fid, tmpArg_v_71);
            String tmpArg_v_82 = null;
            String var1_83 = null;
            String var1_84 = null;
            String var1_85 = null;
            var1_85 = (new String("  ")).concat(clnm);
            var1_84 = var1_85.concat(new String(" (pv) == setValue(pv);"));
            var1_83 = var1_84.concat(nl);
            tmpArg_v_82 = var1_83.concat(nl);
            printFile(fid, tmpArg_v_82);
            String tmpArg_v_93 = null;
            String var1_94 = null;
            String var1_95 = null;
            String var1_96 = null;
            var1_96 = (new String("  public ")).concat(clnm);
            var1_95 = var1_96.concat(new String(": nat * nat * nat ==> "));
            var1_94 = var1_95.concat(clnm);
            tmpArg_v_93 = var1_94.concat(nl);
            printFile(fid, tmpArg_v_93);
            String tmpArg_v_104 = null;
            String var1_105 = null;
            String var1_106 = null;
            String var1_107 = null;
            var1_107 = (new String("  ")).concat(clnm);
            var1_106 = var1_107.concat(new String(" (pv, pline, pcolumn) == ( setValue(pv); setPosition(pline, pcolumn) );"));
            var1_105 = var1_106.concat(nl);
            tmpArg_v_104 = var1_105.concat(nl);
            printFile(fid, tmpArg_v_104);
            createShQuotedTypeDef(clnm, path, rs);
            String tmpArg_v_119 = null;
            tmpArg_v_119 = (new String("instance variables")).concat(nl);
            printFile(fid, tmpArg_v_119);
            String tmpArg_v_124 = null;
            String var1_125 = null;
            var1_125 = (new String("  private val : [nat] := nil")).concat(nl);
            tmpArg_v_124 = var1_125.concat(nl);
            printFile(fid, tmpArg_v_124);
            String tmpArg_v_131 = null;
            tmpArg_v_131 = (new String("operations")).concat(nl);
            printFile(fid, tmpArg_v_131);
            String tmpArg_v_136 = null;
            tmpArg_v_136 = (new String("  public setValue: nat ==> ()")).concat(nl);
            printFile(fid, tmpArg_v_136);
            String tmpArg_v_141 = null;
            tmpArg_v_141 = (new String("  setValue (pval) == val := pval")).concat(nl);
            printFile(fid, tmpArg_v_141);
            String tmpArg_v_146 = null;
            String var1_147 = null;
            String var1_148 = null;
            String var1_149 = null;
            var1_149 = (new String("    pre val = nil and ")).concat(clnm);
            var1_148 = var1_149.concat(new String("Quotes`validQuote(pval);"));
            var1_147 = var1_148.concat(nl);
            tmpArg_v_146 = var1_147.concat(nl);
            printFile(fid, tmpArg_v_146);
            String tmpArg_v_157 = null;
            tmpArg_v_157 = (new String("  public getValue: () ==> nat")).concat(nl);
            printFile(fid, tmpArg_v_157);
            String tmpArg_v_162 = null;
            tmpArg_v_162 = (new String("  getValue () == return val")).concat(nl);
            printFile(fid, tmpArg_v_162);
            String tmpArg_v_167 = null;
            String var1_168 = null;
            var1_168 = (new String("    pre val <> nil;")).concat(nl);
            tmpArg_v_167 = var1_168.concat(nl);
            printFile(fid, tmpArg_v_167);
            String tmpArg_v_174 = null;
            tmpArg_v_174 = (new String("  public getStringValue: () ==> seq of char")).concat(nl);
            printFile(fid, tmpArg_v_174);
            String tmpArg_v_179 = null;
            String var1_180 = null;
            String var1_181 = null;
            var1_181 = (new String("  getStringValue() == return ")).concat(clnm);
            var1_180 = var1_181.concat(new String("Quotes`getQuoteName(val)"));
            tmpArg_v_179 = var1_180.concat(nl);
            printFile(fid, tmpArg_v_179);
            String tmpArg_v_188 = null;
            String var1_189 = null;
            var1_189 = (new String("    pre val <> nil;")).concat(nl);
            tmpArg_v_188 = var1_189.concat(nl);
            printFile(fid, tmpArg_v_188);
        }
        String tmpArg_v_195 = null;
        String var1_196 = null;
        var1_196 = (new String("end ")).concat(clnm);
        tmpArg_v_195 = var1_196.concat(nl);
        printFile(fid, tmpArg_v_195);
        String tmpArg_v_202 = null;
        tmpArg_v_202 = (new String("\\end{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_202);
    }

    private void createShISpec(AstDefinitions ad, AstShorthand shorthand, String clnm, Vector path, Integer fid)
        throws CGException
    {
        String tmpArg_v_8 = null;
        tmpArg_v_8 = (new String("\\begin{vdm_al}")).concat(nl);
        printFile(fid, tmpArg_v_8);
        String tmpArg_v_13 = null;
        String var1_14 = null;
        var1_14 = (new String("class I")).concat(clnm);
        tmpArg_v_13 = var1_14.concat(nl);
        printFile(fid, tmpArg_v_13);
        String tmpVal_19 = null;
        String var1_20 = null;
        var1_20 = ad.getPrefix();
        String var2_21 = null;
        Boolean cond_23 = null;
        String par_24 = null;
        par_24 = shorthand.getName();
        cond_23 = ad.hasInherit(par_24);
        if(cond_23.booleanValue())
        {
            String par_25 = null;
            par_25 = shorthand.getName();
            var2_21 = ad.getInherit(par_25);
        } else
        {
            var2_21 = new String("Node");
        }
        tmpVal_19 = var1_20.concat(var2_21);
        String base = null;
        base = tmpVal_19;
        String tmpArg_v_28 = null;
        String var1_29 = null;
        String var1_30 = null;
        var1_30 = (new String(" is subclass of I")).concat(base);
        var1_29 = var1_30.concat(nl);
        tmpArg_v_28 = var1_29.concat(nl);
        printFile(fid, tmpArg_v_28);
        Tuple tmpVal_36 = new Tuple(2);
        AstType obj_37 = null;
        obj_37 = shorthand.getType();
        tmpVal_36 = obj_37.isQuotedTypeUnion();
        Boolean rb = null;
        HashSet rs = new HashSet();
        boolean succ_35 = true;
        Vector e_l_38 = new Vector();
        for(int i_39 = 1; i_39 <= tmpVal_36.Length(); i_39++)
            e_l_38.add(tmpVal_36.GetField(i_39));

        if(succ_35 = 2 == e_l_38.size())
        {
            rb = (Boolean)e_l_38.get(0);
            rs = (HashSet)(HashSet)e_l_38.get(1);
        }
        if(!succ_35)
            UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
        if(rb.booleanValue())
        {
            String tmpArg_v_43 = null;
            tmpArg_v_43 = (new String("operations")).concat(nl);
            printFile(fid, tmpArg_v_43);
            String tmpArg_v_48 = null;
            tmpArg_v_48 = (new String("  public getValue: () ==> nat")).concat(nl);
            printFile(fid, tmpArg_v_48);
            String tmpArg_v_53 = null;
            String var1_54 = null;
            var1_54 = (new String("  getValue () == is subclass responsibility;")).concat(nl);
            tmpArg_v_53 = var1_54.concat(nl);
            printFile(fid, tmpArg_v_53);
            String tmpArg_v_60 = null;
            tmpArg_v_60 = (new String("  public getStringValue: () ==> seq of char")).concat(nl);
            printFile(fid, tmpArg_v_60);
            String tmpArg_v_65 = null;
            String var1_66 = null;
            var1_66 = (new String("  getStringValue() == is subclass responsibility")).concat(nl);
            tmpArg_v_65 = var1_66.concat(nl);
            printFile(fid, tmpArg_v_65);
        }
        String tmpArg_v_72 = null;
        String var1_73 = null;
        var1_73 = (new String("end I")).concat(clnm);
        tmpArg_v_72 = var1_73.concat(nl);
        printFile(fid, tmpArg_v_72);
        String tmpArg_v_79 = null;
        String var1_80 = null;
        var1_80 = (new String("\\end{vdm_al}")).concat(nl);
        tmpArg_v_79 = var1_80.concat(nl);
        printFile(fid, tmpArg_v_79);
    }

    private void createDefSpecShInit(AstShorthand ash, Integer fid)
        throws CGException
    {
        String tmpArg_v_5 = null;
        tmpArg_v_5 = (new String("  public init: map seq of char to [FieldValue] ==> ()")).concat(nl);
        printFile(fid, tmpArg_v_5);
        String tmpArg_v_10 = null;
        String var1_11 = null;
        var1_11 = (new String("  init (-) == skip;")).concat(nl);
        tmpArg_v_10 = var1_11.concat(nl);
        printFile(fid, tmpArg_v_10);
    }

    private void createShQuotedTypeDef(String ashnm, Vector path, HashSet qtnms)
        throws CGException
    {
        String tmpVal_5 = null;
        tmpVal_5 = ashnm.concat(new String("Quotes"));
        String clnm = null;
        clnm = tmpVal_5;
        Integer fid = null;
        Vector par_8 = null;
        Vector var2_10 = null;
        String e_seq_11 = null;
        e_seq_11 = clnm.concat(new String(".tex"));
        var2_10 = new Vector();
        var2_10.add(e_seq_11);
        par_8 = (Vector)path.clone();
        par_8.addAll(var2_10);
        fid = createFile(par_8);
        Integer cnt = new Integer(0);
        String qmp = UTIL.ConvertToString(new String());
        if((new Boolean(fid.intValue() >= (new Integer(0)).intValue())).booleanValue())
        {
            String tmpArg_v_19 = null;
            tmpArg_v_19 = (new String("\\begin{vdm_al}")).concat(nl);
            printFile(fid, tmpArg_v_19);
            String tmpArg_v_24 = null;
            String var1_25 = null;
            var1_25 = (new String("class ")).concat(clnm);
            tmpArg_v_24 = var1_25.concat(nl);
            printFile(fid, tmpArg_v_24);
            String tmpArg_v_31 = null;
            tmpArg_v_31 = (new String("instance variables")).concat(nl);
            printFile(fid, tmpArg_v_31);
            String id = null;
            for(Iterator enm_86 = qtnms.iterator(); enm_86.hasNext();)
            {
                String elem_35 = UTIL.ConvertToString(enm_86.next());
                id = elem_35;
                String nstr = nat2str(cnt);
                String tmpArg_v_43 = null;
                String var1_44 = null;
                String var1_45 = null;
                String var1_46 = null;
                String var1_47 = null;
                var1_47 = (new String("  static public IQ")).concat(id);
                var1_46 = var1_47.concat(new String(" : nat := "));
                var1_45 = var1_46.concat(nstr);
                var1_44 = var1_45.concat(new String(";"));
                tmpArg_v_43 = var1_44.concat(nl);
                printFile(fid, tmpArg_v_43);
                String rhs_54 = null;
                String var1_55 = null;
                String var1_56 = null;
                String var1_57 = null;
                String var1_58 = null;
                var1_58 = qmp.concat(new String("IQ"));
                var1_57 = var1_58.concat(id);
                var1_56 = var1_57.concat(new String(" |-> \"<"));
                var1_55 = var1_56.concat(id);
                rhs_54 = var1_55.concat(new String(">\""));
                qmp = UTIL.ConvertToString(UTIL.clone(rhs_54));
                if((new Boolean((new Integer(cnt.intValue() + (new Integer(1)).intValue())).intValue() < (new Integer(qtnms.size())).intValue())).booleanValue())
                {
                    String rhs_76 = null;
                    String var1_77 = null;
                    String var1_78 = null;
                    var1_78 = qmp.concat(new String(","));
                    var1_77 = var1_78.concat(nl);
                    rhs_76 = var1_77.concat(new String("      "));
                    qmp = UTIL.ConvertToString(UTIL.clone(rhs_76));
                } else
                {
                    String rhs_71 = null;
                    String var1_72 = null;
                    var1_72 = qmp.concat(new String(" }"));
                    rhs_71 = var1_72.concat(nl);
                    qmp = UTIL.ConvertToString(UTIL.clone(rhs_71));
                }
                cnt = UTIL.NumberToInt(UTIL.clone(new Integer(cnt.intValue() + (new Integer(1)).intValue())));
            }

            String tmpArg_v_89 = null;
            tmpArg_v_89 = nl.concat(new String("  static private qmap : map nat to seq of char :="));
            printFile(fid, tmpArg_v_89);
            String tmpArg_v_94 = null;
            String var1_95 = null;
            String var1_96 = null;
            String var1_97 = null;
            String var1_98 = null;
            var1_98 = nl.concat(new String("    { "));
            var1_97 = var1_98.concat(qmp);
            var1_96 = var1_97.concat(nl);
            var1_95 = var1_96.concat(new String("operations"));
            tmpArg_v_94 = var1_95.concat(nl);
            printFile(fid, tmpArg_v_94);
            String tmpArg_v_107 = null;
            tmpArg_v_107 = (new String("  static public getQuoteName: nat ==> seq of char")).concat(nl);
            printFile(fid, tmpArg_v_107);
            String tmpArg_v_112 = null;
            tmpArg_v_112 = (new String("  getQuoteName (pid) ==")).concat(nl);
            printFile(fid, tmpArg_v_112);
            String tmpArg_v_117 = null;
            tmpArg_v_117 = (new String("    return qmap(pid)")).concat(nl);
            printFile(fid, tmpArg_v_117);
            String tmpArg_v_122 = null;
            String var1_123 = null;
            var1_123 = (new String("    pre validQuote(pid);")).concat(nl);
            tmpArg_v_122 = var1_123.concat(nl);
            printFile(fid, tmpArg_v_122);
            String tmpArg_v_129 = null;
            tmpArg_v_129 = (new String("  static public validQuote: nat ==> bool")).concat(nl);
            printFile(fid, tmpArg_v_129);
            String tmpArg_v_134 = null;
            String var1_135 = null;
            var1_135 = (new String("  validQuote (pid) == return pid in set dom qmap")).concat(nl);
            tmpArg_v_134 = var1_135.concat(nl);
            printFile(fid, tmpArg_v_134);
            String tmpArg_v_141 = null;
            String var1_142 = null;
            var1_142 = (new String("end ")).concat(clnm);
            tmpArg_v_141 = var1_142.concat(nl);
            printFile(fid, tmpArg_v_141);
            String tmpArg_v_148 = null;
            tmpArg_v_148 = (new String("\\end{vdm_al}")).concat(nl);
            printFile(fid, tmpArg_v_148);
        }
    }

    private void createScript(AstDefinitions ad)
        throws CGException
    {
        String tmpVal_3 = null;
        tmpVal_3 = ad.getDirectory();
        String base = null;
        base = tmpVal_3;
        Vector root = null;
        Vector var1_6 = null;
        Vector var1_7 = null;
        var1_7 = new Vector();
        var1_7.add(new String("src"));
        Vector var2_9 = null;
        var2_9 = ad.getRawPackage();
        var1_6 = (Vector)var1_7.clone();
        var1_6.addAll(var2_9);
        Vector var2_10 = null;
        var2_10 = new Vector();
        var2_10.add(new String("imp"));
        root = (Vector)var1_6.clone();
        root.addAll(var2_10);
        if(createDirectory(base, root).booleanValue())
        {
            Vector tmpArg_v_17 = null;
            Vector var1_18 = null;
            var1_18 = new Vector();
            var1_18.add(base);
            tmpArg_v_17 = (Vector)var1_18.clone();
            tmpArg_v_17.addAll(root);
            makeScript(ad, tmpArg_v_17);
        }
    }

    private void makeScript(AstDefinitions ad, Vector path)
        throws CGException
    {
        Integer fid1 = null;
        Vector par_3 = null;
        Vector var2_5 = null;
        var2_5 = new Vector();
        var2_5.add(new String("vdmpatch"));
        par_3 = (Vector)path.clone();
        par_3.addAll(var2_5);
        fid1 = createFile(par_3);
        Integer fid2 = null;
        Vector par_7 = null;
        Vector var2_9 = null;
        var2_9 = new Vector();
        var2_9.add(new String("script"));
        par_7 = (Vector)path.clone();
        par_7.addAll(var2_9);
        fid2 = createFile(par_7);
        String pack = null;
        pack = ad.getPackage();
        String packspec = null;
        if((new Boolean((new Integer(pack.length())).intValue() > (new Integer(0)).intValue())).booleanValue())
            packspec = pack.concat(new String(".itf"));
        else
            packspec = new String("itf");
        String nname = null;
        String var1_24 = null;
        var1_24 = ad.getPrefix();
        nname = var1_24.concat(new String("Node"));
        String vname = null;
        String var1_28 = null;
        var1_28 = ad.getPrefix();
        vname = var1_28.concat(new String("Visitor"));
        String dname = null;
        String var1_32 = null;
        var1_32 = ad.getPrefix();
        dname = var1_32.concat(new String("Document"));
        String lname = null;
        String var1_36 = null;
        var1_36 = ad.getPrefix();
        lname = var1_36.concat(new String("Lexem"));
        Boolean cond_38 = null;
        if((cond_38 = new Boolean(fid1.intValue() >= (new Integer(0)).intValue())).booleanValue())
            cond_38 = new Boolean(fid2.intValue() >= (new Integer(0)).intValue());
        if(cond_38.booleanValue())
        {
            String tmpArg_v_47 = null;
            tmpArg_v_47 = (new String("#!/usr/bin/bash")).concat(nl);
            printFile(fid1, tmpArg_v_47);
            String tmpArg_v_52 = null;
            tmpArg_v_52 = (new String("echo start patching files")).concat(nl);
            printFile(fid1, tmpArg_v_52);
            String tmpArg_v_57 = null;
            String var1_58 = null;
            String var1_59 = null;
            String var1_60 = null;
            String var1_61 = null;
            var1_61 = (new String("mv -f I")).concat(nname);
            var1_60 = var1_61.concat(new String(".java "));
            var1_59 = var1_60.concat(nname);
            var1_58 = var1_59.concat(new String(".java"));
            tmpArg_v_57 = var1_58.concat(nl);
            printFile(fid1, tmpArg_v_57);
            String tmpArg_v_70 = null;
            tmpArg_v_70 = (new String("rm -rf *.bak *.tmp I*.java")).concat(nl);
            printFile(fid1, tmpArg_v_70);
            String tmpArg_v_75 = null;
            tmpArg_v_75 = (new String("s/imports KEEP=NO/imports KEEP=YES/g")).concat(nl);
            printFile(fid2, tmpArg_v_75);
            String tmpArg_v_80 = null;
            String var1_81 = null;
            String var1_82 = null;
            var1_82 = (new String("s/jp.co.csk.vdm.toolbox.VDM.jdk/")).concat(packspec);
            var1_81 = var1_82.concat(new String("/g"));
            tmpArg_v_80 = var1_81.concat(nl);
            printFile(fid2, tmpArg_v_80);
            String tmpArg_v_89 = null;
            String var1_90 = null;
            String var1_91 = null;
            var1_91 = (new String("/")).concat(IntegerStr);
            var1_90 = var1_91.concat(new String(" IQ/d"));
            tmpArg_v_89 = var1_90.concat(nl);
            printFile(fid2, tmpArg_v_89);
            String tmpArg_v_98 = null;
            tmpArg_v_98 = (new String("/Name=IQ/d")).concat(nl);
            printFile(fid2, tmpArg_v_98);
            String tmpArg_v_103 = null;
            String var1_104 = null;
            String var1_105 = null;
            String var1_106 = null;
            String var1_107 = null;
            String var1_108 = null;
            String var1_109 = null;
            var1_109 = (new String("s/^public class I")).concat(nname);
            var1_108 = var1_109.concat(new String(" .*/public class "));
            var1_107 = var1_108.concat(nname);
            var1_106 = var1_107.concat(new String(" implements I"));
            var1_105 = var1_106.concat(nname);
            var1_104 = var1_105.concat(new String(" {/g"));
            tmpArg_v_103 = var1_104.concat(nl);
            printFile(fid2, tmpArg_v_103);
            String tmpArg_v_120 = null;
            String var1_121 = null;
            String var1_122 = null;
            String var1_123 = null;
            String var1_124 = null;
            var1_124 = (new String("s/^  public I")).concat(nname);
            var1_123 = var1_124.concat(new String("/  public "));
            var1_122 = var1_123.concat(nname);
            var1_121 = var1_122.concat(new String("/g"));
            tmpArg_v_120 = var1_121.concat(nl);
            printFile(fid2, tmpArg_v_120);
            String tmpArg_v_133 = null;
            tmpArg_v_133 = (new String("s/public OmlNode setLexem/public IOmlNode setLexem/g")).concat(nl);
            printFile(fid2, tmpArg_v_133);
            patchFile(fid1, fid2, vname, new String(""), new Boolean(false));
            patchFile(fid1, fid2, dname, new String(""), new Boolean(false));
            patchFile(fid1, fid2, lname, new String(""), new Boolean(false));
            printPatch(nname, fid1);
            printPatch(new String("VdmSlVisitor"), fid1);
            printPatch(new String("VdmPpVisitor"), fid1);
            HashSet iset_163 = new HashSet();
            iset_163 = ad.getComposites();
            String id = null;
            String clnm;
            String base;
            for(Iterator enm_185 = iset_163.iterator(); enm_185.hasNext(); patchFile(fid1, fid2, clnm, base, new Boolean(false)))
            {
                String elem_164 = UTIL.ConvertToString(enm_185.next());
                id = elem_164;
                String tmpVal_168 = null;
                String var1_169 = null;
                var1_169 = ad.getPrefix();
                tmpVal_168 = var1_169.concat(id);
                clnm = null;
                clnm = tmpVal_168;
                String tmpVal_172 = null;
                String var1_173 = null;
                var1_173 = ad.getPrefix();
                String var2_174 = null;
                Boolean cond_176 = null;
                cond_176 = ad.hasInherit(id);
                if(cond_176.booleanValue())
                    var2_174 = ad.getInherit(id);
                else
                    var2_174 = new String("Node");
                tmpVal_172 = var1_173.concat(var2_174);
                base = null;
                base = tmpVal_172;
            }

            HashSet iset_186 = new HashSet();
            iset_186 = ad.getShorthands();
            id = null;
            for(Iterator enm_228 = iset_186.iterator(); enm_228.hasNext();)
            {
                String elem_187 = UTIL.ConvertToString(enm_228.next());
                id = elem_187;
                String tmpVal_191 = null;
                String var1_192 = null;
                var1_192 = ad.getPrefix();
                tmpVal_191 = var1_192.concat(id);
                 clnm = null;
                clnm = tmpVal_191;
                String tmpVal_195 = null;
                String var1_196 = null;
                var1_196 = ad.getPrefix();
                String var2_197 = null;
                Boolean cond_199 = null;
                cond_199 = ad.hasInherit(id);
                if(cond_199.booleanValue())
                    var2_197 = ad.getInherit(id);
                else
                    var2_197 = new String("Node");
                tmpVal_195 = var1_196.concat(var2_197);
                 base = null;
                base = tmpVal_195;
                Boolean cond_202 = null;
                AstType obj_203 = null;
                AstShorthand obj_204 = null;
                obj_204 = ad.getShorthand(id);
                obj_203 = obj_204.getType();
                cond_202 = obj_203.isUnionType();
                if(cond_202.booleanValue())
                    patchFile(fid1, fid2, clnm, base, new Boolean(false));
                Tuple tmpVal_213 = new Tuple(2);
                AstType obj_214 = null;
                AstShorthand obj_215 = null;
                obj_215 = ad.getShorthand(id);
                obj_214 = obj_215.getType();
                tmpVal_213 = obj_214.isQuotedTypeUnion();
                Boolean sb = null;
                boolean succ_212 = true;
                Vector e_l_217 = new Vector();
                for(int i_218 = 1; i_218 <= tmpVal_213.Length(); i_218++)
                    e_l_217.add(tmpVal_213.GetField(i_218));

                if(succ_212 = 2 == e_l_217.size())
                    sb = (Boolean)e_l_217.get(0);
                if(!succ_212)
                    UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
                if(sb.booleanValue())
                {
                    String tmpArg_v_223 = null;
                    tmpArg_v_223 = clnm.concat(new String("Quotes"));
                    patchFile(fid1, fid2, tmpArg_v_223, new String(""), new Boolean(false));
                }
            }

            String nloc = new String("../../visitor");
            String tmpArg_v_233 = null;
            tmpArg_v_233 = (new String("echo Relocating visitors")).concat(nl);
            printFile(fid1, tmpArg_v_233);
            String tmpArg_v_238 = null;
            String var1_239 = null;
            var1_239 = (new String("mkdir ")).concat(nloc);
            tmpArg_v_238 = var1_239.concat(nl);
            printFile(fid1, tmpArg_v_238);
            String tmpArg_v_245 = null;
            String var1_246 = null;
            String var1_247 = null;
            String var1_248 = null;
            String var1_249 = null;
            String var1_250 = null;
            String var1_251 = null;
            String var2_253 = null;
            var2_253 = ad.getPrefix();
            var1_251 = (new String("sed -e \"s/ast.imp/visitor/g\" < ")).concat(var2_253);
            var1_250 = var1_251.concat(new String("Visitor.java > "));
            var1_249 = var1_250.concat(nloc);
            var1_248 = var1_249.concat(new String("/"));
            String var2_257 = null;
            var2_257 = ad.getPrefix();
            var1_247 = var1_248.concat(var2_257);
            var1_246 = var1_247.concat(new String("Visitor.java"));
            tmpArg_v_245 = var1_246.concat(nl);
            printFile(fid1, tmpArg_v_245);
            String tmpArg_v_262 = null;
            tmpArg_v_262 = (new String("rm -f Visitor.java")).concat(nl);
            printFile(fid1, tmpArg_v_262);
            String tmpArg_v_267 = null;
            String var1_268 = null;
            String var1_269 = null;
            var1_269 = (new String("sed -e \"s/ast.imp/visitor/g\" < VdmSlVisitor.java > ")).concat(nloc);
            var1_268 = var1_269.concat(new String("/VdmSlVisitor.java"));
            tmpArg_v_267 = var1_268.concat(nl);
            printFile(fid1, tmpArg_v_267);
            String tmpArg_v_276 = null;
            tmpArg_v_276 = (new String("rm -f VdmSlVisitor.java")).concat(nl);
            printFile(fid1, tmpArg_v_276);
            String tmpArg_v_281 = null;
            String var1_282 = null;
            String var1_283 = null;
            var1_283 = (new String("sed -e \"s/ast.imp/visitor/g\" < VdmPpVisitor.java > ")).concat(nloc);
            var1_282 = var1_283.concat(new String("/VdmPpVisitor.java"));
            tmpArg_v_281 = var1_282.concat(nl);
            printFile(fid1, tmpArg_v_281);
            String tmpArg_v_290 = null;
            tmpArg_v_290 = (new String("rm -f VdmPpVisitor.java")).concat(nl);
            printFile(fid1, tmpArg_v_290);
            printFile(fid1, new String("Echo done patching files!"));
        }
    }

    private void patchFile(Integer fid1, Integer fid2, String clnm, String base, Boolean abstr)
        throws CGException
    {
        String inh = null;
        if((new Boolean((new Integer(base.length())).intValue() > (new Integer(0)).intValue())).booleanValue())
            inh = (new String(" extends ")).concat(base);
        else
            inh = new String("");
        String bgn = null;
        if((new Boolean(!abstr.booleanValue())).booleanValue())
            bgn = new String("public class ");
        else
            bgn = new String("public abstract class ");
        String str = null;
        String var1_22 = null;
        String var1_23 = null;
        String var1_24 = null;
        String var1_25 = null;
        var1_25 = bgn.concat(clnm);
        var1_24 = var1_25.concat(inh);
        var1_23 = var1_24.concat(new String(" implements I"));
        var1_22 = var1_23.concat(clnm);
        str = var1_22.concat(new String(" {"));
        printPatch(clnm, fid1);
        String tmpArg_v_37 = null;
        String var1_38 = null;
        String var1_39 = null;
        String var1_40 = null;
        String var1_41 = null;
        String var1_42 = null;
        var1_42 = (new String("s/^")).concat(bgn);
        var1_41 = var1_42.concat(clnm);
        var1_40 = var1_41.concat(new String(" .*/"));
        var1_39 = var1_40.concat(str);
        var1_38 = var1_39.concat(new String("/g"));
        tmpArg_v_37 = var1_38.concat(nl);
        printFile(fid2, tmpArg_v_37);
    }

    private void printPatch(String fname, Integer fid)
        throws CGException
    {
        String org = null;
        org = fname.concat(new String(".java"));
        String tmp = null;
        tmp = fname.concat(new String(".tmp"));
        String tmpArg_v_13 = null;
        String var1_14 = null;
        String var1_15 = null;
        var1_15 = (new String("echo -n Patching file ")).concat(org);
        var1_14 = var1_15.concat(new String(" .."));
        tmpArg_v_13 = var1_14.concat(nl);
        printFile(fid, tmpArg_v_13);
        String tmpArg_v_22 = null;
        String var1_23 = null;
        String var1_24 = null;
        String var1_25 = null;
        var1_25 = (new String("mv -f ")).concat(org);
        var1_24 = var1_25.concat(new String(" "));
        var1_23 = var1_24.concat(tmp);
        tmpArg_v_22 = var1_23.concat(nl);
        printFile(fid, tmpArg_v_22);
        String tmpArg_v_33 = null;
        String var1_34 = null;
        String var1_35 = null;
        String var1_36 = null;
        var1_36 = (new String("sed -f script < ")).concat(tmp);
        var1_35 = var1_36.concat(new String(" > "));
        var1_34 = var1_35.concat(org);
        tmpArg_v_33 = var1_34.concat(nl);
        printFile(fid, tmpArg_v_33);
        String tmpArg_v_44 = null;
        String var1_45 = null;
        var1_45 = (new String("rm -f ")).concat(tmp);
        tmpArg_v_44 = var1_45.concat(nl);
        printFile(fid, tmpArg_v_44);
        String tmpArg_v_51 = null;
        tmpArg_v_51 = (new String("echo .. done!")).concat(nl);
        printFile(fid, tmpArg_v_51);
    }

    public void print(String var_1_1)
        throws CGException
    {
        System.out.println(UTIL.ConvertToString(var_1_1));
    }

    public void visit(AstDefinitions ad)
        throws CGException
    {
        print(new String("Start code generation..."));
        Boolean tmpVal_5 = null;
        String par_6 = null;
        par_6 = ad.getDirectory();
        Vector par_7 = null;
        Vector var1_8 = null;
        var1_8 = new Vector();
        var1_8.add(new String("src"));
        Vector var2_10 = null;
        var2_10 = ad.getRawPackage();
        par_7 = (Vector)var1_8.clone();
        par_7.addAll(var2_10);
        tmpVal_5 = createDirectory(par_6, par_7);
        createInterfaces(ad);
        createSpecifications(ad);
        createScript(ad);
        closeFiles();
        print(new String("Code generation finished!"));
    }

    public void visit(AstShorthand astshorthand)
        throws CGException
    {
    }

    public void visit(AstComposite astcomposite)
        throws CGException
    {
    }

    public void visit(AstField astfield)
        throws CGException
    {
    }

    public void visit(AstTypeName asttypename)
        throws CGException
    {
    }

    public void visit(AstQuotedType astquotedtype)
        throws CGException
    {
    }

    public void visit(AstUnionType astuniontype)
        throws CGException
    {
    }

    public void visit(AstSeqOfType astseqoftype)
        throws CGException
    {
    }

    public void visit(AstOptionalType astoptionaltype)
        throws CGException
    {
    }

    public void visit(AstMapType astmaptype)
        throws CGException
    {
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();
    private HashMap files;
    private String IntegerStr;
    private static final String nl = new String("\n");
    private static final String digits = new String("0123456789");

}