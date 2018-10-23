package org.overture.codegen.trans;

import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.codegen.assistant.AssistantBase;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
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
}
