package org.overture.interpreter.assistant.definition;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameList;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ValueList;
import org.overture.pog.contexts.POContextStack;
import org.overture.pog.contexts.PONameContext;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;

public class PDefinitionListAssistantInterpreter extends
		PDefinitionListAssistantTC implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PDefinitionListAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public ProofObligationList getProofObligations(
			LinkedList<PDefinition> defs, POContextStack ctxt)
	{
		ProofObligationList obligations = new ProofObligationList();

		for (PDefinition d : defs)
		{
			ctxt.push(new PONameContext(af.createPDefinitionAssistant().getVariableNames(d)));
			obligations.addAll(af.createPDefinitionAssistant().getProofObligations(d, ctxt));
			ctxt.pop();
		}

		return obligations;
	}

	public ValueList getValues(LinkedList<PDefinition> defs, ObjectContext ctxt)
	{
		ValueList list = new ValueList();

		for (PDefinition d : defs)
		{
			list.addAll(af.createPDefinitionAssistant().getValues(d, ctxt));
		}

		return list;
	}

	public PExp findExpression(LinkedList<PDefinition> list, int lineno)
	{
		for (PDefinition d : list)
		{
			PExp found = af.createPDefinitionAssistant().findExpression(d, lineno);

			if (found != null)
			{
				return found;
			}
		}

		return null;
	}

	public NameValuePairList getNamedValues(
			LinkedList<PDefinition> definitions, Context ctxt)
	{
		NameValuePairList nvl = new NameValuePairList();

		for (PDefinition d : definitions)
		{
			nvl.addAll(af.createPDefinitionAssistant().getNamedValues(d, ctxt));
		}

		return nvl;
	}

	public LexNameList getOldNames(LinkedList<PDefinition> definitions)
	{
		LexNameList list = new LexNameList();

		for (PDefinition d : definitions)
		{
			try
			{
				list.addAll(d.apply(af.getOldNameCollector()));
			} catch (AnalysisException e)
			{
				list.add(null);
			}
		}

		return list;
	}

}
