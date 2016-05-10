/*******************************************************************************
 *
 *	Copyright (c) 2010 Overture.
 *
 *	Author: Kenneth Lausdahl
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

package org.overture.interpreter.values;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.Interpreter;

public class ValueFactory
{
	public static class ValueFactoryException extends Exception
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public ValueFactoryException(String message)
		{
			super(message);
		}

		public ValueFactoryException(String message, Exception e)
		{
			super(message, e);
		}

	}

	/**
	 * Interpreter used to look up types
	 */
	private Interpreter interpreter;
	public final Context context;

	/**
	 * Default constructor which sets the interpreter used to look up types
	 */
	public ValueFactory()
	{
		this.interpreter = Interpreter.getInstance();
		// This is how I got the assistantFactory for this class.
		this.context = (Context) Interpreter.getInstance().getAssistantFactory();
	}

	public static BooleanValue create(boolean b) throws ValueFactoryException
	{
		return new BooleanValue(b);
	}

	public static CharacterValue create(char c) throws ValueFactoryException
	{
		return new CharacterValue(c);
	}

	public static IntegerValue create(int c) throws ValueFactoryException
	{
		return new IntegerValue(c);
	}

	public static SeqValue create(String string) throws ValueFactoryException
	{
		return new SeqValue(string);
	}

	public static NilValue createNil()
	{
		return new NilValue();
	}

	public static VoidValue createVoid()
	{
		return new VoidValue();
	}

	public static RealValue create(double c) throws ValueFactoryException
	{
		try
		{
			return new RealValue(c);
		} catch (Exception e)
		{
			throw new ValueFactoryException(e.getMessage(), e);
		}
	}

	public static QuoteValue createQuote(String quote)
	{
		return new QuoteValue(quote);
	}

	public static TokenValue createToken(Value token)
	{
		return new TokenValue(token);
	}

	public SetValue createSet(Collection<Value> collection)
	{
		ValueSet vList = new ValueSet();
		vList.addAll(collection);
		return new SetValue(vList);
	}

	public SeqValue createSeq(Collection<Value> collection)
	{
		ValueList vList = new ValueList();
		vList.addAll(collection);
		return new SeqValue(vList);
	}

	public RecordValue createRecord(String recordName, Object... fields)
			throws ValueFactoryException
	{
		List<Value> values = new ArrayList<Value>();
		for (Object object : fields)
		{
			if (object instanceof Boolean)
			{
				values.add(create((Boolean) object));
			} else if (object instanceof Character)
			{
				values.add(create((Character) object));
			} else if (object instanceof Integer)
			{
				values.add(create((Integer) object));
			} else if (object instanceof Double)
			{
				values.add(create((Double) object));
			} else if (object instanceof String)
			{
				values.add(create((String) object));
			} else if (object instanceof Value)
			{
				values.add((Value) object);
			} else
			{
				throw new ValueFactoryException("The type of field "
						+ object
						+ " is not supported. Only basic types and Value are allowed.");
			}
		}

		return createRecord(recordName, values);
	}

	public RecordValue createRecord(String recordName, Value... fields)
			throws ValueFactoryException
	{

		PType type = interpreter.findType(recordName);
		if (type instanceof ARecordInvariantType)
		{
			ARecordInvariantType rType = (ARecordInvariantType) type;
			if (fields.length != rType.getFields().size())
			{
				throw new ValueFactoryException("Fileds count do not match record field count");
			}
			NameValuePairList list = new NameValuePairList();
			for (int i = 0; i < rType.getFields().size(); i++)
			{
				list.add(rType.getFields().get(i).getTagname(), fields[i]);
			}
			return new RecordValue(rType, list, context); // add the context here as argument.
		}
		throw new ValueFactoryException("Record " + recordName + " not found");
	}

	public MapValue createMap(ValueMap map)
	{
		return new MapValue(map);
	}

	public TupleValue createTuple(Value... fields)
	{
		ValueList list = new ValueList();
		for (Value value : fields)
		{
			list.add(value);
		}
		return new TupleValue(list);
	}
}
