package org.overture.codegen.cgen.tests.output.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Assert;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;

public class OutputTestUtil
{
	public static final String UPDATE_PROPERTY_PREFIX = "tests.cgen.override.";
	public static final String UPDATE_ALL_OUTPUT_TESTS_PROPERTY = UPDATE_PROPERTY_PREFIX
			+ "all";
	
	public static void compare(String expected, String actual)
	{
		Assert.assertEquals("Unexpected code produced by the Java code generator", expected.trim(), actual.trim());
	}

	public static String deSerialize(String resultPath)
			throws FileNotFoundException, IOException
	{
		return GeneralUtils.readFromFile(new File(resultPath));
	}

	public static void testUpdate(String actual, String resultPath)
			throws ParserException, LexException, IOException
	{
		PrintStream out = new PrintStream(new FileOutputStream(new File(resultPath)));
		out.print(actual);
		out.close();
	}
}
