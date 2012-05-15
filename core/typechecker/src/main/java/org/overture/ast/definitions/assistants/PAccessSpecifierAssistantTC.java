package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PAccessSpecifier;
import org.overturetool.vdmj.typechecker.Access;

public class PAccessSpecifierAssistantTC extends PAccessSpecifierAssistant{

	public static boolean isPublic(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAccess() == Access.PUBLIC;
		}		
		return false;
	}
	
	public static boolean isPrivate(AAccessSpecifierAccessSpecifier access) {
		return access.getAccess() == Access.PRIVATE;
	}
	
	public static boolean isProtected(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAccess() == Access.PROTECTED;
		}		
		return false;
	}

	public static boolean isStatic(AAccessSpecifierAccessSpecifier access) {
		
		return access.getStatic();
	}
	
	public static boolean isAsync(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAsync() != null;
		}		
		return false;
	}

	public static boolean narrowerThan(AAccessSpecifierAccessSpecifier access,
			AAccessSpecifierAccessSpecifier other) {		
			return narrowerThan(access.getAccess(),other.getAccess());
	
				
		
	}

	private static boolean narrowerThan(Access access, Access other) {
		switch (access) {
		case PRIVATE:
			return other != Access.PRIVATE;
		case PROTECTED:
			return other == Access.PUBLIC;
		case PUBLIC:
			return false;		
		}
		assert false : "PAccessSpecifierAssistent : narrowerThan PAccess switch is not comprehensive";
		return false;
	}

	public static AAccessSpecifierAccessSpecifier getStatic(PDefinition d, boolean asStatic) {
		AAccessSpecifierAccessSpecifier paccess = d.getAccess();
		if(paccess instanceof AAccessSpecifierAccessSpecifier)
		{			
			return new AAccessSpecifierAccessSpecifier(paccess.getAccess(), asStatic && paccess.getStatic() ?  paccess.getStatic() : false , paccess.getAsync()  ?  paccess.getAsync() : false);
		}
		assert false: "PAccessSpecifier must be instance of AAccessSpecifierAccessSpecifier";
		return null;
		
	}
	

}
