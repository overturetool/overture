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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AOperationTypeAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AOperationTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getPreType(AOperationType type,
			AStateDefinition state, SClassDefinition classname, boolean isStatic)
	{

		if (state != null)
		{
			PTypeList params = new PTypeList();
			params.addAll((LinkedList<PType>) type.getParameters().clone());
			params.add(AstFactory.newAUnresolvedType(state.getName()));
			return AstFactory.newAFunctionType(type.getLocation(), false, params, AstFactory.newABooleanBasicType(type.getLocation()));
		} else if (classname != null && !isStatic)
		{
			PTypeList params = new PTypeList();
			params.addAll((Collection<? extends PType>) type.getParameters().clone());
			params.add(AstFactory.newAUnresolvedType(classname.getName()));
			return AstFactory.newAFunctionType(type.getLocation(), false, params, AstFactory.newABooleanBasicType(type.getLocation()));
		} else
		{
			return AstFactory.newAFunctionType(type.getLocation(), false, (List<PType>) type.getParameters().clone(), AstFactory.newABooleanBasicType(type.getLocation()));
		}
	}

	@SuppressWarnings("unchecked")
	public AFunctionType getPostType(AOperationType type,
			AStateDefinition state, SClassDefinition classname, boolean isStatic)
	{

		PTypeList params = new PTypeList();
		params.addAll((LinkedList<PType>) type.getParameters().clone());

		if (!(type.getResult() instanceof AVoidType))
		{
			params.add(type.getResult().clone());
		}

		if (state != null)
		{
			params.add(AstFactory.newAUnresolvedType(state.getName()));
			params.add(AstFactory.newAUnresolvedType(state.getName()));
		} else if (classname != null)
		{
			AMapMapType map = AstFactory.newAMapMapType(type.getLocation(), AstFactory.newASeqSeqType(type.getLocation(), AstFactory.newACharBasicType(type.getLocation())), AstFactory.newAUnknownType(type.getLocation()));
			params.add(map);

			if (!isStatic)
			{
				params.add(AstFactory.newAUnresolvedType(classname.getName()));
			}
		}

		return AstFactory.newAFunctionType(type.getLocation(), false, params, AstFactory.newABooleanBasicType(type.getLocation()));
	}

}
