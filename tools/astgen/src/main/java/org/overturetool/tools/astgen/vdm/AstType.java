// Decompiled by DJ v3.11.11.95 Copyright 2009 Atanas Neshkov  Date: 29-07-2009 15:19:48
// Home Page: http://members.fortunecity.com/neshkov/dj.html http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AstType.java

package org.overturetool.tools.astgen.vdm;

import java.util.HashSet;
import java.util.Vector;
import jp.co.csk.vdm.toolbox.VDM.*;

// Referenced classes of package nl.marcelverhoef.vdm.ast:
//            AstCore, AstUnionType, AstQuotedType, AstTypeName
@SuppressWarnings("all")
public class AstType extends AstCore
{

    public AstType()
        throws CGException
    {
    }

    public Boolean isBasicType()
        throws CGException
    {
        Boolean rexpr_1 = null;
        Boolean var1_2 = null;
        Boolean var1_3 = null;
        if(!(var1_3 = isCharType()).booleanValue())
            var1_3 = isBoolType();
        if(!(var1_2 = var1_3).booleanValue())
            var1_2 = isNatType();
        if(!(rexpr_1 = var1_2).booleanValue())
            rexpr_1 = isRealType();
        return rexpr_1;
    }

    public Boolean isCharType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isBoolType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isNatType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isRealType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isTypeName()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isQuotedType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isUnionType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isSeqType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isSetType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isStringType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isOptionalType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Boolean isMapType()
        throws CGException
    {
        return new Boolean(false);
    }

    public Tuple isQuotedTypeUnion()
        throws CGException
    {
        Boolean retb = new Boolean(true);
        HashSet rets = new HashSet();
        if(isUnionType().booleanValue())
        {
            AstUnionType aut = (AstUnionType)this;
            Tuple tmpVal_7 = new Tuple(2);
            AstType obj_8 = null;
            obj_8 = aut.getLhs();
            tmpVal_7 = obj_8.isQuotedTypeUnion();
            Boolean lb = null;
            HashSet ls = new HashSet();
            boolean succ_6 = true;
            Vector e_l_9 = new Vector();
            for(int i_10 = 1; i_10 <= tmpVal_7.Length(); i_10++)
                e_l_9.add(tmpVal_7.GetField(i_10));

            if(succ_6 = 2 == e_l_9.size())
            {
                lb = (Boolean)e_l_9.get(0);
                ls = (HashSet)(HashSet)e_l_9.get(1);
            }
            if(!succ_6)
                UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
            if(lb.booleanValue())
            {
                HashSet rhs_13 = new HashSet();
                rhs_13 = (HashSet)rets.clone();
                rhs_13.addAll(ls);
                rets = (HashSet)UTIL.clone(rhs_13);
            } else
            {
                retb = (Boolean)UTIL.clone(new Boolean(false));
            }
            Tuple tmpVal_17 = new Tuple(2);
            AstType obj_18 = null;
            obj_18 = aut.getRhs();
            tmpVal_17 = obj_18.isQuotedTypeUnion();
            Boolean rb = null;
            HashSet rs = new HashSet();
            boolean succ_16 = true;
            Vector e_l_19 = new Vector();
            for(int i_20 = 1; i_20 <= tmpVal_17.Length(); i_20++)
                e_l_19.add(tmpVal_17.GetField(i_20));

            if(succ_16 = 2 == e_l_19.size())
            {
                rb = (Boolean)e_l_19.get(0);
                rs = (HashSet)(HashSet)e_l_19.get(1);
            }
            if(!succ_16)
                UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
            if(rb.booleanValue())
            {
                HashSet rhs_23 = new HashSet();
                rhs_23 = (HashSet)rets.clone();
                rhs_23.addAll(rs);
                rets = (HashSet)UTIL.clone(rhs_23);
            } else
            {
                retb = (Boolean)UTIL.clone(new Boolean(false));
            }
        } else
        if(isQuotedType().booleanValue())
        {
            AstQuotedType aqt = (AstQuotedType)this;
            String elem_5 = null;
            elem_5 = aqt.getName();
            rets.add(elem_5);
        } else
        {
            retb = (Boolean)UTIL.clone(new Boolean(false));
        }
        if(retb.booleanValue())
        {
            Tuple rexpr_30 = new Tuple(2);
            rexpr_30 = new Tuple(2);
            rexpr_30.SetField(1, retb);
            rexpr_30.SetField(2, rets);
            return rexpr_30;
        } else
        {
            Tuple rexpr_27 = new Tuple(2);
            rexpr_27 = new Tuple(2);
            rexpr_27.SetField(1, new Boolean(false));
            rexpr_27.SetField(2, new HashSet());
            return rexpr_27;
        }
    }

