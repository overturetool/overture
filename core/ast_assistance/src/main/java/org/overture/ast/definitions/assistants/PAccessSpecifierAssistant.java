package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;

public class PAccessSpecifierAssistant {

	
	public static AAccessSpecifierAccessSpecifier getDefault()
	{
		return new AAccessSpecifierAccessSpecifier(new APrivateAccess(), null, null);
	}
	
	
}
