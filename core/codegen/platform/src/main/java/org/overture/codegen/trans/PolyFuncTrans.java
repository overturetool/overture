package org.overture.codegen.trans;

import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantBase;
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
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ATemplateTypeIR;
import org.overture.codegen.ir.types.ATokenBasicTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class PolyFuncTrans extends DepthFirstAnalysisAdaptor {

    private TransAssistantIR assist;

    private static final String TYPE_ARG_PREFIX = "_type_";

    private static final String UTIL_CLASS = "Utils";

    public static final String OBJECT = "Object";

    public static final String NAT = "NAT";
    public static final String NAT1 = "NAT1";
    public static final String INT = "INT";
    public static final String REAL = "REAL";
    public static final String RAT = "RAT";
    public static final String BOOL = "BOOL";
    public static final String CHAR = "CHAR";
    public static final String TOKEN = "TOKEN";

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

                STypeIR classType = ev.getClassType();

                if(classType instanceof AClassTypeIR)
                {
                    if(assist.getInfo().getDeclAssistant().isLibraryName(((AClassTypeIR) classType).getName()))
                    {
                        // Libraries don't expect type arguments
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
                if(type instanceof AQuoteTypeIR)
                {
                    AQuoteLiteralExpIR qt = new AQuoteLiteralExpIR();
                    qt.setValue(((AQuoteTypeIR) type).getValue());
                    node.getArgs().add(qt);
                }
                else if(type instanceof ARecordTypeIR)
                {
                    ATypeArgExpIR typeArg = new ATypeArgExpIR();
                    typeArg.setType(type.clone());
                    node.getArgs().add(typeArg);
                }
                else
                {
                    AExternalTypeIR runtimeUtilClass = new AExternalTypeIR();
                    runtimeUtilClass.setName(getUtilClass());

                    AExternalTypeIR anyType = new AExternalTypeIR();
                    anyType.setName(getTypeArgumentFieldName());

                    AExplicitVarExpIR typeArg = new AExplicitVarExpIR();
                    typeArg.setClassType(runtimeUtilClass);
                    typeArg.setIsLocal(false);
                    typeArg.setIsLambda(false);
                    typeArg.setType(anyType);

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
                    else
                    {
                        assist.getInfo().addTransformationWarning(methodInst, "Function instantiation only works for basic types, quotes and records");
                        name = getUnsupportedTypeFieldName();
                    }
                    typeArg.setName(name);

                    node.getArgs().add(typeArg);
                }
            }
        }
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
}
