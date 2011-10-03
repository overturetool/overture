package org.overture.pog.visitors;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.pog.assistants.PDefinitionAssistantPOG;
import org.overture.pog.obligations.POContextStack;
import org.overture.pog.obligations.ProofObligationList;


public class PogVisitor extends QuestionAnswerAdaptor<POContextStack, ProofObligationList> {

	@Override
	public ProofObligationList caseAModuleModules(AModuleModules node,
			POContextStack question) {
		
		return PDefinitionAssistantPOG.getProofObligations(node.getDefs(),this,question);
		
	}
	
}
