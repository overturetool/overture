package org.overture.interpreter.tests.stdlibs;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.util.InterpreterUtil;
import org.overture.interpreter.utilities.stdlibs.CsvParser;
import org.overture.interpreter.utilities.stdlibs.CsvResult;
import org.overture.interpreter.utilities.stdlibs.CsvValueBuilder;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.CharacterValue;
import org.overture.interpreter.values.IntegerValue;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.RealValue;
import org.overture.interpreter.values.SeqValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.TokenValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueSet;

public class CsvParserTest
{
	private CsvParser parser;
	
	@Before
	public void init()
	{
		parser = new CsvParser(new CsvValueBuilder()
		{
			@Override
			public Value createValue(String value)
					throws Exception
			{
				return InterpreterUtil.interpret(value);
			}
		});
	}
	
	@Test
	public void nullCase()
	{
		CsvResult res = parser.parseValues(null);
		
		assumeIncorrectData(CsvParser.INVALID_CSV_MSG, res.getErrorMsg());
		Assert.assertEquals(Arrays.asList(), res.getValues());
	}
	
	@Test
	public void emptyLine()
	{
		CsvResult res = parser.parseValues("");
		assumeCorrectData(res.dataOk());
		Assert.assertEquals(Arrays.asList(), res.getValues());
	}
	
	@Test
	public void threeNats()
	{
		CsvResult res = parser.parseValues("1, 2, 3");
		
		assumeCorrectData(res.dataOk());
		Assert.assertEquals(Arrays.asList(new IntegerValue(1), new IntegerValue(2), new IntegerValue(3)),res.getValues());
	}
	
	@Test
	public void boolCharRealNil() throws Exception
	{
		CsvResult res = parser.parseValues("true,'a',   4.3, nil");
	
		assumeCorrectData(res.dataOk());
		Assert.assertEquals(Arrays.asList(new BooleanValue(true), new CharacterValue('a'), new RealValue(4.3), new NilValue()), res.getValues());
	}
	
	@Test
	public void twoEmptySets()
	{
		CsvResult res = parser.parseValues("{}, {}");
		assumeCorrectData(res.dataOk());
		Assert.assertEquals(Arrays.asList(new SetValue(), new SetValue()), res.getValues());
	}
	
	@Test
	public void twoNatSets()
	{
		CsvResult res = parser.parseValues("{1,2},{3,4}");
		assumeCorrectData(res.dataOk());
		
		ValueSet leftValues = new ValueSet();
		leftValues.add(new IntegerValue(1));
		leftValues.add(new IntegerValue(2));
		
		ValueSet rightValues = new ValueSet();
		rightValues.add(new IntegerValue(3));
		rightValues.add(new IntegerValue(4));

		try
		{
			Assert.assertEquals(Arrays.asList(new SetValue(leftValues), new SetValue(rightValues)), res.getValues());
		}
		catch (ValueException e)
		{
			// Not reached
		}
	}
	
	@Test
	public void twoTuples()
	{
		CsvResult res = parser.parseValues("mk_(1,mk_token(\"x\")), mk_('a', nil)");
		
		assumeCorrectData(res.dataOk());
		
		ValueList leftVals = new ValueList();
		leftVals.add(new IntegerValue(1));
		leftVals.add(new TokenValue(new SeqValue("x")));
		TupleValue leftTup = new TupleValue(leftVals);
		
		ValueList rightVals = new ValueList();
		rightVals.add(new CharacterValue('a'));
		rightVals.add(new NilValue());
		TupleValue rightTup = new TupleValue(rightVals);
		
		Assert.assertEquals(Arrays.asList(leftTup, rightTup), res.getValues());
	}
	
	@Test
	public void illegalValue()
	{
		CsvResult res = parser.parseValues("1,_,2");
		
		assumeIncorrectData("Unexpected character '_' (code 0x5f)", res.getErrorMsg());
		Assert.assertEquals(Arrays.asList(new IntegerValue(1)), res.getValues());
	}
	
	@Test
	public void letAndNat()
	{
		CsvResult res = parser.parseValues("let a = 1, b = 2 in a + b, 5");
		
		assumeCorrectData(res.dataOk());
		Assert.assertEquals(Arrays.asList(new IntegerValue(3), new IntegerValue(5)), res.getValues());
	}
	
	private void assumeCorrectData(Boolean dataOk)
	{
		Assert.assertTrue("Expected data format to be correct", dataOk);
	}
	
	private void assumeIncorrectData(String expectedErrorMsg, String actualErrorMsg)
	{
		Assert.assertEquals(expectedErrorMsg, actualErrorMsg);
	}
}
