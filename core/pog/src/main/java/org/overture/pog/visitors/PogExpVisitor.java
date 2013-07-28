package org.overture.pog.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligationList;

public class PogExpVisitor extends PogParamExpVisitor<POContextStack, ProofObligationList>
{


    /**
     * 
     */
    private static final long serialVersionUID = -1791028954460642701L;

    
    public PogExpVisitor( QuestionAnswerAdaptor<IPOContextStack, ? extends IProofObligationList> parentVisitor){
	super(parentVisitor);
    }


}
