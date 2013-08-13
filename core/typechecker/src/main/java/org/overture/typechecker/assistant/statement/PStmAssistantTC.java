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
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public PStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PTypeSet exitCheck(PStm statement) {
		if (statement instanceof AAlwaysStm) {
			return AAlwaysStmAssistantTC.exitCheck((AAlwaysStm)statement);
		} else if (statement instanceof AAssignmentStm) {
			return AAssignmentStmAssistantTC.exitCheck((AAssignmentStm)statement);
		} else if (statement instanceof ACallStm) {
			return ACallStmAssistantTC.exitCheck((ACallStm)statement);
		} else if (statement instanceof ACallObjectStm) {
			return ACallObjectStatementAssistantTC.exitCheck((ACallObjectStm)statement);
		} else if (statement instanceof ACasesStm) {
			return ACasesStmAssistantTC.exitCheck((ACasesStm)statement);
		} else if (statement instanceof AElseIfStm) {
			return AElseIfStmAssistantTC.exitCheck((AElseIfStm)statement);
		} else if (statement instanceof AExitStm) {
			return AExitStmAssistantTC.exitCheck((AExitStm)statement);
		} else if (statement instanceof AForAllStm) {
			return AForAllStmAssistantTC.exitCheck((AForAllStm)statement);
		} else if (statement instanceof AForIndexStm) {
			return AForIndexStmAssistantTC.exitCheck((AForIndexStm)statement);
		} else if (statement instanceof AForPatternBindStm) {
			return AForPatternBindStmAssitantTC.exitCheck((AForPatternBindStm)statement);
		} else if (statement instanceof AIfStm) {
			return AIfStmAssistantTC.exitCheck((AIfStm)statement);
		} else if (statement instanceof ALetBeStStm) {
			return ALetBeStStmAssistantTC.exitCheck((ALetBeStStm)statement);
		} else if (statement instanceof SLetDefStm) {
			return SLetDefStmAssistantTC.exitCheck((SLetDefStm)statement);
		} else if (statement instanceof AReturnStm) {
			return AReturnStmAssistantTC.exitCheck((AReturnStm)statement);
		} else if (statement instanceof SSimpleBlockStm) {
			return SSimpleBlockStmAssistantTC.exitCheck((SSimpleBlockStm)statement);
		} else if (statement instanceof ATixeStm) {
			return ATixeStmAssistantTC.exitCheck((ATixeStm)statement);
		} else if (statement instanceof ATrapStm) {
			return ATrapStmAssistantTC.exitCheck((ATrapStm)statement);
		} else if (statement instanceof AWhileStm) {
			return AWhileStmAssistantTC.exitCheck((AWhileStm)statement);
		} else {
			return exitCheckBaseCase(statement);
		}
	}
	
	public static PTypeSet exitCheckBaseCase(PStm statement)
	{
		return new PTypeSet();
	}
	

}
