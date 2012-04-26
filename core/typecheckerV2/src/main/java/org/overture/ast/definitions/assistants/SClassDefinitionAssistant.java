package org.overture.ast.definitions.assistants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AClassInvariantStm;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.AClassTypeAssistant;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.Environment;
import org.overture.typecheck.FlatEnvironment;
import org.overture.typecheck.Pass;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overture.typecheck.TypeComparator;
import org.overture.typecheck.visitors.TypeCheckVisitor;
import org.overturetool.vdmj.util.HelpLexNameToken;
import org.overturetool.vdmjV2.lex.LexLocation;
import org.overturetool.vdmjV2.lex.LexNameList;
import org.overturetool.vdmjV2.lex.LexNameToken;
import org.overturetool.vdmjV2.typechecker.ClassDefinitionSettings;
import org.overturetool.vdmjV2.typechecker.NameScope;


public class SClassDefinitionAssistant {

	public static PDefinition findName(SClassDefinition classdef,
			LexNameToken sought, NameScope scope) {
		
		PDefinition def = null;

		for (PDefinition d: classdef.getDefinitions())
		{
			PDefinition found = PDefinitionAssistantTC.findName(d,sought, scope);

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
						PDefinitionAssistantTC.isFunctionOrOperation(def))
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
				PDefinition indef = PDefinitionAssistantTC.findName(d,sought, scope);

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
							 PDefinitionAssistantTC.isFunctionOrOperation(def))
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
		
