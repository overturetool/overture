package org.overture.ast.statements.assistants;

import java.util.Collection;

import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeSet;

public class ATixeStmAssistant {

	public static PTypeSet exitCheck(ATixeStm statement) {
		
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistant.exitCheck(statement.getBody()));

		for (ATixeStmtAlternative tsa: statement.getTraps())
		{
			types.addAll(exitCheck(tsa));
		}

		return types;
	}

	private static Collection<? extends PType> exitCheck(
			ATixeStmtAlternative tsa) {
		return PStmAssistant.exitCheck(tsa.getStatement());
	}

}
