package org.overture.ast.definitions.assistants;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AFieldExp;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AClassTypeAssistant;
import org.overture.runtime.Environment;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.ClassType;


public class SClassDefinitionAssistant {

	public static PDefinition findName(SClassDefinition classdef,
			LexNameToken sought, NameScope scope) {
		
		PDefinition def = null;

		for (PDefinition d: classdef.getDefinitions())
		{
			PDefinition found = PDefinitionAssistant.findName(d,sought, scope);

			// It is possible to have an ambiguous name if the name has
			// type qualifiers that are a union of types that match several
			// overloaded functions/ops (even though they themselves are
			// distinguishable).

			if (found != null)
			{
				if (def == null)
				{
					def = found;

					if (sought.typeQualifier == null)
					{
						break;		// Can't be ambiguous
					}
				}
				else
				{
					if (!def.getLocation().equals(found.getLocation()) &&
						PDefinitionAssistant.isFunctionOrOperation(def))
					{
						TypeCheckerErrors.report(3010, "Name " + sought + " is ambiguous",sought.getLocation(),sought);
						TypeCheckerErrors.detail2("1", def.getLocation(), "2", found.getLocation());
						break;
					}
				}
			}
		}

		if (def == null)
		{
			for (PDefinition d: classdef.getAllInheritedDefinitions())
			{
				PDefinition indef = PDefinitionAssistant.findName(d,sought, scope);

				// See above for the following...

				if (indef != null)
				{
					if (def == null)
					{
						def = indef;

						if (sought.typeQualifier == null)
						{
							break;		// Can't be ambiguous
						}
					}
					else if (def.equals(indef) &&	// Compares qualified names
							 !def.getLocation().equals(indef.getLocation()) &&
							 !hasSupertype(def.getClassDefinition(),indef.getClassDefinition().getType()) &&
							 PDefinitionAssistant.isFunctionOrOperation(def))
					{
						TypeCheckerErrors.report(3011, "Name " + sought + " is multiply defined in class",sought.getLocation(),sought);
						TypeCheckerErrors.detail2("1", def.getLocation(), "2", indef.getLocation());
						break;
					}
				}
			}
		}

		return def;
	}

	public static boolean hasSupertype(SClassDefinition classDefinition,
			PType other) {
		
		if (classDefinition.getType().equals(other))
		{
			return true;
		}
		else
		{
			for (PType type: classDefinition.getSupertypes())
			{
				AClassType sclass = (AClassType)type;

				if (AClassTypeAssistant.hasSupertype(sclass,other))
				{
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isAccessible( Environment env, PDefinition field,
			boolean b) {
		SClassDefinition self = env.findClassDefinition();
		SClassDefinition target = field.getClassDefinition();

		if (self == null)	// Not called from within a class member
		{
			// We're outside, so just public access
			return (field.accessSpecifier.access == Token.PUBLIC);
		}
		else
		{
			ClassType selftype = (ClassType)self.getType();
			ClassType targtype = (ClassType)target.getType();

			if (!selftype.equals(targtype))
			{
				if (selftype.hasSupertype(targtype))
				{
					// We're a subclass, so see public or protected
					return (field.accessSpecifier.access != Token.PRIVATE);
				}
				else
				{
					// We're outside, so just public/static access
					return (field.accessSpecifier.access == Token.PUBLIC &&
							(needStatic ? field.accessSpecifier.isStatic : true));
				}
			}
			else
			{
				// else same type, so anything goes
				return true;
			}
		}
	}

}
