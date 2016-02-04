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

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;

public class PAccessSpecifierAssistant implements IAstAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public PAccessSpecifierAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public boolean isStatic(AAccessSpecifierAccessSpecifier access)
	{

		return access != null && access.getStatic() != null;
	}

	public boolean isPublic(AAccessSpecifierAccessSpecifier access)
	{

		return access != null && access.getAccess() instanceof APublicAccess;
	}

	public AAccessSpecifierAccessSpecifier getPublic()
	{
		return AstFactory.newAAccessSpecifierAccessSpecifier(new APublicAccess(), false, false, false);
	}

	public AAccessSpecifierAccessSpecifier getProtected()
	{
		return AstFactory.newAAccessSpecifierAccessSpecifier(new AProtectedAccess(), false, false, false);
	}

}
