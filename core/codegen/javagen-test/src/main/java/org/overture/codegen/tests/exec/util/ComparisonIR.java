/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.tests.exec.util;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.runtime.Record;
import org.overture.codegen.runtime.Token;
import org.overture.codegen.runtime.Tuple;
import org.overture.codegen.runtime.VDMMap;
import org.overture.codegen.runtime.VDMSeq;
import org.overture.codegen.runtime.VDMSet;
import org.overture.ct.ctruntime.utils.TraceTest;
import org.overture.interpreter.traces.Verdict;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CharacterValue;
import org.overture.interpreter.values.FieldMap;
import org.overture.interpreter.values.FieldValue;
import org.overture.interpreter.values.InvariantValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NameValuePairMap;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.ObjectValue;
import org.overture.interpreter.values.QuoteValue;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.TokenValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;

public class ComparisonIR
{
	public ComparisonIR(File testInputFile)
	{
		if (testInputFile == null)
		{
			throw new IllegalArgumentException("Test file cannot be null");
		}
	}

	public boolean compare(Object cgResult, Object vdmResult)
	{
		// If the VDM result is a String then it must be an error message
		if(vdmResult instanceof String)
		{
			String vdmError = vdmResult.toString();
			String cgError = cgResult.toString();
			
			return vdmError.toLowerCase().contains("error") && vdmError.contains(cgError);
		}
		
		if(!(vdmResult instanceof Value))
		{
			if(vdmResult instanceof List && cgResult instanceof List)
			{
				@SuppressWarnings("rawtypes")
				List vdmList = (List) vdmResult;
				@SuppressWarnings("rawtypes")
				List cgList = (List) cgResult;
				
				if(vdmList.size() != cgList.size())
				{
					return false;
				}
				
				for(int i = 0; i < vdmList.size(); i++)
				{
					Object vdmElem = vdmList.get(i);
					Object cgElem = cgList.get(i);
					
					if(vdmElem instanceof TraceTest && 
							cgElem instanceof org.overture.codegen.runtime.traces.TraceTest)
					{
						TraceTest vdmTest = (TraceTest) vdmElem;
						org.overture.codegen.runtime.traces.TraceTest cgTest = (org.overture.codegen.runtime.traces.TraceTest) cgElem;
						
						if(vdmTest.getVerdict().toString().equals(cgTest.getVerdict().toString()))
						{
							if(!(vdmTest.getNo().equals(cgTest.getNo()) && vdmTest.getTest().equals(cgTest.getTest())))
							{
								return false;
							}
							
							if(vdmTest.getVerdict() == Verdict.PASSED && !vdmTest.getResult().equals(cgTest.getResult()))
							{
								return false;
							}
						}
						else 
						{
							return false;
						}
					}
					else 
					{
						return false;
					}
				}
				return true;
			}
			else
			{
			  return false;
			}
		}
		
		Value vdmValue = (Value) vdmResult;
		
		
		while (vdmValue instanceof UpdatableValue)
		{
			UpdatableValue upValue = (UpdatableValue) vdmValue;
			vdmValue = upValue.getConstant();
		}

		while (vdmValue instanceof InvariantValue)
		{
			vdmValue = vdmValue.deref();
		}

		if (vdmValue instanceof BooleanValue)
		{
			return handleBoolean(cgResult, vdmValue);
		} else if (vdmValue instanceof CharacterValue)
		{
			return handleCharacter(cgResult, vdmValue);
		} else if (vdmValue instanceof MapValue)
		{
			return handleMap(cgResult, vdmValue);
		} else if (vdmValue instanceof QuoteValue)
		{
			return handleQuote(cgResult, vdmValue);
		} else if (vdmValue instanceof NumericValue)
		{
			return handleNumber(cgResult, vdmValue);
		} else if (vdmValue instanceof SeqValue)
		{
			if (cgResult instanceof String)
			{
				return handleString(cgResult, vdmValue);
			} else
			{
				return handleSeq(cgResult, vdmValue);
			}
		} else if (vdmValue instanceof SetValue)
		{
			return handleSet(cgResult, vdmValue);
		} else if (vdmValue instanceof TupleValue)
		{
			return handleTuple(cgResult, vdmValue);
		} else if (vdmValue instanceof TokenValue)
		{
			return handleToken(cgResult, vdmValue);
		} else if (vdmValue instanceof NilValue)
		{
			return cgResult == null;
		} else if (vdmValue instanceof ObjectValue)
		{
			return handleObject(cgResult, vdmValue);
		} else if (vdmValue instanceof RecordValue)
		{
			return handleRecord(cgResult, vdmValue);
		}

		return false;
	}

