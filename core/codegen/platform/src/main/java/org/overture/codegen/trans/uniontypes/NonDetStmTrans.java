package org.overture.codegen.trans.uniontypes;

import org.overture.ast.statements.PStm;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ANonDeterministicBlockStmIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;
import org.apache.log4j.Logger;

public class NonDetStmTrans extends DepthFirstAnalysisAdaptor {

    protected Logger log = Logger.getLogger(this.getClass().getName());

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

        if(!node.getStatements().isEmpty())
        {
            // Just use the first statement
            block.getStatements().add(node.getStatements().getFirst().clone());
        }
        else
        {
            log.error("nondeterministic statement block did not contain any statements: " + node);
        }

        assist.replaceNodeWith(node, block);
    }
}
