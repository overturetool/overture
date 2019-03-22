package org.overture.codegen.trans;

import org.apache.log4j.Logger;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AMethodInstantiationExpIR;
import org.overture.codegen.ir.expressions.AQuoteLiteralExpIR;
import org.overture.codegen.ir.expressions.ATypeArgExpIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AExternalTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMapMapTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.ir.types.ATokenBasicTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class PolyFuncTrans extends DepthFirstAnalysisAdaptor {

    protected Logger log = Logger.getLogger(this.getClass().getName());

    private TransAssistantIR assist;

    private static final String TYPE_ARG_PREFIX = "_type_";

    private static final String UTIL_CLASS = "Utils";
    private static final String SET_UTIL = "SetUtil";
    private static final String VDM_SET = "VDMSet";
    private static final String SET_METHOD_NAME = "set";


    public static final String OBJECT = "Object";

    public static final String NAT = "NAT";
    public static final String NAT1 = "NAT1";
    public static final String INT = "INT";
    public static final String REAL = "REAL";
    public static final String RAT = "RAT";
    public static final String BOOL = "BOOL";
    public static final String CHAR = "CHAR";
    public static final String TOKEN = "TOKEN";
    public static final String STRING = "STRING";
    public static final String SEQ_OF_ANYTHING = "SEQ_OF_ANYTHING";
    public static final String SET_OF_ANYTHING = "SET_OF_ANYTHING";
    public static final String MAP_ANYTHING_TO_ANYTHING = "MAP_ANYTHING_TO_ANYTHING";
    public static final String UNKNOWN = "UNKNOWN";

    public static final String TYPE_NOT_SUPPORTED = "TYPE_NOT_SUPPORTED";

    public PolyFuncTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public void caseAFuncDeclIR(AFuncDeclIR node) throws AnalysisException {

        super.caseAFuncDeclIR(node);

        if (!node.getTemplateTypes().isEmpty()) {

            for(ATemplateTypeIR t : node.getTemplateTypes())
            {
                AFormalParamLocalParamIR param = new AFormalParamLocalParamIR();
                param.setType(new AUnknownTypeIR());
                param.setPattern(assist.getInfo().getPatternAssistant().consIdPattern(toTypeArgName(t)));

                node.getFormalParams().add(param);
            }
        }
    }

    public static String toTypeArgName(ATemplateTypeIR t) {

        return TYPE_ARG_PREFIX + t;
    }

    @Override
    public void caseAApplyExpIR(AApplyExpIR node) throws AnalysisException {

        super.caseAApplyExpIR(node);

        SExpIR root = node.getRoot();

        if(root instanceof AMethodInstantiationExpIR)
        {
            AMethodInstantiationExpIR methodInst = (AMethodInstantiationExpIR) root;

            SExpIR func = methodInst.getFunc();

            if(func instanceof AExplicitVarExpIR)
            {
                AExplicitVarExpIR ev = (AExplicitVarExpIR) func;

                STypeIR type = ev.getClassType();

                if(type instanceof AClassTypeIR)
                {
                    AClassTypeIR classType = (AClassTypeIR) type;
					if(assist.getInfo().getDeclAssistant().isLibraryName(classType.getName())
							&& !isSeqOfChar2Val(ev, classType))
                    {
                        // Most libraries don't expect type arguments
                        return;
                    }
                }
            }
            else if(func instanceof AIdentifierVarExpIR)
            {
                INode var = AssistantBase.getVdmNode(func);

                if(var instanceof AVariableExp) {
                    PDefinition def = ((AVariableExp) var).getVardef();

                    if (def instanceof ARenamedDefinition) {
                        ARenamedDefinition renamedDef = (ARenamedDefinition) def;

                        if (assist.getInfo().getDeclAssistant().isLibraryName(renamedDef.getDef().getName().getModule())) {

                            // Libraries don't expect type arguments
                            return;
                        }
                    }

                }
            }

            for(STypeIR type : methodInst.getActualTypes())
            {
            	SExpIR expToAdd = consTypeArg(methodInst, type);
                
                if(expToAdd != null)
                {
                	node.getArgs().add(expToAdd);
                }
            }
        }
    }

	private boolean isSeqOfChar2Val(AExplicitVarExpIR ev, AClassTypeIR classType) {
		return classType.getName().equals(IRConstants.VDMUTIL_LIB) && 
				ev.getName().equals(IRConstants.SEQ_OF_CHAR2VAL);
	}

	private SExpIR consTypeArg(AMethodInstantiationExpIR methodInst, STypeIR type) {
		SExpIR expToAdd = null;
		if(type instanceof AQuoteTypeIR)
		{
		    AQuoteLiteralExpIR qt = new AQuoteLiteralExpIR();
		    qt.setValue(((AQuoteTypeIR) type).getValue());
		    expToAdd = qt;
		}
		else if(type instanceof ARecordTypeIR)
		{
		    ATypeArgExpIR typeArg = new ATypeArgExpIR();
		    typeArg.setType(type.clone());
		    expToAdd = typeArg;
		}
		else if(type instanceof ATemplateTypeIR)
		{
		    ATemplateTypeIR templateType = (ATemplateTypeIR) type;
		    String paramName = toTypeArgName(templateType);
		    AIdentifierVarExpIR templateTypeArg = assist.getInfo().getExpAssistant().consIdVar(paramName, templateType.clone());
		    expToAdd = templateTypeArg;
		}
		else if(type instanceof AUnionTypeIR)
		{
		    AUnionTypeIR unionType = (AUnionTypeIR) type;

		    if(assist.getInfo().getTypeAssistant().isUnionOfNonCollectionTypes(unionType))
		    {
		        AExplicitVarExpIR setConstructorMember = consTypeArg(getSetUtil());
		        setConstructorMember.setName(getSetMethodName());

		        AExternalTypeIR setType = new AExternalTypeIR();
		        setType.setName(getVdmSet());

		        AApplyExpIR setConstructor = new AApplyExpIR();
		        setConstructor.setType(setType);
		        setConstructor.setRoot(setConstructorMember);

		        for(STypeIR t : unionType.getTypes())
		        {
		            setConstructor.getArgs().add(consTypeArg(methodInst, t));
		        }

		        expToAdd = setConstructor;
		    }
		    else {
		        issueUnsupportedWarning(methodInst);
		        AExplicitVarExpIR typeArg = consTypeArg(getUtilClass());
		        String name = getUnsupportedTypeFieldName();
		        typeArg.setName(name);
		        expToAdd = typeArg;
		    }
		}
		else
		{
		    AExplicitVarExpIR typeArg = consTypeArgFlag(methodInst, type);
		    expToAdd = typeArg;
		}
		return expToAdd;
	}

	private AExplicitVarExpIR consTypeArgFlag(AMethodInstantiationExpIR methodInst, STypeIR type) {
		AExplicitVarExpIR typeArg = consTypeArg(getUtilClass());

		String name;
		if(type instanceof ANatNumericBasicTypeIR)
		{
		    name = NAT;
		}
		else if(type instanceof ANat1NumericBasicTypeIR)
		{
		    name = NAT1;
		}
		else if(type instanceof AIntNumericBasicTypeIR)
		{
		    name = INT;
		}
		else if(type instanceof ARealNumericBasicTypeIR)
		{
		    name = REAL;
		}
		else if(type instanceof ARatNumericBasicTypeIR)
		{
		    name = RAT;
		}
		else if(type instanceof ABoolBasicTypeIR)
		{
		    name = BOOL;
		}
		else if(type instanceof ACharBasicTypeIR)
		{
		    name = CHAR;
		}
		else if(type instanceof ATokenBasicTypeIR)
		{
		    name = TOKEN;
		}
		else if(type instanceof AStringTypeIR)
		{
		    name = STRING;
		}
		else if (type instanceof ASeqSeqTypeIR) {
			ASeqSeqTypeIR seqType = ((ASeqSeqTypeIR) type);
			STypeIR seqOf = seqType.getSeqOf();

			if (seqOf instanceof AUnknownTypeIR && !seqType.getSeq1()) {
				name = SEQ_OF_ANYTHING;
			} else {
				issueUnsupportedWarning(methodInst);
				name = getUnsupportedTypeFieldName();
			}
		} else if (type instanceof ASetSetTypeIR) {
			ASetSetTypeIR setType = ((ASetSetTypeIR) type);
			STypeIR setOf = setType.getSetOf();

			// set1 is not accounted for (it's not supported by the IR)
			if (setOf instanceof AUnknownTypeIR) {
				name = SET_OF_ANYTHING;
			} else {
				issueUnsupportedWarning(methodInst);
				name = getUnsupportedTypeFieldName();
			}
		} else if (type instanceof AMapMapTypeIR) {
			AMapMapTypeIR mapType = ((AMapMapTypeIR) type);
			STypeIR from = mapType.getFrom();
			STypeIR to = mapType.getTo();

			// Injective maps are not accounted for
			if (from instanceof AUnknownTypeIR && to instanceof AUnknownTypeIR) {
				name = MAP_ANYTHING_TO_ANYTHING;
			} else {
				issueUnsupportedWarning(methodInst);
				name = getUnsupportedTypeFieldName();
			}
		}
		else if(type instanceof AUnknownTypeIR)
		{
		    name = UNKNOWN;
		}
		else
		{
		    issueUnsupportedWarning(methodInst);
		    name = getUnsupportedTypeFieldName();
		}
		typeArg.setName(name);
		return typeArg;
	}

    public AExplicitVarExpIR consTypeArg(String className) {

        AExternalTypeIR runtimeUtilClass = new AExternalTypeIR();
        runtimeUtilClass.setName(className);

        AExternalTypeIR anyType = new AExternalTypeIR();
        anyType.setName(getTypeArgumentFieldName());

        AExplicitVarExpIR typeArg = new AExplicitVarExpIR();
        typeArg.setClassType(runtimeUtilClass);
        typeArg.setIsLocal(false);
        typeArg.setIsLambda(false);
        typeArg.setType(anyType);
        return typeArg;
    }

    public void issueUnsupportedWarning(AMethodInstantiationExpIR methodInst) {
        assist.getInfo().addTransformationWarning(methodInst, "Function instantiation only " +
                "works for basic types, set of ?, seq of ?, map ? to ?, quotes, unions " + 
        		"of non-collection types, strings, polymorphic types and records");
    }

    public String getUnsupportedTypeFieldName() {
        return TYPE_NOT_SUPPORTED;
    }

    public String getTypeArgumentFieldName() {
        return OBJECT;
    }

    public String getUtilClass() {
        return UTIL_CLASS;
    }

    public String getSetUtil(){
        return SET_UTIL;
    }

    public String getVdmSet()
    {
        return VDM_SET;
    }

    public String getSetMethodName(){
        return SET_METHOD_NAME;
    }
}
