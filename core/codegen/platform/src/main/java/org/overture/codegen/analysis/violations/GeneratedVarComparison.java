/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.analysis.violations;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SFunctionDefinition;
import org.overture.ast.definitions.SOperationDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.codegen.ir.IRInfo;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class GeneratedVarComparison extends NamingComparison
{
	public GeneratedVarComparison(String[] names,
			IRInfo info, String correctionPrefix)
	{
		super(names, info, correctionPrefix);
	}

	@Override
	public boolean mustHandleNameToken(ILexNameToken nameToken)
	{
		PDefinitionAssistantTC defAssistant = irInfo.getTcFactory().createPDefinitionAssistant();
		
		if (irInfo.getAssistantManager().getTypeAssistant().getTypeDef(nameToken, defAssistant) != null)
		{
			return false;
		}

		PDefinition def = nameToken.getAncestor(PDefinition.class);

		if (def instanceof SOperationDefinition
				|| def instanceof SFunctionDefinition)
		{
			return false;
		}

		for (String name : this.getNames())
		{
			if (nameToken.getName().startsWith(name))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mustHandleLexIdentifierToken(LexIdentifierToken lexId)
	{
		return false;
	}
}
