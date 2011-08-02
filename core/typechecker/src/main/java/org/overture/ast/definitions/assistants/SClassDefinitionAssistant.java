package org.overture.ast.definitions.assistants;

import java.util.HashSet;
import java.util.LinkedList;
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
import org.overture.typecheck.Environment;
import org.overture.typecheck.FlatEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.ClassDefinitionSettings;
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
			return (PAccessSpecifierTCAssistant.isPublic(field.getAccess()));
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
					return (PAccessSpecifierTCAssistant.isPrivate(field.getAccess()));
				}
				else
				{
					// We're outside, so just public/static access
					return (PAccessSpecifierTCAssistant.isPublic(field.getAccess()) &&
							(needStatic ? PAccessSpecifierTCAssistant.isStatic(field.getAccess()) : true));
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
		
		Set<PDefinition> set = PDefinitionListAssistant.findMatches(classdef.getDefinitions(),sought);
		set.addAll(PDefinitionListAssistant.findMatches(classdef.getAllInheritedDefinitions(),sought));
		return set;
	}

	public static void unusedCheck(SClassDefinition classdef) {
		if (!classdef.getUsed())
		{
			TypeCheckerErrors.warning(5000, "Definition '" + classdef.getName() + "' not used",classdef.getLocation(),classdef);
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
				classDefinition.getName().getSelfName(), NameScope.LOCAL,
				false, null, null, classDefinition.getType(), false);
		PDefinitionAssistant.markUsed(def);
		return def;
	}

	public static LexNameList getVariableNames(SClassDefinition d) {
		return PDefinitionListAssistant.getVariableNames(d.getDefinitions());
	}

	public static void implicitDefinitions(SClassDefinition d, Environment publicClasses) {
		
		setInherited(d,publicClasses);
		setInheritedDefinitions(d);

		PDefinition invariant = getInvDefinition(d);

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

	private static PDefinition getInvDefinition(SClassDefinition d) {
		
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
		AExplicitOperationDefinition res = new AExplicitOperationDefinition(invloc, invname, NameScope.GLOBAL, false, PAccessSpecifierTCAssistant.getDefault(), null, body, null, null, type, null,false);
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
				AInheritedDefinition local = new AInheritedDefinition(definition.getLocation(),localname,null,false,null, null, null, d, null);
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
			if (!PAccessSpecifierTCAssistant.isPrivate(d.getAccess()))
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

				LexNameToken localname = d.getName().getModifiedName(d.getName().name);

				if (PDefinitionListAssistant.findName(defs, localname, NameScope.NAMESANDSTATE) == null)
				{
					AInheritedDefinition local = new AInheritedDefinition(d.getLocation(),null,null,false,null,null,null,d, null);
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
				d.getSupertypes().add(superdef.getType());
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
		
		TypeCheckInfo newInfo = new TypeCheckInfo();
		newInfo.env = cenv;
		newInfo.qualifiers = (LinkedList<PType>) question.qualifiers.clone();
		newInfo.scope = question.scope;
		
		PDefinitionListAssistant.typeResolve(d.getDefinitions(),rootVisitor,newInfo);
		
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
			def.setClasstype(new AClassType(def.getLocation(), false, null, def));
		}

		return def.getClasstype();
	}
	
	
}
