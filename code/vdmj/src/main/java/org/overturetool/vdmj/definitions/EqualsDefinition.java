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
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.SetBind;
import org.overturetool.vdmj.patterns.TypeBind;
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
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueSet;

/**
 * A class to hold an equals definition.
 */

public class EqualsDefinition extends Definition
{
	public final Pattern pattern;
	public final TypeBind typebind;
	public final SetBind setbind;
	public final Expression test;

	public Type expType = null;
	private DefinitionList defs = null;

	public EqualsDefinition(LexLocation location, Pattern pattern, Expression test)
	{
		super(Pass.DEFS, location, null, NameScope.LOCAL);
		this.pattern = pattern;
		this.typebind = null;
		this.setbind = null;
		this.test = test;
	}

	public EqualsDefinition(LexLocation location, TypeBind typebind, Expression test)
	{
		super(Pass.DEFS, location, null, NameScope.LOCAL);
		this.pattern = null;
		this.typebind = typebind;
		this.setbind = null;
		this.test = test;
	}

	public EqualsDefinition(LexLocation location, SetBind setbind, Expression test)
	{
		super(Pass.DEFS, location, null, NameScope.LOCAL);
		this.pattern = null;
		this.typebind = null;
		this.setbind = setbind;
		this.test = test;
	}

	@Override
	public Type getType()
	{
		return expType != null ? expType : new UnknownType(location);
	}

	@Override
	public String toString()
	{
		return (pattern != null ? pattern :
				typebind != null ? typebind : setbind) + " = " + test;
	}

	@Override
	public void typeCheck(Environment base, NameScope scope)
	{
		expType = test.typeCheck(base, null, scope);

		if (pattern != null)
		{
			pattern.typeResolve(base);
			defs = pattern.getDefinitions(expType, nameScope);
		}
		else if (typebind != null)
		{
			if (!TypeComparator.compatible(typebind.type, expType))
			{
				typebind.report(3014, "Expression is not compatible with type bind");
			}

			defs = typebind.pattern.getDefinitions(expType, nameScope);
		}
		else
		{
			Type st = setbind.set.typeCheck(base, null, scope);

			if (!st.isSet())
			{
				report(3015, "Set bind is not a set type?");
			}
			else
			{
    			Type setof = st.getSet().setof;

    			if (!TypeComparator.compatible(expType, setof))
    			{
    				setbind.report(3016, "Expression is not compatible with set bind");
    			}
			}

			setbind.pattern.typeResolve(base);
			defs = setbind.pattern.getDefinitions(expType, nameScope);
		}

		defs.typeCheck(base, scope);
	}

	@Override
	public Definition findName(LexNameToken sought, NameScope scope)
	{
		if (defs != null)
		{
			Definition def = defs.findName(sought, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	@Override
	public void unusedCheck()
	{
		if (defs != null)
		{
			defs.unusedCheck();
		}
	}

	@Override
	public DefinitionList getDefinitions()
	{
		return defs == null ? new DefinitionList() : defs;
	}

	@Override
	public LexNameList getVariableNames()
	{
		return defs == null ? new LexNameList() : defs.getVariableNames();
	}

	@Override
	public NameValuePairList getNamedValues(Context ctxt)
	{
		Value v = test.eval(ctxt);
		NameValuePairList nvpl = null;

		if (pattern != null)
		{
			try
			{
				nvpl = pattern.getNamedValues(v, ctxt);
			}
			catch (PatternMatchException e)
			{
				abort(e, ctxt);
			}
		}
		else if (typebind != null)
		{
			try
			{
				Value converted = v.convertTo(typebind.type, ctxt);
				nvpl = typebind.pattern.getNamedValues(converted, ctxt);
			}
			catch (PatternMatchException e)
			{
				abort(e, ctxt);
			}
			catch (ValueException e)
			{
				abort(e);
			}
		}
		else if (setbind != null)
		{
			try
			{
				ValueSet set = setbind.set.eval(ctxt).setValue(ctxt);

				if (!set.contains(v))
				{
					abort(4002, "Expression value is not in set bind", ctxt);
				}

				nvpl = setbind.pattern.getNamedValues(v, ctxt);
			}
			catch (PatternMatchException e)
			{
				abort(e, ctxt);
			}
			catch (ValueException e)
			{
				abort(e);
			}
		}

		return nvpl;
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = new ProofObligationList();

		if (pattern != null)
		{
			if (!(pattern instanceof IdentifierPattern) &&
				!(pattern instanceof IgnorePattern) &&
				expType.isUnion())
			{
				Type patternType = pattern.getPossibleType();	// With unknowns
				UnionType ut = expType.getUnion();
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

	    			if (!TypeComparator.isSubType(expType, compatible))
	    			{
	    				list.add(new ValueBindingObligation(this, ctxt));
	    				list.add(new SubTypeObligation(test, compatible, expType, ctxt));
	    			}
				}
			}
		}
		else if (typebind != null)
		{
			// Nothing to do
		}
		else if (setbind != null)
		{
			list.addAll(setbind.set.getProofObligations(ctxt));
		}

		list.addAll(test.getProofObligations(ctxt));
		return list;
	}

	@Override
	public String kind()
	{
		return "equals";
	}
}
