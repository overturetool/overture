package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;

public class PAccessSpecifierAssistant {

	
	public static AAccessSpecifierAccessSpecifier getDefault()
	{
		return new AAccessSpecifierAccessSpecifier(new APrivateAccess(), null, null);
	}
	
	public static boolean isStatic(AAccessSpecifierAccessSpecifier access) {
		
		return access != null && access.getStatic() != null;
	}
	
	public static boolean isPublic(AAccessSpecifierAccessSpecifier access) {
		
		return access != null && access.getAccess() instanceof APublicAccess;
	}

	public static AAccessSpecifierAccessSpecifier getPublic() {
		return new AAccessSpecifierAccessSpecifier(new APublicAccess(), null, null);
	}
	
}
