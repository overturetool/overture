package org.overture.ast.statements.assistants;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.EStateDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmjV2.typechecker.NameScope;


public class PStateDesignatorAssistant {

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