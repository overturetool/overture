/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.assistant.statement;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PStateDesignatorAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public PStateDesignatorAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public PDefinition targetDefinition(PStateDesignator pStateDesignator,
			TypeCheckInfo question)
	{
		if (pStateDesignator instanceof AIdentifierStateDesignator)
		{
			AIdentifierStateDesignator stateDesignator = (AIdentifierStateDesignator) pStateDesignator;
			return question.env.findName(stateDesignator.getName(), NameScope.STATE);
		} else
		{
			return null;
		}

	}
}
