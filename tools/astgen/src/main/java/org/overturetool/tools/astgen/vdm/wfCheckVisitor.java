// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:52
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   wfCheckVisitor.java

package org.overturetool.tools.astgen.vdm;

import java.io.PrintStream;
import java.util.*;
import jp.co.csk.vdm.toolbox.VDM.*;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstVisitor, AstShorthand, AstType, AstComposite, 
//            AstField, AstTypeName, AstDefinitions, AstUnionType, 
//            AstSeqOfType, AstSetOfType, AstOptionalType, AstMapType, 
//            AstQuotedType
@SuppressWarnings("all")
public class wfCheckVisitor extends AstVisitor
{

    public wfCheckVisitor()
        throws CGException
    {
        ad = null;
        use_uadt = null;
        try
        {
            ad = null;
            use_uadt = new Boolean(false);
        }
        catch(Exception e)
        {
            e.printStackTrace(System.out);
            System.out.println(e.getMessage());
        }
    }

    public void printError(String var_1_1)
        throws CGException
    {
        errors = UTIL.NumberToInt(UTIL.clone(new Integer(errors.intValue() + (new Integer(1)).intValue())));
        System.out.println(UTIL.ConvertToString(var_1_1));
    }

    public void elabShorthand(AstShorthand ash)
        throws CGException
    {
        AstType tmpVal_3 = null;
        tmpVal_3 = ash.getType();
        AstType tp = null;
        tp = tmpVal_3;
        AstType par_5 = null;
        Boolean par_7 = null;
        par_7 = tp.isUnionType();
        par_5 = elabShType(tp, par_7);
        ash.setType(par_5);
    }

    public void elabComposite(AstComposite acmp)
        throws CGException
    {
        Vector sq_2 = null;
        sq_2 = acmp.getFields();
        AstField field = null;
        for(Iterator enm_9 = sq_2.iterator(); enm_9.hasNext(); elabField(field))
        {
            AstField elem_3 = (AstField)enm_9.next();
            field = elem_3;
        }

    }

    public void elabField(AstField afld)
        throws CGException
    {
        AstType tp = null;
        tp = afld.getType();
        afld.setType(elabCmpType(tp));
    }

    public AstType elabShType(AstType tp, Boolean inu)
        throws CGException
    {
        Boolean cond_3 = null;
        cond_3 = tp.isTypeName();
        if(cond_3.booleanValue())
        {
            AstTypeName atn = (AstTypeName)tp;
            String name = null;
            name = atn.getName();
            Boolean cond_38 = null;
            Boolean var1_39 = null;
            HashSet var2_41 = new HashSet();
            var2_41 = new HashSet();
            var2_41.add(new String("Pattern"));
            var2_41.add(new String("Bind"));
            var1_39 = new Boolean(var2_41.contains(name));
            if(!(cond_38 = var1_39).booleanValue())
            {
                Boolean var2_44 = null;
                HashSet var2_46 = new HashSet();
                var2_46 = ad.getComposites();
                var2_44 = new Boolean(var2_46.contains(name));
                cond_38 = var2_44;
            }
            if(cond_38.booleanValue())
                return tp;
            Boolean cond_47 = null;
            HashSet var2_49 = new HashSet();
            var2_49 = ad.getShorthands();
            cond_47 = new Boolean(var2_49.contains(name));
            if(cond_47.booleanValue())
            {
                AstType nshtp = null;
                AstShorthand obj_58 = null;
                obj_58 = ad.getShorthand(name);
                nshtp = obj_58.getType();
                if(inu.booleanValue())
                    return elabShType(nshtp, inu);
                else
                    return tp;
            } else
            {
                String tmpArg_v_51 = null;
                String var1_52 = null;
                var1_52 = (new String("Undefined name \"")).concat(name);
                tmpArg_v_51 = var1_52.concat(new String("\" in shorthand"));
                printError(tmpArg_v_51);
                return tp;
            }
        }
        Boolean cond_4 = null;
        cond_4 = tp.isUnionType();
        if(cond_4.booleanValue())
        {
            AstUnionType aut = (AstUnionType)tp;
            AstUnionType rexpr_29 = null;
            AstType arg_30 = null;
            AstType par_32 = null;
            par_32 = aut.getLhs();
            arg_30 = elabShType(par_32, new Boolean(true));
            AstType arg_31 = null;
            AstType par_34 = null;
            par_34 = aut.getRhs();
            arg_31 = elabShType(par_34, new Boolean(true));
            rexpr_29 = new AstUnionType(arg_30, arg_31);
            return rexpr_29;
        }
        Boolean cond_5 = null;
        cond_5 = tp.isSeqType();
        if(cond_5.booleanValue())
        {
            AstSeqOfType ast = (AstSeqOfType)tp;
            AstSeqOfType rexpr_25 = null;
            AstType arg_26 = null;
            AstType par_27 = null;
            par_27 = ast.getType();
            arg_26 = elabShType(par_27, new Boolean(false));
            rexpr_25 = new AstSeqOfType(arg_26);
            return rexpr_25;
        }
        Boolean cond_6 = null;
        cond_6 = tp.isSetType();
        if(cond_6.booleanValue())
        {
            AstSetOfType ast = (AstSetOfType)tp;
            AstSetOfType rexpr_21 = null;
            AstType arg_22 = null;
            AstType par_23 = null;
            par_23 = ast.getType();
            arg_22 = elabShType(par_23, new Boolean(false));
            rexpr_21 = new AstSetOfType(arg_22);
            return rexpr_21;
        }
        Boolean cond_7 = null;
        cond_7 = tp.isOptionalType();
        if(cond_7.booleanValue())
        {
            AstOptionalType aot = (AstOptionalType)tp;
            AstOptionalType rexpr_17 = null;
            AstType arg_18 = null;
            AstType par_19 = null;
            par_19 = aot.getType();
            arg_18 = elabShType(par_19, new Boolean(false));
            rexpr_17 = new AstOptionalType(arg_18);
            return rexpr_17;
        }
        Boolean cond_8 = null;
        cond_8 = tp.isMapType();
        if(cond_8.booleanValue())
        {
            AstMapType amt = (AstMapType)tp;
            AstMapType rexpr_10 = null;
            AstType arg_11 = null;
            AstType par_13 = null;
            par_13 = amt.getDomType();
            arg_11 = elabShType(par_13, new Boolean(false));
            AstType arg_12 = null;
            AstType par_15 = null;
            par_15 = amt.getRngType();
            arg_12 = elabShType(par_15, new Boolean(false));
            rexpr_10 = new AstMapType(arg_11, arg_12);
            return rexpr_10;
        } else
        {
            return tp;
        }
    }

