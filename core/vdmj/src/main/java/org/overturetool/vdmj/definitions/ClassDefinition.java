/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.definitions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ObjectContext;
import org.overturetool.vdmj.runtime.StateContext;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.statements.ClassInvariantStatement;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatEnvironment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.values.ClassInvariantListener;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.OperationValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

/**
 * A class to represent a VDM++ class definition.
 */

public class ClassDefinition extends Definition
{
	private static final long serialVersionUID = 1L;

	/** The names of the superclasses of this class. */
	public final LexNameList supernames;
	/** The definitions in this class (excludes superclasses). */
	public final DefinitionList definitions;

	/** Definitions inherited from superclasses. */
	public DefinitionList superInheritedDefinitions = null;
	/** Definitions inherited, but accessed locally. */
	public DefinitionList localInheritedDefinitions = null;
	/** The combination of all inherited definitions. */
	public DefinitionList allInheritedDefinitions = null;

	/** A list of ClassTypes for the superclasses. */
	public TypeList supertypes = null;
	/** A list of ClassDefinitions for the superclasses. */
	public ClassList superdefs = null;
	/** This class' class type. */
	public Type classtype = null;

	/** Used during the linkage of the class hierarchy. */
	private enum Setting { UNSET, INPROGRESS, DONE }
	volatile private Setting settingHierarchy = Setting.UNSET;

	/** True if loaded from an object file (so type checked) */
	public boolean typechecked = false;

	/** The private or protected static values in the class. */
	private NameValuePairMap privateStaticValues = null;
	/** The public visible static values in the class. */
	private NameValuePairMap publicStaticValues = null;
	/** True if the class' static members are initialized. */
	private boolean staticInit = false;
	/** True if the class' static values are initialized. */
	private boolean staticValuesInit = false;

	/** The class invariant operation definition, if any. */
	public ExplicitOperationDefinition invariant = null;
	/** The class invariant operation value, if any. */
	public OperationValue invopvalue = null;
	/** A listener for changes that require the class invariant to be checked. */
	public ClassInvariantListener invlistener = null;
	/** True if the class defines any abstract operations or functions. */
	public boolean isAbstract = false;

	/**
	 * Create a class definition with the given name, list of superclass names,
	 * and list of local definitions.
	 *
	 * @param className
	 * @param supernames
	 * @param definitions
	 */

	public ClassDefinition(LexNameToken className,
		LexNameList supernames, DefinitionList definitions)
	{
		super(Pass.DEFS, className.location, className, NameScope.CLASSNAME);

		this.supernames = supernames;
		this.definitions = definitions;

		this.used = true;
		this.superdefs = new ClassList();
		this.supertypes = new TypeList();
		this.superInheritedDefinitions = new DefinitionList();
		this.localInheritedDefinitions = new DefinitionList();
		this.allInheritedDefinitions = new DefinitionList();

		// Classes are all effectively public types
		this.setAccessSpecifier(new AccessSpecifier(false, false, Token.PUBLIC));
		this.definitions.setClassDefinition(this);
	}

	/**
	 * Create a dummy class for the interpreter.
	 */

	public ClassDefinition()
	{
		this(new LexNameToken("CLASS", "DEFAULT", new LexLocation()),
			new LexNameList(), new DefinitionList());

		privateStaticValues = new NameValuePairMap();
		publicStaticValues = new NameValuePairMap();
	}

	/**
	 * Link the class hierarchy and generate the invariant operation.
	 * @see org.overturetool.vdmj.definitions.Definition#implicitDefinitions(org.overturetool.vdmj.typechecker.Environment)
	 */

	@Override
	public void implicitDefinitions(Environment publicClasses)
	{
		setInherited(publicClasses);
		setInheritedDefinitions();

		invariant = getInvDefinition();

		if (invariant != null)
		{
			invariant.setClassDefinition(this);
			invopvalue = new OperationValue(invariant, null, null, null);
			invlistener = new ClassInvariantListener(invopvalue);
		}
	}

	/**
	 * Check definitions for illegal function/operation overloading and
	 * for illegal overriding.
	 */

	public void checkOver()
	{
		int inheritedThreads = 0;
		checkOverloads();

		List<DefinitionList> superlist = new Vector<DefinitionList>();

		for (Definition def: superdefs)
		{
			ClassDefinition superdef = (ClassDefinition)def;
			DefinitionList inheritable = superdef.getInheritable();
			superlist.add(inheritable);

			if (checkOverrides(inheritable))
			{
				inheritedThreads++;
			}
		}

		if (inheritedThreads > 1)
		{
			report(3001, "Class inherits thread definition from multiple supertypes");
		}

		checkAmbiguities(superlist);
	}

