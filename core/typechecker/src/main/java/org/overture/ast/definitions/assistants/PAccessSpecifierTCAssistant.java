package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.EAccess;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PAccessSpecifier;

public class PAccessSpecifierTCAssistant extends PAccessSpecifierAssistant{

	public static boolean isPublic(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAccess().kindPAccess() == EAccess.PUBLIC;
		}		
		return false;
	}
	
	public static boolean isPrivate(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAccess().kindPAccess() == EAccess.PRIVATE;
		}		
		return false;
	}
	
	public static boolean isProtected(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAccess().kindPAccess() == EAccess.PROTECTED;
		}		
		return false;
	}

	public static boolean isStatic(AAccessSpecifierAccessSpecifier access) {
		
		return access.getStatic() != null;
	}
	
	public static boolean isAsync(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return a.getAsync() != null;
		}		
		return false;
	}

	public static boolean narrowerThan(PAccessSpecifier access,
			PAccessSpecifier other) {
		if(access instanceof AAccessSpecifierAccessSpecifier && other instanceof AAccessSpecifierAccessSpecifier)
		{
			return narrowerThan(((AAccessSpecifierAccessSpecifier)access).getAccess(),((AAccessSpecifierAccessSpecifier)other).getAccess());
		}
		
		assert false : "PAccessSpecifierAssistent : narrowerThan arguments are not access specifiers";
		return false;
		
	}

	private static boolean narrowerThan(PAccess access, PAccess other) {
		switch (access.kindPAccess()) {
		case PRIVATE:
			return other.kindPAccess() != EAccess.PRIVATE;
		case PROTECTED:
			return other.kindPAccess() != EAccess.PROTECTED;
		case PUBLIC:
			return other.kindPAccess() != EAccess.PUBLIC;		
		}
		assert false : "PAccessSpecifierAssistent : narrowerThan PAccess switch is not comprehensive";
		return false;
	}

	public static AAccessSpecifierAccessSpecifier getStatic(PDefinition d, boolean asStatic) {
		AAccessSpecifierAccessSpecifier paccess = d.getAccess();
		if(paccess instanceof AAccessSpecifierAccessSpecifier)
		{			
			return new AAccessSpecifierAccessSpecifier(paccess.getAccess().clone(), asStatic && paccess.getStatic() != null ?  paccess.getStatic().clone() : null, paccess.getAsync() != null ?  paccess.getAsync().clone() : null);
		}
		assert false: "PAccessSpecifier must be instance of AAccessSpecifierAccessSpecifier";
		return null;
		
	}
	

}
