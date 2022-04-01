/*
 * #%~
 * UML2 Translator
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
package org.overture.core.uml2.uml2vdm;

import org.eclipse.uml2.uml.VisibilityKind;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;

public class Uml2VdmUtil
{
	public static AAccessSpecifierAccessSpecifier createAccessSpecifier(
			VisibilityKind visibility, boolean isStatic, boolean isAsync)
	{
		PAccess access = null;

		if (visibility == VisibilityKind.PRIVATE_LITERAL)
		{
			access = new APrivateAccess();
		} else if (visibility == VisibilityKind.PROTECTED_LITERAL)
		{
			access = new AProtectedAccess();
		} else
		{
			access = new APublicAccess();
		}

		return AstFactory.newAAccessSpecifierAccessSpecifier(access, isStatic, isAsync);
	}

	public static AAccessSpecifierAccessSpecifier createAccessSpecifier(
			VisibilityKind visibility)
	{
		return createAccessSpecifier(visibility, false, false);
	}
}
