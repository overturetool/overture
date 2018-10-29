package org.overture.codegen.trans;

import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ACallStm;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class RenamedTrans extends DepthFirstAnalysisAdaptor {

    private TransAssistantIR assist;

    public RenamedTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public void caseAIdentifierVarExpIR(AIdentifierVarExpIR node) throws AnalysisException {

        INode var = AssistantBase.getVdmNode(node);

        if(var instanceof AVariableExp)
        {
            PDefinition def = ((AVariableExp) var).getVardef();

            if(def instanceof ARenamedDefinition)
            {
                ARenamedDefinition renamedDef = (ARenamedDefinition) def;
                
                AClassTypeIR definingClass = new AClassTypeIR();
                definingClass.setName(renamedDef.getDef().getName().getModule());
                definingClass.setSourceNode(node.getSourceNode());

                AExplicitVarExpIR expVar = new AExplicitVarExpIR();
                expVar.setName(renamedDef.getDef().getName().getName());
                expVar.setType(node.getType().clone());
                expVar.setIsLambda(node.getIsLambda());
                expVar.setIsLocal(node.getIsLocal());
                expVar.setMetaData(node.getMetaData());
                expVar.setTag(node.getTag());
                expVar.setSourceNode(node.getSourceNode());
                expVar.setClassType(definingClass);

                assist.replaceNodeWith(node, expVar);
            }
        }
    }

    @Override
    public void caseAPlainCallStmIR(APlainCallStmIR node) throws AnalysisException {

        super.caseAPlainCallStmIR(node);

        INode vdmNode = AssistantBase.getVdmNode(node);

        if(vdmNode instanceof ACallStm)
        {
            ACallStm cs = (ACallStm) vdmNode;
            PDefinition def = cs.getRootdef();

            if(def instanceof ARenamedDefinition)
            {
                ARenamedDefinition rd = (ARenamedDefinition) def;

                String opName = rd.getDef().getName().getName();
                String moduleName = rd.getDef().getName().getModule();

                AClassTypeIR definingClass = new AClassTypeIR();
                definingClass.setName(moduleName);
                definingClass.setSourceNode(node.getSourceNode());

                APlainCallStmIR call = new APlainCallStmIR();
                call.setClassType(definingClass);
                call.setName(opName);
                call.setIsStatic(false);
                call.setMetaData(node.getMetaData());
                call.setSourceNode(node.getSourceNode());
                call.setTag(node.getTag());
                call.setType(node.getType().clone());
                for(SExpIR a : node.getArgs())
                {
                    call.getArgs().add(a.clone());
                }

                assist.replaceNodeWith(node, call);
            }
        }
    }
}
