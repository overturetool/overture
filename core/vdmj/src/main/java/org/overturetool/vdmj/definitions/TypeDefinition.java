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

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.Pass;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.InvariantType;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnresolvedType;
import org.overturetool.vdmj.values.FunctionValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;


/**
 * A class to hold a type definition.
 */

public class TypeDefinition extends Definition
{
	private static final long serialVersionUID = 1L;
	public Type type;
	public final Pattern invPattern;
	public final Expression invExpression;
	public ExplicitFunctionDefinition invdef;
	public boolean infinite = false;

	public TypeDefinition(LexNameToken name, InvariantType type, Pattern invPattern,
		Expression invExpression)
	{
		super(Pass.TYPES, name.location, name, NameScope.TYPENAME);

		this.type = type;
		this.invPattern = invPattern;
		this.invExpression = invExpression;
	}

	@Override
	public String toString()
	{
		return accessSpecifier.ifSet(" ") +
				name.name + " = " + type.toDetailedString() +
				(invPattern == null ? "" :
					"\n\tinv " + invPattern + " == " + invExpression);
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
	}

	@Override
	public void implicitDefinitions(Environment base)
	{
		if (invPattern != null)
		{
    		invdef = getInvDefinition();
    		invdef.setAccessSpecifier(accessSpecifier);
    		((InvariantType)type).setInvariant(invdef);
		}
		else
		{
			invdef = null;
		}
	}

	@Override
	public void typeResolve(Environment base)
	{
		try
		{
			infinite = false;
			type = type.typeResolve(base, this);

			if (infinite)
			{
				report(3050, "Type '" + name + "' is infinite");
			}

			if (invdef != null)
			{
				invdef.typeResolve(base);
				invPattern.typeResolve(base);
			}
		}
		catch (TypeCheckException e)
		{
			type.unResolve();
			throw e;
		}
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		if (invdef != null)
		{
			invdef.typeCheck(base, scope);
		}
	}

	@Override
	public Type getType()
	{
		if (type instanceof NamedType)
		{
			return type;
		}
		else
		{
			return new NamedType(name, type);
		}
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope incState)
	{
		if (invdef != null && invdef.findName(sought, incState) != null)
		{
			return invdef;
		}

		return null;
	}

	@Override
	public Definition findType(LexNameToken sought)
	{
		if (type instanceof NamedType)
		{
			NamedType nt = (NamedType)type;

			if (nt.type instanceof RecordType)
			{
				RecordType rt = (RecordType)nt.type;

				if (rt.name.equals(sought))
				{
					return this;	// T1 = compose T2 x:int end;
				}
			}
		}

		return super.findName(sought, NameScope.TYPENAME);
	}

	@Override
	public Expression findExpression(int lineno)
	{
		if (invdef != null)
		{
			Expression found = invdef.findExpression(lineno);
			if (found != null) return found;
		}

		return null;
	}

	@Override
	public DefinitionList getDefinitions()
	{
		DefinitionList defs = new DefinitionList(this);

		if (invdef != null)
		{
			defs.add(invdef);
		}

		return defs;
	}

	@Override
	public LexNameList getVariableNames()
	{
		// This is only used in VDM++ type inheritance
		return new LexNameList(name);
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		NameValuePairList nvl = new NameValuePairList();

		if (invdef != null)
		{
			FunctionValue invfunc =	new FunctionValue(invdef, null, null, ctxt);
			nvl.add(new NameValuePair(invdef.name, invfunc));
		}

		return nvl;
	}

	private ExplicitFunctionDefinition getInvDefinition()
	{
		LexLocation loc = invPattern.location;
		PatternList params = new PatternList();
		params.add(invPattern);

		List<PatternList> parameters = new Vector<PatternList>();
		parameters.add(params);

		TypeList ptypes = new TypeList();
		ptypes.add(new UnresolvedType(name));
		FunctionType ftype =
			new FunctionType(loc, false, ptypes, new BooleanType(loc));

		ExplicitFunctionDefinition def = new ExplicitFunctionDefinition(
			name.getInvName(loc),
			NameScope.GLOBAL, null, ftype, parameters, invExpression,
			null, null, true, false, null);

		def.setAccessSpecifier(accessSpecifier);	// Same as type's
		def.classDefinition = classDefinition;
		ftype.definitions = new DefinitionList(def);
		return def;
	}

	@Override
	public boolean isRuntime()
	{
		return false;	// Though the inv definition is, of course
	}

	@Override
	public boolean isTypeDefinition()
	{
		return true;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = new ProofObligationList();

		if (invdef != null)
		{
			list.addAll(invdef.getProofObligations(ctxt));
		}

		return list;
	}

	@Override
	public String kind()
	{
		return "type";
	}
}
