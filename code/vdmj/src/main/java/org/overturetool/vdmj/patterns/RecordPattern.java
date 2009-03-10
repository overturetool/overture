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

import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.PatternMatchException;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeCheckException;
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
	public String getMatchingValue()
	{
		return "mk_" + type + "(" + plist.getMatchingValues() + ")";
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
	public DefinitionList getDefinitions(Type exptype, NameScope scope)
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
    			defs.addAll(p.getDefinitions(pf.type, scope));
    		}
		}

		return defs;
	}

	@Override
	public LexNameList getVariableNames()
	{
		LexNameList list = new LexNameList();

		for (Pattern p: plist)
		{
			list.addAll(p.getVariableNames());
		}

		return list;
	}

	@Override
	public int getLength()
	{
		return plist.size();
	}

	@Override
	public NameValuePairList getNamedValues(Value expval, Context ctxt)
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

		if (!type.equals(exprec.type))
		{
			patternFail(4114, "Record type does not match pattern");
		}

		if (fields.size() != plist.size())
		{
			patternFail(4115, "Record expression does not match pattern");
		}

		Iterator<FieldValue> iter = fields.iterator();
		NameValuePairMap results = new NameValuePairMap();

		for (Pattern p: plist)
		{
			for (NameValuePair nvp: p.getNamedValues(iter.next().value, ctxt))
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

		return results.asList();
	}

	@Override
	public Type getPossibleType()
	{
		return type;
	}
}
