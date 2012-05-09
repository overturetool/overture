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

package org.overturetool.vdmj.patterns;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.expressions.ExpressionList;
import org.overturetool.vdmj.expressions.MkTypeExpression;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.traces.Permutor;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.UnresolvedType;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FieldMap;
import org.overturetool.vdmj.values.FieldValue;
import org.overturetool.vdmj.values.NameValuePair;
import org.overturetool.vdmj.values.NameValuePairList;
import org.overturetool.vdmj.values.NameValuePairMap;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;


public class RecordPattern extends Pattern
{
	private static final long serialVersionUID = 1L;
	public final LexNameToken typename;
	public final PatternList plist;
	public Type type;

	public RecordPattern(LexNameToken typename, PatternList list)
	{
		super(typename.location);
		this.plist = list;
		this.typename = typename;
		this.type = new UnresolvedType(typename);
	}

	@Override
	public String toString()
	{
		return "mk_" + type + "(" + Utils.listToString(plist) + ")";
	}

	@Override
	public Expression getMatchingExpression()
	{
		ExpressionList list = new ExpressionList();

		for (Pattern p: plist)
		{
			list.add(p.getMatchingExpression());
		}

		return new MkTypeExpression(typename, list);
	}

	@Override
	public void unResolve()
	{
		type.unResolve();
		resolved = false;
	}

	@Override
	public void typeResolve(Environment env)
	{
		if (resolved) return; else { resolved = true; }

		try
		{
			plist.typeResolve(env);
			type = type.typeResolve(env, null);
		}
		catch (TypeCheckException e)
		{
			unResolve();
			throw e;
		}
	}

	@Override
	public DefinitionList getAllDefinitions(Type exptype, NameScope scope)
	{
		DefinitionList defs = new DefinitionList();

		if (!type.isRecord())
		{
			report(3200, "Mk_ expression is not a record type");
			detail("Type", type);
			return defs;
		}

		RecordType pattype = type.getRecord();
		Type using = exptype.isType(pattype.name.getName());

		if (using == null || !(using instanceof RecordType))
		{
			report(3201, "Matching expression is not a compatible record type");
			detail2("Pattern type", type, "Expression type", exptype);
			return defs;
		}

		// RecordType usingrec = (RecordType)using;

		if (pattype.fields.size() != plist.size())
		{
			report(3202, "Record pattern argument/field count mismatch");
		}
		else
		{
			Iterator<Field> patfi = pattype.fields.iterator();

    		for (Pattern p: plist)
    		{
    			Field pf = patfi.next();
    			// defs.addAll(p.getDefinitions(usingrec.findField(pf.tag).type, scope));
    			defs.addAll(p.getAllDefinitions(pf.type, scope));
    		}
		}

		return defs;
	}

	@Override
	public LexNameList getAllVariableNames()
	{
		LexNameList list = new LexNameList();

		for (Pattern p: plist)
		{
			list.addAll(p.getAllVariableNames());
		}

		return list;
	}

	@Override
	protected List<NameValuePairList> getAllNamedValues(Value expval, Context ctxt)
		throws PatternMatchException
	{
		FieldMap fields = null;
		RecordValue exprec = null;

		try
		{
			exprec = expval.recordValue(ctxt);
			fields = exprec.fieldmap;
		}
		catch (ValueException e)
		{
			patternFail(e);
		}

		// if (!type.equals(exprec.type))
		if (!TypeComparator.compatible(type, exprec.type))
		{
			patternFail(4114, "Record type does not match pattern");
		}

		if (fields.size() != plist.size())
		{
			patternFail(4115, "Record expression does not match pattern");
		}

		Iterator<FieldValue> iter = fields.iterator();
		List<List<NameValuePairList>> nvplists = new Vector<List<NameValuePairList>>();
		int psize = plist.size();
		int[] counts = new int[psize];
		int i = 0;

		for (Pattern p: plist)
		{
			List<NameValuePairList> pnvps = p.getAllNamedValues(iter.next().value, ctxt);
			nvplists.add(pnvps);
			counts[i++] = pnvps.size();
		}

		Permutor permutor = new Permutor(counts);
		List<NameValuePairList> finalResults = new Vector<NameValuePairList>();

		if (plist.isEmpty())
		{
			finalResults.add(new NameValuePairList());
			return finalResults;
		}

		while (permutor.hasNext())
		{
			try
			{
				NameValuePairMap results = new NameValuePairMap();
				int[] selection = permutor.next();

				for (int p=0; p<psize; p++)
				{
					for (NameValuePair nvp: nvplists.get(p).get(selection[p]))
					{
						Value v = results.get(nvp.name);

						if (v == null)
						{
							results.put(nvp);
						}
						else	// Names match, so values must also
						{
							if (!v.equals(nvp.value))
							{
								patternFail(4116, "Values do not match record pattern");
							}
						}
					}
				}

				finalResults.add(results.asList());		// Consistent set of nvps
			}
			catch (PatternMatchException pme)
			{
				// try next perm
			}
		}

		if (finalResults.isEmpty())
		{
			patternFail(4116, "Values do not match record pattern");
		}

		return finalResults;
	}

	@Override
	public Type getPossibleType()
	{
		return type;
	}

	@Override
	public boolean isConstrained()
	{
		for (Pattern p: plist)
		{
			if (p.isConstrained()) return true;
		}

		return false;
	}

	@Override
	public List<IdentifierPattern> findIdentifiers()
	{
		List<IdentifierPattern> list = new Vector<IdentifierPattern>();

		for (Pattern p: plist)
		{
			list.addAll(p.findIdentifiers());
		}

		return list;
	}
}
