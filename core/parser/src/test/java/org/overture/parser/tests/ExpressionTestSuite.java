package org.overture.parser.tests;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ExpressionTestSuite extends TestSuite
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Parser Expression TestSuite");
		File[] files = new File("src\\test\\resources\\expressions").listFiles();
		if (files != null)
		{
			for (File file : files)
			{
				suite.addTest(new ExpressionTestCase(file));
			}
		}
		return suite;
	}
}
