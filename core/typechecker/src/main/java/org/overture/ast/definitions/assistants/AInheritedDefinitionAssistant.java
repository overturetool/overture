package org.overture.ast.definitions.assistants;

import java.util.List;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameList;
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

	public static List<PDefinition> getDefinitions(AInheritedDefinition d) {

		return PDefinitionAssistant.getDefinitions(d.getSuperdef());
	}

	public static LexNameList getVariableNames(AInheritedDefinition d) {
		LexNameList names = new LexNameList();
		checkSuperDefinition(d);

		for (LexNameToken vn: PDefinitionAssistant.getVariableNames(d.getSuperdef()))
		{
			names.add(vn.getModifiedName(d.getName().module));
		}

		return names;
	}
	
	private static void checkSuperDefinition(AInheritedDefinition d)
	{
		// This is used to get over the case where an inherited definition
		// is a ValueDefinition that has since been replaced with a new
		// LocalDefinition. It would be better to somehow list the
		// inherited definitions that refer to a LocalDefinition and update
		// them...

		if (d.getSuperdef() instanceof AUntypedDefinition)
		{
			if (d.getClassDefinition() != null)
			{
				d.setSuperdef(PDefinitionAssistant.findName(d.getClassDefinition(), d.getSuperdef().getName(), d.getNameScope()));
			}
		}
	}

	public static PType getType(AInheritedDefinition def) {
		checkSuperDefinition(def);
		return PDefinitionAssistant.getType(def.getSuperdef());
	}

}
