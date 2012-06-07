package org.overture.pog.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.overture.pog.tests.framework.BaseTestSuite;
import org.overture.pog.tests.framework.VdmjModuleSlPoTestCase;
import org.overturetool.vdmj.lex.LexLocation;

import junit.framework.Test;
import junit.framework.TestSuite;



public class VdmjModuleSlPoTestSuite extends BaseTestSuite {
	
	public static Test suite() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		String name = "VDMJ PO Module TestSuite";
		String root = "src\\test\\resources\\modules\\";
		TestSuite test = createTestCompleteFile(name, root, VdmjModuleSlPoTestCase.class);
		return test;
	}
}
