package org.overture.ast.statements.assistants;

import java.util.Collection;

import org.overture.ast.statements.ATixeStm;
import org.overture.ast.statements.ATixeStmtAlternative;
import org.overture.ast.types.PType;
import org.overture.ast.utils.PTypeSet;

public class ATixeStmAssistantTC {

	public static PTypeSet exitCheck(ATixeStm statement) {
		
		PTypeSet types = new PTypeSet();
		types.addAll(PStmAssistantTC.exitCheck(statement.getBody()));

		for (ATixeStmtAlternative tsa: statement.getTraps())
		{
			types.addAll(exitCheck(tsa));
		}

		return types;
	}

	private static Collection<? extends PType> exitCheck(
			ATixeStmtAlternative tsa) {
		return PStmAssistantTC.exitCheck(tsa.getStatement());
	}

}