    public AstType elabCmpType(AstType tp)
        throws CGException
    {
        Boolean cond_2 = null;
        cond_2 = tp.isTypeName();
        if(cond_2.booleanValue())
        {
            AstTypeName atn = (AstTypeName)tp;
            String name = null;
            name = atn.getName();
            Boolean cond_30 = null;
            HashSet var2_32 = new HashSet();
            var2_32 = ad.getComposites();
            cond_30 = new Boolean(var2_32.contains(name));
            if(cond_30.booleanValue())
                return tp;
            Boolean cond_33 = null;
            HashSet var2_35 = new HashSet();
            var2_35 = ad.getShorthands();
            cond_33 = new Boolean(var2_35.contains(name));
            if(cond_33.booleanValue())
            {
                AstType nshtp = null;
                AstShorthand obj_44 = null;
                obj_44 = ad.getShorthand(name);
                nshtp = obj_44.getType();
                Boolean cond_46 = null;
                cond_46 = nshtp.isUnionType();
                if(cond_46.booleanValue())
                    return tp;
                else
                    return elabCmpType(nshtp);
            } else
            {
                String tmpArg_v_37 = null;
                String var1_38 = null;
                var1_38 = (new String("Undefined name \"")).concat(name);
                tmpArg_v_37 = var1_38.concat(new String("\" in composite"));
                printError(tmpArg_v_37);
                return tp;
            }
        }
        Boolean cond_3 = null;
        cond_3 = tp.isUnionType();
        if(cond_3.booleanValue())
        {
            AstUnionType aut = (AstUnionType)tp;
            AstUnionType rexpr_23 = null;
            AstType arg_24 = null;
            AstType par_26 = null;
            par_26 = aut.getLhs();
            arg_24 = elabCmpType(par_26);
            AstType arg_25 = null;
            AstType par_27 = null;
            par_27 = aut.getRhs();
            arg_25 = elabCmpType(par_27);
            rexpr_23 = new AstUnionType(arg_24, arg_25);
            return rexpr_23;
        }
        Boolean cond_4 = null;
        cond_4 = tp.isSeqType();
        if(cond_4.booleanValue())
        {
            AstSeqOfType ast = (AstSeqOfType)tp;
            AstSeqOfType rexpr_20 = null;
            AstType arg_21 = null;
            AstType par_22 = null;
            par_22 = ast.getType();
            arg_21 = elabCmpType(par_22);
            rexpr_20 = new AstSeqOfType(arg_21);
            return rexpr_20;
        }
        Boolean cond_5 = null;
        cond_5 = tp.isSetType();
        if(cond_5.booleanValue())
        {
            AstSetOfType ast = (AstSetOfType)tp;
            AstSetOfType rexpr_17 = null;
            AstType arg_18 = null;
            AstType par_19 = null;
            par_19 = ast.getType();
            arg_18 = elabCmpType(par_19);
            rexpr_17 = new AstSetOfType(arg_18);
            return rexpr_17;
        }
        Boolean cond_6 = null;
        cond_6 = tp.isOptionalType();
        if(cond_6.booleanValue())
        {
            AstOptionalType aot = (AstOptionalType)tp;
            AstOptionalType rexpr_14 = null;
            AstType arg_15 = null;
            AstType par_16 = null;
            par_16 = aot.getType();
            arg_15 = elabCmpType(par_16);
            rexpr_14 = new AstOptionalType(arg_15);
            return rexpr_14;
        }
        Boolean cond_7 = null;
        cond_7 = tp.isMapType();
        if(cond_7.booleanValue())
        {
            AstMapType amt = (AstMapType)tp;
            AstMapType rexpr_9 = null;
            AstType arg_10 = null;
            AstType par_12 = null;
            par_12 = amt.getDomType();
            arg_10 = elabCmpType(par_12);
            AstType arg_11 = null;
            AstType par_13 = null;
            par_13 = amt.getRngType();
            arg_11 = elabCmpType(par_13);
            rexpr_9 = new AstMapType(arg_10, arg_11);
            return rexpr_9;
        } else
        {
            return tp;
        }
    }

    public void wfComposite(AstComposite acmp)
        throws CGException
    {
        use_uadt = (Boolean)UTIL.clone(new Boolean(false));
        Vector sq_3 = null;
        sq_3 = acmp.getFields();
        AstField field = null;
        for(Iterator enm_28 = sq_3.iterator(); enm_28.hasNext();)
        {
            AstField elem_4 = (AstField)enm_28.next();
            field = elem_4;
            String cname = null;
            cname = acmp.getName();
            String tmpVal_10 = null;
            tmpVal_10 = field.getRawName();
            String fname = null;
            fname = tmpVal_10;
            Boolean cond_11 = null;
            cond_11 = new Boolean(keywords.contains(fname));
            if(cond_11.booleanValue())
            {
                String tmpArg_v_18 = null;
                String var1_19 = null;
                String var1_20 = null;
                String var1_21 = null;
                var1_21 = (new String("Composite field name in \"")).concat(cname);
                var1_20 = var1_21.concat(new String("::"));
                var1_19 = var1_20.concat(fname);
                tmpArg_v_18 = var1_19.concat(new String("\" is a reserved word"));
                printError(tmpArg_v_18);
            } else
            {
                wfCmpField(cname, field);
            }
        }

        acmp.setAdts(use_uadt);
    }

    public void wfCmpField(String acmpnm, AstField afld)
        throws CGException
    {
        String tmpArg_v_5 = null;
        tmpArg_v_5 = afld.getName();
        AstType tmpArg_v_6 = null;
        tmpArg_v_6 = afld.getType();
        wfCmpType(acmpnm, tmpArg_v_5, tmpArg_v_6);
    }

