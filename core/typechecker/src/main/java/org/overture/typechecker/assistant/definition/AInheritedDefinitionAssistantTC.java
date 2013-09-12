package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.util.HelpLexNameToken;

public class AInheritedDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AInheritedDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}


	public static PDefinition findName(AInheritedDefinition d,
			ILexNameToken sought, NameScope scope)
	{
		// The problem is, when the InheritedDefinition is created, we
		// don't know its fully qualified name.

		ILexNameToken name = d.getName();
		name.setTypeQualifier(d.getSuperdef().getName().getTypeQualifier());

		if (HelpLexNameToken.isEqual(name, sought))
		{
			return d;
		} else if (scope.matches(NameScope.OLDSTATE)
				&& d.getOldname().equals(sought))
		{
			return d;
		}

		return null;
	}

	public static void markUsed(AInheritedDefinition d)
	{
		d.setUsed(true);
		PDefinitionAssistantTC.markUsed(d.getSuperdef());
	}

	public static void checkSuperDefinition(AInheritedDefinition d)
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
				d.setSuperdef(PDefinitionAssistantTC.findName(d.getClassDefinition(), d.getSuperdef().getName(), d.getNameScope()));
			}
		}
	}

	public static PType getType(AInheritedDefinition def)
	{
		checkSuperDefinition(def);
		return af.createPDefinitionAssistant().getType(def.getSuperdef());
	}

	public static boolean isUsed(AInheritedDefinition u)
	{
		return PDefinitionAssistantTC.isUsed(u.getSuperdef());
	}

}