	private boolean handleRecord(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof Record))
		{
			return false;
		}

		RecordValue vdmRecord = (RecordValue) vdmValue;
		Record cgRecord = (Record) cgValue;

		if (!cgRecord.getClass().getName().endsWith(vdmRecord.type.getName().getName()))
		{
			return false;
		}

		Field[] cgRecFields = cgRecord.getClass().getFields();

		FieldMap vdmRecFields = vdmRecord.fieldmap;

		for (int i = 0; i < vdmRecFields.size(); i++)
		{
			FieldValue vdmField = vdmRecFields.get(i);
			Field cgField = cgRecFields[i];

			try
			{
				if (!cgField.getName().equals(vdmField.name))
				{
					return false;
				}

				if (!compare(cgField.get(cgRecord), vdmField.value))
				{
					return false;
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	private boolean handleObject(Object cgValue, Value vdmValue)
	{
		ObjectValue vdmObject = (ObjectValue) vdmValue;

		Field[] fields = cgValue.getClass().getFields();
		NameValuePairMap memberValues = vdmObject.getMemberValues();
		Set<ILexNameToken> keySet = memberValues.keySet();

		for (Field field : fields)
		{
			boolean foundMatchingField = false;

			for (ILexNameToken tok : keySet)
			{
				if (field.getName().equals(tok.getName()))
				{
					Value vdmFieldValue = memberValues.get(tok);

					if (vdmFieldValue != null)
					{
						try
						{
							Object cgFieldValue = field.get(cgValue);

							if (compare(cgFieldValue, vdmFieldValue))
							{
								foundMatchingField = true;
								break;
							}

						} catch (Exception e)
						{
							e.printStackTrace();
							return false;
						}
					}
				}
			}

			if (!foundMatchingField)
			{
				return false;
			}
		}

		return true;
	}

	private boolean handleQuote(Object cgValue, Value vdmValue)
	{
		if(cgValue == null)
		{
			return false;
		}
		
		// For example, the replacement constructs <A> from <AQuote> 
		return cgValue.toString().replace("Quote>", ">").equals(vdmValue.toString());
	}

	private boolean handleToken(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof Token))
		{
			return false;
		}

		Token cgToken = (Token) cgValue;
		TokenValue vdmToken = (TokenValue) vdmValue;

		try
		{
			Field f = vdmToken.getClass().getDeclaredField("value");
			f.setAccessible(true);
			Value value = (Value) f.get(vdmToken);

			return compare(cgToken.getValue(), value);

		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	private static boolean handleCharacter(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof Character))
		{
			return false;
		}

		Character cgChar = (Character) cgValue;
		CharacterValue vdmChar = (CharacterValue) vdmValue;

		return cgChar != null && cgChar == vdmChar.unicode;
	}

	private static boolean handleNumber(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof Number))
		{
			return false;
		}

		Number number = (Number) cgValue;
		NumericValue vdmNumeric = (NumericValue) vdmValue;

		return number.doubleValue() == vdmNumeric.value;
	}

	private boolean handleSeq(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof VDMSeq))
		{
			return false;
		}

		VDMSeq cgSeq = (VDMSeq) cgValue;
		SeqValue vdmSeq = (SeqValue) vdmValue;

		if (cgSeq.size() != vdmSeq.values.size())
		{
			return false;
		}

		for (int i = 0; i < cgSeq.size(); i++)
		{
			Object cgElement = cgSeq.get(i);
			Value vdmElement = vdmSeq.values.get(i);

			if (!compare(cgElement, vdmElement))
			{
				return false;
			}
		}

		return true;
	}

	private boolean handleSet(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof VDMSet))
		{
			return false;
		}

		VDMSet cgSet = (VDMSet) cgValue;
		SetValue vdmSet = (SetValue) vdmValue;

		if (cgSet.size() != vdmSet.values.size())
		{
			return false;
		}

		@SuppressWarnings("unchecked")
		List<Object> cgValuesList = new LinkedList<Object>(cgSet);
		List<Value> vdmValuesList = new LinkedList<Value>(vdmSet.values);

		for (int i = 0; i < cgValuesList.size(); i++)
		{
			Object cgElement = cgValuesList.get(i);

			boolean match = false;
			for (int k = 0; k < vdmValuesList.size(); k++)
			{
				Value vdmElement = vdmValuesList.get(k);

				if (compare(cgElement, vdmElement))
				{
					match = true;
					break;
				}
			}

			if (!match)
			{
				return false;
			}
		}

		return true;
	}

	private boolean handleTuple(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof Tuple))
		{
			return false;
		}

		Tuple javaTuple = (Tuple) cgValue;
		TupleValue vdmTuple = (TupleValue) vdmValue;

		if (javaTuple.size() != vdmTuple.values.size())
		{
			return false;
		}

		for (int i = 0; i < javaTuple.size(); i++)
		{
			if (!compare(javaTuple.get(i), vdmTuple.values.get(i)))
			{
				return false;
			}
		}

		return true;
	}

	private boolean handleString(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof String))
		{
			return false;
		}

		String cgString = (String) cgValue;
		SeqValue vdmSeq = (SeqValue) vdmValue;

		if (cgString.length() != vdmSeq.values.size())
		{
			return false;
		}

		for (int i = 0; i < cgString.length(); i++)
		{
			if (!compare(cgString.charAt(i), vdmSeq.values.get(i)))
			{
				return false;
			}
		}

		return true;
	}

	private boolean handleMap(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof VDMMap))
		{
			return false;
		}

		VDMMap cgMap = (VDMMap) cgValue;
		MapValue vdmMap = (MapValue) vdmValue;

		if (cgMap.size() != vdmMap.values.size())
		{
			return false;
		}

		for (Object cgKey : cgMap.keySet())
		{
			boolean match = false;
			for (Value vdmKey : vdmMap.values.keySet())
			{
				if (compare(cgKey, vdmKey))
				{
					Object cgVal = cgMap.get(cgKey);
					Value vdmVal = vdmMap.values.get(vdmKey);

					if (compare(cgVal, vdmVal))
					{
						match = true;
						break;
					}
				}
			}

			if (!match)
			{
				return false;
			}
		}

		return true;
	}

	private boolean handleBoolean(Object cgValue, Value vdmValue)
	{
		if (!(cgValue instanceof Boolean))
		{
			return false;
		}

		Boolean cgBool = (Boolean) cgValue;
		BooleanValue vdmBool = (BooleanValue) vdmValue;

		return cgBool != null && cgBool == vdmBool.value;
	}
}
