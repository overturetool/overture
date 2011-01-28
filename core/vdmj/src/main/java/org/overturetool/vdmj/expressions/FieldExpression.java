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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligationList;
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
import org.overturetool.vdmj.values.FieldMap;
import org.overturetool.vdmj.values.ObjectValue;
import org.overturetool.vdmj.values.RecordValue;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueList;

public class FieldExpression extends Expression
{
	private static final long serialVersionUID = 1L;
	public final Expression object;
	public final LexIdentifierToken field;

	private LexNameToken memberName = null;

	public FieldExpression(Expression object, LexIdentifierToken field)
	{
		super(object);
		this.object = object;
		this.field = field;
		this.field.location.executable(true);
	}

	public FieldExpression(Expression object, LexNameToken field)
	{
		super(object);
		this.object = object;
		this.field = new LexIdentifierToken(field.name, field.old, field.location);
		this.memberName = field;
		this.field.location.executable(true);
	}

	@Override
	public String toString()
	{
		return "(" + object + "." +
			(memberName == null ? field.name : memberName.getName()) + ")";
	}

	@Override
	public Type typeCheck(Environment env, TypeList qualifiers, NameScope scope)
	{
		Type root = object.typeCheck(env, null, scope);

		if (root.isUnknown())
		{
			memberName = new LexNameToken("?", field);
			return root;
		}

		TypeSet results = new TypeSet();
		boolean recOrClass = false;
		boolean unique = !root.isUnion();

		if (root.isRecord())
		{
    		RecordType rec = root.getRecord();
    		Field cf = rec.findField(field.name);

   			if (cf != null)
   			{
   				results.add(cf.type);
    		}
   			else
   			{
   				field.concern(unique,
   					3090, "Unknown field " + field.name + " in record " + rec.name);
   			}

   			recOrClass = true;
		}

		if (env.isVDMPP() && root.isClass())
		{
    		ClassType cls = root.getClassType();

    		if (memberName == null)
    		{
    			memberName = cls.getMemberName(field);
    		}

    		memberName.setTypeQualifier(qualifiers);
    		Definition fdef = cls.findName(memberName);

   			if (fdef == null)
   			{
    			// The field may be a map or sequence, which would not
    			// have the type qualifier of its arguments in the name...

    			TypeList oldq = memberName.typeQualifier;
    			memberName.setTypeQualifier(null);
    			fdef = cls.findName(memberName);
    			memberName.setTypeQualifier(oldq);	// Just for error text!
    		}

			if (fdef == null && memberName.typeQualifier == null)
			{
				// We might be selecting a bare function or operation, without
				// applying it (ie. no qualifiers). In this case, if there is
				// precisely one possibility, we choose it.

				for (Definition possible: env.findMatches(memberName))
				{
					if (possible.isFunctionOrOperation())
					{
						if (fdef != null)
						{
							fdef = null;	// Alas, more than one
							break;
						}
						else
						{
							fdef = possible;
						}
					}
				}
			}

			if (fdef == null)
			{
				field.concern(unique,
					3091, "Unknown member " + memberName + " of class " + cls.name.name);

				if (unique)
				{
					env.listAlternatives(memberName);
				}
			}
			else if (ClassDefinition.isAccessible(env, fdef, false))
   			{
				// The following gives lots of warnings for self.value access
				// to values as though they are fields of self in the CSK test
				// suite, so commented out for now.

				if (fdef.isStatic())// && !env.isStatic())
				{
					// warning(5005, "Should access member " + field + " from a static context");
				}

   				results.add(fdef.getType());
   				// At runtime, type qualifiers must match exactly
   				memberName.setTypeQualifier(fdef.name.typeQualifier);
    		}
   			else
   			{
   				field.concern(unique,
   					3092, "Inaccessible member " + memberName + " of class " + cls.name.name);
   			}

   			recOrClass = true;
		}

		if (results.isEmpty())
		{
    		if (!recOrClass)
    		{
    			object.report(3093, "Field '" + field.name + "' applied to non-aggregate type");
    		}

    		return new UnknownType(location);
		}

		return results.getType(location);
	}

	@Override
	public Value eval(Context ctxt)
	{
		breakpoint.check(location, ctxt);
		this.field.location.hit();

		try
		{
    		Value v = object.eval(ctxt);
    		Type objtype = null;
    		Value r = null;

    		if (v.isType(ObjectValue.class))
    		{
    			ObjectValue ov = v.objectValue(ctxt);
    	   		objtype = ov.type;
    	   		r = ov.get(memberName, memberName.explicit);
    		}
    		else
    		{
    			RecordValue rv = v.recordValue(ctxt);
    	   		objtype = rv.type;
    			FieldMap fields = rv.fieldmap;
         		r = fields.get(field.name);
    		}

    		if (r == null)
    		{
    			field.abort(4006, "Type " + objtype + " has no field " + field.name, ctxt);
    		}

    		return r;
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

		return object.findExpression(lineno);
	}

	@Override
	public ProofObligationList getProofObligations(POContextStack ctxt)
	{
		return object.getProofObligations(ctxt);
	}

	@Override
	public String kind()
	{
		return "field name";
	}

	@Override
	public ValueList getValues(Context ctxt)
	{
		return object.getValues(ctxt);
	}
}
