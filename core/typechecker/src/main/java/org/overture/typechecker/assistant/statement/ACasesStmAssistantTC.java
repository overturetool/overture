package org.overture.typechecker.assistant.statement;

import org.overture.ast.statements.ACaseAlternativeStm;
import org.overture.ast.statements.ACasesStm;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;


public class ACasesStmAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACasesStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static PTypeSet exitCheck(ACasesStm statement) {
		PTypeSet types = new PTypeSet();

		for (ACaseAlternativeStm c: statement.getCases())
		{
			types.addAll(ACaseAlternativeStmAssistantTC.exitCheck(c));
		}

		return types;
	}

}
