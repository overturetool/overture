package org.overture.ast.assistant.definition;

import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;

public class PAccessSpecifierAssistant {

	
	public static AAccessSpecifierAccessSpecifier getDefault()
	{
		return AstFactory.newAAccessSpecifierAccessSpecifier(new APrivateAccess(), false, false);
	}
	
	public static boolean isStatic(AAccessSpecifierAccessSpecifier access) {
		
		return access != null && access.getStatic() != null;
	}
	
	public static boolean isPublic(AAccessSpecifierAccessSpecifier access) {
		
		return access != null && access.getAccess() instanceof APublicAccess;
	}

	public static AAccessSpecifierAccessSpecifier getPublic() {
		return AstFactory.newAAccessSpecifierAccessSpecifier(new APublicAccess(), false, false);
	}
	

	public static AAccessSpecifierAccessSpecifier getProtected() {
		return AstFactory.newAAccessSpecifierAccessSpecifier(new AProtectedAccess(), false, false);
	}
	
}
