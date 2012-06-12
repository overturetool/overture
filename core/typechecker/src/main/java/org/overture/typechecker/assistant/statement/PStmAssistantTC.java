package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.AAlwaysStm;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.statements.AElseIfStm;
import org.overture.ast.statements.AExitStm;
import org.overture.ast.statements.AForAllStm;
import org.overture.ast.statements.AForIndexStm;
import org.overture.ast.statements.AForPatternBindStm;
import org.overture.ast.statements.AIfStm;
import org.overture.ast.statements.ALetBeStStm;
import org.overture.ast.statements.AReturnStm;
import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATrapStm;
import org.overture.ast.statements.AWhileStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.statements.SLetDefStm;
import org.overture.ast.statements.SSimpleBlockStm;
import org.overture.ast.util.PTypeSet;

public class PStmAssistantTC {

	public static PTypeSet exitCheck(PStm statement) {
		
		switch (statement.kindPStm()) {		
		case ALWAYS:
			return AAlwaysStmAssistantTC.exitCheck((AAlwaysStm)statement);
		case ASSIGNMENT:
			return AAssignmentStmAssistantTC.exitCheck((AAssignmentStm)statement);		
		case CALL:
			return ACallStmAssistantTC.exitCheck((ACallStm)statement);
		case CALLOBJECT:
			return ACallObjectStatementAssistantTC.exitCheck((ACallObjectStm)statement);
		case CASES:
			return ACasesStmAssistantTC.exitCheck((ACasesStm)statement);
		case ELSEIF:
			return AElseIfStmAssistantTC.exitCheck((AElseIfStm)statement);		
		case EXIT:
			return AExitStmAssistantTC.exitCheck((AExitStm)statement);
		case FORALL:
			return AForAllStmAssistantTC.exitCheck((AForAllStm)statement);
		case FORINDEX:
			return AForIndexStmAssistantTC.exitCheck((AForIndexStm)statement);
		case FORPATTERNBIND:
			return AForPatternBindStmAssitantTC.exitCheck((AForPatternBindStm)statement);
		case IF:
			return AIfStmAssistantTC.exitCheck((AIfStm)statement);
		case LETBEST:
			return ALetBeStStmAssistantTC.exitCheck((ALetBeStStm)statement);
		case LETDEF:
			return SLetDefStmAssistantTC.exitCheck((SLetDefStm)statement);		
		case RETURN:
			return AReturnStmAssistantTC.exitCheck((AReturnStm)statement);
		case SIMPLEBLOCK:
			return SSimpleBlockStmAssistantTC.exitCheck((SSimpleBlockStm)statement);
		case TIXE:
			return ATixeStmAssistantTC.exitCheck((ATixeStm)statement);
		case TRAP:
			return ATrapStmAssistantTC.exitCheck((ATrapStm)statement);
		case WHILE:
			return AWhileStmAssistantTC.exitCheck((AWhileStm)statement);
		default:
			return exitCheckBaseCase(statement);
		}
	}
	
	public static PTypeSet exitCheckBaseCase(PStm statement)
	{
		return new PTypeSet();
	}
	

}