    public Tuple isTypeNameUnion()
        throws CGException
    {
        Boolean retb = new Boolean(true);
        HashSet rets = new HashSet();
        if(isUnionType().booleanValue())
        {
            AstUnionType aut = (AstUnionType)this;
            Tuple tmpVal_7 = new Tuple(2);
            AstType obj_8 = null;
            obj_8 = aut.getLhs();
            tmpVal_7 = obj_8.isTypeNameUnion();
            Boolean lb = null;
            HashSet ls = new HashSet();
            boolean succ_6 = true;
            Vector e_l_9 = new Vector();
            for(int i_10 = 1; i_10 <= tmpVal_7.Length(); i_10++)
                e_l_9.add(tmpVal_7.GetField(i_10));

            if(succ_6 = 2 == e_l_9.size())
            {
                lb = (Boolean)e_l_9.get(0);
                ls = (HashSet)(HashSet)e_l_9.get(1);
            }
            if(!succ_6)
                UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
            if(lb.booleanValue())
            {
                HashSet rhs_13 = new HashSet();
                rhs_13 = (HashSet)rets.clone();
                rhs_13.addAll(ls);
                rets = (HashSet)UTIL.clone(rhs_13);
            } else
            {
                retb = (Boolean)UTIL.clone(new Boolean(false));
            }
            Tuple tmpVal_17 = new Tuple(2);
            AstType obj_18 = null;
            obj_18 = aut.getRhs();
            tmpVal_17 = obj_18.isTypeNameUnion();
            Boolean rb = null;
            HashSet rs = new HashSet();
            boolean succ_16 = true;
            Vector e_l_19 = new Vector();
            for(int i_20 = 1; i_20 <= tmpVal_17.Length(); i_20++)
                e_l_19.add(tmpVal_17.GetField(i_20));

            if(succ_16 = 2 == e_l_19.size())
            {
                rb = (Boolean)e_l_19.get(0);
                rs = (HashSet)(HashSet)e_l_19.get(1);
            }
            if(!succ_16)
                UTIL.RunTime("Run-Time Error:Pattern match did not succeed in value definition");
            if(rb.booleanValue())
            {
                HashSet rhs_23 = new HashSet();
                rhs_23 = (HashSet)rets.clone();
                rhs_23.addAll(rs);
                rets = (HashSet)UTIL.clone(rhs_23);
            } else
            {
                retb = (Boolean)UTIL.clone(new Boolean(false));
            }
        } else
        if(isTypeName().booleanValue())
        {
            AstTypeName atn = (AstTypeName)this;
            String elem_5 = null;
            elem_5 = atn.getName();
            rets.add(elem_5);
        } else
        {
            retb = (Boolean)UTIL.clone(new Boolean(false));
        }
        if(retb.booleanValue())
        {
            Tuple rexpr_30 = new Tuple(2);
            rexpr_30 = new Tuple(2);
            rexpr_30.SetField(1, retb);
            rexpr_30.SetField(2, rets);
            return rexpr_30;
        } else
        {
            Tuple rexpr_27 = new Tuple(2);
            rexpr_27 = new Tuple(2);
            rexpr_27.SetField(1, new Boolean(false));
            rexpr_27.SetField(2, new HashSet());
            return rexpr_27;
        }
    }

    static jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare vdmComp = new jp.co.csk.vdm.toolbox.VDM.UTIL.VDMCompare();

}