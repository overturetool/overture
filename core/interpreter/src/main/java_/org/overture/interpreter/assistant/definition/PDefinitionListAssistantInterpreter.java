package org.overture.interpreter.assistant.definition;

import java.util.LinkedList;

import org.overture.ast.definitions.PDefinition;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.PONameContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;

public class PDefinitionListAssistantInterpreter extends PDefinitionListAssistantTC
{

	public static ProofObligationList getProofObligations(
			LinkedList<PDefinition> defs, POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (PDefinition d: defs)
		{
			ctxt.push(new PONameContext(PDefinitionAssistantInterpreter.getVariableNames(d)));
			obligations.addAll(PDefinitionAssistantInterpreter.getProofObligations(d,ctxt));
			ctxt.pop();
		}

		return obligations;
	}

}
