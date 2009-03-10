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
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.ClassType;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnknownType;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;

public class ObjectFieldDesignator extends ObjectDesignator
{
	private static final long serialVersionUID = 1L;
	public final ObjectDesignator object;
	public String classname;
	public final String fieldname;

	private LexNameToken field = null;

	public ObjectFieldDesignator(
		ObjectDesignator object, String classname, String fieldname)
	{
		super(object.location);
		this.object = object;
		this.classname = classname;
		this.fieldname = fieldname;
	}

	@Override
	public String toString()
	{
		return object + "." +
			(classname == null ? "" : classname + "`") + fieldname;
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers)
	{
		Type type = object.typeCheck(env, qualifiers);
		TypeSet result = new TypeSet();
		boolean unique = !type.isUnion();

		if (type.isClass())
		{
			ClassType ctype = type.getClassType();
			String cname = (classname == null) ? ctype.name.name : classname;

			field = new LexNameToken(cname, fieldname, location);
			field.setTypeQualifier(qualifiers);
			Definition fdef = ctype.classdef.findName(field, NameScope.NAMESANDSTATE);

			if (fdef == null)
			{
				concern(unique, 3260, "Unknown class member name, '" + field + "'");
				result.add(new UnknownType(location));
			}
			else
			{
				result.add(fdef.getType());
			}
		}

		if (type.isRecord())
		{
			RecordType rec = type.getRecord();
			Field rf = rec.findField(fieldname);

			if (rf == null)
			{
				concern(unique, 3261, "Unknown field name, '" + fieldname + "'");
				result.add(new UnknownType(location));
			}
			else
			{
				result.add(rf.type);
			}
		}

		if (result.isEmpty())
		{
			report(3262, "Field assignment is not of a class or record type");
			detail2("Expression", object, "Type", type);
			return new UnknownType(location);
		}

		return result.getType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		try
		{
			Value val = object.eval(ctxt).deref();

			if (val instanceof ObjectValue && field != null)
			{
    			ObjectValue ov = val.objectValue(ctxt);
    			Value rv = ov.get(field, (classname != null));

    			if (rv == null)
    			{
    				abort(4045, "Object does not contain value for field: " + field, ctxt);
    			}

    			return rv;
			}
			else if (val instanceof RecordValue)
			{
				RecordValue rec = val.recordValue(ctxt);
				Value result = rec.fieldmap.get(fieldname);

				if (result == null)
				{
					abort(4046, "No such field: " + fieldname, ctxt);
				}

				return result;
			}
			else
			{
				return abort(4020,
					"State value is neither a record nor an object", ctxt);
			}
		}
		catch (ValueException e)
		{
			return abort(e);
		}
	}

	@Override
	public void setUpdatablesOnly(boolean value)
	{
		updatablesOnly = value;
		object.setUpdatablesOnly(value);
	}
}
