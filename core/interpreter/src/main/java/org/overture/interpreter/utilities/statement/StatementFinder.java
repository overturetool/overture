package org.overture.interpreter.utilities.statement;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.ACyclesStm;
import org.overture.ast.statements.ADurationStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.ALetStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.definition.PDefinitionAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AAlwaysStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AAtomicStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACasesStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ACyclesStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ADurationStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AElseIfStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForAllStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForIndexStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AForPatternBindStmAssitantInterpreter;
import org.overture.interpreter.assistant.statement.AIfStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ALetBeStStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ATixeStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.ATrapStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.AWhileStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.PStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.SLetDefStmAssistantInterpreter;
import org.overture.interpreter.assistant.statement.SSimpleBlockStmAssistantInterpreter;

/***************************************
 * 
 * This method finds a statement type in a model. 
 * 
 * @author gkanos
 *
 ****************************************/
public class StatementFinder extends QuestionAnswerAdaptor<Integer, PStm>
{
	protected static IInterpreterAssistantFactory af;

	@SuppressWarnings("static-access")
	public StatementFinder(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PStm caseAAlwaysStm(AAlwaysStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AAlwaysStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		found = stm.getAlways().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getAlways(), lineno);
		if (found != null)
			return found;
		return stm.getBody().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getBody(), lineno);
	}
	
	@Override
	public PStm caseAAtomicStm(AAtomicStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AAtomicStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;

		for (AAssignmentStm stmt : stm.getAssignments())
		{
			found = stmt.apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stmt, lineno);
			if (found != null)
				break;
		}

		return found;
	}
	
	@Override
	public PStm caseACasesStm(ACasesStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ACasesStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;

		for (ACaseAlternativeStm stmt : stm.getCases())
		{
			found = stmt.getResult().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stmt.getResult(), lineno);
			if (found != null)
				break;
		}

		return found;
	}
	
	@Override
	public PStm caseACyclesStm(ACyclesStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ACyclesStmAssistantInterpreter.findStatement(stm, lineno);
		//return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
	}
	
	@Override
	public PStm caseADurationStm(ADurationStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ADurationStmAssistantInterpreter.findStatement(stm, lineno);
		//return PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
		return stm.getStatement().apply(THIS, lineno);
	}
	
	@Override
	public PStm caseAElseIfStm(AElseIfStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AElseIfStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		return stm.getThenStm().apply(THIS,lineno);//PStmAssistantInterpreter.findStatement(stm.getThenStm(), lineno);
	}
	
	@Override
	public PStm caseAForAllStm(AForAllStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AForAllStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		return stm.apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}
	
	@Override
	public PStm caseAForIndexStm(AForIndexStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AForIndexStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		return stm.getStatement().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}
	
	@Override
	public PStm caseAForPatternBindStm(AForPatternBindStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AForPatternBindStmAssitantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		return stm.getStatement().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}
	
	@Override
	public PStm caseAIfStm(AIfStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AIfStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found =af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		found = stm.getThenStm().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getThenStm(), lineno);
		if (found != null)
			return found;

		for (AElseIfStm stmt : stm.getElseIf())
		{
			found = stmt.apply(THIS, lineno);//af.createPStmAssistant().findStatement(stmt, lineno);
			if (found != null)
				return found;
		}

		if (stm.getElseStm() != null)
		{
			found = stm.getElseStm().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getElseStm(), lineno);
		}

		return found;
	}
	@Override
	public PStm caseALetBeStStm(ALetBeStStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ALetBeStStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		return stm.getStatement().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}
	
	@Override
	public PStm caseALetStm(ALetStm stm, Integer lineno)
			throws AnalysisException
	{
		//return SLetDefStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;

		found = af.createPDefinitionAssistant().findStatement(stm.getLocalDefs(), lineno);
		if (found != null)
			return found;

		return stm.getStatement().apply(THIS, lineno); //StmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}

	@Override
	public PStm defaultSSimpleBlockStm(SSimpleBlockStm stm, Integer lineno)
			throws AnalysisException
	{
		//return SSimpleBlockStmAssistantInterpreter.findStatement(stm, lineno);
		if (stm.getLocation().getStartLine() == lineno)
			return stm;
		PStm found = null;

		for (PStm stmt : stm.getStatements())
		{
			found = stmt.apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stmt, lineno);
			if (found != null)
				break;
		}

		return found;
	}
	
	@Override
	public PStm caseATixeStm(ATixeStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ATixeStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		found = stm.getBody().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getBody(), lineno);
		if (found != null)
			return found;

		for (ATixeStmtAlternative tsa : stm.getTraps())
		{
			found = tsa.getStatement().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(tsa.getStatement(), lineno);
			if (found != null)
				break;
		}

		return found;
	}
	
	@Override
	public PStm caseATrapStm(ATrapStm stm, Integer lineno)
			throws AnalysisException
	{
		//return ATrapStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		found = stm.getBody().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getBody(), lineno);
		if (found != null)
			return found;
		return stm.getWith().apply(THIS, lineno);//PStmAssistantInterpreter.findStatement(stm.getWith(), lineno);
	}
	
	@Override
	public PStm caseAWhileStm(AWhileStm stm, Integer lineno)
			throws AnalysisException
	{
		//return AWhileStmAssistantInterpreter.findStatement(stm, lineno);
		PStm found = af.createPStmAssistant().findStatementBaseCase(stm, lineno);
		if (found != null)
			return found;
		return stm.getStatement().apply(THIS, lineno); //PStmAssistantInterpreter.findStatement(stm.getStatement(), lineno);
	}
	
	@Override
	public PStm defaultPStm(PStm stm, Integer lineno)
			throws AnalysisException
	{
		return af.createPStmAssistant().findStatementBaseCase(stm, lineno);
	}
	
	@Override
	public PStm createNewReturnValue(INode node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PStm createNewReturnValue(Object node, Integer question)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
