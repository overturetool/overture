package org.overture.typechecker.assistant.definition;

import org.overture.ast.assistant.definition.PAccessSpecifierAssistant;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.PAccess;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.PAccessSpecifier;

public class PAccessSpecifierAssistantTC extends PAccessSpecifierAssistant{

	public static boolean isPublic(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return APublicAccess.kindPAccess.equals(a.getAccess().kindPAccess());
		}		
		return false;
	}
	
	public static boolean isPrivate(AAccessSpecifierAccessSpecifier access) {
		return APrivateAccess.kindPAccess.equals(access.getAccess().kindPAccess());
	}
	
	public static boolean isProtected(PAccessSpecifier access) {
		if(access instanceof AAccessSpecifierAccessSpecifier)
		{
			AAccessSpecifierAccessSpecifier a = (AAccessSpecifierAccessSpecifier) access;
			return AProtectedAccess.kindPAccess.equals(a.getAccess().kindPAccess());
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

	public static boolean narrowerThan(AAccessSpecifierAccessSpecifier access,
			AAccessSpecifierAccessSpecifier other) {		
			return narrowerThan(access.getAccess(),other.getAccess());
	
				
		
	}

	private static boolean narrowerThan(PAccess access, PAccess other) {
		switch (access.kindPAccess()) {
		case APrivateAccess.kindPAccess:
			return ! APrivateAccess.kindPAccess.equals(other.kindPAccess());
		case AProtectedAccess.kindPAccess:
			return APublicAccess.kindPAccess.equals(other.kindPAccess());
		case APublicAccess.kindPAccess:
			return false;		
		}
		assert false : "PAccessSpecifierAssistent : narrowerThan PAccess switch is not comprehensive";
		return false;
	}

	public static AAccessSpecifierAccessSpecifier getStatic(PDefinition d, boolean asStatic) {
		AAccessSpecifierAccessSpecifier paccess = d.getAccess();
		if(paccess instanceof AAccessSpecifierAccessSpecifier)
		{			
			return AstFactory.newAAccessSpecifierAccessSpecifier(paccess.getAccess().clone(), asStatic && paccess.getStatic() != null , paccess.getAsync() != null);
		}
		assert false: "PAccessSpecifier must be instance of AAccessSpecifierAccessSpecifier";
		return null;
		
	}

}