    public void wfCmpType(String acmpnm, String afldnm, AstType atp)
        throws CGException
    {
        Boolean cond_4 = null;
        cond_4 = atp.isTypeName();
        if(cond_4.booleanValue())
        {
            wfCmpTypeName(acmpnm, afldnm, (AstTypeName)atp);
        } else
        {
            Boolean cond_5 = null;
            cond_5 = atp.isQuotedType();
            if(cond_5.booleanValue())
            {
                String tmpArg_v_41 = null;
                String var1_42 = null;
                String var1_43 = null;
                String var1_44 = null;
                var1_44 = (new String("Quoted types not allowed in \"")).concat(acmpnm);
                var1_43 = var1_44.concat(new String("::"));
                var1_42 = var1_43.concat(afldnm);
                tmpArg_v_41 = var1_42.concat(new String("\""));
                printError(tmpArg_v_41);
            } else
            {
                Boolean cond_6 = null;
                cond_6 = atp.isUnionType();
                if(cond_6.booleanValue())
                {
                    String tmpArg_v_31 = null;
                    String var1_32 = null;
                    String var1_33 = null;
                    String var1_34 = null;
                    var1_34 = (new String("Union types not allowed in \"")).concat(acmpnm);
                    var1_33 = var1_34.concat(new String("::"));
                    var1_32 = var1_33.concat(afldnm);
                    tmpArg_v_31 = var1_32.concat(new String("\""));
                    printError(tmpArg_v_31);
                } else
                {
                    Boolean cond_7 = null;
                    cond_7 = atp.isSeqType();
                    if(cond_7.booleanValue())
                    {
                        use_uadt = (Boolean)UTIL.clone(new Boolean(true));
                        wfCmpSeqType(acmpnm, afldnm, (AstSeqOfType)atp);
                    } else
                    {
                        Boolean cond_8 = null;
                        cond_8 = atp.isSetType();
                        if(cond_8.booleanValue())
                        {
                            use_uadt = (Boolean)UTIL.clone(new Boolean(true));
                            wfCmpSetType(acmpnm, afldnm, (AstSetOfType)atp);
                        } else
                        {
                            Boolean cond_9 = null;
                            cond_9 = atp.isOptionalType();
                            if(cond_9.booleanValue())
                            {
                                wfCmpOptType(acmpnm, afldnm, (AstOptionalType)atp);
                            } else
                            {
                                Boolean cond_10 = null;
                                cond_10 = atp.isMapType();
                                if(cond_10.booleanValue())
                                {
                                    use_uadt = (Boolean)UTIL.clone(new Boolean(true));
                                    wfCmpMapType(acmpnm, afldnm, (AstMapType)atp);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void wfCmpTypeName(String acmpnm, String afldnm, AstTypeName atp)
        throws CGException
    {
        String atpnm = null;
        atpnm = atp.getName();
        Boolean cond_6 = null;
        Boolean unArg_7 = null;
        HashSet var2_9 = new HashSet();
        HashSet var1_10 = new HashSet();
        var1_10 = ad.getComposites();
        HashSet var2_11 = new HashSet();
        var2_11 = ad.getShorthands();
        var2_9 = (HashSet)var1_10.clone();
        var2_9.addAll(var2_11);
        unArg_7 = new Boolean(var2_9.contains(atpnm));
        cond_6 = new Boolean(!unArg_7.booleanValue());
        if(cond_6.booleanValue())
        {
            String tmpArg_v_13 = null;
            String var1_14 = null;
            String var1_15 = null;
            String var1_16 = null;
            String var1_17 = null;
            String var1_18 = null;
            var1_18 = (new String("Type name \"")).concat(atpnm);
            var1_17 = var1_18.concat(new String("\" is not defined in \""));
            var1_16 = var1_17.concat(acmpnm);
            var1_15 = var1_16.concat(new String("::"));
            var1_14 = var1_15.concat(afldnm);
            tmpArg_v_13 = var1_14.concat(new String("\""));
            printError(tmpArg_v_13);
        }
    }

    public void wfCmpSeqType(String acmpnm, String afldnm, AstSeqOfType atp)
        throws CGException
    {
        AstType tmpVal_5 = null;
        tmpVal_5 = atp.getType();
        AstType tp = null;
        tp = tmpVal_5;
        Boolean cond_6 = null;
        cond_6 = tp.isTypeName();
        if(cond_6.booleanValue())
        {
            wfCmpTypeName(acmpnm, afldnm, (AstTypeName)tp);
        } else
        {
            Boolean cond_7 = null;
            Boolean var1_8 = null;
            Boolean unArg_9 = null;
            unArg_9 = tp.isBasicType();
            var1_8 = new Boolean(!unArg_9.booleanValue());
            if((cond_7 = var1_8).booleanValue())
            {
                Boolean var2_10 = null;
                Boolean unArg_11 = null;
                unArg_11 = tp.isStringType();
                var2_10 = new Boolean(!unArg_11.booleanValue());
                cond_7 = var2_10;
            }
            if(cond_7.booleanValue())
            {
                String tmpArg_v_13 = null;
                String var1_14 = null;
                String var1_15 = null;
                String var1_16 = null;
                var1_16 = (new String("Type is not allowed in sequence in \"")).concat(acmpnm);
                var1_15 = var1_16.concat(new String("::"));
                var1_14 = var1_15.concat(afldnm);
                tmpArg_v_13 = var1_14.concat(new String("\""));
                printError(tmpArg_v_13);
            }
        }
    }

    public void wfCmpSetType(String acmpnm, String afldnm, AstSetOfType atp)
        throws CGException
    {
        AstType tmpVal_5 = null;
        tmpVal_5 = atp.getType();
        AstType tp = null;
        tp = tmpVal_5;
        Boolean cond_6 = null;
        cond_6 = tp.isTypeName();
        if(cond_6.booleanValue())
        {
            wfCmpTypeName(acmpnm, afldnm, (AstTypeName)tp);
        } else
        {
            Boolean cond_7 = null;
            Boolean var1_8 = null;
            Boolean unArg_9 = null;
            unArg_9 = tp.isBasicType();
            var1_8 = new Boolean(!unArg_9.booleanValue());
            if((cond_7 = var1_8).booleanValue())
            {
                Boolean var2_10 = null;
                Boolean unArg_11 = null;
                unArg_11 = tp.isStringType();
                var2_10 = new Boolean(!unArg_11.booleanValue());
                cond_7 = var2_10;
            }
            if(cond_7.booleanValue())
            {
                String tmpArg_v_13 = null;
                String var1_14 = null;
                String var1_15 = null;
                String var1_16 = null;
                var1_16 = (new String("Type is not allowed in set in \"")).concat(acmpnm);
                var1_15 = var1_16.concat(new String("::"));
                var1_14 = var1_15.concat(afldnm);
                tmpArg_v_13 = var1_14.concat(new String("\""));
                printError(tmpArg_v_13);
            }
        }
    }

    public void wfCmpOptType(String acmpnm, String afldnm, AstOptionalType atp)
        throws CGException
    {
        AstType tmpVal_5 = null;
        tmpVal_5 = atp.getType();
        AstType tp = null;
        tp = tmpVal_5;
        Boolean cond_6 = null;
        cond_6 = tp.isTypeName();
        if(cond_6.booleanValue())
        {
            wfCmpTypeName(acmpnm, afldnm, (AstTypeName)tp);
        } else
        {
            Boolean cond_7 = null;
            Boolean var1_8 = null;
            Boolean unArg_9 = null;
            unArg_9 = tp.isBasicType();
            var1_8 = new Boolean(!unArg_9.booleanValue());
            if((cond_7 = var1_8).booleanValue())
            {
                Boolean var2_10 = null;
                Boolean unArg_11 = null;
                unArg_11 = tp.isStringType();
                var2_10 = new Boolean(!unArg_11.booleanValue());
                cond_7 = var2_10;
            }
            if(cond_7.booleanValue())
            {
                String tmpArg_v_13 = null;
                String var1_14 = null;
                String var1_15 = null;
                String var1_16 = null;
                var1_16 = (new String("Type is not allowed in optional type in \"")).concat(acmpnm);
                var1_15 = var1_16.concat(new String("::"));
                var1_14 = var1_15.concat(afldnm);
                tmpArg_v_13 = var1_14.concat(new String("\""));
                printError(tmpArg_v_13);
            }
        }
    }

    public void wfCmpMapType(String acmpnm, String afldnm, AstMapType atp)
        throws CGException
    {
        AstType tmpVal_5 = null;
        tmpVal_5 = atp.getDomType();
        AstType tp = null;
        tp = tmpVal_5;
        Boolean cond_6 = null;
        cond_6 = tp.isTypeName();
        if(cond_6.booleanValue())
        {
            wfCmpTypeName(acmpnm, afldnm, (AstTypeName)tp);
        } else
        {
            Boolean cond_7 = null;
            Boolean var1_8 = null;
            Boolean unArg_9 = null;
            unArg_9 = tp.isBasicType();
            var1_8 = new Boolean(!unArg_9.booleanValue());
            if((cond_7 = var1_8).booleanValue())
            {
                Boolean var2_10 = null;
                Boolean unArg_11 = null;
                unArg_11 = tp.isStringType();
                var2_10 = new Boolean(!unArg_11.booleanValue());
                cond_7 = var2_10;
            }
            if(cond_7.booleanValue())
            {
                String tmpArg_v_13 = null;
                String var1_14 = null;
                String var1_15 = null;
                String var1_16 = null;
                var1_16 = (new String("Type is not allowed in domain of map in \"")).concat(acmpnm);
                var1_15 = var1_16.concat(new String("::"));
                var1_14 = var1_15.concat(afldnm);
                tmpArg_v_13 = var1_14.concat(new String("\""));
                printError(tmpArg_v_13);
            }
        }
        AstType tmpVal_27 = null;
        tmpVal_27 = atp.getRngType();
        tp = null;
        tp = tmpVal_27;
        Boolean cond_28 = null;
        cond_28 = tp.isTypeName();
        if(cond_28.booleanValue())
        {
            wfCmpTypeName(acmpnm, afldnm, (AstTypeName)tp);
        } else
        {
            Boolean cond_29 = null;
            Boolean var1_30 = null;
            Boolean unArg_31 = null;
            unArg_31 = tp.isBasicType();
            var1_30 = new Boolean(!unArg_31.booleanValue());
            if((cond_29 = var1_30).booleanValue())
            {
                Boolean var2_32 = null;
                Boolean unArg_33 = null;
                unArg_33 = tp.isStringType();
                var2_32 = new Boolean(!unArg_33.booleanValue());
                cond_29 = var2_32;
            }
            if(cond_29.booleanValue())
            {
                String tmpArg_v_35 = null;
                String var1_36 = null;
                String var1_37 = null;
                String var1_38 = null;
                var1_38 = (new String("Type is not allowed in range of map in \"")).concat(acmpnm);
                var1_37 = var1_38.concat(new String("::"));
                var1_36 = var1_37.concat(afldnm);
                tmpArg_v_35 = var1_36.concat(new String("\""));
                printError(tmpArg_v_35);
            }
        }
    }

    public void wfShorthand(AstShorthand ash)
        throws CGException
    {
        String tmpArg_v_3 = null;
        tmpArg_v_3 = ash.getName();
        AstType tmpArg_v_4 = null;
        tmpArg_v_4 = ash.getType();
        wfShType(tmpArg_v_3, tmpArg_v_4);
    }

    public void wfShType(String sh, AstType atp)
        throws CGException
    {
        Boolean cond_3 = null;
        cond_3 = atp.isTypeName();
        if(cond_3.booleanValue())
            wfShTypeName(sh, (AstTypeName)atp);
        Boolean cond_7 = null;
        cond_7 = atp.isQuotedType();
        if(cond_7.booleanValue())
        {
            String tmpArg_v_9 = null;
            String var1_10 = null;
            var1_10 = (new String("Quoted type is not allowed in shorthand \"")).concat(sh);
            tmpArg_v_9 = var1_10.concat(new String("\""));
            printError(tmpArg_v_9);
        }
        Boolean cond_14 = null;
        cond_14 = atp.isUnionType();
        if(cond_14.booleanValue())
            wfShUnionType(sh, (AstUnionType)atp);
        Boolean cond_18 = null;
        cond_18 = atp.isSeqType();
        if(cond_18.booleanValue())
            wfShSeqType(sh, (AstSeqOfType)atp);
        Boolean cond_22 = null;
        cond_22 = atp.isSetType();
        if(cond_22.booleanValue())
            wfShSetType(sh, (AstSetOfType)atp);
        Boolean cond_26 = null;
        cond_26 = atp.isOptionalType();
        if(cond_26.booleanValue())
            wfShOptType(sh, (AstOptionalType)atp);
        Boolean cond_30 = null;
        cond_30 = atp.isMapType();
        if(cond_30.booleanValue())
            wfShMapType(sh, (AstMapType)atp);
    }

    public void wfShTypeName(String sh, AstTypeName atn)
        throws CGException
    {
        String name = null;
        name = atn.getName();
        Boolean cond_5 = null;
        Boolean var1_6 = null;
        HashSet var2_8 = new HashSet();
        var2_8 = ad.getComposites();
        var1_6 = new Boolean(!var2_8.contains(name));
        if((cond_5 = var1_6).booleanValue())
        {
            Boolean var2_9 = null;
            HashSet var2_11 = new HashSet();
            var2_11 = ad.getShorthands();
            var2_9 = new Boolean(!var2_11.contains(name));
            cond_5 = var2_9;
        }
        if(cond_5.booleanValue())
        {
            String tmpArg_v_13 = null;
            String var1_14 = null;
            String var1_15 = null;
            String var1_16 = null;
            var1_16 = (new String("Unknown composite type \"")).concat(name);
            var1_15 = var1_16.concat(new String("\" in shorthand \""));
            var1_14 = var1_15.concat(sh);
            tmpArg_v_13 = var1_14.concat(new String("\""));
            printError(tmpArg_v_13);
        }
    }

    public void wfShUnionType(String sh, AstUnionType aut)
        throws CGException
    {
        Tuple tmpVal_4 = new Tuple(2);
        tmpVal_4 = aut.isQuotedTypeUnion();
        Boolean qtub = null;
        boolean succ_3 = true;
        Vector e_l_5 = new Vector();
        for(int i_6 = 1; i_6 <= tmpVal_4.Length(); i_6++)
            e_l_5.add(tmpVal_4.GetField(i_6));

        if(succ_3 = 2 == e_l_5.size())
            qtub = (Boolean)e_l_5.get(0);
        if(!succ_3)
            UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
        if(qtub.booleanValue())
        {
            AstShorthand obj_9 = null;
            obj_9 = ad.getShorthand(sh);
            obj_9.setQuotedTypeUnion();
            return;
        }
        Tuple tmpVal_12 = new Tuple(2);
        tmpVal_12 = aut.isTypeNameUnion();
        Boolean tnub = null;
        HashSet tnus = new HashSet();
        boolean succ_11 = true;
        Vector e_l_13 = new Vector();
        for(int i_14 = 1; i_14 <= tmpVal_12.Length(); i_14++)
            e_l_13.add(tmpVal_12.GetField(i_14));

        if(succ_11 = 2 == e_l_13.size())
        {
            tnub = (Boolean)e_l_13.get(0);
            tnus = (HashSet)(HashSet)e_l_13.get(1);
        }
        if(!succ_11)
            UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
        if(tnub.booleanValue())
        {
            AstShorthand obj_17 = null;
            obj_17 = ad.getShorthand(sh);
            obj_17.setTypeNameUnion();
            String tnu = null;
            for(Iterator enm_65 = tnus.iterator(); enm_65.hasNext();)
            {
                String elem_20 = UTIL.ConvertToString(enm_65.next());
                tnu = elem_20;
                Boolean cond_23 = null;
                HashSet var2_25 = new HashSet();
                HashSet var1_26 = new HashSet();
                var1_26 = ad.getComposites();
                HashSet var2_27 = new HashSet();
                var2_27 = new HashSet();
                var2_27.add(new String("Pattern"));
                var2_27.add(new String("Bind"));
                var2_25 = (HashSet)var1_26.clone();
                var2_25.addAll(var2_27);
                cond_23 = new Boolean(!var2_25.contains(tnu));
                if(cond_23.booleanValue())
                {
                    String tmpArg_v_56 = null;
                    String var1_57 = null;
                    String var1_58 = null;
                    String var1_59 = null;
                    var1_59 = (new String("Unknown composite type \"")).concat(tnu);
                    var1_58 = var1_59.concat(new String("\" in shorthand union \""));
                    var1_57 = var1_58.concat(sh);
                    tmpArg_v_56 = var1_57.concat(new String("\""));
                    printError(tmpArg_v_56);
                } else
                {
                    Boolean cond_30 = null;
                    cond_30 = ad.hasInherit(tnu);
                    if(cond_30.booleanValue())
                    {
                        String inh = null;
                        inh = ad.getInherit(tnu);
                        if((new Boolean(!UTIL.equals(inh, sh))).booleanValue())
                        {
                            String tmpArg_v_42 = null;
                            String var1_43 = null;
                            String var1_44 = null;
                            String var1_45 = null;
                            String var1_46 = null;
                            String var1_47 = null;
                            var1_47 = (new String("Type \"")).concat(tnu);
                            var1_46 = var1_47.concat(new String("\" is used in two shorthands, \""));
                            var1_45 = var1_46.concat(sh);
                            var1_44 = var1_45.concat(new String("\" and \""));
                            var1_43 = var1_44.concat(inh);
                            tmpArg_v_42 = var1_43.concat(new String("\""));
                            printError(tmpArg_v_42);
                        }
                    } else
                    {
                        ad.addInherit(tnu, sh);
                    }
                }
            }

            return;
        } else
        {
            String tmpArg_v_67 = null;
            String var1_68 = null;
            var1_68 = (new String("Illegal (possibly nested) type in shorthand union \"")).concat(sh);
            tmpArg_v_67 = var1_68.concat(new String("\""));
            printError(tmpArg_v_67);
            return;
        }
    }

    public void wfShSeqType(String sh, AstSeqOfType ast)
        throws CGException
    {
        AstType tmpVal_4 = null;
        tmpVal_4 = ast.getType();
        AstType tp = null;
        tp = tmpVal_4;
        Boolean cond_5 = null;
        cond_5 = tp.isTypeName();
        if(cond_5.booleanValue())
        {
            wfShTypeName(sh, (AstTypeName)tp);
        } else
        {
            Boolean cond_6 = null;
            cond_6 = tp.isQuotedType();
            if(cond_6.booleanValue())
            {
                String tmpArg_v_43 = null;
                String var1_44 = null;
                var1_44 = (new String("Quoted type is not allowed in shorthand sequence \"")).concat(sh);
                tmpArg_v_43 = var1_44.concat(new String("\""));
                printError(tmpArg_v_43);
            } else
            {
                Boolean cond_7 = null;
                cond_7 = tp.isUnionType();
                if(cond_7.booleanValue())
                {
                    String tmpArg_v_37 = null;
                    String var1_38 = null;
                    var1_38 = (new String("Union type is not allowed in shorthand sequence \"")).concat(sh);
                    tmpArg_v_37 = var1_38.concat(new String("\""));
                    printError(tmpArg_v_37);
                } else
                {
                    Boolean cond_8 = null;
                    cond_8 = tp.isSeqType();
                    if(cond_8.booleanValue())
                    {
                        String tmpArg_v_31 = null;
                        String var1_32 = null;
                        var1_32 = (new String("Sequence type is not allowed in shorthand sequence \"")).concat(sh);
                        tmpArg_v_31 = var1_32.concat(new String("\""));
                        printError(tmpArg_v_31);
                    } else
                    {
                        Boolean cond_9 = null;
                        cond_9 = tp.isSetType();
                        if(cond_9.booleanValue())
                        {
                            String tmpArg_v_25 = null;
                            String var1_26 = null;
                            var1_26 = (new String("Set type is not allowed in shorthand sequence \"")).concat(sh);
                            tmpArg_v_25 = var1_26.concat(new String("\""));
                            printError(tmpArg_v_25);
                        } else
                        {
                            Boolean cond_10 = null;
                            cond_10 = tp.isOptionalType();
                            if(cond_10.booleanValue())
                            {
                                String tmpArg_v_19 = null;
                                String var1_20 = null;
                                var1_20 = (new String("Optional type is not allowed in shorthand sequence \"")).concat(sh);
                                tmpArg_v_19 = var1_20.concat(new String("\""));
                                printError(tmpArg_v_19);
                            } else
                            {
                                Boolean cond_11 = null;
                                cond_11 = tp.isMapType();
                                if(cond_11.booleanValue())
                                {
                                    String tmpArg_v_13 = null;
                                    String var1_14 = null;
                                    var1_14 = (new String("Map type is not allowed in shorthand sequence \"")).concat(sh);
                                    tmpArg_v_13 = var1_14.concat(new String("\""));
                                    printError(tmpArg_v_13);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void wfShSetType(String sh, AstSetOfType ast)
        throws CGException
    {
        AstType tmpVal_4 = null;
        tmpVal_4 = ast.getType();
        AstType tp = null;
        tp = tmpVal_4;
        Boolean cond_5 = null;
        cond_5 = tp.isTypeName();
        if(cond_5.booleanValue())
        {
            wfShTypeName(sh, (AstTypeName)tp);
        } else
        {
            Boolean cond_6 = null;
            cond_6 = tp.isQuotedType();
            if(cond_6.booleanValue())
            {
                String tmpArg_v_43 = null;
                String var1_44 = null;
                var1_44 = (new String("Quoted type is not allowed in shorthand sequence \"")).concat(sh);
                tmpArg_v_43 = var1_44.concat(new String("\""));
                printError(tmpArg_v_43);
            } else
            {
                Boolean cond_7 = null;
                cond_7 = tp.isUnionType();
                if(cond_7.booleanValue())
                {
                    String tmpArg_v_37 = null;
                    String var1_38 = null;
                    var1_38 = (new String("Union type is not allowed in shorthand sequence \"")).concat(sh);
                    tmpArg_v_37 = var1_38.concat(new String("\""));
                    printError(tmpArg_v_37);
                } else
                {
                    Boolean cond_8 = null;
                    cond_8 = tp.isSeqType();
                    if(cond_8.booleanValue())
                    {
                        String tmpArg_v_31 = null;
                        String var1_32 = null;
                        var1_32 = (new String("Sequence type is not allowed in shorthand sequence \"")).concat(sh);
                        tmpArg_v_31 = var1_32.concat(new String("\""));
                        printError(tmpArg_v_31);
                    } else
                    {
                        Boolean cond_9 = null;
                        cond_9 = tp.isSetType();
                        if(cond_9.booleanValue())
                        {
                            String tmpArg_v_25 = null;
                            String var1_26 = null;
                            var1_26 = (new String("Set type is not allowed in shorthand sequence \"")).concat(sh);
                            tmpArg_v_25 = var1_26.concat(new String("\""));
                            printError(tmpArg_v_25);
                        } else
                        {
                            Boolean cond_10 = null;
                            cond_10 = tp.isOptionalType();
                            if(cond_10.booleanValue())
                            {
                                String tmpArg_v_19 = null;
                                String var1_20 = null;
                                var1_20 = (new String("Optional type is not allowed in shorthand sequence \"")).concat(sh);
                                tmpArg_v_19 = var1_20.concat(new String("\""));
                                printError(tmpArg_v_19);
                            } else
                            {
                                Boolean cond_11 = null;
                                cond_11 = tp.isMapType();
                                if(cond_11.booleanValue())
                                {
                                    String tmpArg_v_13 = null;
                                    String var1_14 = null;
                                    var1_14 = (new String("Map type is not allowed in shorthand sequence \"")).concat(sh);
                                    tmpArg_v_13 = var1_14.concat(new String("\""));
                                    printError(tmpArg_v_13);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void wfShOptType(String sh, AstOptionalType aot)
        throws CGException
    {
        AstType tmpVal_4 = null;
        tmpVal_4 = aot.getType();
        AstType tp = null;
        tp = tmpVal_4;
        Boolean cond_5 = null;
        cond_5 = tp.isTypeName();
        if(cond_5.booleanValue())
        {
            wfShTypeName(sh, (AstTypeName)tp);
        } else
        {
            Boolean cond_6 = null;
            cond_6 = tp.isQuotedType();
            if(cond_6.booleanValue())
            {
                String tmpArg_v_45 = null;
                String var1_46 = null;
                var1_46 = (new String("Quoted type is not allowed in shorthand optional type \"")).concat(sh);
                tmpArg_v_45 = var1_46.concat(new String("\""));
                printError(tmpArg_v_45);
            } else
            {
                Boolean cond_7 = null;
                cond_7 = tp.isUnionType();
                if(cond_7.booleanValue())
                {
                    String tmpArg_v_39 = null;
                    String var1_40 = null;
                    var1_40 = (new String("Union type is not allowed in shorthand optional type \"")).concat(sh);
                    tmpArg_v_39 = var1_40.concat(new String("\""));
                    printError(tmpArg_v_39);
                } else
                {
                    Boolean cond_8 = null;
                    Boolean var1_9 = null;
                    var1_9 = tp.isSeqType();
                    if(!(cond_8 = var1_9).booleanValue())
                    {
                        Boolean var2_10 = null;
                        var2_10 = tp.isStringType();
                        cond_8 = var2_10;
                    }
                    if(cond_8.booleanValue())
                    {
                        String tmpArg_v_33 = null;
                        String var1_34 = null;
                        var1_34 = (new String("Sequence type is not allowed in shorthand optional type \"")).concat(sh);
                        tmpArg_v_33 = var1_34.concat(new String("\""));
                        printError(tmpArg_v_33);
                    } else
                    {
                        Boolean cond_11 = null;
                        cond_11 = tp.isSetType();
                        if(cond_11.booleanValue())
                        {
                            String tmpArg_v_27 = null;
                            String var1_28 = null;
                            var1_28 = (new String("Set type is not allowed in shorthand optional type \"")).concat(sh);
                            tmpArg_v_27 = var1_28.concat(new String("\""));
                            printError(tmpArg_v_27);
                        } else
                        {
                            Boolean cond_12 = null;
                            cond_12 = tp.isOptionalType();
                            if(cond_12.booleanValue())
                            {
                                String tmpArg_v_21 = null;
                                String var1_22 = null;
                                var1_22 = (new String("Optional type is not allowed in shorthand optional type \"")).concat(sh);
                                tmpArg_v_21 = var1_22.concat(new String("\""));
                                printError(tmpArg_v_21);
                            } else
                            {
                                Boolean cond_13 = null;
                                cond_13 = tp.isMapType();
                                if(cond_13.booleanValue())
                                {
                                    String tmpArg_v_15 = null;
                                    String var1_16 = null;
                                    var1_16 = (new String("Map type is not allowed in shorthand optional \"")).concat(sh);
                                    tmpArg_v_15 = var1_16.concat(new String("\""));
                                    printError(tmpArg_v_15);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void wfShMapType(String sh, AstMapType amt)
        throws CGException
    {
        AstType tp1 = null;
        tp1 = amt.getDomType();
        AstType tp2 = null;
        tp2 = amt.getRngType();
        Boolean cond_7 = null;
        Boolean var1_8 = null;
        var1_8 = tp1.isBasicType();
        if(!(cond_7 = var1_8).booleanValue())
        {
            Boolean var2_9 = null;
            var2_9 = tp1.isStringType();
            cond_7 = var2_9;
        }
        if(!cond_7.booleanValue())
        {
            Boolean cond_10 = null;
            cond_10 = tp1.isTypeName();
            if(cond_10.booleanValue())
            {
                wfShTypeName(sh, (AstTypeName)tp1);
            } else
            {
                String tmpArg_v_12 = null;
                String var1_13 = null;
                var1_13 = (new String("Type not allowed in domain of map in shorthand \"")).concat(sh);
                tmpArg_v_12 = var1_13.concat(new String("\""));
                printError(tmpArg_v_12);
            }
        }
        Boolean cond_20 = null;
        Boolean var1_21 = null;
        var1_21 = tp2.isBasicType();
        if(!(cond_20 = var1_21).booleanValue())
        {
            Boolean var2_22 = null;
            var2_22 = tp2.isStringType();
            cond_20 = var2_22;
        }
        if(!cond_20.booleanValue())
        {
            Boolean cond_23 = null;
            cond_23 = tp2.isTypeName();
            if(cond_23.booleanValue())
            {
                wfShTypeName(sh, (AstTypeName)tp2);
            } else
            {
                String tmpArg_v_25 = null;
                String var1_26 = null;
                var1_26 = (new String("Type not allowed in range of map in shorthand \"")).concat(sh);
                tmpArg_v_25 = var1_26.concat(new String("\""));
                printError(tmpArg_v_25);
            }
        }
    }

    public void visit(AstDefinitions pad)
        throws CGException
    {
        HashSet names = new HashSet();
        ad = (AstDefinitions)UTIL.clone(pad);
        HashSet iset_3 = new HashSet();
        iset_3 = ad.getComposites();
        String name = null;
        for(Iterator enm_27 = iset_3.iterator(); enm_27.hasNext();)
        {
            String elem_4 = UTIL.ConvertToString(enm_27.next());
            name = elem_4;
            Boolean cond_7 = null;
            cond_7 = new Boolean(keywords.contains(name));
            if(cond_7.booleanValue())
            {
                String tmpArg_v_22 = null;
                String var1_23 = null;
                var1_23 = (new String("Composite type \"")).concat(name);
                tmpArg_v_22 = var1_23.concat(new String("\" is a reserved word"));
                printError(tmpArg_v_22);
            } else
            {
                Boolean cond_10 = null;
                cond_10 = new Boolean(names.contains(name));
                if(cond_10.booleanValue())
                {
                    String tmpArg_v_16 = null;
                    String var1_17 = null;
                    var1_17 = (new String("Composite type \"")).concat(name);
                    tmpArg_v_16 = var1_17.concat(new String("\" is defined more than once"));
                    printError(tmpArg_v_16);
                } else
                {
                    names.add(name);
                }
            }
        }

        HashSet iset_28 = new HashSet();
        iset_28 = ad.getShorthands();
        name = null;
        for(Iterator enm_52 = iset_28.iterator(); enm_52.hasNext();)
        {
            String elem_29 = UTIL.ConvertToString(enm_52.next());
            name = elem_29;
            Boolean cond_32 = null;
            cond_32 = new Boolean(keywords.contains(name));
            if(cond_32.booleanValue())
            {
                String tmpArg_v_47 = null;
                String var1_48 = null;
                var1_48 = (new String("Composite type \"")).concat(name);
                tmpArg_v_47 = var1_48.concat(new String("\" is a reserved word"));
                printError(tmpArg_v_47);
            } else
            {
                Boolean cond_35 = null;
                cond_35 = new Boolean(names.contains(name));
                if(cond_35.booleanValue())
                {
                    String tmpArg_v_41 = null;
                    String var1_42 = null;
                    var1_42 = (new String("Shorthand type \"")).concat(name);
                    tmpArg_v_41 = var1_42.concat(new String("\" is defined more than once"));
                    printError(tmpArg_v_41);
                } else
                {
                    names.add(name);
                }
            }
        }

        Vector topnames = null;
        topnames = ad.getTop();
        if((new Boolean(UTIL.equals(topnames, new Vector()))).booleanValue())
        {
            printError(new String("no top-level entry specification"));
        } else
        {
            name = null;
            for(Iterator enm_72 = topnames.iterator(); enm_72.hasNext();)
            {
                String elem_59 = UTIL.ConvertToString(enm_72.next());
                name = elem_59;
                Boolean cond_62 = null;
                cond_62 = new Boolean(!names.contains(name));
                if(cond_62.booleanValue())
                {
                    String tmpArg_v_66 = null;
                    String var1_67 = null;
                    var1_67 = (new String("top-level entry \"")).concat(name);
                    tmpArg_v_66 = var1_67.concat(new String("\" does not exist!"));
                    printError(tmpArg_v_66);
                }
            }

        }
        HashSet iset_75 = new HashSet();
        iset_75 = ad.getShorthands();
        String sh = null;
        AstShorthand tmpArg_v_80;
        for(Iterator enm_82 = iset_75.iterator(); enm_82.hasNext(); elabShorthand(tmpArg_v_80))
        {
            String elem_76 = UTIL.ConvertToString(enm_82.next());
            sh = elem_76;
            tmpArg_v_80 = null;
            tmpArg_v_80 = ad.getShorthand(sh);
        }

        HashSet iset_83 = new HashSet();
        iset_83 = ad.getShorthands();
        sh = null;
        AstShorthand tmpArg_v_88;
        for(Iterator enm_90 = iset_83.iterator(); enm_90.hasNext(); wfShorthand(tmpArg_v_88))
        {
            String elem_84 = UTIL.ConvertToString(enm_90.next());
            sh = elem_84;
            tmpArg_v_88 = null;
            tmpArg_v_88 = ad.getShorthand(sh);
        }

        HashSet iset_91 = new HashSet();
        iset_91 = ad.getComposites();
        String composite = null;
        AstComposite tmpArg_v_96;
        for(Iterator enm_98 = iset_91.iterator(); enm_98.hasNext(); elabComposite(tmpArg_v_96))
        {
            String elem_92 = UTIL.ConvertToString(enm_98.next());
            composite = elem_92;
            tmpArg_v_96 = null;
            tmpArg_v_96 = ad.getComposite(composite);
        }

        HashSet iset_99 = new HashSet();
        iset_99 = ad.getComposites();
        composite = null;
        AstComposite tmpArg_v_104;
        for(Iterator enm_106 = iset_99.iterator(); enm_106.hasNext(); wfComposite(tmpArg_v_104))
        {
            String elem_100 = UTIL.ConvertToString(enm_106.next());
            composite = elem_100;
            tmpArg_v_104 = null;
            tmpArg_v_104 = ad.getComposite(composite);
        }

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
    public static Integer errors = new Integer(0);
    private AstDefinitions ad;
    private Boolean use_uadt;
    private static final HashSet keywords;

    static 
    {
        HashSet keywordstemp = new HashSet();
        try
        {
            HashSet tmpVal_1 = new HashSet();
            tmpVal_1 = new HashSet();
            tmpVal_1.add(new String("abs"));
            tmpVal_1.add(new String("all"));
            tmpVal_1.add(new String("always"));
            tmpVal_1.add(new String("and"));
            tmpVal_1.add(new String("atomic"));
            tmpVal_1.add(new String("async"));
            tmpVal_1.add(new String("be"));
            tmpVal_1.add(new String("bool"));
            tmpVal_1.add(new String("by"));
            tmpVal_1.add(new String("card"));
            tmpVal_1.add(new String("cases"));
            tmpVal_1.add(new String("char"));
            tmpVal_1.add(new String("class"));
            tmpVal_1.add(new String("comp"));
            tmpVal_1.add(new String("compose"));
            tmpVal_1.add(new String("conc"));
            tmpVal_1.add(new String("cycles"));
            tmpVal_1.add(new String("dcl"));
            tmpVal_1.add(new String("def"));
            tmpVal_1.add(new String("dinter"));
            tmpVal_1.add(new String("div"));
            tmpVal_1.add(new String("do"));
            tmpVal_1.add(new String("dom"));
            tmpVal_1.add(new String("dunion"));
            tmpVal_1.add(new String("duration"));
            tmpVal_1.add(new String("elems"));
            tmpVal_1.add(new String("else"));
            tmpVal_1.add(new String("elseif"));
            tmpVal_1.add(new String("end"));
            tmpVal_1.add(new String("error"));
            tmpVal_1.add(new String("errs"));
            tmpVal_1.add(new String("exists"));
            tmpVal_1.add(new String("exists1"));
            tmpVal_1.add(new String("exit"));
            tmpVal_1.add(new String("ext"));
            tmpVal_1.add(new String("false"));
            tmpVal_1.add(new String("floor"));
            tmpVal_1.add(new String("for"));
            tmpVal_1.add(new String("forall"));
            tmpVal_1.add(new String("from"));
            tmpVal_1.add(new String("functions"));
            tmpVal_1.add(new String("hd"));
            tmpVal_1.add(new String("if"));
            tmpVal_1.add(new String("in"));
            tmpVal_1.add(new String("inds"));
            tmpVal_1.add(new String("inmap"));
            tmpVal_1.add(new String("instance"));
            tmpVal_1.add(new String("int"));
            tmpVal_1.add(new String("inter"));
            tmpVal_1.add(new String("inv"));
            tmpVal_1.add(new String("inverse"));
            tmpVal_1.add(new String("iota"));
            tmpVal_1.add(new String("is"));
            tmpVal_1.add(new String("is_"));
            tmpVal_1.add(new String("isofbaseclass"));
            tmpVal_1.add(new String("isofclass"));
            tmpVal_1.add(new String("lambda"));
            tmpVal_1.add(new String("len"));
            tmpVal_1.add(new String("let"));
            tmpVal_1.add(new String("map"));
            tmpVal_1.add(new String("merge"));
            tmpVal_1.add(new String("mk_"));
            tmpVal_1.add(new String("mod"));
            tmpVal_1.add(new String("mu"));
            tmpVal_1.add(new String("munion"));
            tmpVal_1.add(new String("mutex"));
            tmpVal_1.add(new String("nat"));
            tmpVal_1.add(new String("nat1"));
            tmpVal_1.add(new String("new"));
            tmpVal_1.add(new String("nil"));
            tmpVal_1.add(new String("not"));
            tmpVal_1.add(new String("of"));
            tmpVal_1.add(new String("operations"));
            tmpVal_1.add(new String("or"));
            tmpVal_1.add(new String("others"));
            tmpVal_1.add(new String("per"));
            tmpVal_1.add(new String("periodic"));
            tmpVal_1.add(new String("post"));
            tmpVal_1.add(new String("power"));
            tmpVal_1.add(new String("pre"));
            tmpVal_1.add(new String("pre_"));
            tmpVal_1.add(new String("private"));
            tmpVal_1.add(new String("protected"));
            tmpVal_1.add(new String("psubset"));
            tmpVal_1.add(new String("public"));
            tmpVal_1.add(new String("rat"));
            tmpVal_1.add(new String("rd"));
            tmpVal_1.add(new String("real"));
            tmpVal_1.add(new String("rem"));
            tmpVal_1.add(new String("responsibility"));
            tmpVal_1.add(new String("return"));
            tmpVal_1.add(new String("reverse"));
            tmpVal_1.add(new String("rng"));
            tmpVal_1.add(new String("samebaseclass"));
            tmpVal_1.add(new String("sameclass"));
            tmpVal_1.add(new String("self"));
            tmpVal_1.add(new String("seq"));
            tmpVal_1.add(new String("seq1"));
            tmpVal_1.add(new String("set"));
            tmpVal_1.add(new String("skip"));
            tmpVal_1.add(new String("specified"));
            tmpVal_1.add(new String("st"));
            tmpVal_1.add(new String("start"));
            tmpVal_1.add(new String("startlist"));
            tmpVal_1.add(new String("static"));
            tmpVal_1.add(new String("subclass"));
            tmpVal_1.add(new String("subset"));
            tmpVal_1.add(new String("sync"));
            tmpVal_1.add(new String("system"));
            tmpVal_1.add(new String("then"));
            tmpVal_1.add(new String("thread"));
            tmpVal_1.add(new String("threadid"));
            tmpVal_1.add(new String("time"));
            tmpVal_1.add(new String("tixe"));
            tmpVal_1.add(new String("tl"));
            tmpVal_1.add(new String("to"));
            tmpVal_1.add(new String("token"));
            tmpVal_1.add(new String("trap"));
            tmpVal_1.add(new String("true"));
            tmpVal_1.add(new String("types"));
            tmpVal_1.add(new String("undefined"));
            tmpVal_1.add(new String("union"));
            tmpVal_1.add(new String("values"));
            tmpVal_1.add(new String("variables"));
            tmpVal_1.add(new String("while"));
            tmpVal_1.add(new String("with"));
            tmpVal_1.add(new String("wr"));
            tmpVal_1.add(new String("yet"));
            tmpVal_1.add(new String("Node"));
            tmpVal_1.add(new String("Visitor"));
            tmpVal_1.add(new String("ContextInfo"));
            tmpVal_1.add(new String("Token"));
            tmpVal_1.add(new String("Document"));
            tmpVal_1.add(new String("Lexem"));
            keywordstemp = tmpVal_1;
        }
        catch(Throwable e)
        {
            System.out.println(e.getMessage());
        }
        keywords = keywordstemp;
    }
}