	/**
	 * Create the class hierarchy. This populates the superdefs and supertypes
	 * fields, while first calling setInherited of the superclasses first. The
	 * settingHierarchy field is used to avoid loops.
	 *
	 * @param base
	 */

	private void setInherited(Environment base)
	{
		switch (settingHierarchy)
		{
			case UNSET:
				settingHierarchy = Setting.INPROGRESS;
				break;

			case INPROGRESS:
				report(3002, "Circular class hierarchy detected: " + name);
				return;

			case DONE:
				return;
		}

		definitions.implicitDefinitions(base);

		for (LexNameToken supername: supernames)
		{
			Definition def = base.findType(supername);

			if (def == null)
			{
				report(3003, "Undefined superclass: " + supername);
			}
			else if (def instanceof CPUClassDefinition)
			{
				report(3298, "Cannot inherit from CPU");
			}
			else if (def instanceof BUSClassDefinition)
			{
				report(3299, "Cannot inherit from BUS");
			}
			else if (def instanceof SystemDefinition)
			{
				report(3278, "Cannot inherit from system class " + supername);
			}
			else if (def instanceof ClassDefinition)
			{
				ClassDefinition superdef = (ClassDefinition)def;
				superdef.setInherited(base);
				superdefs.add(superdef);
				supertypes.add(superdef.getType());
			}
			else
			{
				report(3004, "Superclass name is not a class: " + supername);
			}
		}

		settingHierarchy = Setting.DONE;
		return;
	}

	/**
	 * Check for illegal overrides, given a list of inherited functions and
	 * operations.
	 *
	 * @param inheritable
	 */

	private boolean checkOverrides(DefinitionList inheritable)
	{
		boolean inheritedThread = false;

		for (Definition indef: inheritable)
		{
			if (indef.name.name.equals("thread"))
			{
				inheritedThread = true;
				continue;	// No other checks needed for threads
			}

			LexNameToken localName = indef.name.getModifiedName(name.name);

			Definition override =
				definitions.findName(localName,	NameScope.NAMESANDSTATE);

			if (override == null)
			{
				override = definitions.findType(localName);
			}

			if (override != null)
			{
				if (!indef.kind().equals(override.kind()))
				{
					override.report(3005, "Overriding a superclass member of a different kind: " + override.name);
					override.detail2("This", override.kind(), "Super", indef.kind());
				}
				else if (override.accessSpecifier.narrowerThan(indef.accessSpecifier))
				{
					override.report(3006, "Overriding definition narrows scope");
					override.detail2("This", override.name, "Super", indef.name);
				}
				else
				{
					Type to = indef.getType();
					Type from = override.getType();

					// Note this uses the "parameters only" comparator option

					if (!TypeComparator.compatible(to, from, true))
					{
						override.report(3007, "Overriding member incompatible type: " + override.name.name);
						override.detail2("This", override.getType(), "Super", indef.getType());
					}
				}
			}
		}

		return inheritedThread;
	}

	private void checkAmbiguities(List<DefinitionList> superlist)
	{
		int count = superlist.size();

		for (int i=0; i<count; i++)
		{
			DefinitionList defs = superlist.get(i);

			for (int j=i+1; j<count; j++)
			{
				DefinitionList defs2 = superlist.get(j);
				checkAmbiguities(defs, defs2);
    		}
		}
	}

	private void checkAmbiguities(DefinitionList defs, DefinitionList defs2)
	{
		for (Definition indef: defs)
		{
			LexNameToken localName = indef.name.getModifiedName(name.name);

			for (Definition indef2: defs2)
			{
    			if (!indef.location.equals(indef2.location) &&
    				indef.kind().equals(indef2.kind()))
    			{
    				LexNameToken localName2 = indef2.name.getModifiedName(name.name);

    				if (localName.equals(localName2))
    				{
    					Definition override =
    						definitions.findName(localName,	NameScope.NAMESANDSTATE);

    					if (override == null)	// OK if we override the ambiguity
    					{
        					report(3276, "Ambiguous definitions inherited by " + name.name);
        					detail("1", indef.name + " " + indef.location);
        					detail("2", indef2.name + " " + indef2.location);
    					}
    				}
    			}
			}
		}
	}

