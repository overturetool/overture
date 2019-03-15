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
package org.overture.ast.assistant.type;

import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.util.PTypeSet;

public class AUnionTypeAssistant implements IAstAssistant
{
	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public AUnionTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public void expand(AUnionType type)
	{

		if (type.getExpanded())
		{
			return;
		}

		PTypeSet exptypes = new PTypeSet(af);

		for (PType t : type.getTypes())
		{
			if (t instanceof AUnionType)
			{
				AUnionType ut = (AUnionType) t;
				ut.setExpanded(false);
				expand(ut);
				exptypes.addAll(ut.getTypes());
			} else
			{
				exptypes.add(t);
			}
		}

		Vector<PType> v = new Vector<PType>(exptypes);
		type.setTypes(v);
		type.setExpanded(true);
		List<PDefinition> definitions = type.getDefinitions();

		for (PType t : type.getTypes())
		{
			if (t.getDefinitions() != null)
			{
				definitions.addAll(t.getDefinitions());
			}
		}

	}

	public boolean isNumeric(AUnionType type, String fromModule)
	{
		return getNumeric(type, fromModule) != null;
	}

	public SNumericBasicType getNumeric(AUnionType type, String fromModule)
	{
		if (!type.getNumDone())
		{
			type.setNumDone(true);
			type.setNumType(AstFactory.newANatNumericBasicType(type.getLocation())); // lightest default
			boolean found = false;

			for (PType t : type.getTypes())
			{
				if (af.createPTypeAssistant().isNumeric(t, fromModule))
				{
					SNumericBasicType nt = af.createPTypeAssistant().getNumeric(t, fromModule);

					if (af.createSNumericBasicTypeAssistant().getWeight(nt) > af.createSNumericBasicTypeAssistant().getWeight(type.getNumType()))
					{
						type.setNumType(nt);
					}

					found = true;
				}
			}

			if (!found)
			{
				type.setNumType(null);
			}
		}

		return type.getNumType();
	}

}
