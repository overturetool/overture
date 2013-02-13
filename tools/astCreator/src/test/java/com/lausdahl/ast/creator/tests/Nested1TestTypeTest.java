package com.lausdahl.ast.creator.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import com.lausdahl.ast.creator.AstCreatorException;
import com.lausdahl.ast.creator.Main;

public class Nested1TestTypeTest extends TestCase {
	private static final String TESTDATA_BASE = "src\\test\\resources\\";

	public void test() throws IOException, InstantiationException,
			IllegalAccessException, AstCreatorException {
		System.out.println(new File(".").getAbsolutePath());
		File output = new File(
				FilePathUtil.getPlatformPath("target/testData/nested1"));
		String inputFile = TESTDATA_BASE + "nested1.astv2";
		Main.create(
				null,
				new FileInputStream(new File(new File(".").getParentFile(),
						FilePathUtil.getPlatformPath(inputFile))
						.getAbsolutePath()), output, true, false);
	}
}
