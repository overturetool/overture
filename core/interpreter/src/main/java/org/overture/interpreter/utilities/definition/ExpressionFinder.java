package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AErrorCase;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/***************************************
 * This method finds a expession within a definition.
 * 
 * @author gkanos
 ****************************************/
public class ExpressionFinder extends QuestionAnswerAdaptor<Integer, PExp>
{
	protected IInterpreterAssistantFactory af;

	public ExpressionFinder(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PExp caseAAssignmentDefinition(AAssignmentDefinition def,
			Integer lineno) throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(def.getExpression(), lineno);

	}

	@Override
	public PExp caseAClassInvariantDefinition(AClassInvariantDefinition def,
			Integer lineno) throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(def.getExpression(), lineno);

	}

	@Override
	public PExp defaultSClassDefinition(SClassDefinition def, Integer lineno)
			throws AnalysisException
	{
		return af.createSClassDefinitionAssistant().findExpression(def, lineno);
	}

	@Override
	public PExp caseAEqualsDefinition(AEqualsDefinition def, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(def.getTest(), lineno);

	}

	@Override
	public PExp caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition def, Integer lineno)
			throws AnalysisException
	{
		if (def.getPredef() != null)
		{
			PExp found = def.getPredef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getPostdef() != null)
		{
			PExp found = def.getPostdef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		return af.createPExpAssistant().findExpression(def.getBody(), lineno);

	}

	@Override
	public PExp caseAExplicitOperationDefinition(
			AExplicitOperationDefinition def, Integer lineno)
			throws AnalysisException
	{
		if (def.getPredef() != null)
		{
			PExp found = def.getPredef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getPostdef() != null)
		{
			PExp found = def.getPostdef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		return af.createPStmAssistant().findExpression(def.getBody(), lineno);
	}

	@Override
	public PExp caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition def, Integer lineno)
			throws AnalysisException
	{
		if (def.getPredef() != null)
		{
			PExp found = def.getPredef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getPostdef() != null)
		{
			PExp found = def.getPostdef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}
		return def.getBody() == null ? null
				: af.createPExpAssistant().findExpression(def.getBody(), lineno);
	}

	@Override
	public PExp caseAImplicitOperationDefinition(
			AImplicitOperationDefinition def, Integer lineno)
			throws AnalysisException
	{
		if (def.getPredef() != null)
		{
			PExp found = def.getPredef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getPostdef() != null)
		{
			PExp found = def.getPostdef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getErrors() != null)
		{
			for (AErrorCase err : def.getErrors())
			{
				PExp found = err.apply(THIS, lineno);// AErrorCaseAssistantInterpreter.findExpression(err, lineno);
				if (found != null)
				{
					return found;
				}
			}
		}

		return def.getBody() == null ? null
				: af.createPStmAssistant().findExpression(def.getBody(), lineno);
	}

	@Override
	public PExp caseAInstanceVariableDefinition(
			AInstanceVariableDefinition def, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(def.getExpression(), lineno);
	}

	@Override
	public PExp caseAPerSyncDefinition(APerSyncDefinition def, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(def.getGuard(), lineno);
	}

	@Override
	public PExp caseAStateDefinition(AStateDefinition def, Integer lineno)
			throws AnalysisException
	{
		if (def.getInvExpression() != null)
		{
			PExp found = af.createPExpAssistant().findExpression(def.getInvExpression(), lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getInitExpression() != null)
		{
			if (def.getInitExpression() instanceof AEqualsBinaryExp)
			{
				AEqualsBinaryExp ee = (AEqualsBinaryExp) def.getInitExpression();
				PExp found = af.createPExpAssistant().findExpression(ee.getRight(), lineno);
				if (found != null)
				{
					return found;
				}
			}
		}

		return null;
	}

	@Override
	public PExp caseAThreadDefinition(AThreadDefinition def, Integer lineno)
			throws AnalysisException
	{
		return af.createPStmAssistant().findExpression(def.getStatement(), lineno);
	}

	@Override
	public PExp caseATypeDefinition(ATypeDefinition def, Integer lineno)
			throws AnalysisException
	{
		if (def.getInvdef() != null)
		{
			PExp found = def.getInvdef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getEqRelation().getRelDef() != null)
		{
			PExp found = def.getEqRelation().getRelDef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getOrdRelation().getRelDef() != null)
		{
			PExp found = def.getOrdRelation().getRelDef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getOrdRelation().getMinDef() != null)
		{
			PExp found = def.getOrdRelation().getMinDef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (def.getOrdRelation().getMaxDef() != null)
		{
			PExp found = def.getOrdRelation().getMaxDef().apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		return null;
	}

	@Override
	public PExp caseAValueDefinition(AValueDefinition def, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(def.getExpression(), lineno);
	}

	@Override
	public PExp defaultPDefinition(PDefinition node, Integer question)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public PExp createNewReturnValue(INode node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PExp createNewReturnValue(Object node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
