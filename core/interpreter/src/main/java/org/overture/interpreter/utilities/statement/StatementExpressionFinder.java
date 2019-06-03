package org.overture.interpreter.utilities.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAnnotatedStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.AStopStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/***************************************
 * This method finds an Expression in a statement.
 * 
 * @author gkanos
 ****************************************/

public class StatementExpressionFinder extends
		QuestionAnswerAdaptor<Integer, PExp>
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public StatementExpressionFinder(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public PExp caseAAnnotatedStm(AAnnotatedStm node, Integer question)	throws AnalysisException
	{
		return node.getStmt().apply(THIS, question);
	}
	
	@Override
	public PExp caseAAlwaysStm(AAlwaysStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = stm.getAlways().apply(THIS, lineno);
		if (found != null)
		{
			return found;
		}

		return stm.getBody().apply(THIS, lineno);
	}

	@Override
	public PExp caseAAssignmentStm(AAssignmentStm stm, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(stm.getExp(), lineno);
	}

	@Override
	public PExp caseAAtomicStm(AAtomicStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = null;

		for (AAssignmentStm stmt : stm.getAssignments())
		{
			found = stmt.apply(THIS, lineno);
			if (found != null)
			{
				break;
			}
		}

		return found;
	}

	@Override
	public PExp caseACallStm(ACallStm stm, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(stm.getArgs(), lineno);
	}

	@Override
	public PExp caseACallObjectStm(ACallObjectStm stm, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(stm.getArgs(), lineno);
	}

	@Override
	public PExp caseACasesStm(ACasesStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = null;

		for (ACaseAlternativeStm stmt : stm.getCases())
		{
			found = stmt.getResult().apply(THIS, lineno);
			if (found != null)
			{
				break;
			}
		}

		return found;
	}

	@Override
	public PExp caseACyclesStm(ACyclesStm stm, Integer lineno)
			throws AnalysisException
	{
		return stm.getStatement().apply(THIS, lineno);
	}

	@Override
	public PExp caseADurationStm(ADurationStm stm, Integer lineno)
			throws AnalysisException
	{
		return stm.getStatement().apply(THIS, lineno);

	}

	@Override
	public PExp caseAElseIfStm(AElseIfStm stm, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(stm.getElseIf(), lineno);
	}

	@Override
	public PExp caseAExitStm(AExitStm stm, Integer lineno)
			throws AnalysisException
	{
		return stm.getExpression() == null ? null
				: af.createPExpAssistant().findExpression(stm.getExpression(), lineno);
	}

	@Override
	public PExp caseAForAllStm(AForAllStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = af.createPExpAssistant().findExpression(stm.getSet(), lineno);
		if (found != null)
		{
			return found;
		}
		return stm.getStatement().apply(THIS, lineno);
	}

	@Override
	public PExp caseAForIndexStm(AForIndexStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = af.createPExpAssistant().findExpression(stm.getFrom(), lineno);
		if (found != null)
		{
			return found;
		}
		found = stm.getTo().apply(THIS, lineno);
		if (found != null)
		{
			return found;
		}
		found = af.createPExpAssistant().findExpression(stm.getBy(), lineno);
		if (found != null)
		{
			return found;
		}
		return stm.getStatement().apply(THIS, lineno);
	}

	@Override
	public PExp caseAForPatternBindStm(AForPatternBindStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = af.createPExpAssistant().findExpression(stm.getExp(), lineno);
		if (found != null)
		{
			return found;
		}
		return stm.getStatement().apply(THIS, lineno);
	}

	@Override
	public PExp caseAIfStm(AIfStm stm, Integer lineno) throws AnalysisException
	{
		PExp found = stm.getThenStm().apply(THIS, lineno);
		if (found != null)
		{
			return found;
		}

		for (AElseIfStm stmt : stm.getElseIf())
		{
			found = stmt.apply(THIS, lineno);
			if (found != null)
			{
				return found;
			}
		}

		if (stm.getElseStm() != null)
		{
			found = stm.getElseStm().apply(THIS, lineno);
		}

		return found;
	}

	@Override
	public PExp caseALetBeStStm(ALetBeStStm stm, Integer lineno)
			throws AnalysisException
	{
		if (stm.getSuchThat() != null)
		{
			PExp found = af.createPExpAssistant().findExpression(stm.getSuchThat(), lineno);
			if (found != null)
			{
				return found;
			}
		}

		return stm.getStatement().apply(THIS, lineno);
	}

	@Override
	public PExp caseALetStm(ALetStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = af.createPDefinitionListAssistant().findExpression(stm.getLocalDefs(), lineno);
		if (found != null)
		{
			return found;
		}

		return stm.getStatement().apply(THIS, lineno);
	}

	@Override
	public PExp caseAReturnStm(AReturnStm stm, Integer lineno)
			throws AnalysisException
	{
		return stm.getExpression() == null ? null
				: af.createPExpAssistant().findExpression(stm.getExpression(), lineno);
	}

	@Override
	public PExp defaultSSimpleBlockStm(SSimpleBlockStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = null;

		for (PStm stmt : stm.getStatements())
		{
			found = stmt.apply(THIS, lineno);// PStmAssistantInterpreter.findExpression(stmt, lineno);
			if (found != null)
			{
				break;
			}
		}

		return found;
	}

	@Override
	public PExp caseAStartStm(AStartStm stm, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(stm.getObj(), lineno);
	}

	@Override
	public PExp caseAStopStm(AStopStm stm, Integer lineno)
			throws AnalysisException
	{
		return af.createPExpAssistant().findExpression(stm.getObj(), lineno);
	}

	@Override
	public PExp caseATixeStm(ATixeStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = stm.getBody().apply(THIS, lineno);// PStmAssistantInterpreter.findExpression(stm.getBody(),
														// lineno);
		if (found != null)
		{
			return found;
		}

		for (ATixeStmtAlternative tsa : stm.getTraps())
		{
			found = tsa.getStatement().apply(THIS, lineno);// PStmAssistantInterpreter.findExpression(tsa.getStatement(),
															// lineno);
			if (found != null)
			{
				break;
			}
		}

		return found;
	}

	@Override
	public PExp caseATrapStm(ATrapStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = stm.getBody().apply(THIS, lineno);// PStmAssistantInterpreter.findExpression(stm.getBody(),
														// lineno);
		if (found != null)
		{
			return found;
		}
		return stm.getWith().apply(THIS, lineno);// PStmAssistantInterpreter.findExpression(stm.getWith(), lineno);
	}

	@Override
	public PExp caseAWhileStm(AWhileStm stm, Integer lineno)
			throws AnalysisException
	{
		PExp found = af.createPExpAssistant().findExpression(stm.getExp(), lineno);
		if (found != null)
		{
			return found;
		}
		return stm.getStatement().apply(THIS, lineno);// PStmAssistantInterpreter.findExpression(stm.getStatement(),
														// lineno);
	}

	@Override
	public PExp defaultPStm(PStm stm, Integer lineno) throws AnalysisException
	{
		return null;
	}
	
	@Override
	public PExp defaultSUnaryExp(SUnaryExp node, Integer question) throws AnalysisException
	{
		return node.getExp().apply(THIS, question);
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
