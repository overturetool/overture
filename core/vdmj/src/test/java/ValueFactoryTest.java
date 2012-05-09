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

import java.util.Collection;

import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueFactory;
import org.overturetool.vdmj.values.ValueList;
import org.overturetool.vdmj.values.ValueMap;

/**
 * External VDM test class for VDM Value Factory
 * @author kela
 *
 */
public class ValueFactoryTest
{

	public static Value getRecord(Value arg) throws ValueException, Exception
	{
		ValueFactory factory = new ValueFactory();
		return factory.createRecord("Person", ValueFactory.create(true),ValueFactory.create('A'));
	}
	
	public static Value getNil(Value arg) throws ValueException, Exception
	{
		return ValueFactory.createNil();
	}
	
	public static Value getQuote(Value arg) throws ValueException, Exception
	{
		return ValueFactory.createQuote("A");
	}
	
	public static Value getInt(Value arg) throws ValueException, Exception
	{
		return ValueFactory.create(1);
	}
	
	public static Value getToken(Value arg) throws ValueException, Exception
	{
		return ValueFactory.createToken(getInt(null));
	}
	
	public static Value getBool(Value arg) throws ValueException, Exception
	{
		return ValueFactory.create(true);
	}
	
	public static Value getChar(Value arg) throws ValueException, Exception
	{
		return ValueFactory.create('c');
	}
	
	public static Value getReal(Value arg) throws ValueException, Exception
	{
		return ValueFactory.create(1.1);
	}
	
	public static Value getSeqChar(Value arg) throws ValueException, Exception
	{
		return ValueFactory.create("Hello");
	}
	
	public static Value getMap(Value arg) throws ValueException, Exception
	{
		ValueFactory factory = new ValueFactory();
		ValueMap map = new ValueMap();
		map.put(ValueFactory.create(1), ValueFactory.create(2));
		return factory.createMap(map);
	}
	
	public static Value getSeq(Value arg) throws ValueException, Exception
	{
		ValueFactory factory = new ValueFactory();
		Collection<Value> collection = new ValueList();
		for (int i = 0; i < 10; i++)
		{
			collection.add(ValueFactory.create(i));	
		}
		
		return factory.createSeq(collection);
	}
	
	public static Value getSet(Value arg) throws ValueException, Exception
	{
		ValueFactory factory = new ValueFactory();
		Collection<Value> collection = new ValueList();
		for (int i = 0; i < 10; i++)
		{
			collection.add(ValueFactory.create(i));	
		}
		
		return factory.createSet(collection);
	}
	
	public static Value getTuple(Value arg) throws ValueException, Exception
	{
		ValueFactory factory = new ValueFactory();
		return factory.createTuple(ValueFactory.create(1),ValueFactory.create(2));
	}
	
	public static Value println(Value arg) throws ValueException, Exception
	{
		return IO.println(arg);
	}
}
