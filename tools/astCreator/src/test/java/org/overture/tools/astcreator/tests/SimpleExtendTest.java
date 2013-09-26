package org.overture.tools.astcreator.tests;

import java.io.File;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.overture.tools.astcreator.Main;

public class SimpleExtendTest extends TestCase {

	public void test() throws Exception {
		InputStream cmlSource = getClass().getResourceAsStream(
				"/extend/t1.astv2");
		InputStream ovtSource = getClass().getResourceAsStream(
				"/extend/t2.astv2");
		File outputFolder = new File(
				FilePathUtil.getPlatformPath("target/testData/simpleExtend"));
//		File outputFolder = new File("../../testdata/generatedCode");

		Assert.assertNotNull("Unable to load CML Source Ast file", cmlSource);
		Assert.assertNotNull("Unable to load Overture Souce Ast file",
				ovtSource);

		Main.create(null, null, ovtSource, cmlSource, outputFolder, "Interpreter",
				false, true);
	}

}
