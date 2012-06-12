package org.overture.typechecker.assistant.statement;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.EStateDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.TypeCheckInfo;


public class PStateDesignatorAssistantTC {

	public static PDefinition targetDefinition(PStateDesignator pStateDesignator, TypeCheckInfo question) {
		if (pStateDesignator.kindPStateDesignator() == EStateDesignator.IDENTIFIER) 
		{
			AIdentifierStateDesignator stateDesignator = (AIdentifierStateDesignator) pStateDesignator;
			return question.env.findName(stateDesignator.getName(),NameScope.STATE);
		} else {
			return null;
		}

	}
}
