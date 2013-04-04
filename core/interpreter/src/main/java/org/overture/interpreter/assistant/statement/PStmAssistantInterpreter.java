package org.overture.interpreter.assistant.statement;

import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.AAtomicStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
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
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.AStartStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SLetDefStm;
import org.overture.ast.statements.SSimpleBlockStm;

public class PStmAssistantInterpreter
{

	public static PExp findExpression(PStm stm, int lineno)
	{
		switch (stm.kindPStm())
		{
			case AAlwaysStm.kindPStm:
				return AAlwaysStmAssistantInterpreter.findExpression((AAlwaysStm)stm,lineno);
			case AAssignmentStm.kindPStm:
				return AAssignmentStmAssistantInterpreter.findExpression((AAssignmentStm)stm,lineno);
			case AAtomicStm.kindPStm:
				return AAtomicStmAssistantInterpreter.findExpression((AAtomicStm)stm,lineno);
			case ACallStm.kindPStm:
				return ACallStmAssistantInterpreter.findExpression((ACallStm)stm,lineno);
			case ACallObjectStm.kindPStm:
				return ACallObjectStatementAssistantInterpreter.findExpression((ACallObjectStm)stm,lineno);
			case ACasesStm.kindPStm:
				return ACasesStmAssistantInterpreter.findExpression((ACasesStm)stm,lineno);
			case ACyclesStm.kindPStm:
				return ACyclesStmAssistantInterpreter.findExpression((ACyclesStm)stm,lineno);
			case ADurationStm.kindPStm:
				return ADurationStmAssistantInterpreter.findExpression((ADurationStm)stm,lineno);
			case AElseIfStm.kindPStm:
				return AElseIfStmAssistantInterpreter.findExpression((AElseIfStm)stm, lineno);
			case AExitStm.kindPStm:
				return AExitStmAssistantInterpreter.findExpression((AExitStm)stm,lineno);
			case AForAllStm.kindPStm:
				return AForAllStmAssistantInterpreter.findExpression((AForAllStm)stm,lineno);
			case AForIndexStm.kindPStm:
				return AForIndexStmAssistantInterpreter.findExpression((AForIndexStm)stm,lineno);
			case AForPatternBindStm.kindPStm:
				return AForPatternBindStmAssitantInterpreter.findExpression((AForPatternBindStm)stm,lineno);
			case AIfStm.kindPStm:
				return AIfStmAssistantInterpreter.findExpression((AIfStm)stm,lineno);
			case ALetBeStStm.kindPStm:
				return ALetBeStStmAssistantInterpreter.findExpression((ALetBeStStm)stm,lineno);
			case SLetDefStm.kindPStm:
				return SLetDefStmAssistantInterpreter.findExpression((SLetDefStm)stm,lineno);
			case AReturnStm.kindPStm:
				return AReturnStmAssistantInterpreter.findExpression((AReturnStm)stm,lineno);
			case SSimpleBlockStm.kindPStm:
				return SSimpleBlockStmAssistantInterpreter.findExpression((SSimpleBlockStm)stm,lineno);
			case AStartStm.kindPStm:
				return AStartStmAssistantInterpreter.findExpression((AStartStm)stm,lineno);
			case ATixeStm.kindPStm:
				return ATixeStmAssistantInterpreter.findExpression((ATixeStm)stm,lineno);
			case ATrapStm.kindPStm:
				return ATrapStmAssistantInterpreter.findExpression((ATrapStm)stm,lineno);
			case AWhileStm.kindPStm:
				return AWhileStmAssistantInterpreter.findExpression((AWhileStm)stm,lineno);
			default:
				return null;
		}
		
	}

	/**
	 * Find a statement starting on the given line. Single statements just
	 * compare their location to lineno, but block statements and statements
	 * with sub-statements iterate over their branches.
	 *
	 * @param lineno The line number to locate.
	 * @return A statement starting on the line, or null.
	 */
	public static PStm findStatement(PStm stm, int lineno)
	{
		switch (stm.kindPStm())
		{
			case AAlwaysStm.kindPStm:
				return AAlwaysStmAssistantInterpreter.findStatement((AAlwaysStm)stm,lineno);
			case AAtomicStm.kindPStm:
				return AAtomicStmAssistantInterpreter.findStatement((AAtomicStm)stm,lineno);
			case ACasesStm.kindPStm:
				return ACasesStmAssistantInterpreter.findStatement((ACasesStm)stm,lineno);
			case ACyclesStm.kindPStm:
				return ACyclesStmAssistantInterpreter.findStatement((ACyclesStm)stm,lineno);
			case ADurationStm.kindPStm:
				return ADurationStmAssistantInterpreter.findStatement((ADurationStm)stm,lineno);
			case AElseIfStm.kindPStm:
				return AElseIfStmAssistantInterpreter.findStatement((AElseIfStm)stm,lineno);
			case AForAllStm.kindPStm:
				return AForAllStmAssistantInterpreter.findStatement((AForAllStm)stm,lineno);
			case AForIndexStm.kindPStm:
				return AForIndexStmAssistantInterpreter.findStatement((AForIndexStm)stm,lineno);
			case AForPatternBindStm.kindPStm:
				return AForPatternBindStmAssitantInterpreter.findStatement((AForPatternBindStm)stm,lineno);
			case AIfStm.kindPStm:
				return AIfStmAssistantInterpreter.findStatement((AIfStm)stm,lineno);
			case ALetBeStStm.kindPStm:
				return ALetBeStStmAssistantInterpreter.findStatement((ALetBeStStm)stm,lineno);
			case SLetDefStm.kindPStm:
				return SLetDefStmAssistantInterpreter.findStatement((SLetDefStm)stm,lineno);
			case SSimpleBlockStm.kindPStm:
				return SSimpleBlockStmAssistantInterpreter.findStatement((SSimpleBlockStm)stm,lineno);
			case ATixeStm.kindPStm:
				return ATixeStmAssistantInterpreter.findStatement((ATixeStm)stm,lineno);
			case ATrapStm.kindPStm:
				return ATrapStmAssistantInterpreter.findStatement((ATrapStm)stm,lineno);
			case AWhileStm.kindPStm:
				return AWhileStmAssistantInterpreter.findStatement((AWhileStm)stm,lineno);
			default:
				return findStatementBaseCase(stm, lineno);
		}
	}
	
	public static PStm findStatementBaseCase(PStm stm, int lineno)
	{
		return (stm.getLocation().startLine == lineno) ? stm : null;
	}
}
