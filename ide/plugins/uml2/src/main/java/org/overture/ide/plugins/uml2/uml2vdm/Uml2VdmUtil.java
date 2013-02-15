package org.overture.ide.plugins.uml2.uml2vdm;

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
