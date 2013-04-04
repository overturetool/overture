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
		case AAlwaysStm.kindPStm:
			return AAlwaysStmAssistantTC.exitCheck((AAlwaysStm)statement);
		case AAssignmentStm.kindPStm:
			return AAssignmentStmAssistantTC.exitCheck((AAssignmentStm)statement);		
		case ACallStm.kindPStm:
			return ACallStmAssistantTC.exitCheck((ACallStm)statement);
		case ACallObjectStm.kindPStm:
			return ACallObjectStatementAssistantTC.exitCheck((ACallObjectStm)statement);
		case ACasesStm.kindPStm:
			return ACasesStmAssistantTC.exitCheck((ACasesStm)statement);
		case AElseIfStm.kindPStm:
			return AElseIfStmAssistantTC.exitCheck((AElseIfStm)statement);		
		case AExitStm.kindPStm:
			return AExitStmAssistantTC.exitCheck((AExitStm)statement);
		case AForAllStm.kindPStm:
			return AForAllStmAssistantTC.exitCheck((AForAllStm)statement);
		case AForIndexStm.kindPStm:
			return AForIndexStmAssistantTC.exitCheck((AForIndexStm)statement);
		case AForPatternBindStm.kindPStm:
			return AForPatternBindStmAssitantTC.exitCheck((AForPatternBindStm)statement);
		case AIfStm.kindPStm:
			return AIfStmAssistantTC.exitCheck((AIfStm)statement);
		case ALetBeStStm.kindPStm:
			return ALetBeStStmAssistantTC.exitCheck((ALetBeStStm)statement);
		case SLetDefStm.kindPStm:
			return SLetDefStmAssistantTC.exitCheck((SLetDefStm)statement);		
		case AReturnStm.kindPStm:
			return AReturnStmAssistantTC.exitCheck((AReturnStm)statement);
		case SSimpleBlockStm.kindPStm:
			return SSimpleBlockStmAssistantTC.exitCheck((SSimpleBlockStm)statement);
		case ATixeStm.kindPStm:
			return ATixeStmAssistantTC.exitCheck((ATixeStm)statement);
		case ATrapStm.kindPStm:
			return ATrapStmAssistantTC.exitCheck((ATrapStm)statement);
		case AWhileStm.kindPStm:
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
