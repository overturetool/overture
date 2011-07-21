package org.overture.ast.statements.assistants;

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
import org.overture.ast.types.assistants.PTypeSet;

public class PStmAssistant {

	public static PTypeSet exitCheck(PStm statement) {
		
		switch (statement.kindPStm()) {		
		case ALWAYS:
			return AAlwaysStmAssistant.exitCheck((AAlwaysStm)statement);
		case ASSIGNMENT:
			return AAssignmentStmAssistant.exitCheck((AAssignmentStm)statement);		
		case CALL:
			return ACallStmAssistant.exitCheck((ACallStm)statement);
		case CALLOBJECT:
			return ACallObjectStatementAssistant.exitCheck((ACallObjectStm)statement);
		case CASES:
			return ACasesStmAssistant.exitCheck((ACasesStm)statement);
		case ELSEIF:
			return AElseIfStmAssistant.exitCheck((AElseIfStm)statement);		
		case EXIT:
			return AExitStmAssistant.exitCheck((AExitStm)statement);
		case FORALL:
			return AForAllStmAssistant.exitCheck((AForAllStm)statement);
		case FORINDEX:
			return AForIndexStmAssistant.exitCheck((AForIndexStm)statement);
		case FORPATTERNBIND:
			return AForPatternBindStmAssitant.exitCheck((AForPatternBindStm)statement);
		case IF:
			return AIfStmAssistant.exitCheck((AIfStm)statement);
		case LETBEST:
			return ALetBeStStmAssistant.exitCheck((ALetBeStStm)statement);
		case LETDEF:
			return SLetDefStmAssistant.exitCheck((SLetDefStm)statement);		
		case RETURN:
			return AReturnStmAssistant.exitCheck((AReturnStm)statement);
		case SIMPLEBLOCK:
			return SSimpleBlockStmAssistant.exitCheck((SSimpleBlockStm)statement);
		case TIXE:
			return ATixeStmAssistant.exitCheck((ATixeStm)statement);
		case TRAP:
			return ATrapStmAssistant.exitCheck((ATrapStm)statement);
		case WHILE:
			return AWhileStmAssistant.exitCheck((AWhileStm)statement);
		default:
			return exitCheckBaseCase(statement);
		}
	}
	
	public static PTypeSet exitCheckBaseCase(PStm statement)
	{
		return new PTypeSet();
	}
	

}
