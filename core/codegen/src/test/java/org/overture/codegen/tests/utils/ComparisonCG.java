package org.overture.codegen.tests.utils;


import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AInterfaceDeclCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.runtime.Record;
import org.overture.codegen.runtime.Token;
import org.overture.codegen.runtime.Tuple;
import org.overture.codegen.runtime.VDMMap;
import org.overture.codegen.runtime.VDMSeq;
import org.overture.codegen.runtime.VDMSet;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
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

public class ComparisonCG
{
	private File testInputFile;
	private AInterfaceDeclCG quotes;
	
	public ComparisonCG(File testInputFile)
	{
		if(testInputFile == null)
			throw new IllegalArgumentException("Test file cannot be null");
		
		this.testInputFile = testInputFile;
		this.quotes = null;
	}
	
	public boolean compare(Object cgValue, Value vdmValue)
	{
		while(vdmValue instanceof UpdatableValue)
		{
			UpdatableValue upValue = (UpdatableValue)vdmValue;
			vdmValue = upValue.getConstant();
		}
		
		while(vdmValue instanceof InvariantValue)
		{
			vdmValue = vdmValue.deref();
		}
		
		if(vdmValue instanceof BooleanValue)
		{
			return handleBoolean(cgValue, vdmValue);
		}
		else if(vdmValue instanceof CharacterValue)
		{
			return handleCharacter(cgValue, vdmValue);
		}
		else if(vdmValue instanceof MapValue)
		{
			return handleMap(cgValue, vdmValue);
		}
		else if(vdmValue instanceof QuoteValue)
		{
			return handleQuote(cgValue, vdmValue);
		}
		else if(vdmValue instanceof NumericValue)
		{
			return handleNumber(cgValue, vdmValue);
		}
		else if(vdmValue instanceof SeqValue)
		{
			if(cgValue instanceof String)
				return handleString(cgValue, vdmValue);
			else
				return handleSeq(cgValue, vdmValue);
		}
		else if(vdmValue instanceof SetValue)
		{
			return handleSet(cgValue, vdmValue);
		}
		else if(vdmValue instanceof TupleValue)
		{
			return handleTuple(cgValue, vdmValue);
		}
		else if(vdmValue instanceof TokenValue)
		{
			return handleToken(cgValue, vdmValue);
		}
		else if(vdmValue instanceof NilValue)
		{
			return cgValue == null;
		}
		else if(vdmValue instanceof ObjectValue)
		{
			return handleObject(cgValue, vdmValue);
		}
		else if(vdmValue instanceof RecordValue)
		{
			return handleRecord(cgValue, vdmValue);
		}
		
		return false;
	}

	private boolean handleRecord(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof Record))
			return false;

		RecordValue vdmRecord = (RecordValue) vdmValue;
		Record cgRecord = (Record) cgValue;
		
		if(!cgRecord.getClass().getName().endsWith(vdmRecord.type.getName().getName()))
			return false;
		
		Field[] cgRecFields = cgRecord.getClass().getFields();
		
		FieldMap vdmRecFields = vdmRecord.fieldmap;
		
		for(int i = 0; i < vdmRecFields.size(); i++)
		{
			FieldValue vdmField = vdmRecFields.get(i);
			Field cgField = cgRecFields[i];
			
			try
			{
				if(!cgField.getName().equals(vdmField.name))
					return false;
				
				if(!(compare(cgField.get(cgRecord), vdmField.value)))
					return false;
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
		
		for(Field field : fields)
		{
			boolean foundMatchingField = false;
			
			for(ILexNameToken tok : keySet)
			{
				if(field.getName().equals(tok.getName()))
				{
					Value vdmFieldValue = memberValues.get(tok);
					
					if(vdmFieldValue != null)
					{
						try
						{
							Object cgFieldValue = field.get(cgValue);
							
							if(compare(cgFieldValue, vdmFieldValue))
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
			
			if(!foundMatchingField)
				return false;
		}
		
		return true;
	}

	private boolean handleQuote(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof Number))
			return false;

		QuoteValue vdmQuote = (QuoteValue) vdmValue; 
		Number cgQuote = (Number) cgValue;
		
		try
		{
			if (quotes == null)
			{
				List<File> files = new LinkedList<File>();
				files.add(testInputFile);

				List<SClassDefinition> mergedParseList = JavaCodeGenUtil.consMergedParseList(files);

				JavaCodeGen javaCg = new JavaCodeGen();
				javaCg.generateJavaFromVdm(mergedParseList);

				quotes = javaCg.getInfo().getQuotes();
			}
			
			for(AFieldDeclCG quote: quotes.getFields())
			{
				if(quote.getName().equals(vdmQuote.value))
				{
					SExpCG exp = quote.getInitial();
					
					if(exp instanceof AIntLiteralExpCG)
					{
						AIntLiteralExpCG intLit = (AIntLiteralExpCG) exp;
						return cgQuote.equals(intLit.getValue());
					}
				}
			}
			
			return false;
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	private boolean handleToken(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof Token))
			 return false;
		
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
		if(!(cgValue instanceof Character))
			return false;
		
		Character cgChar = (Character) cgValue;
		CharacterValue vdmChar = (CharacterValue) vdmValue;
		
		return cgChar != null && cgChar == vdmChar.unicode;
	}

	private static boolean handleNumber(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof Number))
			return false;
		
		Number number = (Number) cgValue;
		NumericValue vdmNumeric = (NumericValue) vdmValue;
		
		return number.doubleValue() == vdmNumeric.value;
	}

	private boolean handleSeq(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof VDMSeq))
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

	private boolean handleSet(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof VDMSet))
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

	private boolean handleTuple(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof Tuple))
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

	private boolean handleString(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof String))
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

	private boolean handleMap(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof VDMMap))
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

	private boolean handleBoolean(Object cgValue, Value vdmValue)
	{
		if(!(cgValue instanceof Boolean))
			return false;
		
		Boolean cgBool = (Boolean) cgValue;
		BooleanValue vdmBool = (BooleanValue) vdmValue;
		
		return cgBool != null && cgBool == vdmBool.value;
	}
}
