package org.overture.pog.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.overture.pog.tests.framework.BaseTestSuite;
import org.overture.pog.tests.framework.ModuleTestCase;

public class ModulesVDMSlTypeCheckTest extends BaseTestSuite {

	public static Test suite() throws IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, IOException {
		String name = "Type Check Module TestSuite";
		String root = "src\\test\\resources\\modules";
		TestSuite test = createTestCompleteFile(name, root,
				ModuleTestCase.class);
		return test;
	}

}
