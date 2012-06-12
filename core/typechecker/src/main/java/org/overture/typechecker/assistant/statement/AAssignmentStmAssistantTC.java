package org.overture.typechecker.assistant.statement;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.util.PTypeSet;


public class AAssignmentStmAssistantTC {

	public static PTypeSet exitCheck(AAssignmentStm statement) {
		
		// TODO We don't know what an expression call will raise
		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()));

	}

}
