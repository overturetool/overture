package com.lausdahl.ast.creator.tests;

import java.io.File;

import junit.framework.TestCase;

import com.lausdahl.ast.creator.Main;

public class ExternalJavaTypeTest extends TestCase
{
	private static final String TESTDATA_BASE = "src\\test\\resources\\";

	public void test()
	{
		System.out.println(new File(".").getAbsolutePath());
		File output = new File(FilePathUtil.getPlatformPath("target/testData/external"));
		String inputFile = TESTDATA_BASE + "external.astv2";
		Main.test = true;
		try
		{
			Main.create(new File(new File(".").getParentFile(), FilePathUtil.getPlatformPath(inputFile)).getAbsolutePath(), output, true);
		} catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
