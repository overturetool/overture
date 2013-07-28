package org.overture.pog.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligationList;

public class PogStmVisitor extends
	PogParamStmVisitor<POContextStack, ProofObligationList> {



    /**
     * 
     */
    private static final long serialVersionUID = -8893110308150868534L;

    public PogStmVisitor(
	    QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor) {
	super(parentVisitor);
    }

}
