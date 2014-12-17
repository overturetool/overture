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
package org.overture.typechecker.assistant.type;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.type.AUnionTypeAssistant;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AUnionTypeAssistantTC extends AUnionTypeAssistant implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AUnionTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	//FIXME: only used once. move it
	public boolean isUnknown(AUnionType type)
	{
		for (PType t : type.getTypes())
		{
			if (af.createPTypeAssistant().isUnknown(t))
			{
				return true;
			}
		}

		return false;
	}
}
