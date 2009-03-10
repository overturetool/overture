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

package org.overturetool.vdmj.statements;

import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;

public class FieldDesignator extends StateDesignator
{
	private static final long serialVersionUID = 1L;
	public final StateDesignator object;
	public final LexIdentifierToken field;
	private LexNameToken objectfield = null;

	public FieldDesignator(StateDesignator object, LexIdentifierToken field)
	{
		super(object.location);
		this.object = object;
		this.field = field;
	}

	@Override
	public String toString()
	{
		return object + "." + field;
	}

	@Override
	public Type typeCheck(Environment env)
	{
		Type type = object.typeCheck(env);
		TypeSet result = new TypeSet();
		boolean unique = !type.isUnion();

		if (type.isRecord())
		{
    		RecordType rec = type.getRecord();
    		Field rf = rec.findField(field.name);

    		if (rf == null)
    		{
    			concern(unique, 3246, "Unknown field name, '" + field + "'");
    			result.add(new UnknownType(field.location));
    		}
    		else
    		{
    			result.add(rf.type);
    		}
		}

		if (type.isClass())
		{
			ClassType ctype = type.getClassType();
			String cname = ctype.name.name;

			objectfield = new LexNameToken(cname, field.name, location);
			Definition fdef = ctype.classdef.findName(objectfield, NameScope.NAMESANDSTATE);

			if (fdef == null)
			{
				concern(unique, 3260, "Unknown class field name, '" + field + "'");
				result.add(new UnknownType(location));
			}
			else
			{
				result.add(fdef.getType());
			}
		}

		if (result.isEmpty())
		{
			report(3245, "Field assignment is not of a record or object type");
			detail2("Expression", object, "Type", type);
			return new UnknownType(field.location);
		}

		return result.getType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		Value result = null;

		try
		{
			result = object.eval(ctxt).deref();

			if (result instanceof ObjectValue && objectfield != null)
			{
    			ObjectValue ov = result.objectValue(ctxt);
    			Value rv = ov.get(objectfield, false);

    			if (rv == null)
    			{
    				abort(4045, "Object does not contain value for field: " + field, ctxt);
    			}

    			return rv;
			}
			else if (result instanceof RecordValue)
			{
    			RecordValue rec = result.recordValue(ctxt);
    			result = rec.fieldmap.get(field.name);

    			if (result == null)
    			{
    				field.abort(4037, "No such field: " + field, ctxt);
    			}

    			return result;
			}
		}
		catch (ValueException e)
		{
			abort(e);
		}

		return result;
	}
}
