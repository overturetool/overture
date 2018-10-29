package org.overture.codegen.trans;


import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.*;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.types.*;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class IsExpSimplifyTrans  extends DepthFirstAnalysisAdaptor {

    private TransAssistantIR assist;

    public IsExpSimplifyTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public void caseAGeneralIsExpIR(AGeneralIsExpIR node) throws AnalysisException {

        super.caseAGeneralIsExpIR(node);

        SExpIR isExp = consIsExp(node.getExp(), node.getCheckedType());

        if (isExp == null) {
            assist.getInfo().addTransformationWarning(node, "The 'is' expression is not supported for type: "
                    + node.getCheckedType().getClass().getName());
            return;
        }
        else
        {
            assist.replaceNodeWith(node, isExp);
        }
    }

    public static SExpIR consIsExp(SExpIR exp, STypeIR checkedType)
    {
        exp = exp.clone();
        checkedType = checkedType.clone();

        if (checkedType instanceof AUnionTypeIR)
        {
            return consGeneralIsExp(exp, checkedType);
        } else if (checkedType instanceof SBasicTypeIR)
        {
            return consIsExpBasicType(exp, checkedType);
        } else if (checkedType instanceof AQuoteTypeIR)
        {
            return consIsExpQuoteType(exp, (AQuoteTypeIR) checkedType);
        } else if (checkedType instanceof ATupleTypeIR)
        {
            return consTupleIsExp(exp, checkedType);
        } else if (checkedType instanceof ARecordTypeIR
                || checkedType instanceof AClassTypeIR
                || checkedType instanceof AStringTypeIR)
        {
            return consGeneralIsExp(exp, checkedType);
        } else
        {
            if (checkedType instanceof ASeqSeqTypeIR)
            {
                ASeqSeqTypeIR seqType = (ASeqSeqTypeIR) checkedType;

                if (seqType.getSeqOf() instanceof AUnknownTypeIR)
                {
                    return consGeneralIsExp(exp, checkedType);
                }
            } else if (checkedType instanceof AMapMapTypeIR)
            {
                AMapMapTypeIR mapType = (AMapMapTypeIR) checkedType;

                if (mapType.getFrom() instanceof AUnknownTypeIR
                        && mapType.getTo() instanceof AUnknownTypeIR)
                {
                    return consGeneralIsExp(exp, checkedType);
                }
            }
            else if(checkedType instanceof ATemplateTypeIR)
            {
                ATemplateTypeIR templateType = (ATemplateTypeIR) checkedType;

                String argName = PolyFuncTrans.toTypeArgName(templateType);

                AExternalTypeIR typeVar = new AExternalTypeIR();
                typeVar.setName(argName);

                return consGeneralIsExp(exp, typeVar);
            }

            return null;
        }
    }

    public static SExpIR consIsExpQuoteType(SExpIR exp, AQuoteTypeIR quoteType)
    {
        AQuoteLiteralExpIR lit = new AQuoteLiteralExpIR();
        lit.setType(quoteType);
        lit.setValue(quoteType.getValue());

        AEqualsBinaryExpIR equals = new AEqualsBinaryExpIR();
        equals.setType(new ABoolBasicTypeIR());
        equals.setLeft(exp);
        equals.setRight(lit);

        return equals;
    }

    public static SExpIR consGeneralIsExp(SExpIR expCg, STypeIR checkedTypeCg)
    {
        AGeneralIsExpIR generalIsExp = new AGeneralIsExpIR();
        generalIsExp = new AGeneralIsExpIR();
        generalIsExp.setType(new ABoolBasicTypeIR());
        generalIsExp.setExp(expCg);
        generalIsExp.setCheckedType(checkedTypeCg);

        return generalIsExp;
    }

    public static ATupleIsExpIR consTupleIsExp(SExpIR exp, STypeIR checkedType)
    {
        ATupleIsExpIR tupleIsExp = new ATupleIsExpIR();
        tupleIsExp.setType(new ABoolBasicTypeIR());
        tupleIsExp.setExp(exp);
        tupleIsExp.setCheckedType(checkedType);

        return tupleIsExp;
    }

    public static SExpIR consIsExpBasicType(SExpIR expCg, STypeIR checkedType)
    {
        SIsExpIR basicIsExp = null;

        if (checkedType instanceof ABoolBasicTypeIR)
        {
            basicIsExp = new ABoolIsExpIR();
        } else if (checkedType instanceof ANatNumericBasicTypeIR)
        {
            basicIsExp = new ANatIsExpIR();
        } else if (checkedType instanceof ANat1NumericBasicTypeIR)
        {
            basicIsExp = new ANat1IsExpIR();
        } else if (checkedType instanceof AIntNumericBasicTypeIR)
        {
            basicIsExp = new AIntIsExpIR();
        } else if (checkedType instanceof ARatNumericBasicTypeIR)
        {
            basicIsExp = new ARatIsExpIR();
        } else if (checkedType instanceof ARealNumericBasicTypeIR)
        {
            basicIsExp = new ARealIsExpIR();
        } else if (checkedType instanceof ACharBasicTypeIR)
        {
            basicIsExp = new ACharIsExpIR();
        } else if (checkedType instanceof ATokenBasicTypeIR)
        {
            basicIsExp = new ATokenIsExpIR();
        } else
        {
            return null;
        }

        basicIsExp.setType(new ABoolBasicTypeIR());
        basicIsExp.setExp(expCg);

        return basicIsExp;
    }
}
