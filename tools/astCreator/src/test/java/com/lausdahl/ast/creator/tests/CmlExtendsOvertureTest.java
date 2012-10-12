package com.lausdahl.ast.creator.tests;

import java.io.File;
import java.io.InputStream;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.lausdahl.ast.creator.Main;

public class CmlExtendsOvertureTest extends TestCase {

	public void test() throws Exception {
		InputStream cmlSource = getClass().getResourceAsStream("/cml.ast");
		InputStream ovtSource = getClass().getResourceAsStream(
				"/cmlextendsovertureII/ovt/overtureII.astv2");
		File outputFolder = new File("../../testdata/generatedCode");

		Assert.assertNotNull("Unable to load CML Source Ast file", cmlSource);
		Assert.assertNotNull("Unable to load Overture Souce Ast file",
				ovtSource);

		Main.create(ovtSource, cmlSource, outputFolder, "CML", false);
	}

}
