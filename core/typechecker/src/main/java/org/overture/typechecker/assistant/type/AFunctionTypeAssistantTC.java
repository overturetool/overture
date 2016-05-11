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

import java.util.List;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AFunctionTypeAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AFunctionTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getCurriedPreType(AFunctionType t, Boolean isCurried)
	{

		if (isCurried && t.getResult() instanceof AFunctionType)
		{
			AFunctionType ft = (AFunctionType) t.getResult().clone();
			AFunctionType type = AstFactory.newAFunctionType(t.getLocation(), false, (List<PType>) t.getParameters().clone(), getCurriedPreType(ft, isCurried));
			type.setDefinitions((List<PDefinition>) t.getDefinitions().clone());
			type.setInstantiated(null);
			return type;
		} else
		{
			return getPreType(t);
		}
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getPreType(AFunctionType t)
	{
		AFunctionType type = AstFactory.newAFunctionType(t.getLocation(), false, (List<PType>) t.getParameters().clone(), AstFactory.newABooleanBasicType(t.getLocation()));
		type.setDefinitions((List<PDefinition>) t.getDefinitions().clone());
		type.setInstantiated(null);
		return type;
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getCurriedPostType(AFunctionType type,
			Boolean isCurried)
	{
		if (isCurried && type.getResult() instanceof AFunctionType)
		{
			AFunctionType ft = (AFunctionType) type.getResult().clone();
			AFunctionType t = AstFactory.newAFunctionType(type.getLocation(), false, (List<PType>) type.getParameters().clone(), getCurriedPostType(ft, isCurried));
			t.setDefinitions(type.getDefinitions());
			t.setInstantiated(null);
			return t;
		} else
		{
			return getPostType(type);
		}
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getPostType(AFunctionType t)
	{
		List<PType> params = new PTypeList();
		params.addAll((List<PType>) t.getParameters().clone());
		params.add(t.getResult().clone());
		AFunctionType type = AstFactory.newAFunctionType(t.getLocation(), false, params, AstFactory.newABooleanBasicType(t.getLocation()));
		type.setDefinitions((List<? extends PDefinition>) t.getDefinitions().clone());
		type.setInstantiated(null);
		return type;
	}

}
