package org.overture.interpreter.utilities.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AAlwaysStm;
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
import org.overture.interpreter.assistant.definition.PDefinitionListAssistantInterpreter;
import org.overture.interpreter.assistant.expression.PExpAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AAlwaysStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AAssignmentStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AAtomicStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACallObjectStatementAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACallStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACasesStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACyclesStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ADurationStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AElseIfStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AExitStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForAllStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForIndexStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForPatternBindStmAssitantInterpreter;
import org.overture.interpreter.assistant.statement.AIfStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ALetBeStStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AReturnStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AStartStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ATixeStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ATrapStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AWhileStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.SLetDefStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.SSimpleBlockStmAssistantInterpreter;

/***************************************
 * 
 * This method finds an Expression in a statement. 
 * 
 * @author gkanos
 *
 ****************************************/

public class StatementExpressionFinder extends QuestionAnswerAdaptor<Integer, PExp>
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public StatementExpressionFinder(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PExp caseAAlwaysStm(AAlwaysStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AAlwaysStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = stm.getAlways().apply(THIS, lineno); //PStmAssistantInterpreter.findExpression(stm.getAlways(), lineno);
		if (found != null)
			return found;
		//return PStmAssistantInterpreter.findExpression(stm.getBody(), lineno);
		return stm.getBody().apply(THIS, lineno);
	}
	
	@Override
	public PExp caseAAssignmentStm(AAssignmentStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AAssignmentStmAssistantInterpreter.findExpression(stm, lineno);
		return af.createPExpAssistant().findExpression(stm.getExp(), lineno);
	}
	
	@Override
	public PExp caseAAtomicStm(AAtomicStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AAtomicStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = null;

		for (AAssignmentStm stmt : stm.getAssignments())
		{
			//found = AAssignmentStmAssistantInterpreter.findExpression(stmt, lineno);
			found = stmt.apply(THIS, lineno);
			if (found != null)
				break;
		}
		
		return found;
	}
	
	@Override
	public PExp caseACallStm(ACallStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ACallStmAssistantInterpreter.findExpression(stm, lineno);
		return af.createPExpAssistant().findExpression(stm.getArgs(), lineno);
	}
	
	@Override
	public PExp caseACallObjectStm(ACallObjectStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ACallObjectStatementAssistantInterpreter.findExpression(stm, lineno);
		return af.createPExpAssistant().findExpression(stm.getArgs(), lineno);
	}
	
	@Override
	public PExp caseACasesStm(ACasesStm stm, Integer lineno)
			throws AnalysisException
	{
		///return ACasesStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = null;

		for (ACaseAlternativeStm stmt : stm.getCases())
		{
			//found = PStmAssistantInterpreter.findExpression(stmt.getResult(), lineno);
			found = stmt.getResult().apply(THIS, lineno);
			if (found != null)
				break;
		}

		return found;
	}

	@Override
	public PExp caseACyclesStm(ACyclesStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ACyclesStmAssistantInterpreter.findExpression(stm, lineno);
		//return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
	}
	
	@Override
	public PExp caseADurationStm(ADurationStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ADurationStmAssistantInterpreter.findExpression(stm, lineno);
		//return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
		
	}
	
	@Override
	public PExp caseAElseIfStm(AElseIfStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AElseIfStmAssistantInterpreter.findExpression(stm, lineno);
		return af.createPExpAssistant().findExpression(stm.getElseIf(), lineno);
	}
	@Override
	public PExp caseAExitStm(AExitStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AExitStmAssistantInterpreter.findExpression(stm, lineno);
		return stm.getExpression() == null ? null
				: af.createPExpAssistant().findExpression(stm.getExpression(), lineno);
	}
	
	@Override
	public PExp caseAForAllStm(AForAllStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AForAllStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = af.createPExpAssistant().findExpression(stm.getSet(), lineno);
		//PExp found = stm.getSet().apply(THIS, lineno);
		if (found != null)
			return found;
		//return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
	}
	
	@Override
	public PExp caseAForIndexStm(AForIndexStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AForIndexStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = af.createPExpAssistant().findExpression(stm.getFrom(), lineno);
		if (found != null)
			return found;
		//found = PExpAssistantInterpreter.findExpression(stm.getTo(), lineno);
		found = stm.getTo().apply(THIS, lineno);
		if (found != null)
			return found;
		found = af.createPExpAssistant().findExpression(stm.getBy(), lineno);
		if (found != null)
			return found;
		//return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
	}
	
	@Override
	public PExp caseAForPatternBindStm(AForPatternBindStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AForPatternBindStmAssitantInterpreter.findExpression(stm, lineno);
		PExp found = af.createPExpAssistant().findExpression(stm.getExp(), lineno);
		//PExp found = stm.getExp().apply(THIS, lineno);
		if (found != null)
			return found;
		//return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
	}
	
	@Override
	public PExp caseAIfStm(AIfStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AIfStmAssistantInterpreter.findExpression(stm, lineno);
		//PExp found = PStmAssistantInterpreter.findExpression(stm.getThenStm(), lineno);
		PExp found = stm.getThenStm().apply(THIS, lineno);
		if (found != null)
			return found;

		for (AElseIfStm stmt : stm.getElseIf())
		{
			//found = AElseIfStmAssistantInterpreter.findExpression(stmt, lineno);
			found = stmt.apply(THIS, lineno);
			if (found != null)
				return found;
		}

		if (stm.getElseStm() != null)
		{
			//found = PStmAssistantInterpreter.findExpression(stm.getElseStm(), lineno);
			found = stm.getElseStm().apply(THIS, lineno);
		}

		return found;
	}

	@Override
	public PExp caseALetBeStStm(ALetBeStStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ALetBeStStmAssistantInterpreter.findExpression(stm, lineno);
		if (stm.getSuchThat() != null)
		{
			PExp found = af.createPExpAssistant().findExpression(stm.getSuchThat(), lineno);
			if (found != null)
				return found;
		}

		//return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
	}
	
	@Override
	public PExp caseALetStm(ALetStm stm, Integer lineno)
			throws AnalysisException
	{
		//return SLetDefStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = af.createPDefinitionListAssistant().findExpression(stm.getLocalDefs(), lineno);
		if (found != null)
			return found;

		//return PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
	}
	
	@Override
	public PExp caseAReturnStm(AReturnStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AReturnStmAssistantInterpreter.findExpression(stm, lineno);
		return stm.getExpression() == null ? null
				: af.createPExpAssistant().findExpression(stm.getExpression(), lineno);
	}
	
	@Override
	public PExp defaultSSimpleBlockStm(SSimpleBlockStm stm, Integer lineno)
			throws AnalysisException
	{
		//return SSimpleBlockStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = null;

		for (PStm stmt : stm.getStatements())
		{
			found = stmt.apply(THIS, lineno);//PStmAssistantInterpreter.findExpression(stmt, lineno);
			if (found != null)
				break;
		}

		return found;
	}
	
	@Override
	public PExp caseAStartStm(AStartStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AStartStmAssistantInterpreter.findExpression(stm, lineno);
		return af.createPExpAssistant().findExpression(stm.getObj(), lineno);
	}
	
	@Override
	public PExp caseAStopStm(AStopStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AStartStmAssistantInterpreter.findExpression(stm, lineno);
		return af.createPExpAssistant().findExpression(stm.getObj(), lineno);
	}
	
	@Override
	public PExp caseATixeStm(ATixeStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ATixeStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = stm.getBody().apply(THIS, lineno);//PStmAssistantInterpreter.findExpression(stm.getBody(), lineno);
		if (found != null)
			return found;

		for (ATixeStmtAlternative tsa : stm.getTraps())
		{
			found =  tsa.getStatement().apply(THIS, lineno);//PStmAssistantInterpreter.findExpression(tsa.getStatement(), lineno);
			if (found != null)
				break;
		}

		return found;
	}
	
	@Override
	public PExp caseATrapStm(ATrapStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ATrapStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = stm.getBody().apply(THIS, lineno);//PStmAssistantInterpreter.findExpression(stm.getBody(), lineno);
		if (found != null)
			return found;
		return stm.getWith().apply(THIS, lineno);//PStmAssistantInterpreter.findExpression(stm.getWith(), lineno);
	}
	
	@Override
	public PExp caseAWhileStm(AWhileStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AWhileStmAssistantInterpreter.findExpression(stm, lineno);
		PExp found = af.createPExpAssistant().findExpression(stm.getExp(), lineno);
		if (found != null)
			return found;
		return  stm.getStatement().apply(THIS, lineno);//PStmAssistantInterpreter.findExpression(stm.getStatement(), lineno);
	}

	@Override
	public PExp defaultPStm(PStm stm, Integer lineno)
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