		if (PTypeAssistant.equals(getType(classDefinition),other))
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
			return (PAccessSpecifierAssistantTC.isPublic(field.getAccess()));
		}
		else
		{
			AClassType selftype =  (AClassType) getType(self);
			AClassType targtype = (AClassType) getType(target);

			if (!PTypeAssistant.equals(selftype, targtype))
			{
				if (AClassTypeAssistant.hasSupertype(selftype,targtype))
				{
					// We're a subclass, so see public or protected
					return (!PAccessSpecifierAssistantTC.isPrivate(field.getAccess()));
				}
				else
				{
					// We're outside, so just public/static access
					return (PAccessSpecifierAssistantTC.isPublic(field.getAccess()) &&
							(needStatic ? PAccessSpecifierAssistantTC.isStatic(field.getAccess()) : true));
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

			PDefinition def = PDefinitionAssistantTC.findType(classdef.getDefinitions(), sought, null);

			if (def == null)
			{
				for (PDefinition d: classdef.getAllInheritedDefinitions())
				{
					PDefinition indef = PDefinitionAssistantTC.findType(d,sought, null);

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
		
		Set<PDefinition> set = PDefinitionListAssistant.findMatches(classdef.getDefinitions(),sought);
		set.addAll(PDefinitionListAssistant.findMatches(classdef.getAllInheritedDefinitions(),sought));
		return set;
	}
//
//	public static void unusedCheck(SClassDefinition classdef) {
//		if (!classdef.getUsed())
//		{
//			TypeCheckerErrors.warning(5000, "Definition '" + classdef.getName() + "' not used",classdef.getLocation(),classdef);
//			//System.out.println("Definition '" + classdef.getName() + "' not used");
//			markUsed(classdef);		// To avoid multiple warnings
//		}
//		
//	}

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
			if(sClassDefinition.getName().name.equals(module))
				return sClassDefinition;
		}
		return null;
	}

	public static PDefinition findType(List<SClassDefinition> classes,
			LexNameToken name) {

		for (SClassDefinition d: classes)
		{
			PDefinition def = PDefinitionAssistantTC.findType(d,name, null);

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
			PDefinitionAssistantTC.unusedCheck(d);
		}
		
	}

	public static List<PDefinition> getLocalDefinitions(SClassDefinition classDefinition) {
		
		List<PDefinition> all = new Vector<PDefinition>();

		all.addAll(classDefinition.getLocalInheritedDefinitions());
		all.addAll(PDefinitionListAssistant.singleDefinitions(classDefinition.getDefinitions()));

		return all;
	}

	public static List<PDefinition> getDefinitions(SClassDefinition d) {
		
		List<PDefinition> all = new Vector<PDefinition>();

		all.addAll(d.getAllInheritedDefinitions());
		all.addAll(PDefinitionListAssistant.singleDefinitions(d.getDefinitions()));

		return all;
	}

	public static PDefinition getSelfDefinition(
			SClassDefinition classDefinition) {

		PDefinition def = new ALocalDefinition(classDefinition.getLocation(),
				NameScope.LOCAL,
				false, null, null, PDefinitionAssistantTC.getType(classDefinition), false,classDefinition.getName().getSelfName());
		PDefinitionAssistantTC.markUsed(def);
		return def;
	}

	public static LexNameList getVariableNames(SClassDefinition d) {
		return PDefinitionListAssistant.getVariableNames(d.getDefinitions());
	}

	public static void implicitDefinitions(SClassDefinition d, Environment publicClasses) {
		
		setInherited(d,publicClasses);
		setInheritedDefinitions(d);

		AExplicitOperationDefinition invariant = getInvDefinition(d);

		d.setInvariant(invariant);
		
		if (invariant != null)
		{
			invariant.setClassDefinition(d);

			// This listener is created for static invariants. This gets called
			// when any statics get updated, but that could affect the validity
			// of all instances that mention the static in their inv clause.
			// For now, we suppress the trigger for static updates... one for
			// the LB :-)

			// invlistenerlist = null;

//			OperationValue invop = new OperationValue(invariant, null, null, null);
//			invop.isStatic = true;
//			ClassInvariantListener listener = new ClassInvariantListener(invop);
//			invlistenerlist = new ValueListenerList(listener);
		}
		
	}

	private static AExplicitOperationDefinition getInvDefinition(SClassDefinition d) {
		
		List<PDefinition> invdefs = getInvDefs(d);

		if (invdefs.isEmpty())
		{
			return null;
		}

		// Location of last local invariant
		LexLocation invloc = invdefs.get(invdefs.size() - 1).getLocation();

		AOperationType type = new AOperationType(
			invloc, false,null, new Vector<PType>(), new ABooleanBasicType(invloc,false,null));

		LexNameToken invname =
			new LexNameToken(d.getName().name, "inv_" + d.getName().name, invloc);

		PStm body = new AClassInvariantStm(d.getLocation(),invname, invdefs);

		
//		AExplicitOperationDefinition res = new AExplicitOperationDefinition(invloc, invname, null, false, null, null, null, null, body, null, null, type, null, null, null, null, null, false);
		AExplicitOperationDefinition res = new AExplicitOperationDefinition(invloc, invname, NameScope.GLOBAL, false, d, PAccessSpecifierAssistantTC.getDefault(), null, body, null, null, type, null,null, invdefs, null, null, false);
		res.setParameterPatterns(new Vector<PPattern>());
		
		return res;
		
		
	}
	
	public static List<PDefinition> getInvDefs(SClassDefinition def)
	{
		List<PDefinition> invdefs = new Vector<PDefinition>();

		if (def.getGettingInvDefs())
		{
			// reported elsewhere
			return invdefs;
		}

		def.setGettingInvDefs(true);

		for (SClassDefinition d: def.getSuperDefs())
		{
			invdefs.addAll(getInvDefs(d));
		}

		for (PDefinition d:def.getDefinitions())
		{
			if (d instanceof AClassInvariantDefinition)
			{
				invdefs.add(d);
			}
		}

		def.setGettingInvDefs(false);
		return invdefs;
	}

	private static void setInheritedDefinitions(SClassDefinition definition) {
		List<PDefinition> indefs = new Vector<PDefinition>();

		for (SClassDefinition sclass: definition.getSuperDefs())
		{
			indefs.addAll(getInheritable(sclass));
		}

		// The inherited definitions are ordered such that the
		// definitions, taken in order, will consider the overriding
		// members before others.

		List<PDefinition> superInheritedDefinitions = new Vector<PDefinition>();

		for (PDefinition d: indefs)
		{
			superInheritedDefinitions.add(d);

			LexNameToken localname = d.getName().getModifiedName(definition.getName().name);

			if (PDefinitionListAssistant.findName(definition.getDefinitions(),localname, NameScope.NAMESANDSTATE) == null)
			{
				AInheritedDefinition local = new AInheritedDefinition(definition.getLocation(),localname,d.getNameScope(),false,d.getClassDefinition(), d.getAccess().clone(), null, d, null);
				definition.getLocalInheritedDefinitions().add(local);
			}
		}

		definition.setAllInheritedDefinitions(new Vector<PDefinition>());
		definition.getAllInheritedDefinitions().addAll(superInheritedDefinitions);
		definition.getAllInheritedDefinitions().addAll(definition.getLocalInheritedDefinitions());
		
	}

	private static List<PDefinition> getInheritable(
			SClassDefinition def) {

		List<PDefinition> defs = new Vector<PDefinition>();

		if (def.getGettingInheritable())
		{
			TypeCheckerErrors.report(3009, "Circular class hierarchy detected: " + def.getName(),def.getLocation(),def);
			return defs;
		}

		def.setGettingInheritable(true);

		// The inherited definitions are ordered such that the
		// definitions, taken in order, will consider the overriding
		// members before others. So we add the local definitions
		// before the inherited ones.

		List<PDefinition> singles = PDefinitionListAssistant.singleDefinitions(def.getDefinitions());

		for (PDefinition d: singles)
		{
			if (!PAccessSpecifierAssistantTC.isPrivate(d.getAccess()))
			{
				defs.add(d);
			}
		}

		for (SClassDefinition sclass: def.getSuperDefs())
		{
			List<PDefinition> sdefs = getInheritable(sclass);

			for (PDefinition d: sdefs)
			{
				defs.add(d);

				LexNameToken localname = d.getName().getModifiedName(def.getName().name);

				if (PDefinitionListAssistant.findName(defs, localname, NameScope.NAMESANDSTATE) == null)
				{
					AInheritedDefinition local = new AInheritedDefinition(d.getLocation(),localname,d.getNameScope(),false,d.getClassDefinition(),d.getAccess().clone(),null,d, null);
					defs.add(local);
				}
			}
		}

		def.setGettingInheritable(false);
		return defs;
	}

	private static void setInherited(SClassDefinition d, Environment base) {
		switch (d.getSettingHierarchy())
		{
			case UNSET:
				d.setSettingHierarchy(ClassDefinitionSettings.INPROGRESS);
				break;

			case INPROGRESS:
				TypeCheckerErrors.report(3002, "Circular class hierarchy detected: " + d.getName(),d.getLocation(),d);
				return;

			case DONE:
				return;
		}

		PDefinitionListAssistant.implicitDefinitions(d.getDefinitions(), base);

		for (LexNameToken supername: d.getSupernames())
		{
			PDefinition def = base.findType(supername, null);

			if (def == null)
			{
				TypeCheckerErrors.report(3003, "Undefined superclass: " + supername,d.getLocation(),d);
			}
			else if (def instanceof ACpuClassDefinition)
			{
				TypeCheckerErrors.report(3298, "Cannot inherit from CPU",d.getLocation(),d);
			}
			else if (def instanceof ABusClassDefinition)
			{
				TypeCheckerErrors.report(3299, "Cannot inherit from BUS",d.getLocation(),d);
			}
			else if (def instanceof ASystemClassDefinition)
			{
				TypeCheckerErrors.report(3278, "Cannot inherit from system class " + supername,d.getLocation(),d);
			}
			else if (def instanceof SClassDefinition)
			{
				SClassDefinition superdef = (SClassDefinition)def;
				setInherited(superdef, base);
				
				d.getSuperDefs().add(superdef);
				d.getSupertypes().add(PDefinitionAssistantTC.getType(superdef));
			}
			else
			{
				TypeCheckerErrors.report(3004, "Superclass name is not a class: " + supername,d.getLocation(),d);
			}
		}

		d.setSettingHierarchy(ClassDefinitionSettings.DONE);
		return;
		
	}

	@SuppressWarnings("unchecked")
	public static void typeResolve(SClassDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		Environment cenv = new FlatEnvironment(d.getDefinitions(),  question.env);
		PDefinitionListAssistant.typeResolve(d.getDefinitions(),rootVisitor,new TypeCheckInfo(cenv));
	}
	
	public static PDefinition findThread(SClassDefinition d)
	{
		return  SClassDefinitionAssistant.findName(d, d.getName().getThreadName(), NameScope.NAMES);
	}

	public static PDefinition findConstructor(SClassDefinition classdef,
			List<PType> argtypes) {
		
		LexNameToken constructor = getCtorName(classdef, argtypes);
		return findName(classdef,constructor, NameScope.NAMES);
	}

	public static LexNameToken getCtorName(SClassDefinition classdef, List<PType> argtypes) {
		LexNameToken name = classdef.getName();
		LexNameToken cname = new LexNameToken(name.name, name.name, classdef.getLocation());
   		cname.setTypeQualifier(argtypes);
 		return cname;
	}

	public static PType getType(SClassDefinition def) {
		if (def.getClasstype() == null)
		{
			def.setClasstype(new AClassType(def.getLocation(), false, null,def.getName().clone(), def));
		}

		return def.getClasstype();
	}

	public static void checkOver(SClassDefinition c) {
		int inheritedThreads = 0;
		checkOverloads(c);

		List<List<PDefinition>> superlist = new Vector<List<PDefinition>>();

		for (PDefinition def: c.getSuperDefs())
		{
			SClassDefinition superdef = (SClassDefinition)def;
			List<PDefinition> inheritable = SClassDefinitionAssistant.getInheritable(superdef);
			superlist.add(inheritable);

			if (checkOverrides(c,inheritable))
			{
				inheritedThreads++;
			}
		}

		if (inheritedThreads > 1)
		{
			TypeCheckerErrors.report(3001, "Class inherits thread definition from multiple supertypes",c.getLocation(),c);
		}

		checkAmbiguities(c,superlist);
		
	}

	private static void checkAmbiguities(SClassDefinition c, List<List<PDefinition>> superlist) {
		int count = superlist.size();

		for (int i=0; i<count; i++)
		{
			List<PDefinition> defs = superlist.get(i);

			for (int j=i+1; j<count; j++)
			{
				List<PDefinition> defs2 = superlist.get(j);
				checkAmbiguities(c,defs, defs2);
    		}
		}
		
	}

	private static void checkAmbiguities(SClassDefinition c, List<PDefinition> defs,
			List<PDefinition> defs2) {
		
		for (PDefinition indef: defs)
		{
			LexNameToken localName = indef.getName().getModifiedName(c.getName().name);

			for (PDefinition indef2: defs2)
			{
    			if (!indef.getLocation().equals(indef2.getLocation()) &&
    				PDefinitionAssistantTC.kind(indef).equals(PDefinitionAssistantTC.kind(indef2)))
    			{
    				LexNameToken localName2 = indef2.getName().getModifiedName(c.getName().name);

    				if (HelpLexNameToken.isEqual(localName, localName2))
    				{
    					PDefinition override =
    						PDefinitionListAssistant.findName(c.getDefinitions(),localName,	NameScope.NAMESANDSTATE);

    					if (override == null)	// OK if we override the ambiguity
    					{
        					TypeCheckerErrors.report(3276, "Ambiguous definitions inherited by " + c.getName().name,c.getLocation(),c);
        					TypeCheckerErrors.detail("1", indef.getName() + " " + indef.getLocation());
        					TypeCheckerErrors.detail("2", indef2.getName() + " " + indef2.getLocation());
    					}
    				}
    			}
			}
		}
		
	}

	private static boolean checkOverrides(SClassDefinition c,
			List<PDefinition> inheritable) {
		boolean inheritedThread = false;

		for (PDefinition indef: inheritable)
		{
			if (indef.getName().name.equals("thread"))
			{
				inheritedThread = true;
				continue;	// No other checks needed for threads
			}

			LexNameToken localName = indef.getName().getModifiedName(c.getName().name);

			PDefinition override =
				PDefinitionListAssistant.findName(c.getDefinitions(),localName,NameScope.NAMESANDSTATE);

			if (override == null)
			{
				override = PDefinitionListAssistant.findType(c.getDefinitions(),localName, null);
			}

			if (override != null)
			{
				if (!PDefinitionAssistantTC.kind(indef).equals(PDefinitionAssistantTC.kind(override)))
				{
					TypeCheckerErrors.report(3005, "Overriding a superclass member of a different kind: " + override.getName(),override.getName().location,override);
					TypeCheckerErrors.detail2("This", PDefinitionAssistantTC.kind(override), "Super", PDefinitionAssistantTC.kind(indef));
				}
				else if (PAccessSpecifierAssistantTC.narrowerThan(override.getAccess(),indef.getAccess()))
				{
					TypeCheckerErrors.report(3006, "Overriding definition reduces visibility",override.getName().getLocation(),override);
					TypeCheckerErrors.detail2("This", override.getName(), "Super", indef.getName());
				}
				else
				{
					PType to = PDefinitionAssistantTC.getType(indef);
					PType from = PDefinitionAssistantTC.getType(override);

					// Note this uses the "parameters only" comparator option

					if (!TypeComparator.compatible(to, from, true))
					{
						TypeCheckerErrors.report(3007, "Overriding member incompatible type: " + override.getName().name,override.getLocation(),override);
						TypeCheckerErrors.detail2("This", override.getType(), "Super", indef.getType());
					}
				}
			}
		}

		return inheritedThread;
	}

	private static void checkOverloads(SClassDefinition c) {
		List<String> done = new Vector<String>();

		List<PDefinition> singles = PDefinitionListAssistant.singleDefinitions(c.getDefinitions());

		for (PDefinition def1: singles)
		{
			for (PDefinition def2: singles)
			{
				if (def1 != def2 &&
					def1.getName() != null && def2.getName() != null &&
					def1.getName().name.equals(def2.getName().name) &&
					!done.contains(def1.getName().name))
				{
					if ((PDefinitionAssistantTC.isFunction(def1)  && PDefinitionAssistantTC.isFunction(def2)) ||
						(PDefinitionAssistantTC.isOperation(def1) && PDefinitionAssistantTC.isOperation(def2)))
					{
    					PType to = def1.getType();
    					PType from = def2.getType();

    					// Note this uses the "parameters only" comparator option

    					if (TypeComparator.compatible(to, from, true))
    					{
    						TypeCheckerErrors.report(3008, "Overloaded members indistinguishable: " + def1.getName().name,def1.getLocation(),def1);
    						TypeCheckerErrors.detail2(def1.getName().name, def1.getType(), def2.getName().name, def2.getType());
    						done.add(def1.getName().name);
    					}
					}
					else
					{
						// Class invariants can duplicate if there are several
						// "inv" clauses in one class...

						if (!(def1 instanceof AClassInvariantDefinition) &&
							!(def2 instanceof AClassInvariantDefinition) &&
							!(def1 instanceof APerSyncDefinition) &&
							!(def2 instanceof APerSyncDefinition))
						{
    						TypeCheckerErrors.report(3017, "Duplicate definitions for " + def1.getName().name,def1.getName().getLocation(),def1);
    						TypeCheckerErrors.detail2(def1.getName().name, def1.getLocation(), def2.getName().name, def2.getLocation());
    						done.add(def1.getName().name);
						}
					}
				}
			}
		}
		
	}

	public static void typeCheckPass(SClassDefinition c, Pass p,
			Environment base, TypeCheckVisitor tc) {
		if (c.getIsTypeChecked()) return;

		for (PDefinition d: c.getDefinitions())
		{
			if (PDefinitionAssistantTC.getPass(d)== p)
			{
				d.apply(tc,new TypeCheckInfo(base, NameScope.NAMES));
			}
		}

		if (c.getInvariant() != null && PDefinitionAssistantTC.getPass(c.getInvariant()) == p)
		{
			c.getInvariant().apply(tc,new TypeCheckInfo(base, NameScope.NAMES));
		}
		
	}

	public static void initializedCheck(SClassDefinition c) {
		PDefinitionListAssistant.initializedCheck(c.getDefinitions());		
	}
	
	
}
