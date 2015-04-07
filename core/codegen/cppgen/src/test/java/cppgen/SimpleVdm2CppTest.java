package cppgen;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.vdm2cpp.CppCodeGenMain;

public class SimpleVdm2CppTest {

	@Test
	public void testModule1()
	{
		String output = "target/test-results/"+getClass().getSimpleName()+"/model1/";
		CppCodeGenMain.main(new String[]{"oo", "stdlib" ,"true" ,"src/test/resources/simple-oo/model1".replace('/', File.separatorChar),output.replace('/', File.separatorChar)});
		
		Assert.assertTrue("No file generated", new File((output+"A.hpp").replace('/', File.separatorChar)).exists());
	}
}
