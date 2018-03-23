/*******************************************************************************
 *
 *	Copyright (c) 2017 Fujitsu Services Ltd.
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

package org.overture.interpreter.values;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.interpreter.runtime.Interpreter;
import org.overture.interpreter.runtime.ValueException;

/**
 * Create Values from the arguments passed, which is useful from native Java implementations.
 */
public class ValueFactory
{
	public static BooleanValue mkBool(boolean b)
	{
		return new BooleanValue(b);
	}
	
	public static CharacterValue mkChar(char c)
	{
		return new CharacterValue(c);
	}
	
	public static IntegerValue mkInt(long i)
	{
		return new IntegerValue(i);
	}
	
	public static NaturalValue mkNat(long n) throws Exception
	{
		return new NaturalValue(n);
	}
	
	public static NaturalOneValue mkNat1(long n) throws Exception
	{
		return new NaturalOneValue(n);
	}
	
	public static RationalValue mkRat(long p, long q) throws Exception
	{
		return new RationalValue((double)p/q);
	}

	public static RationalValue mkRat(double d) throws Exception
	{
		return new RationalValue(d);
	}

	public static RealValue mkReal(double d) throws Exception
	{
		return new RealValue(d);
	}

	public static NilValue mkNil()
	{
		return new NilValue();
	}
	
	public static QuoteValue mkQuote(String q)
	{
		return new QuoteValue(q);
	}
	
	public static SeqValue mkSeq(Value ...args)
	{
		return new SeqValue(new ValueList(args));
	}
	
	public static SetValue mkSet(Value ...args) throws ValueException
	{
		return new SetValue(new ValueSet(args));
	}
	
	public static TupleValue mkTuple(Value ...args)
	{
		return new TupleValue(new ValueList(args));
	}
	
	public static TokenValue mkToken(Value arg)
	{
		return new TokenValue(arg);
	}
	
	public static RecordValue mkRecord(String module, String name, Value ...args) throws AnalysisException
	{
		PType type = getType(module, name);
		
		if (type instanceof ARecordInvariantType)
		{
			ARecordInvariantType r = (ARecordInvariantType)type;
    		ValueList l = new ValueList();
    		
    		for (int a=0; a<args.length; a++)
    		{
    			l.add(args[a]);
    		}
    		
    		return new RecordValue(r, l, Interpreter.getInstance().initialContext);
		}
		else
		{
			throw new ValueException(69, "Definition " + module + "`" + name +
				" is " + type.getClass().getSimpleName() + " not TCRecordType", null);
		}
	}

	public static InvariantValue mkInvariant(String module, String name, Value x) throws AnalysisException
	{
		PType type = getType(module, name);
		
		if (type instanceof ANamedInvariantType)
		{
			ANamedInvariantType r = (ANamedInvariantType)type;
			return new InvariantValue(r, x, Interpreter.getInstance().initialContext);
		}
		else
		{
			throw new ValueException(69, "Definition " + module + "`" + name +
				" is " + type.getClass().getSimpleName() + " not TCNamedType", null);
		}
	}
	
	private static PType getType(String module, String name) throws ValueException
	{
		Interpreter i = Interpreter.getInstance();
		LexNameToken tcname = new LexNameToken(module, name,new LexLocation());
		PDefinition def = i.getGlobalEnvironment(module).findType(tcname, i.getDefaultName());
		
		if (def == null)
		{
			throw new ValueException(70, "Definition " + tcname.getExplicit(true) + " not found", null);
		}
		
		return def.getType();
	}
}
