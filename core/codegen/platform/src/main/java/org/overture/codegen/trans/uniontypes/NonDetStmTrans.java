package org.overture.codegen.trans.uniontypes;

import org.overture.ast.statements.PStm;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ANonDeterministicBlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class NonDetStmTrans extends DepthFirstAnalysisAdaptor {

    private final TransAssistantIR assist;

    public NonDetStmTrans(TransAssistantIR assist)
    {
        this.assist = assist;
    }

    @Override
    public void caseANonDeterministicBlockStmIR(ANonDeterministicBlockStmIR node) throws AnalysisException {
        super.caseANonDeterministicBlockStmIR(node);

        ABlockStmIR block = new ABlockStmIR();
        block.setScoped(false);
        block.setTag(node.getTag());
        block.setSourceNode(node.getSourceNode());

        for(SStmIR s : node.getStatements())
        {
            block.getStatements().add(s.clone());
        }

        assist.replaceNodeWith(node, block);
    }
}