	/**
	 * Check for illegal overloads of the definitions in the class. Note that
	 * ambiguous overloads between classes in the hierarchy are only detected
	 * when the name is used, and so appears in findName below.
	 */

	private void checkOverloads()
	{
		List<String> done = new Vector<String>();

		for (Definition def1: definitions)
		{
			for (Definition def2: definitions)
			{
				if (def1 != def2 &&
					def1.name != null && def2.name != null &&
					def1.name.name.equals(def2.name.name) &&
					!done.contains(def1.name.name))
				{
					if (def1.isFunctionOrOperation() && def2.isFunctionOrOperation() &&
						def1.getClass() == def2.getClass())		// Both fns or ops
					{
    					Type to = def1.getType();
    					Type from = def2.getType();

    					// Note this uses the "parameters only" comparator option

    					if (TypeComparator.compatible(to, from, true))
    					{
    						def1.report(3008, "Overloaded members indistinguishable: " + def1.name.name);
    						detail2(def1.name.name, def1.getType(), def2.name.name, def2.getType());
    						done.add(def1.name.name);
    					}
					}
					else
					{
						// Class invariants can duplicate if there are several
						// "inv" clauses in one class...

						if (!(def1 instanceof ClassInvariantDefinition) &&
							!(def2 instanceof ClassInvariantDefinition) &&
							!(def1 instanceof PerSyncDefinition) &&
							!(def2 instanceof PerSyncDefinition))
						{
    						def1.report(3017, "Duplicate definitions for " + def1.name.name);
    						detail2(def1.name.name, def1.getType(), def2.name.name, def2.getType());
    						done.add(def1.name.name);
						}
					}
				}
			}
		}
	}

	/**
	 * Set superInheritedDefinitions and localInheritedDefinitions.
	 */

	private void setInheritedDefinitions()
	{
		DefinitionList indefs = new DefinitionList();

		for (ClassDefinition sclass: superdefs)
		{
			indefs.addAll(sclass.getInheritable());
		}

		// The inherited definitions are ordered such that the
		// definitions, taken in order, will consider the overriding
		// members before others.

		superInheritedDefinitions = new DefinitionList();

		for (Definition d: indefs)
		{
			superInheritedDefinitions.add(d);

			LexNameToken localname = d.name.getModifiedName(name.name);

			if (definitions.findName(localname, NameScope.NAMES) == null)
			{
				InheritedDefinition local = new InheritedDefinition(localname, d);
				localInheritedDefinitions.add(local);
			}
		}

		allInheritedDefinitions =  new DefinitionList();
		allInheritedDefinitions.addAll(superInheritedDefinitions);
		allInheritedDefinitions.addAll(localInheritedDefinitions);
	}

	private boolean gettingInheritable = false;

	/**
	 * Get a list of inheritable definitions from all the superclasses. This
	 * is a deep search, including definitions from super-superclasses etc.
	 */

	private DefinitionList getInheritable()
	{
		DefinitionList defs = new DefinitionList();

		if (gettingInheritable)
		{
			report(3009, "Circular class hierarchy detected: " + name);
			return defs;
		}

		gettingInheritable = true;

		// The inherited definitions are ordered such that the
		// definitions, taken in order, will consider the overriding
		// members before others. So we add the local definitions
		// before the inherited ones.

		DefinitionList singles = definitions.singleDefinitions();

		for (Definition d: singles)
		{
			if (d.accessSpecifier.access != Token.PRIVATE)
			{
				defs.add(d);
			}
		}

		for (ClassDefinition sclass: superdefs)
		{
			DefinitionList sdefs = sclass.getInheritable();

			for (Definition d: sdefs)
			{
				defs.add(d);

				LexNameToken localname = d.name.getModifiedName(name.name);

				if (defs.findName(localname, NameScope.NAMES) == null)
				{
					InheritedDefinition local = new InheritedDefinition(localname, d);
					defs.add(local);
				}
			}
		}

		gettingInheritable = false;
		return defs;
	}

	/**
	 * Get a list of all definitions for this class, including the local
	 * definitions as well as those inherited.
	 *
	 * @see org.overturetool.vdmj.definitions.Definition#getDefinitions()
	 */

	@Override
	public DefinitionList getDefinitions()
	{
		DefinitionList all = new DefinitionList();

		all.addAll(allInheritedDefinitions);
		all.addAll(definitions.singleDefinitions());

		return all;
	}

