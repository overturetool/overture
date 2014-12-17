/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.assistant.definition;

import java.util.List;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;

public class PDefinitionAssistant implements IAstAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public PDefinitionAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public void setClassDefinition(PDefinition pDefinition, SClassDefinition def)
	{
		if (pDefinition instanceof SClassDefinition)
		{
			for (PDefinition d : def.getDefinitions())
			{
				setClassDefinition(d, def);
			}
		} else if (pDefinition instanceof AExplicitFunctionDefinition)
		{
			af.createPDefinitionAssistant().setClassDefinitionBaseCase(pDefinition, def);
			AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition) pDefinition;
			if (efd.getPredef() != null)
			{
				setClassDefinition(efd.getPredef(), def);
			}
			if (efd.getPostdef() != null)
			{
				setClassDefinition(efd.getPostdef(), def);
			}
		} else if (pDefinition instanceof AValueDefinition)
		{
			af.createPDefinitionAssistant().setClassDefinitionBaseCase(pDefinition, def);
			AValueDefinition vd = (AValueDefinition) pDefinition;
			for (PDefinition d : vd.getDefs())
			{
				setClassDefinition(d, def);
			}
		} else
		{
			af.createPDefinitionAssistant().setClassDefinitionBaseCase(pDefinition, def);
		}

	}

	public String getName(PDefinition node)
	{
		if (node.getName() != null)
		{
			return node.getName().getName();
		}

		return null;
	}

	public void setClassDefinitionBaseCase(PDefinition pDefinition,
			SClassDefinition def)
	{
		pDefinition.setClassDefinition(def);
	}

	public void setClassDefinition(List<PDefinition> defs,
			SClassDefinition classDefinition)
	{
		for (PDefinition d : defs)
		{
			af.createPDefinitionAssistant().setClassDefinition(d, classDefinition);
		}

	}

}
