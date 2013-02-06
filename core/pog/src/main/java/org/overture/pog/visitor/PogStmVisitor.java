package org.overture.pog.visitor;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;

public class PogStmVisitor extends
	PogParamStmVisitor<POContextStack, ProofObligationList> {



    /**
     * 
     */
    private static final long serialVersionUID = -8893110308150868534L;

    public PogStmVisitor(
	    QuestionAnswerAdaptor<POContextStack, ProofObligationList> parentVisitor) {
	super(parentVisitor);
    }

}
