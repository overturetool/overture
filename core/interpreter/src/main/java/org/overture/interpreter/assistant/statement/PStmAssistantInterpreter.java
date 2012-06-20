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
			case ALWAYS:
				return AAlwaysStmAssistantInterpreter.findExpression((AAlwaysStm)stm,lineno);
			case ASSIGNMENT:
				return AAssignmentStmAssistantInterpreter.findExpression((AAssignmentStm)stm,lineno);
			case ATOMIC:
				return AAtomicStmAssistantInterpreter.findExpression((AAtomicStm)stm,lineno);
			case CALL:
				return ACallStmAssistantInterpreter.findExpression((ACallStm)stm,lineno);
			case CALLOBJECT:
				return ACallObjectStatementAssistantInterpreter.findExpression((ACallObjectStm)stm,lineno);
			case CASES:
				return ACasesStmAssistantInterpreter.findExpression((ACasesStm)stm,lineno);
			case CYCLES:
				return ACyclesStmAssistantInterpreter.findExpression((ACyclesStm)stm,lineno);
			case DURATION:
				return ADurationStmAssistantInterpreter.findExpression((ADurationStm)stm,lineno);
			case ELSEIF:
				return AElseIfStmAssistantInterpreter.findExpression((AElseIfStm)stm, lineno);
			case EXIT:
				return AExitStmAssistantInterpreter.findExpression((AExitStm)stm,lineno);
			case FORALL:
				return AForAllStmAssistantInterpreter.findExpression((AForAllStm)stm,lineno);
			case FORINDEX:
				return AForIndexStmAssistantInterpreter.findExpression((AForIndexStm)stm,lineno);
			case FORPATTERNBIND:
				return AForPatternBindStmAssitantInterpreter.findExpression((AForPatternBindStm)stm,lineno);
			case IF:
				return AIfStmAssistantInterpreter.findExpression((AIfStm)stm,lineno);
			case LETBEST:
				return ALetBeStStmAssistantInterpreter.findExpression((ALetBeStStm)stm,lineno);
			case LETDEF:
				return SLetDefStmAssistantInterpreter.findExpression((SLetDefStm)stm,lineno);
			case RETURN:
				return AReturnStmAssistantInterpreter.findExpression((AReturnStm)stm,lineno);
			case SIMPLEBLOCK:
				return SSimpleBlockStmAssistantInterpreter.findExpression((SSimpleBlockStm)stm,lineno);
			case START:
				return AStartStmAssistantInterpreter.findExpression((AStartStm)stm,lineno);
			case TIXE:
				return ATixeStmAssistantInterpreter.findExpression((ATixeStm)stm,lineno);
			case TRAP:
				return ATrapStmAssistantInterpreter.findExpression((ATrapStm)stm,lineno);
			case WHILE:
				return AWhileStmAssistantInterpreter.findExpression((AWhileStm)stm,lineno);
			default:
				return null;
		}
		
	}

}
