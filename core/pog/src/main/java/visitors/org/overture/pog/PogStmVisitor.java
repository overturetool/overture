package org.overture.pog;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.pog.POContextStack;
import org.overture.pog.ProofObligationList;

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
