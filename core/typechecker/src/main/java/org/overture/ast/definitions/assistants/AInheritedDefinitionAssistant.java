package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AInheritedDefinitionAssistant {

	public static PDefinition findType(AInheritedDefinition d,
			LexNameToken sought, String fromModule) {
		
		if (d.getSuperdef() instanceof ATypeDefinition && sought.equals(d.getName()))
		{
			return d;
		}

		return null;
	}

	public static PDefinition findName(AInheritedDefinition d,
			LexNameToken sought, NameScope scope) {
		// The problem is, when the InheritedDefinition is created, we
		// don't know its fully qualified name.

		LexNameToken name = d.getName();
		name.setTypeQualifier(d.getSuperdef().getName().typeQualifier);

		if (name.equals(sought))
		{
			return d;
		}
		else if (scope.matches(NameScope.OLDSTATE) && d.getOldname().equals(sought))
		{
			return d;
		}

		return null;
	}

	public static void markUsed(AInheritedDefinition d) {
		d.setUsed(true);
		PDefinitionAssistant.markUsed(d.getSuperdef());		
	}

}
