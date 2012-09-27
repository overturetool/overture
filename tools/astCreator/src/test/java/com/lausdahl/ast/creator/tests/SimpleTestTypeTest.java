package com.lausdahl.ast.creator.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.lausdahl.ast.creator.AstCreatorException;
import com.lausdahl.ast.creator.Main;

import junit.framework.TestCase;

public class SimpleTestTypeTest extends TestCase
{
	private static final String TESTDATA_BASE = "src\\test\\resources\\";

	public void test() throws IOException, InstantiationException, IllegalAccessException, AstCreatorException
	{
		System.out.println(new File(".").getAbsolutePath());
		File output = new File(FilePathUtil.getPlatformPath("target/testData/external"));
		String inputFile= TESTDATA_BASE+"extend/t1.astv2";
		Main.create(new FileInputStream( new File(new File(".").getParentFile(),FilePathUtil.getPlatformPath(inputFile)).getAbsolutePath() ), output, true,false);
	}
}
