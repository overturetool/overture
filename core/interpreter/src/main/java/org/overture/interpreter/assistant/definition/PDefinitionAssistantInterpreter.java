package org.overture.interpreter.assistant.definition;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.PStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.ValueList;
import org.overture.pog.contexts.POContextStack;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IProofObligationList;
import org.overture.pog.pub.ProofObligationGenerator;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class PDefinitionAssistantInterpreter extends PDefinitionAssistantTC implements IAstAssistant
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public PDefinitionAssistantInterpreter(IInterpreterAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public NameValuePairList getNamedValues(PDefinition def,
			Context initialContext)
	{
		try
		{
			return def.apply(af.getNamedValueLister(), initialContext);
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public IProofObligationList getProofObligations(PDefinition def,
			POContextStack ctxt)
	{
		try
		{

			ProofObligationGenerator.generateProofObligations(def);

		} catch (AnalysisException e)
		{
			e.printStackTrace();
		}
		return new ProofObligationList();
	}

	/**
	 * Return a list of external values that are read by the definition.
	 * 
	 * @param def
	 * @param ctxt
	 *            The context in which to evaluate the expressions.
	 * @return A list of values read.
	 */
	public ValueList getValues(PDefinition def, ObjectContext ctxt)
	{
		try
		{
			return def.apply(af.getValuesDefinitionLocator(), ctxt);
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public PExp findExpression(PDefinition def, int lineno)
	{
		try
		{
			return def.apply(af.getExpressionFinder(), lineno);
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public boolean isTypeDefinition(PDefinition def)
	{
		try
		{
			return def.apply(af.getTypeDefinitionChecker());
		} catch (AnalysisException e)
		{
			return false;
		}

	}

	public boolean isRuntime(PDefinition def)
	{
		try
		{
			return def.apply(af.getDefinitionRunTimeChecker());
		} catch (AnalysisException e)
		{
			return true;
		}
	}

	public boolean isValueDefinition(PDefinition def)
	{
		try
		{
			return def.apply(af.getDefintionValueChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public boolean isInstanceVariable(PDefinition def)
	{
		try
		{
			return def.apply(af.getInstanceVariableChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public PStm findStatement(LinkedList<PDefinition> definitions, int lineno)
	{
		for (PDefinition d : definitions)
		{
			PStm found = findStatement(d, lineno);

			if (found != null)
			{
				return found;
			}
		}

		return null;
	}

	private PStm findStatement(PDefinition def, int lineno)
	{
		try
		{
			return def.apply(af.getDefinitionStatementFinder(), lineno);
		} catch (AnalysisException e)
		{
			return null;
		}
	}

}