	/**
	 * Get a list of all local definitions for this class. These are the names
	 * which can be accessed locally, without a class qualifier.
	 */

	public DefinitionList getLocalDefinitions()
	{
		DefinitionList all = new DefinitionList();

		all.addAll(localInheritedDefinitions);
		all.addAll(definitions.singleDefinitions());

		return all;
	}

	/**
	 * Generate a local definition for "self".
	 *
	 * @return a LocalDefinition for "self".
	 */

	@Override
	public Definition getSelfDefinition()
	{
		Definition def = new LocalDefinition(location,
			name.getSelfName(), NameScope.LOCAL, getType());
		def.markUsed();
		return def;
	}

	/**
	 * Get a list of the names of the variables defined locally.
	 * @see org.overturetool.vdmj.definitions.Definition#getVariableNames()
	 */

	@Override
	public LexNameList getVariableNames()
	{
		return definitions.getVariableNames();
	}

	/**
	 * Get this class' ClassType.
	 * @see org.overturetool.vdmj.definitions.Definition#getType()
	 */

	@Override
	public Type getType()
	{
		if (classtype == null)
		{
			classtype = new ClassType(location, this);
		}

		return classtype;
	}

	/**
	 * True, if this class has the one passed as a super type. Note that the
	 * class type itself is considered as "true".
	 */

	public boolean hasSupertype(Type other)
	{
		if (getType().equals(other))
		{
			return true;
		}
		else
		{
			for (Type type: supertypes)
			{
				ClassType sclass = (ClassType)type;

				if (sclass.hasSupertype(other))
				{
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * True, if the field passed can be accessed from the current context.
	 */

	static public boolean isAccessible(
		Environment env, Definition field, boolean needStatic)
	{
		ClassDefinition self = env.findClassDefinition();
		ClassDefinition target = field.classDefinition;

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

	@Override
	public String toString()
	{
		return	"class " + name.name +
				(supernames.isEmpty() ? "" : " is subclass of " + supernames) + "\n" +
				definitions.toString() +
				"end " + name.name + "\n";
	}

	/**
	 * Find a definition within this class by name. Note that this includes a
	 * check for ambiguous names that could be resolved in different ways from
	 * different overloaded members.
	 *
	 * @see org.overturetool.vdmj.definitions.Definition#findName(org.overturetool.vdmj.lex.LexNameToken, org.overturetool.vdmj.typechecker.NameScope)
	 */

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		Definition def = null;

		for (Definition d: definitions)
		{
			Definition found = d.findName(sought, scope);

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
					if (!def.location.equals(found.location) &&
						def.isFunctionOrOperation())
					{
						sought.report(3010, "Name " + sought + " is ambiguous");
						detail2("1", def.location, "2", found.location);
						break;
					}
				}
			}
		}

		if (def == null)
		{
			for (Definition d: allInheritedDefinitions)
			{
				Definition indef = d.findName(sought, scope);

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
							 !def.location.equals(indef.location) &&
							 !def.classDefinition.hasSupertype(indef.classDefinition.getType()) &&
							 def.isFunctionOrOperation())
					{
						sought.report(3011, "Name " + sought + " is multiply defined in class");
						detail2("1", def.location, "2", indef.location);
						break;
					}
				}
			}
		}

		return def;
	}

	/**
	 * Find a type definition within this class by name. Note that this includes
	 * a check for ambiguous types that could be resolved in different ways from
	 * different supertypes.
	 *
	 * @see org.overturetool.vdmj.definitions.Definition#findType(org.overturetool.vdmj.lex.LexNameToken)
	 */

	@Override
	public Definition findType(LexNameToken sought)
	{
		if ((!sought.explicit && sought.name.equals(name.name)) ||
			sought.equals(name.getClassName()))
		{
			return this;	// Class referred to as "A" or "CLASS`A"
		}

		Definition def = definitions.findType(sought);

		if (def == null)
		{
			for (Definition d: allInheritedDefinitions)
			{
				Definition indef = d.findType(sought);

				if (indef != null)
				{
					def = indef;
					break;
				}
			}
		}

		return def;
	}

	public DefinitionSet findMatches(LexNameToken sought)
	{
		DefinitionSet set = definitions.findMatches(sought);
		set.addAll(allInheritedDefinitions.findMatches(sought));
		return set;
	}

