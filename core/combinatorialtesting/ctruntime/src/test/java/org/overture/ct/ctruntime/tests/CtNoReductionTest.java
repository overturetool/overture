package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.test.framework.BaseTestSuite;
import org.overture.test.framework.Properties;

public class CtNoReductionTest extends BaseTestSuite
{
	public static final String ROOT = "src" + File.separatorChar + "test"
			+ File.separatorChar + "resources" + File.separatorChar
			+ "no_reduction_sl_specs";
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		Properties.recordTestResults = false;
		
		String name = "CT No Rection VDM-SL test case";
		TestSuite test =  createTestCompleteFile(name, ROOT, CtNoReductionTestCase.class,"");
		return test;
	}
}
