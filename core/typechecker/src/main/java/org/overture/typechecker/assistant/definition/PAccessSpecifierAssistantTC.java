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
package org.overture.typechecker.assistant.definition;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.definition.PAccessSpecifierAssistant;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PAccessSpecifierAssistantTC extends PAccessSpecifierAssistant implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public PAccessSpecifierAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public boolean isPublic(PAccessSpecifier access)
	{
		if (access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAccess() instanceof APublicAccess;
		}
		return false;
	}

	public boolean isPrivate(AAccessSpecifierAccessSpecifier access)
	{
		return access.getAccess() instanceof APrivateAccess;
	}

	public boolean isProtected(PAccessSpecifier access)
	{
		if (access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAccess() instanceof AProtectedAccess;
		}
		return false;
	}

	public boolean isStatic(AAccessSpecifierAccessSpecifier access)
	{

		return access.getStatic() != null;
	}

	public boolean isAsync(PAccessSpecifier access)
	{
		if (access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAsync() != null;
		}
		return false;
	}

	public boolean narrowerThan(AAccessSpecifierAccessSpecifier access,
			AAccessSpecifierAccessSpecifier other)
	{
		return narrowerThan(access.getAccess(), other.getAccess());

	}

	private boolean narrowerThan(PAccess access, PAccess other)
	{
		if (access instanceof APrivateAccess)
		{
			return !(other instanceof APrivateAccess);
		} else if (access instanceof AProtectedAccess)
		{
			return other instanceof APublicAccess;
		} else if (access instanceof APublicAccess)
		{
			return false;
		}
		assert false : "PAccessSpecifierAssistent : narrowerThan PAccess switch is not comprehensive";
		return false;
	}

	public AAccessSpecifierAccessSpecifier getStatic(PDefinition d,
			boolean asStatic)
	{
		AAccessSpecifierAccessSpecifier paccess = d.getAccess();
		if (paccess instanceof AAccessSpecifierAccessSpecifier)
		{
			return AstFactory.newAAccessSpecifierAccessSpecifier(paccess.getAccess().clone(), asStatic
					&& paccess.getStatic() != null, paccess.getAsync() != null, false);
		}
		assert false : "PAccessSpecifier must be instance of AAccessSpecifierAccessSpecifier";
		return null;

	}

}
