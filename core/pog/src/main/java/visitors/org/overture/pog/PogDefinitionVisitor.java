package org.overture.pog;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.pog.POContextStack;
import org.overture.pog.ProofObligationList;

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
