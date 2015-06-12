package org.overturetool.cgisa;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BinaryExpTest extends AbsExpTest

{

	String expression;
	String expected;

	public BinaryExpTest(String input, String result)
	{
		super(input, result);
	}

	@Parameters(name = "{index}: exp: {0}")
	public static Collection<Object[]> getParams()
	{
		Object[] a1 = { "1+1", "(1 + 1)" };
		Object[] a2 = { "1-1", "(1 - 1)" };
		Object[] a3 = { "1*1", "(1 * 1)" };
		Object[] a4 = { "1/1", "1/1" }; // FIXME talk to PVJ about real division

		return Arrays.asList(new Object[][] { a1, a2, a3 });

	}

}
