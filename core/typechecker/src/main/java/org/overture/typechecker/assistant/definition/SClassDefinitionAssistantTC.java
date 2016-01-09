/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.assistant.definition;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.ClassDefinitionSettings;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.typechecker.Pass;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SClassDefinitionAssistantTC implements IAstAssistant
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public SClassDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public boolean hasSupertype(SClassDefinition classDefinition, PType other)
	{

		if (af.createPTypeAssistant().equals(getType(classDefinition), other))
		{
			return true;
		} else
		{
			for (PType type : classDefinition.getSupertypes())
			{
				AClassType sclass = (AClassType) type;

				if (af.createAClassTypeAssistant().hasSupertype(sclass, other))
				{
					return true;
				}
			}
		}

		return false;
	}

	public boolean isAccessible(Environment env, PDefinition field,
			boolean needStatic)
	{
		SClassDefinition self = env.findClassDefinition();
		SClassDefinition target = field.getClassDefinition();

		if (self == null) // Not called from within a class member
		{
			// We're outside, so just public access
			return af.createPAccessSpecifierAssistant().isPublic(field.getAccess());
		} else
		{
			AClassType selftype = (AClassType) getType(self);
			AClassType targtype = (AClassType) getType(target);

			if (!af.createPTypeAssistant().equals(selftype, targtype))
			{
				if (af.createAClassTypeAssistant().hasSupertype(selftype, targtype))
				{
					// We're a subclass, so see public or protected
					return !af.createPAccessSpecifierAssistant().isPrivate(field.getAccess());
				} else
				{
					// We're outside, so just public/static access
					return af.createPAccessSpecifierAssistant().isPublic(field.getAccess())
							&& (needStatic ? af.createPAccessSpecifierAssistant().isStatic(field.getAccess())
									: true);
				}
			} else
			{
				// else same type, so anything goes
				return true;
			}
		}
	}

	// public static PDefinition findType(SClassDefinition classdef,
	// ILexNameToken sought, String fromModule)
	// {
	// // FIXME: This method is used and outside the TypeFinder visitor so I can't delete it!
	// // It is used in this class "public class PrivateClassEnvironment"
	// // How do I proceed in this case?
	// if (!sought.getExplicit()
	// && sought.getName().equals(classdef.getName().getName())
	// || sought.equals(classdef.getName().getClassName()))
	// {
	// return classdef; // Class referred to as "A" or "CLASS`A"
	// }
	//
	// PDefinition def = PDefinitionAssistantTC.findType(classdef.getDefinitions(), sought, null);
	//
	// if (def == null)
	// {
	// for (PDefinition d : classdef.getAllInheritedDefinitions())
	// {
	// PDefinition indef = PDefinitionAssistantTC.findType(d, sought, null);
	//
	// if (indef != null)
	// {
	// def = indef;
	// break;
	// }
	// }
	// }
	//
	// return def;
	// }

	public Set<PDefinition> findMatches(SClassDefinition classdef,
			ILexNameToken sought)
	{

		Set<PDefinition> set = af.createPDefinitionListAssistant().findMatches(classdef.getDefinitions(), sought);
		set.addAll(af.createPDefinitionListAssistant().findMatches(classdef.getAllInheritedDefinitions(), sought));
		return set;
	}

	public PDefinition findName(List<SClassDefinition> classes,
			ILexNameToken name, NameScope scope)
	{

		SClassDefinition d = get(classes, name.getModule());
		if (d != null)
		{
			PDefinition def = af.createPDefinitionAssistant().findName(d, name, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	private SClassDefinition get(List<SClassDefinition> classes, String module)
	{

		for (SClassDefinition sClassDefinition : classes)
		{
			if (sClassDefinition.getName().getName().equals(module))
			{
				return sClassDefinition;
			}
		}
		return null;
	}

	public PDefinition findType(List<SClassDefinition> classes,
			ILexNameToken name)
	{

		for (SClassDefinition d : classes)
		{
			PDefinition def = af.createPDefinitionAssistant().findType(d, name, null);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public Set<PDefinition> findMatches(List<SClassDefinition> classes,
			ILexNameToken name)
	{

		Set<PDefinition> set = new HashSet<PDefinition>();

		for (SClassDefinition d : classes)
		{
			set.addAll(af.createSClassDefinitionAssistant().findMatches(d, name));
		}

		return set;
	}

	public void unusedCheck(List<SClassDefinition> classes)
	{
		for (SClassDefinition d : classes)
		{
			af.createPDefinitionAssistant().unusedCheck(d);
		}

	}

	// public static List<PDefinition> getLocalDefinitions(
	// SClassDefinition classDefinition)
	// {
	//
	// List<PDefinition> all = new Vector<PDefinition>();
	//
	// all.addAll(classDefinition.getLocalInheritedDefinitions());
	// all.addAll(PDefinitionListAssistantTC.singleDefinitions(classDefinition.getDefinitions()));
	//
	// return all;
	// }

	public void implicitDefinitions(SClassDefinition d,
			Environment publicClasses)
	{
		if (d instanceof ASystemClassDefinition)
		{
			af.createPDefinitionAssistant().implicitDefinitions(d, publicClasses);
			// ASystemClassDefinitionAssistantTC.implicitDefinitions((ASystemClassDefinition) d, );
		} else
		{
			implicitDefinitionsBase(d, publicClasses);
		}

	}

	public void implicitDefinitionsBase(SClassDefinition d,
			Environment publicClasses)
	{
		setInherited(d, publicClasses);
		setInheritedDefinitions(d);

		AExplicitOperationDefinition invariant = getInvDefinition(d);

		d.setInvariant(invariant);

		if (invariant != null)
		{
			af.createPDefinitionAssistant().setClassDefinition(invariant, d);
		}
	}

	private AExplicitOperationDefinition getInvDefinition(SClassDefinition d)
	{

		List<PDefinition> invdefs = getInvDefs(d);

		if (invdefs.isEmpty())
		{
			return null;
		}

		// Location of last local invariant
		ILexLocation invloc = invdefs.get(invdefs.size() - 1).getLocation();

		AOperationType type = AstFactory.newAOperationType(invloc, new Vector<PType>(), AstFactory.newABooleanBasicType(invloc));
		type.setPure(true);

		LexNameToken invname = new LexNameToken(d.getName().getName(), "inv_"
				+ d.getName().getName(), invloc);

		PStm body = AstFactory.newAClassInvariantStm(invname, invdefs);

		return AstFactory.newAExplicitOperationDefinition(invname, type, new Vector<PPattern>(), null, null, body);
	}

	public List<PDefinition> getInvDefs(SClassDefinition def)
	{
		List<PDefinition> invdefs = new Vector<PDefinition>();

		if (def.getGettingInvDefs())
		{
			// reported elsewhere
			return invdefs;
		}

		def.setGettingInvDefs(true);

		for (SClassDefinition d : def.getSuperDefs())
		{
			invdefs.addAll(getInvDefs(d));
		}

		for (PDefinition d : def.getDefinitions())
		{
			if (d instanceof AClassInvariantDefinition)
			{
				invdefs.add(d);
			}
		}

		def.setGettingInvDefs(false);
		return invdefs;
	}

	private void setInheritedDefinitions(SClassDefinition definition)
	{
		List<PDefinition> indefs = new Vector<PDefinition>();

		for (SClassDefinition sclass : definition.getSuperDefs())
		{
			indefs.addAll(getInheritable(sclass));
		}

		// The inherited definitions are ordered such that the
		// definitions, taken in order, will consider the overriding
		// members before others.

		List<PDefinition> superInheritedDefinitions = new Vector<PDefinition>();

		for (PDefinition d : indefs)
		{
			superInheritedDefinitions.add(d);

			ILexNameToken localname = d.getName().getModifiedName(definition.getName().getName());

			if (af.createPDefinitionListAssistant().findName(definition.getDefinitions(), localname, NameScope.NAMESANDSTATE) == null)
			{
				AInheritedDefinition local = AstFactory.newAInheritedDefinition(localname, d);
				definition.getLocalInheritedDefinitions().add(local);
			}
		}

		definition.setSuperInheritedDefinitions(superInheritedDefinitions);
		definition.setAllInheritedDefinitions(new Vector<PDefinition>());
		definition.getAllInheritedDefinitions().addAll(superInheritedDefinitions);
		definition.getAllInheritedDefinitions().addAll(definition.getLocalInheritedDefinitions());

	}

	private List<PDefinition> getInheritable(SClassDefinition def)
	{

		List<PDefinition> defs = new Vector<PDefinition>();

		if (def.getGettingInheritable())
		{
			TypeCheckerErrors.report(3009, "Circular class hierarchy detected: "
					+ def.getName(), def.getLocation(), def);
			return defs;
		}

		def.setGettingInheritable(true);

		// The inherited definitions are ordered such that the
		// definitions, taken in order, will consider the overriding
		// members before others. So we add the local definitions
		// before the inherited ones.

		List<PDefinition> singles = af.createPDefinitionListAssistant().singleDefinitions(def.getDefinitions());

		for (PDefinition d : singles)
		{
			if (!af.createPAccessSpecifierAssistant().isPrivate(d.getAccess()))
			{
				defs.add(d);
			}
		}

		for (SClassDefinition sclass : def.getSuperDefs())
		{
			List<PDefinition> sdefs = getInheritable(sclass);

			for (PDefinition d : sdefs)
			{
				defs.add(d);

				ILexNameToken localname = d.getName().getModifiedName(def.getName().getName());

				if (af.createPDefinitionListAssistant().findName(defs, localname, NameScope.NAMESANDSTATE) == null)
				{
					AInheritedDefinition local = AstFactory.newAInheritedDefinition(localname, d);
					defs.add(local);
				}
			}
		}

		def.setGettingInheritable(false);
		return defs;
	}

	private void setInherited(SClassDefinition d, Environment base)
	{
		switch (d.getSettingHierarchy())
		{
			case UNSET:
				d.setSettingHierarchy(ClassDefinitionSettings.INPROGRESS);
				break;

			case INPROGRESS:
				TypeCheckerErrors.report(3002, "Circular class hierarchy detected: "
						+ d.getName(), d.getLocation(), d);
				return;

			case DONE:
				return;
		}

		af.createPDefinitionListAssistant().implicitDefinitions(d.getDefinitions(), base);

		for (ILexNameToken supername : d.getSupernames())
		{
			PDefinition def = base.findType(supername, null);

			if (def == null)
			{
				TypeCheckerErrors.report(3003, "Undefined superclass: "
						+ supername, d.getLocation(), d);
			} else if (def instanceof ACpuClassDefinition)
			{
				TypeCheckerErrors.report(3298, "Cannot inherit from CPU", d.getLocation(), d);
			} else if (def instanceof ABusClassDefinition)
			{
				TypeCheckerErrors.report(3299, "Cannot inherit from BUS", d.getLocation(), d);
			} else if (def instanceof ASystemClassDefinition)
			{
				TypeCheckerErrors.report(3278, "Cannot inherit from system class "
						+ supername, d.getLocation(), d);
			} else if (def instanceof SClassDefinition)
			{
				SClassDefinition superdef = (SClassDefinition) def;
				setInherited(superdef, base);

				d.getSuperDefs().add(superdef);
				d.getSupertypes().add(af.createPDefinitionAssistant().getType(superdef));
			} else
			{
				TypeCheckerErrors.report(3004, "Superclass name is not a class: "
						+ supername, d.getLocation(), d);
			}
		}

		d.setSettingHierarchy(ClassDefinitionSettings.DONE);
		return;

	}

	public void typeResolve(SClassDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		Environment cenv = new FlatEnvironment(question.assistantFactory, d.getDefinitions(), question.env);
		af.createPDefinitionListAssistant().typeResolve(d.getDefinitions(), rootVisitor, new TypeCheckInfo(question.assistantFactory, cenv));
	}

	public PDefinition findThread(SClassDefinition d)
	{
		// return SClassDefinitionAssistantTC.findName(d, d.getName().getThreadName(), NameScope.NAMES);
		return af.createPDefinitionAssistant().findName(d, d.getName().getThreadName(), NameScope.NAMES);
	}

	public PDefinition findConstructor(SClassDefinition classdef,
			List<PType> argtypes)
	{

		LexNameToken constructor = getCtorName(classdef, argtypes);
		return af.createPDefinitionAssistant().findName(classdef, constructor, NameScope.NAMES);
	}

	public static LexNameToken getCtorName(SClassDefinition classdef,
			List<PType> argtypes)
	{
		ILexNameToken name = classdef.getName();
		LexNameToken cname = new LexNameToken(name.getName(), name.getName(), classdef.getLocation());
		cname.setTypeQualifier(argtypes);
		return cname;
	}

	public PType getType(SClassDefinition def)
	{
		if (def.getClasstype() == null)
		{
			def.setClasstype(AstFactory.newAClassType(def.getLocation(), def));
		}

		return def.getClasstype();
	}

	public void checkOver(SClassDefinition c)
	{
		int inheritedThreads = 0;
		af.createSClassDefinitionAssistant().checkOverloads(c);

		List<List<PDefinition>> superlist = new Vector<List<PDefinition>>();

		for (PDefinition def : c.getSuperDefs())
		{
			SClassDefinition superdef = (SClassDefinition) def;
			List<PDefinition> inheritable = af.createSClassDefinitionAssistant().getInheritable(superdef);
			superlist.add(inheritable);

			if (checkOverrides(c, inheritable))
			{
				inheritedThreads++;
			}
		}

		if (inheritedThreads > 1)
		{
			TypeCheckerErrors.report(3001, "Class inherits thread definition from multiple supertypes", c.getLocation(), c);
		}

		checkAmbiguities(c, superlist);

	}

	private void checkAmbiguities(SClassDefinition c,
			List<List<PDefinition>> superlist)
	{
		int count = superlist.size();

		for (int i = 0; i < count; i++)
		{
			List<PDefinition> defs = superlist.get(i);

			for (int j = i + 1; j < count; j++)
			{
				List<PDefinition> defs2 = superlist.get(j);
				checkAmbiguities(c, defs, defs2);
			}
		}

	}

	private void checkAmbiguities(SClassDefinition c, List<PDefinition> defs,
			List<PDefinition> defs2)
	{

		for (PDefinition indef : defs)
		{
			ILexNameToken localName = indef.getName().getModifiedName(c.getName().getName());

			for (PDefinition indef2 : defs2)
			{
				if (!indef.getLocation().equals(indef2.getLocation())
						&& af.createPDefinitionAssistant().kind(indef).equals(af.createPDefinitionAssistant().kind(indef2)))
				{
					ILexNameToken localName2 = indef2.getName().getModifiedName(c.getName().getName());

					if (af.getLexNameTokenAssistant().isEqual(localName, localName2))
					{
						PDefinition override = af.createPDefinitionListAssistant().findName(c.getDefinitions(), localName, NameScope.NAMESANDSTATE);

						if (override == null) // OK if we override the ambiguity
						{
							TypeCheckerErrors.report(3276, "Ambiguous definitions inherited by "
									+ c.getName().getName(), c.getLocation(), c);
							TypeCheckerErrors.detail("1", indef.getName() + " "
									+ indef.getLocation());
							TypeCheckerErrors.detail("2", indef2.getName()
									+ " " + indef2.getLocation());
						}
					}
				}
			}
		}

	}

	private boolean checkOverrides(SClassDefinition c,
			List<PDefinition> inheritable)
	{
		boolean inheritedThread = false;

		for (PDefinition indef : inheritable)
		{
			if (indef.getName().getName().equals("thread"))
			{
				inheritedThread = true;
				continue; // No other checks needed for threads
			}

			ILexNameToken localName = indef.getName().getModifiedName(c.getName().getName());

			PDefinition override = af.createPDefinitionListAssistant().findName(c.getDefinitions(), localName, NameScope.NAMESANDSTATE);

			if (override == null)
			{
				override = af.createPDefinitionListAssistant().findType(c.getDefinitions(), localName, null);
			}

			if (override != null)
			{
				if (!af.createPDefinitionAssistant().kind(indef).equals(af.createPDefinitionAssistant().kind(override)))
				{
					TypeCheckerErrors.report(3005, "Overriding a superclass member of a different kind: "
							+ override.getName(), override.getName().getLocation(), override);
					TypeCheckerErrors.detail2("This", af.createPDefinitionAssistant().kind(override), "Super", af.createPDefinitionAssistant().kind(indef));
				} else if (af.createPAccessSpecifierAssistant().narrowerThan(override.getAccess(), indef.getAccess()))
				{
					TypeCheckerErrors.report(3006, "Overriding definition reduces visibility", override.getName().getLocation(),override);
					TypeCheckerErrors.detail2("This", override.getAccess().getAccess()+" "+ override.getName(), "Super",indef.getAccess().getAccess()+" "+ indef.getName());
				}
				else if (override.getAccess().getPure() != indef.getAccess().getPure())
				{
					TypeCheckerErrors.report(3341, "Overriding definition must " + (override.getAccess().getPure() ? "not" : "also") + " be pure", override.getName().getLocation(),override);
				} else
				{
					PType to = af.createPDefinitionAssistant().getType(indef);
					PType from = af.createPDefinitionAssistant().getType(override);

					// Note this uses the "parameters only" comparator option

					if (!af.getTypeComparator().compatible(to, from, true))
					{
						TypeCheckerErrors.report(3007, "Overriding member incompatible type: "
								+ override.getName().getName(), override.getLocation(), override);
						TypeCheckerErrors.detail2("This", override.getType(), "Super", indef.getType());
					}
				}
			}
		}

		return inheritedThread;
	}

	private void checkOverloads(SClassDefinition c)
	{
		List<String> done = new Vector<String>();

		List<PDefinition> singles = af.createPDefinitionListAssistant().singleDefinitions(c.getDefinitions());

		for (PDefinition def1 : singles)
		{
			for (PDefinition def2 : singles)
			{
				if (def1 != def2
						&& def1.getName() != null
						&& def2.getName() != null
						&& def1.getName().getName().equals(def2.getName().getName())
						&& !done.contains(def1.getName().getName()))
				{
					if (af.createPDefinitionAssistant().isFunction(def1)
							&& af.createPDefinitionAssistant().isFunction(def2)
							|| af.createPDefinitionAssistant().isOperation(def1)
							&& af.createPDefinitionAssistant().isOperation(def2))
					{
						PType to = def1.getType();
						PType from = def2.getType();

						// Note this uses the "parameters only" comparator option

						if (af.getTypeComparator().compatible(to, from, true))
						{
							TypeCheckerErrors.report(3008, "Overloaded members indistinguishable: "
									+ def1.getName().getName(), def1.getLocation(), def1);
							TypeCheckerErrors.detail2(def1.getName().getName(), def1.getType(), def2.getName().getName(), def2.getType());
							done.add(def1.getName().getName());
						}
					} else
					{
						// Class invariants can duplicate if there are several
						// "inv" clauses in one class...

						if (!(def1 instanceof AClassInvariantDefinition)
								&& !(def2 instanceof AClassInvariantDefinition)
								&& !(def1 instanceof APerSyncDefinition)
								&& !(def2 instanceof APerSyncDefinition))
						{
							TypeCheckerErrors.report(3017, "Duplicate definitions for "
									+ def1.getName().getName(), def1.getName().getLocation(), def1);
							TypeCheckerErrors.detail2(def1.getName().getName(), def1.getLocation().getFile().getName()
									+ " " + def1.getLocation().toShortString(), def2.getName().getName(), def2.getLocation().getFile().getName()
									+ " " + def2.getLocation().toShortString());
							done.add(def1.getName().getName());
						}
					}
				}
			}
		}

	}

	public void typeCheckPass(SClassDefinition c, Pass p, Environment base,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> tc)
			throws AnalysisException
	{
		if (c.getTypeChecked())
		{
			return;
		}

		if (p == Pass.TYPES) // First one
		{
			List<PDefinition> localDefs = new LinkedList<PDefinition>();
			localDefs.addAll(c.getDefinitions());
			localDefs.addAll(c.getLocalInheritedDefinitions());
			af.createPDefinitionListAssistant().removeDuplicates(localDefs);
			c.setIsAbstract(af.createPDefinitionListAssistant().hasSubclassResponsibilities(localDefs));
		}

		for (PDefinition d : c.getDefinitions())
		{
			if (d.getPass() == p)
			{
				Environment env = base;

				if (d instanceof AValueDefinition)
				{
					// ValueDefinition body always a static context
					FlatCheckedEnvironment checked = new FlatCheckedEnvironment(af, new Vector<PDefinition>(), base, NameScope.NAMES);
					checked.setStatic(true);
					env = checked;
				}

				d.apply(tc, new TypeCheckInfo(af, env, NameScope.NAMES));
			}
		}

		if (c.getInvariant() != null && c.getInvariant().getPass() == p)
		{
			c.getInvariant().apply(tc, new TypeCheckInfo(af, base, NameScope.NAMES));
		}

	}

	public void initializedCheck(SClassDefinition c)
	{
		af.createPDefinitionListAssistant().initializedCheck(c.getDefinitions());
	}

}
