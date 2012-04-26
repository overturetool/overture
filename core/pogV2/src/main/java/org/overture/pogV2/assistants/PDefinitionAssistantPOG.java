package org.overture.pogV2.assistants;

import java.util.LinkedList;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.pogV2.obligations.POContextStack;
import org.overture.pogV2.obligations.PONameContext;
import org.overture.pogV2.obligations.ProofObligationList;

public class PDefinitionAssistantPOG extends PDefinitionAssistantTC {

	public static ProofObligationList getProofObligations(
			LinkedList<PDefinition> defs,
			QuestionAnswerAdaptor<POContextStack, ProofObligationList> pogVisitor,
			POContextStack ctxt) {
		ProofObligationList obligations = new ProofObligationList();

		for (PDefinition d : defs) {
			ctxt.push(new PONameContext(getVariableNames(d)));
			obligations.addAll(d.apply(pogVisitor, ctxt));
			ctxt.pop();
		}

		return obligations;
	}

}
