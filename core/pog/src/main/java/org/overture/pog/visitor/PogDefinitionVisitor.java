package org.overture.pog.visitor;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;

public class PogDefinitionVisitor extends
		PogParamDefinitionVisitor<POContextStack, ProofObligationList>
{
    
    /**
     * 
     */
    private static final long serialVersionUID = -4015296332732118274L;

    public PogDefinitionVisitor( QuestionAnswerAdaptor<POContextStack, ProofObligationList> parentVisitor){
	super(parentVisitor);
    }
}
