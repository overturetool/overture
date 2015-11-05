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

import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.statements.ACallStm;
import org.overture.codegen.assistant.TypeAssistantCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

public class TypenameComparison extends NamingComparison
{
	public TypenameComparison(String[] names,
			IRInfo irInfo, String correctionPrefix)
	{
		super(names, irInfo, correctionPrefix);
	}

	@Override
	public boolean isModuleViolation(ILexNameToken nameToken)
	{
		return nameToken.parent() instanceof ACallStm && this.getNames().contains(nameToken.getModule());
	}
	
	@Override
	public boolean mustHandleNameToken(ILexNameToken nameToken)
	{
		if (this.getNames().contains(nameToken.getName()))
		{
			PDefinitionAssistantTC defAssistant = irInfo.getTcFactory().createPDefinitionAssistant();
			TypeAssistantCG typeAssistantCg = irInfo.getAssistantManager().getTypeAssistant();
			
			PDefinition def = typeAssistantCg.getTypeDef(nameToken, defAssistant);

			if (def instanceof ATypeDefinition)
			{
				ATypeDefinition typeDef = (ATypeDefinition) def;
				return typeDef.getInvType() == null;
			}

			return def != null;
		}
		else if(isModuleViolation(nameToken))
		{
			return true;
		}

		return false;
	}

	@Override
	public boolean mustHandleLexIdentifierToken(LexIdentifierToken lexId)
	{
		return false;
	}
}
