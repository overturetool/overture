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

package org.overturetool.vdmj.expressions;

import java.util.List;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.pog.SubTypeObligation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.util.Utils;
import org.overturetool.vdmj.values.FieldMap;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;


public class MuExpression extends Expression
{
	public final Expression record;
	public final List<RecordModifier> modifiers;

	private RecordType recordType = null;
	private TypeList modTypes = null;

	public MuExpression(LexLocation location,
		Expression record, List<RecordModifier> modifiers)
	{
		super(location);
		this.record = record;
		this.modifiers = modifiers;
	}

	@Override
	public String toString()
	{
		return "mu(" + record + ", " + Utils.listToString(modifiers) + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Type rtype = record.typeCheck(env, null, scope);

		if (rtype.isUnknown())
		{
			return rtype;
		}

		if (rtype.isRecord())
		{
			recordType = rtype.getRecord();
			modTypes = new TypeList();

    		for (RecordModifier rm: modifiers)
    		{
    			Type mtype = rm.value.typeCheck(env, null, scope);
    			modTypes.add(mtype);
    			Field f = recordType.findField(rm.tag.name);

    			if (f != null)
    			{
					if (!TypeComparator.compatible(f.type, mtype))
					{
						report(3130, "Modifier for " + f.tag + " should be " + f.type);
						detail("Actual", mtype);
					}
    			}
    			else
    			{
    				report(3131, "Modifier tag " + rm.tag + " not found in record");
    			}
    		}
		}
		else
		{
			report(3132, "mu operation on non-record type");
		}

		return rtype;
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);

		try
		{
    		RecordValue r = record.eval(ctxt).recordValue(ctxt);
    		FieldMap fields = new FieldMap(r.fieldmap);

    		for (RecordModifier rm: modifiers)
    		{
    			Field f = r.type.findField(rm.tag.name);

    			if (f == null)
    			{
        			abort(4023, "Mu type conflict? No field tag " + rm.tag.name, ctxt);
    			}
    			else
    			{
    				fields.add(rm.tag.name, rm.value.eval(ctxt), !f.equalityAbstration);
    			}
     		}

     		return new RecordValue(r.type, fields, ctxt);
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public Expression findExpression(int lineno)
	{
		Expression found = super.findExpression(lineno);
		if (found != null) return found;

		return record.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		ProofObligationList list = record.getProofObligations(ctxt);
		int i = 0;

		for (RecordModifier rm: modifiers)
		{
			list.addAll(rm.value.getProofObligations(ctxt));

			Field f = recordType.findField(rm.tag.name);
			Type mtype = modTypes.get(i++);

			if (f != null)
			{
				if (!TypeComparator.isSubType(mtype, f.type))
				{
					list.add(new SubTypeObligation(rm.value, f.type, mtype, ctxt));
				}
			}
		}

		return list;
	}

	@Override
	public String kind()
	{
		return "mu";
	}
}
