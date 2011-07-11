package org.overture.ast.definitions.assistants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AClassTypeAssistant;
import org.overture.runtime.Environment;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;


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
			boolean needStatic) {
		SClassDefinition self = env.findClassDefinition();
		SClassDefinition target = field.getClassDefinition();

		if (self == null)	// Not called from within a class member
		{
			// We're outside, so just public access
			return (PAccessSpecifierAssistant.isPublic(field.getAccess()));
		}
		else
		{
			AClassType selftype = (AClassType)self.getType();
			AClassType targtype = (AClassType)target.getType();

			if (!selftype.equals(targtype))
			{
				if (AClassTypeAssistant.hasSupertype(selftype,targtype))
				{
					// We're a subclass, so see public or protected
					return (PAccessSpecifierAssistant.isPrivate(field.getAccess()));
				}
				else
				{
					// We're outside, so just public/static access
					return (PAccessSpecifierAssistant.isPublic(field.getAccess()) &&
							(needStatic ? PAccessSpecifierAssistant.isStatic(field.getAccess()) : true));
				}
			}
			else
			{
				// else same type, so anything goes
				return true;
			}
		}
	}
	
	public static PDefinition findType(SClassDefinition classdef,
			LexNameToken sought, String fromModule) {
		
		if ((!sought.explicit && sought.name.equals(classdef.getName().name)) ||
				sought.equals(classdef.getName().getClassName()))
			{
				return classdef;	// Class referred to as "A" or "CLASS`A"
			}

			PDefinition def = PDefinitionAssistant.findType(classdef.getDefinitions(), sought, null);

			if (def == null)
			{
				for (PDefinition d: classdef.getAllInheritedDefinitions())
				{
					PDefinition indef = PDefinitionAssistant.findType(d,sought, null);

					if (indef != null)
					{
						def = indef;
						break;
					}
				}
			}

			return def;
	}
	
	public static Set<PDefinition> findMatches(SClassDefinition classdef,
			LexNameToken sought) {
		
		Set<PDefinition> set = PDefinitionAssistant.findMatches(classdef.getDefinitions(),sought);
		set.addAll(PDefinitionAssistant.findMatches(classdef.getAllInheritedDefinitions(),sought));
		return set;
	}

	public static void unusedCheck(SClassDefinition classdef) {
		if (!classdef.getUsed())
		{
			TypeCheckerErrors.warning(classdef, 5000, "Definition '" + classdef.getName() + "' not used");
			markUsed(classdef);		// To avoid multiple warnings
		}
		
	}

	private static void markUsed(SClassDefinition classdef) {
		classdef.setUsed(true);
		
	}
	
	public static PDefinition findName(List<SClassDefinition> classes,
			LexNameToken name, NameScope scope) {
		
		SClassDefinition d = get(classes, name.module);

		if (d != null)
		{
			PDefinition def = SClassDefinitionAssistant.findName(d,name, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	private static SClassDefinition get(List<SClassDefinition> classes,
			String module) {
		
		for (SClassDefinition sClassDefinition : classes) {
			if(sClassDefinition.getName().module.equals(module))
				return sClassDefinition;
		}
		return null;
	}

	public static PDefinition findType(List<SClassDefinition> classes,
			LexNameToken name) {

		for (SClassDefinition d: classes)
		{
			PDefinition def = PDefinitionAssistant.findType(d,name, null);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public static Set<PDefinition> findMatches(List<SClassDefinition> classes,
			LexNameToken name) {
		
		Set<PDefinition> set = new HashSet<PDefinition>();

		for (SClassDefinition d: classes)
		{
			set.addAll(SClassDefinitionAssistant.findMatches(d,name));
		}

		return set;
	}

	public static void unusedCheck(List<SClassDefinition> classes) {
		for (SClassDefinition d: classes)
		{
			unusedCheck(d);
		}
		
	}
}