	@Override
	public Statement findStatement(int lineno)
	{
		return definitions.findStatement(lineno);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return definitions.findExpression(lineno);
	}

	/**
	 * Return the name of the constructor for this class, given a list of
	 * parameter types.
	 */

	public LexNameToken getCtorName(TypeList argtypes)
	{
		LexNameToken cname = new LexNameToken(name.name, name.name, location);
   		cname.setTypeQualifier(argtypes);
 		return cname;
	}

	/**
	 * Find a constructor definition for this class, given a list of parameter
	 * types.
	 */

	public Definition findConstructor(TypeList argtypes)
	{
		LexNameToken constructor = getCtorName(argtypes);
		return findName(constructor, NameScope.NAMES);
	}

	/**
	 * Find a thread definition for this class.
	 */

	public Definition findThread()
	{
		return findName(name.getThreadName(), NameScope.NAMES);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#typeResolve(org.overturetool.vdmj.typechecker.Environment)
	 */

	@Override
	public void typeResolve(Environment globals)
	{
		Environment cenv = new FlatEnvironment(definitions, globals);
		definitions.typeResolve(cenv);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#typeCheck(org.overturetool.vdmj.typechecker.Environment, org.overturetool.vdmj.typechecker.NameScope)
	 */

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		assert false : "Can't call Class definition type check";
	}

	/**
	 * Call typeCheck for the definitions within the class that are of the Pass
	 * type given.
	 */

	public void typeCheckPass(Pass p, Environment base)
	{
		if (typechecked) return;

		for (Definition d: definitions)
		{
			if (d.pass == p)
			{
				d.typeCheck(base, NameScope.NAMES);
			}
		}

		if (invariant != null && invariant.pass == p)
		{
			invariant.typeCheck(base, NameScope.NAMES);
		}
	}

	/**
	 * Force an initialization of the static functions/operations for the class.
	 */

	public void staticInit(Context ctxt)
	{
		staticInit = false;		// Forced initialization
		setStatics(ctxt);
	}

	/**
	 * Force an initialization of the static values for the class.
	 */

	public void staticValuesInit(Context ctxt)
	{
		staticValuesInit = false;		// Forced initialization
		setStaticValues(ctxt);
	}

	/**
	 * Initialize the static functions/operations for the class if it has not
	 * already been done. All statics are added to the global context passed in.
	 * Note that this includes private statics - access is limited by the type
	 * checker.
	 */

	private void setStatics(Context initCtxt)
	{
		if (staticInit) return; else staticInit = true;

		for (ClassDefinition sdef: superdefs)
		{
			sdef.setStatics(initCtxt);
		}

		privateStaticValues = new NameValuePairMap();
		publicStaticValues = new NameValuePairMap();

		// We initialize function and operation definitions first as these
		// can be called by variable initializations.

		setStaticDefinitions(definitions, initCtxt);
		setStaticDefinitions(localInheritedDefinitions, initCtxt);
	}

	private void setStaticDefinitions(DefinitionList defs, Context initCtxt)
	{
		for (Definition d: defs)
		{
			if ((d.isStatic() && d.isFunctionOrOperation()) ||
				d.isTypeDefinition())
			{
				// Note function and operation values are not updatable.
				// Type invariants are implicitly static, but not updatable

				// The context here is just used for free variables, of
				// which there are none at static func/op creation...

				Context empty = new Context(location, "empty", null);
				NameValuePairList nvl = d.getNamedValues(empty);

				switch (d.accessSpecifier.access)
				{
					case PRIVATE:
					case PROTECTED:
						privateStaticValues.putAllNew(nvl);
						initCtxt.putList(nvl);
						break;

					case PUBLIC:
						publicStaticValues.putAllNew(nvl);
						initCtxt.putList(nvl);
						break;
				}
			}
		}
	}

	private void setStaticValues(Context initCtxt)
	{
		if (!staticInit)
		{
			assert false : "setStaticValues called before setStatics";
		}

		if (staticValuesInit) return; else staticValuesInit = true;

		for (ClassDefinition sdef: superdefs)
		{
			sdef.setStaticValues(initCtxt);
		}

		setStaticValues(definitions, initCtxt);
		setStaticValues(localInheritedDefinitions, initCtxt);
	}

	private void setStaticValues(DefinitionList defs, Context initCtxt)
	{
		for (Definition d: defs)
		{
			if (d.isValueDefinition())
			{
				// Values are implicitly static, but NOT updatable

				NameValuePairList nvl = d.getNamedValues(initCtxt);

				switch (d.accessSpecifier.access)
				{
					case PRIVATE:
					case PROTECTED:
						privateStaticValues.putAllNew(nvl);
						initCtxt.putAllNew(nvl);
						break;

					case PUBLIC:
						publicStaticValues.putAllNew(nvl);
						initCtxt.putAllNew(nvl);
						break;
				}
			}
			else if (d.isStatic() && d.isInstanceVariable())
			{
				// Static instance variables are updatable

				NameValuePairList nvl =
					d.getNamedValues(initCtxt).getUpdatable(invlistener);

				switch (d.accessSpecifier.access)
				{
					case PRIVATE:
					case PROTECTED:
						privateStaticValues.putAllNew(nvl);
						initCtxt.putAllNew(nvl);
						break;

					case PUBLIC:
						publicStaticValues.putAllNew(nvl);
						initCtxt.putAllNew(nvl);
						break;
				}
			}
		}
	}

	public Value getStatic(LexNameToken sought)
	{
		LexNameToken local = (sought.explicit) ? sought :
								sought.getModifiedName(name.name);

		Value v = privateStaticValues.get(local);

		if (v == null)
		{
			v = publicStaticValues.get(local);

			if (v == null)
			{
				for (ClassDefinition sdef: superdefs)
				{
					v = sdef.getStatic(local);

					if (v != null)
					{
						break;
					}
				}
			}
		}

		return v;
	}

	/**
	 * Create a new ObjectValue instance of this class. If non-null, the
	 * constructor definition and argument values passed are used, otherwise
	 * the default constructor is used. If there is no default constructor,
	 * just field initializations are made.
	 */

	public ObjectValue newInstance(
		Definition ctorDefinition, ValueList argvals, Context ctxt)
		throws ValueException
	{
		if (isAbstract)
		{
			abort(4000, "Cannot instantiate abstract class " + name, ctxt);
		}

		return makeNewInstance(
			ctorDefinition, argvals, ctxt, new HashMap<LexNameToken, ObjectValue>());
	}

	/**
	 * A method to make new instances, including a list of supertype
	 * objects already constructed to allow for virtual inheritance in
	 * "diamond" inheritance graphs.
	 */

	protected ObjectValue makeNewInstance(
		Definition ctorDefinition, ValueList argvals,
		Context ctxt, Map<LexNameToken, ObjectValue> done)
		throws ValueException
	{
		setStatics(ctxt.getGlobal());		// When static member := new X()
		setStaticValues(ctxt.getGlobal());	// When static member := new X()

		List<ObjectValue> inherited = new Vector<ObjectValue>();
		NameValuePairMap members = new NameValuePairMap();

		for (ClassDefinition sdef: superdefs)
		{
			// Check the "done" list for virtual inheritance
			ObjectValue obj = done.get(sdef.name);

			if (obj == null)
			{
				obj = sdef.makeNewInstance(null, null, ctxt, done);
				done.put(sdef.name, obj);
			}

			inherited.add(obj);
		}

		// NB. we don't use localInheritedDefinitions because we're creating
		// the local definitions in this loop.

		for (Definition idef: superInheritedDefinitions)
		{
			// Inherited definitions don't notice when their referenced
			// definition names are updated with type qualifiers.
			// TODO This ought to be done at the end of type checking...

			if (idef instanceof InheritedDefinition)
			{
				InheritedDefinition i = (InheritedDefinition)idef;
				i.name.setTypeQualifier(i.superdef.name.typeQualifier);
			}

			if (idef.isRuntime())	// eg. TypeDefinitions aren't
			{
				Value v = null;

				for (ObjectValue sobj: inherited)
				{
					v = sobj.get(idef.name, true);

					if (v != null)
					{
						LexNameToken localname = idef.name.getModifiedName(name.name);

						// In a cascade of classes all overriding a name, we may
						// have already created the local name for the nearest
						// name - superInheritedDefinitions has the nearest first.

						if (members.get(localname) == null)
						{
							members.put(localname, v);
						}

						break;
					}
				}

				if (v == null)
				{
					abort(6, "Constructor for " + name.name +
											" can't find " + idef.name, ctxt);
				}
			}
		}

		members.putAll(publicStaticValues);
		members.putAll(privateStaticValues);

		// We create a RootContext here so that the scope for member
		// initializations are restricted.

		Context initCtxt = new StateContext(location, "field initializers", ctxt, null);
		initCtxt.putList(members.asList());

		// We create an empty context to pass for function creation, so that
		// there are no spurious free variables created.

		Context empty = new Context(location, "empty", null);

		for (Definition d: definitions)
		{
			if (!d.isStatic() && d.isFunctionOrOperation())
			{
				NameValuePairList nvpl = d.getNamedValues(empty);
				initCtxt.putList(nvpl);
				members.putAll(nvpl);
			}
		}

		for (Definition d: definitions)
		{
			if (!d.isStatic() && !d.isFunctionOrOperation())
			{
				NameValuePairList nvpl =
					d.getNamedValues(initCtxt).getUpdatable(invlistener);

				initCtxt.putList(nvpl);
				members.putAll(nvpl);
			}
		}

		setPermissions(definitions, members, initCtxt);
		setPermissions(superInheritedDefinitions, members, initCtxt);

		ObjectValue object =
			new ObjectValue((ClassType)getType(), members, inherited);

		if (invariant != null)
		{
			// inv_C isn't a callable member any more, but we have to set self
			// so that the invariant system can call it.

			invopvalue.setSelf(object);
		}

		Value ctor = null;

		if (ctorDefinition == null)
		{
			argvals = new ValueList();
			LexNameToken cname = getCtorName(new TypeList());
     		ctor = object.get(cname, false);
		}
		else
		{
     		ctor = object.get(ctorDefinition.name, false);
		}

		if (ctor != null)	// Class may have no constructor defined
		{
     		OperationValue ov = ctor.operationValue(ctxt);

    		ObjectContext ctorCtxt = new ObjectContext(
    				location, name.name + " constructor", ctxt, object);

       		ov.eval(argvals, ctorCtxt);
		}

		return object;
	}

	private void setPermissions(
		DefinitionList defs, NameValuePairMap members, Context initCtxt)
		throws ValueException
	{
		for (Definition d: defs)
		{
			while (d instanceof InheritedDefinition)
			{
				d = ((InheritedDefinition)d).superdef;
			}

    		if (d instanceof PerSyncDefinition)
    		{
    			PerSyncDefinition sync = (PerSyncDefinition)d;
    			ValueList overloads = members.getOverloads(sync.opname);
    			Expression exp = sync.getExpression();

    			for (Value op: overloads)
    			{
    				op.operationValue(initCtxt).setGuard(exp);
    			}
    		}
    		else if (d instanceof MutexSyncDefinition)
    		{
    			MutexSyncDefinition sync = (MutexSyncDefinition)d;

    			for (LexNameToken opname: sync.operations)
    			{
    				Expression exp = sync.getExpression(opname);
    				ValueList overloads = members.getOverloads(opname);

    				for (Value op: overloads)
    				{
    					op.operationValue(initCtxt).setGuard(exp);
    				}
    			}
    		}
		}
	}

	private boolean gettingInvDefs = false;

	/**
	 * Get a list of class invariant operation definitions, for this class and
	 * all of its supertypes.
	 */

	public DefinitionList getInvDefs()
	{
		DefinitionList invdefs = new DefinitionList();

		if (gettingInvDefs)
		{
			// reported elsewhere
			return invdefs;
		}

		gettingInvDefs = true;

		for (ClassDefinition d: superdefs)
		{
			invdefs.addAll(d.getInvDefs());
		}

		for (Definition d: definitions)
		{
			if (d instanceof ClassInvariantDefinition)
			{
				invdefs.add(d);
			}
		}

		gettingInvDefs = false;
		return invdefs;
	}

	/**
	 * Generate the class invariant definition for this class, if any.
	 */

	private ExplicitOperationDefinition getInvDefinition()
	{
		DefinitionList invdefs = getInvDefs();

		if (invdefs.isEmpty())
		{
			return null;
		}

		OperationType type = new OperationType(
			location, new TypeList(), new BooleanType(location));

		LexNameToken invname =
			new LexNameToken(name.name, "inv_" + name.name, location);

		Statement body = new ClassInvariantStatement(invname, invdefs);

		return new ExplicitOperationDefinition(invname,
						type, new PatternList(), null, null, body);
	}

	/**
	 * @see org.overturetool.vdmj.definitions.Definition#kind()
	 */

	@Override
	public String kind()
	{
		return "class";
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return definitions.getProofObligations(ctxt);
	}
}
