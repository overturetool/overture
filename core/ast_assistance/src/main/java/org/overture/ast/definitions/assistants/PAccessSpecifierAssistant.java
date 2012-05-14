package org.overture.ast.definitions.assistants;

import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overturetool.vdmj.typechecker.Access;

public class PAccessSpecifierAssistant {

	
	public static AAccessSpecifierAccessSpecifier getDefault()
	{
		return new AAccessSpecifierAccessSpecifier(Access.PRIVATE, false, false);
	}
	
	public static boolean isStatic(AAccessSpecifierAccessSpecifier access) {
		
		return access != null && access.getStatic();
	}
	
	public static boolean isPublic(AAccessSpecifierAccessSpecifier access) {
		
		return access != null && access.getAccess() == Access.PUBLIC;
	}

	public static AAccessSpecifierAccessSpecifier getPublic() {
		return new AAccessSpecifierAccessSpecifier(Access.PUBLIC, false, false);
	}

	public static AAccessSpecifierAccessSpecifier getProtected() {
		return new AAccessSpecifierAccessSpecifier(Access.PROTECTED, false, false);
	}
	
}
