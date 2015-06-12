package org.overturetool.cgisa;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class QuantifierExpTest extends AbsExpTest

{

	String expression;
	String expected;

	public QuantifierExpTest(String input, String result)
	{
		super(input, result);
	}

	@Parameters(name = "{index}: exp: {0}")
	public static Collection<Object[]> getParams()
	{
		Object[] q1 = { "forall x : int & true", "(forall x : @int & true)" };
		Object[] q2 = { "forall x,y : int & true",
				"(forall x, y : @int & true)" };
		Object[] q3 = { "forall x : int, y:nat & true",
				"(forall x : @int, y : @nat & true)" };
		Object[] q4 = { "forall x:int & x <> 0", "(forall x : @int & (<x> <> 0))" };

		return Arrays.asList(new Object[][] { q1, q2, q3, q4 });

	}

}
