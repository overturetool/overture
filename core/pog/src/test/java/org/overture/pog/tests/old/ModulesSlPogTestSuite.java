package org.overture.pog.tests.old;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.ast.lex.LexLocation;
import org.overture.pog.tests.old.ModuleSlPoTestCase;
import org.overture.test.framework.BaseTestSuite;

public class ModulesSlPogTestSuite extends BaseTestSuite
{
	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException
	{
		LexLocation.absoluteToStringLocation = false;
		String name = "POG Modules TestSuite";
		String root = "src\\test\\resources\\old\\single";
		TestSuite test = createTestCompleteFile(name, root, ModuleSlPoTestCase.class, "");
		return test;
	}
}
