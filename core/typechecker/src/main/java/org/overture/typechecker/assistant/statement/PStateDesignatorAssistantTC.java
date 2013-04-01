package org.overture.typechecker.assistant.statement;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.TypeCheckInfo;


public class PStateDesignatorAssistantTC {

	public static PDefinition targetDefinition(PStateDesignator pStateDesignator, TypeCheckInfo question) {
		if (AIdentifierStateDesignator.kindPStateDesignator.equals(pStateDesignator.kindPStateDesignator()))
		{
			AIdentifierStateDesignator stateDesignator = (AIdentifierStateDesignator) pStateDesignator;
			return question.env.findName(stateDesignator.getName(),NameScope.STATE);
		} else {
			return null;
		}

	}
}
