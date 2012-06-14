package org.overture.interpreter.assistant.definition;


import org.overture.ast.definitions.PDefinition;
import org.overture.interpreter.runtime.RootContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.visitor.PogVisitor;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PDefinitionAssistantInterpreter extends PDefinitionAssistantTC
{

	public static NameValuePairList getNamedValues(PDefinition d,
			RootContext initialContext)
	{
		// TODO Auto-generated method stub
		assert false : "not implemented";
		return null;
	}

	public static ProofObligationList getProofObligations(
			PDefinition d, POContextStack ctxt)
	{
		return d.apply(new PogVisitor(), new POContextStack());
	}

}
