package org.overture.tools.astcreator.tests;

import java.io.File;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.overture.tools.astcreator.Main;

public class CmlTest extends TestCase {

	public void test() throws Exception {
		InputStream cmlSource = getClass().getResourceAsStream(
				"/cml/cml.ast");
		InputStream ovtSource = getClass().getResourceAsStream(
				"/overtureII.astv2");
		File outputFolder = new File(
				FilePathUtil.getPlatformPath("target/testData/cml"));
//		File outputFolder = new File("../../testdata/generatedCode");

		Assert.assertNotNull("Unable to load CML Source Ast file", cmlSource);
		Assert.assertNotNull("Unable to load Overture Souce Ast file",
				ovtSource);

		Main.create(null, null, ovtSource, cmlSource, outputFolder, "CML",
				false, true);
	}

}
