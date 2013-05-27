package org.overture.pog;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.pog.POContextStack;
import org.overture.pog.PONameContext;
import org.overture.pog.ProofObligationList;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PDefinitionAssistantPOG extends PDefinitionAssistantTC {

	public static ProofObligationList getProofObligations(
			LinkedList<PDefinition> defs,
			QuestionAnswerAdaptor<POContextStack, ProofObligationList> pogVisitor,
			POContextStack ctxt) throws AnalysisException {
		ProofObligationList obligations = new ProofObligationList();

		for (PDefinition d : defs) {
			ctxt.push(new PONameContext(getVariableNames(d)));
			obligations.addAll(d.apply(pogVisitor, ctxt));
			ctxt.pop();
		}

		return obligations;
	}

}
