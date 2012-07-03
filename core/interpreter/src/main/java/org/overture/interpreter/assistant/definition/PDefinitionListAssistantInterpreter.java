package org.overture.interpreter.assistant.definition;

import java.util.LinkedList;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ValueList;
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

	public static ValueList getValues(LinkedList<PDefinition> defs,
			ObjectContext ctxt)
	{
		ValueList list = new ValueList();

		for (PDefinition d: defs)
		{
			list.addAll(PDefinitionAssistantInterpreter.getValues(d,ctxt));
		}

		return list;
	}

	public static PExp findExpression(LinkedList<PDefinition> list,
			int lineno)
	{
		for (PDefinition d: list)
		{
			PExp found = PDefinitionAssistantInterpreter.findExpression(d,lineno);

			if (found != null)
			{
				return found;
			}
		}

   		return null;
	}

	public static NameValuePairList getNamedValues(
			LinkedList<PDefinition> definitions, Context ctxt)
	{
		NameValuePairList nvl = new NameValuePairList();

		for (PDefinition d: definitions)
		{
			nvl.addAll(PDefinitionAssistantInterpreter.getNamedValues(d,ctxt));
		}

		return nvl;
	}

}
