/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.pog.ValueBindingObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;

/**
 * A class to hold a value definition.
 */

public class ValueDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public final Pattern pattern;
	public Type type;
	public final Expression exp;

	private DefinitionList defs = null;
	protected Type expType = null;

	public ValueDefinition(Pattern p, NameScope scope, Type type, Expression exp)
	{
		super(Pass.VALUES, p.location, null, scope);

		this.pattern = p;
		this.type = type;
		this.exp = exp;

		defs = new DefinitionList();	// Overwritten in typeCheck

		for (LexNameToken var: pattern.getVariableNames())
		{
			defs.add(new UntypedDefinition(location, var, scope));
		}
	}

	@Override
	public void setClassDefinition(ClassDefinition def)
	{
		super.setClassDefinition(def);
		defs.setClassDefinition(def);
	}

	@Override
	public void setAccessSpecifier(AccessSpecifier access)
	{
		if (access == null)
		{
			access = new AccessSpecifier(true, false, Token.PRIVATE);
		}
		else if (!access.isStatic)
		{
			access = new AccessSpecifier(true, false, access.access);
		}

		super.setAccessSpecifier(access);
		defs.setAccessibility(accessSpecifier);
	}

	@Override
	public String toString()
	{
		return accessSpecifier.ifSet(" ") + pattern +
				(type == null ? "" : ":" + type) + " = " + exp;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		expType = exp.typeCheck(base, null, scope);

		if (type != null)
		{
			type = type.typeResolve(base, null);

			if (!TypeComparator.compatible(type, expType))
			{
				report(3051, "Expression does not match declared type");
				detail2("Declared", type, "Expression", expType);
			}
		}
		else
		{
			type = expType;
		}

		if (base.isVDMPP() && type instanceof NamedType)
		{
			NamedType named = (NamedType)type;
    		Definition typedef = base.findType(named.typename);

    		if (typedef.accessSpecifier.narrowerThan(accessSpecifier))
    		{
    			report(3052, "Value type visibility less than value definition");
    		}
		}

		pattern.typeResolve(base);
		DefinitionList newdefs = pattern.getDefinitions(type, nameScope);

		// The untyped definitions may have had "used" markers, so we copy
		// those into the new typed definitions, lest we get warnings

		for (Definition d: newdefs)
		{
			for (Definition u: defs)
			{
				if (u.name.equals(d.name))
				{
					if (u.isUsed())
					{
						d.markUsed();
					}

					break;
				}
			}
		}

		defs = newdefs;
		defs.setAccessibility(accessSpecifier);
		defs.setClassDefinition(classDefinition);
		defs.typeCheck(base, scope);
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		if (scope.matches(NameScope.NAMES))
		{
			return defs.findName(sought, scope);
		}

		return null;
	}

	@Override
	public Expression findExpression(int lineno)
	{
		return exp.findExpression(lineno);
	}

	@Override
	public Type getType()
	{
		return type != null ? type :
				(expType != null ? expType : new UnknownType(location));
	}

	@Override
	public void unusedCheck()
	{
		if (used)	// Indicates all definitions exported (used)
		{
			return;
		}

		if (defs != null)
		{
    		for (Definition def: defs)
    		{
    			def.unusedCheck();
    		}
		}
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return defs;	// May be UntypedDefinitions...
	}

	@Override
	public LexNameList getVariableNames()
	{
		return pattern.getVariableNames();
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		Value v = null;

		try
		{
			// Let values are dereferenced as they cannot be updated.
			v = exp.eval(ctxt).convertTo(getType(), ctxt).deref();
			return pattern.getNamedValues(v, ctxt);
     	}
	    catch (ValueException e)
     	{
     		abort(e);
     	}
		catch (PatternMatchException e)
		{
			abort(e, ctxt);
		}

		return null;
	}

	@Override
	public boolean isValueDefinition()
	{
		return true;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = exp.getProofObligations(ctxt);

		if (!(pattern instanceof IdentifierPattern) &&
			!(pattern instanceof IgnorePattern) &&
			type.isUnion())
		{
			Type patternType = pattern.getPossibleType();	// With unknowns
			UnionType ut = type.getUnion();
			TypeSet set = new TypeSet();

			for (Type u: ut.types)
			{
				if (TypeComparator.compatible(u, patternType))
				{
					set.add(u);
				}
			}

			if (!set.isEmpty())
			{
    			Type compatible = set.getType(location);

    			if (!TypeComparator.isSubType(type, compatible))
    			{
    				list.add(new ValueBindingObligation(this, ctxt));
    				list.add(new SubTypeObligation(exp, compatible, type, ctxt));
    			}
			}
		}

		if (!TypeComparator.isSubType(ctxt.checkType(exp, expType), type))
		{
			list.add(new SubTypeObligation(exp, type, expType, ctxt));
		}

		return list;
	}

	@Override
	public String kind()
	{
		return "value";
	}
}
