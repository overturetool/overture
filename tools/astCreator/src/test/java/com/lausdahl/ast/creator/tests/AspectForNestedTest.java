package com.lausdahl.ast.creator.tests;

import java.io.File;
import java.io.IOException;

import com.lausdahl.ast.creator.AstCreatorException;
import com.lausdahl.ast.creator.Main;

import junit.framework.TestCase;

public class AspectForNestedTest extends TestCase
{
	private static final String TESTDATA_BASE = "src\\test\\resources\\";
	public void test() throws IOException, InstantiationException, IllegalAccessException, AstCreatorException
	{
		System.out.println(new File(".").getAbsolutePath());
		File output = new File("target/testData/aspectForNested".replace('/', File.separatorChar));
		String inputFile= TESTDATA_BASE+"aspectForNested.astv2".replace('/', File.separatorChar);
		Main.create(new File(new File(".").getParentFile(),inputFile).getAbsolutePath(), output, true);
	}
}
