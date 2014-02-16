package org.overture.codegen.tests.utils;


import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.javalib.Tuple;
import org.overture.codegen.javalib.VDMMap;
import org.overture.codegen.javalib.VDMSeq;
import org.overture.codegen.javalib.VDMSet;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CharacterValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.Value;

public class ComparisonCG
{
	public static boolean compare(Object cgValue, Value vdmValue)
	{
		if(cgValue instanceof Boolean)
		{
			return handleBoolean(cgValue, vdmValue);
		}
		else if(cgValue instanceof Character)
		{
			return handleCharacter(cgValue, vdmValue);
		}
		else if(cgValue instanceof VDMMap)
		{
			return handleMap(cgValue, vdmValue);
		}
		else if(cgValue instanceof Number)
		{
			return handleNumber(cgValue, vdmValue);
		}
		else if(cgValue instanceof VDMSeq)
		{
			return handleSeq(cgValue, vdmValue);
		}
		else if(cgValue instanceof VDMSet)
		{
			return handleSet(cgValue, vdmValue);
		}
		else if(cgValue instanceof Tuple)
		{
			return handleTuple(cgValue, vdmValue);
		}
		else if(cgValue instanceof String)
		{
			return handString(cgValue, vdmValue);
		}
		else if(cgValue == null)
		{
			if(!(vdmValue instanceof NilValue))
				return false;
			
			return true;
		}
		
		
		return false;
	}

	private static boolean handleCharacter(Object cgValue, Value vdmValue)
	{
		if(!(vdmValue instanceof CharacterValue))
			return false;
		
		Character cgChar = (Character) cgValue;
		CharacterValue vdmChar = (CharacterValue) vdmValue;
		
		return cgChar != null && cgChar == vdmChar.unicode;
	}

	private static boolean handleNumber(Object cgValue, Value vdmValue)
	{
		if(!(vdmValue instanceof NumericValue))
			return false;
		
		Number number = (Number) cgValue;
		NumericValue vdmNumeric = (NumericValue) vdmValue;
		
		return number.doubleValue() == vdmNumeric.value;
	}

	private static boolean handleSeq(Object cgValue, Value vdmValue)
	{
		if(!(vdmValue instanceof SeqValue))
			return false;
		
		VDMSeq cgSeq = (VDMSeq) cgValue;
		SeqValue vdmSeq = (SeqValue) vdmValue;
		
		if(cgSeq.size() != vdmSeq.values.size())
			return false;
		
		for(int i = 0; i < cgSeq.size(); i++)
		{
			Object cgElement = cgSeq.get(i);
			Value vdmElement = vdmSeq.values.get(i);
			
			if(!compare(cgElement, vdmElement))
				return false;
		}
		
		return true;
	}

	private static boolean handleSet(Object cgValue, Value vdmValue)
	{
		if(!(vdmValue instanceof SetValue))
			return false;
		
		VDMSet cgSet = (VDMSet) cgValue;
		SetValue vdmSet = (SetValue) vdmValue;
		
		if(cgSet.size() != vdmSet.values.size())
			return false;
		
		@SuppressWarnings("unchecked")
		List<Object> cgValuesList = new LinkedList<Object>(cgSet);
		List<Value> vdmValuesList = new LinkedList<Value>(vdmSet.values);
		
		for(int i = 0; i < cgValuesList.size(); i++)
		{
			Object cgElement = cgValuesList.get(i);
			
			boolean match = false;
			for(int k = 0; k < vdmValuesList.size(); k++)
			{
				Value vdmElement = vdmValuesList.get(k);
				
				if(compare(cgElement, vdmElement))
				{
					match = true;
					break;
				}
			}
			
			if(!match)
				return false;
		}
		
		return true;
	}

	private static boolean handleTuple(Object cgValue, Value vdmValue)
	{
		if(!(vdmValue instanceof TupleValue))
			return false;
		
		Tuple javaTuple = (Tuple) cgValue;
		TupleValue vdmTuple = (TupleValue) vdmValue;
		
		if(javaTuple.size() != vdmTuple.values.size())
			return false;
		
		for(int i = 0; i < javaTuple.size(); i++)
			if(!compare(javaTuple.get(i), vdmTuple.values.get(i)))
					return false;
		
		return true;
	}

	private static boolean handString(Object cgValue, Value vdmValue)
	{
		if(!(vdmValue instanceof SeqValue))
			return false;
		
		String cgString = (String) cgValue;
		SeqValue vdmSeq = (SeqValue) vdmValue;
		
		
		if(cgString.length() != vdmSeq.values.size())
			return false;
		
		for(int i = 0; i < cgString.length(); i++)
			if(!compare(cgString.charAt(i), vdmSeq.values.get(i)))
				return false;
		
		return true;
	}

	private static boolean handleMap(Object cgValue, Value vdmValue)
	{
		if(!(vdmValue instanceof MapValue))
			return false;
		
		VDMMap cgMap = (VDMMap) cgValue;
		MapValue vdmMap = (MapValue) vdmValue;
		
		if(cgMap.size() != vdmMap.values.size())
			return false;
		
		for(Object cgKey : cgMap.keySet())
		{
			boolean match = false;
			for(Value vdmKey : vdmMap.values.keySet())
			{
				if(compare(cgKey, vdmKey))
				{
					Object cgVal = cgMap.get(cgKey);
					Value vdmVal = vdmMap.values.get(vdmKey);
					
					if(compare(cgVal, vdmVal))
					{
						match = true;
						break;
					}
				}
			}
			
			if(!match)
				return false;
		}
		
		return true;
	}

	private static boolean handleBoolean(Object cgValue, Value vdmValue)
	{
		if(!(vdmValue instanceof BooleanValue))
			return false;
		
		Boolean cgBool = (Boolean) cgValue;
		BooleanValue vdmBool = (BooleanValue) vdmValue;
		
		return cgBool != null && cgBool == vdmBool.value;
	}
}
