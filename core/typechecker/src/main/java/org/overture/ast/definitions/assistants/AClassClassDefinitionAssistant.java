//package org.overture.ast.definitions.assistants;
//
//import java.util.Set;
//
//import org.overture.ast.definitions.AClassClassDefinition;
//import org.overture.ast.definitions.PDefinition;
//import org.overture.typecheck.TypeCheckerErrors;
//import org.overturetool.vdmj.lex.LexNameToken;
//import org.overturetool.vdmj.typechecker.NameScope;
//
//public class AClassClassDefinitionAssistant {
//
//	public static PDefinition findName(AClassClassDefinition classdef,
//			LexNameToken sought, NameScope scope) {
//		
//		PDefinition def = null;
//
//		for (PDefinition d: classdef.getDefinitions())
//		{
//			PDefinition found = PDefinitionAssistant.findName(d, sought, scope);
//
//			// It is possible to have an ambiguous name if the name has
//			// type qualifiers that are a union of types that match several
//			// overloaded functions/ops (even though they themselves are
//			// distinguishable).
//
//			if (found != null)
//			{
//				if (def == null)
//				{
//					def = found;
//
//					if (sought.typeQualifier == null)
//					{
//						break;		// Can't be ambiguous
//					}
//				}
//				else
//				{
//					if (!def.getLocation().equals(found.getLocation()) &&
//						PDefinitionAssistant.isFunctionOrOperation(def))
//					{
//						TypeCheckerErrors.report(3010, "Name " + sought + " is ambiguous",sought.getLocation(),sought);
//						TypeCheckerErrors.detail2("1", def.getLocation(), "2", found.getLocation());
//						break;
//					}
//				}
//			}
//		}
//
//		if (def == null)
//		{
//			for (PDefinition d: classdef.getAllInheritedDefinitions())
//			{
//				PDefinition indef = PDefinitionAssistant.findName(d, sought, scope);
//
//				// See above for the following...
//
//				if (indef != null)
//				{
//					if (def == null)
//					{
//						def = indef;
//
//						if (sought.typeQualifier == null)
//						{
//							break;		// Can't be ambiguous
//						}
//					}
//					else if (def.equals(indef) &&	// Compares qualified names
//							 !def.getLocation().equals(indef.getLocation()) &&
//							 !PDefinitionAssistant.hasSupertype(def.getClassDefinition(),indef.getClassDefinition().getType()) &&
//							 PDefinitionAssistant.isFunctionOrOperation(def))
//					{
//						TypeCheckerErrors.report(3011, "Name " + sought + " is multiply defined in class",sought.getLocation(),sought);
//						TypeCheckerErrors.detail2("1", def.getLocation(), "2", indef.getLocation());
//						break;
//					}
//				}
//			}
//		}
//
//		return def;
//	}
//
//	public static PDefinition findType(AClassClassDefinition classdef,
//			LexNameToken sought, String fromModule) {
//		
//		if ((!sought.explicit && sought.name.equals(classdef.getName().name)) ||
//				sought.equals(classdef.getName().getClassName()))
//			{
//				return classdef;	// Class referred to as "A" or "CLASS`A"
//			}
//
//			PDefinition def = PDefinitionAssistant.findType(classdef.getDefinitions(), sought, null);
//
//			if (def == null)
//			{
//				for (PDefinition d: classdef.getAllInheritedDefinitions())
//				{
//					PDefinition indef = PDefinitionAssistant.findType(d,sought, null);
//
//					if (indef != null)
//					{
//						def = indef;
//						break;
//					}
//				}
//			}
//
//			return def;
//	}
//
//	
//
//
//
//}
