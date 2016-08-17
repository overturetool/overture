package org.overture.codegen.tests.other;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.utils.GeneralCodeGenUtils;

public class ClassesToSkipParsingTest
{
	private String[] empty = {};
	private String[] world = { "World" };
	private String[] worldEnv = { "World", "Env" };
	private String[] ioMath = { "IO", "MATH" };

	private Object[] getActual(String userInput)
	{
		return GeneralCodeGenUtils.getClassesToSkip(userInput).toArray();
	}

	private void compare(Object[] expecteds, Object[] actuals)
	{
		Assert.assertArrayEquals(expecteds, actuals);
	}

	@Test
	public void test_01()
	{
		compare(world, getActual("World"));
	}

	@Test
	public void test_02()
	{
		compare(world, getActual("  \n\r  World  \n\r  "));
	}

	@Test
	public void test_03()
	{
		compare(worldEnv, getActual("World;Env"));
	}

	@Test
	public void test_04()
	{
		compare(worldEnv, getActual("   \n\r    World   ;  \n\r      Env"));
	}

	@Test
	public void test_05()
	{
		compare(empty, getActual(""));
	}

	@Test
	public void test_06()
	{
		compare(empty, getActual("\n"));
	}

	@Test
	public void test_07()
	{
		compare(empty, getActual(null));
	}

	@Test
	public void test_08()
	{
		compare(worldEnv, getActual("World;Env;Env;World"));
	}

	@Test
	public void test_09()
	{
		compare(ioMath, getActual("IO;MATH"));
	}
}
