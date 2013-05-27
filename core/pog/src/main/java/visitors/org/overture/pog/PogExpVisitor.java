package org.overture.pog;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.pog.POContextStack;
import org.overture.pog.ProofObligationList;

public class PogExpVisitor extends PogParamExpVisitor<POContextStack, ProofObligationList>
{


    /**
     * 
     */
    private static final long serialVersionUID = -1791028954460642701L;

    
    public PogExpVisitor( QuestionAnswerAdaptor<POContextStack, ProofObligationList> parentVisitor){
	super(parentVisitor);
    }


}
