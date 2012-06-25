//package org.overture.intepreter.tests.external;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//
//import junit.framework.Test;
//import junit.framework.TestSuite;
//
//import org.overturetool.test.framework.BaseTestSuite;
//import org.overturetool.test.framework.Properties;
//import org.overturetool.vdmj.lex.LexLocation;
//
//public class ExternalClassesRtTypeCheckTest extends BaseTestSuite
//{
//	public static Test suite() throws IllegalArgumentException,
//			SecurityException, InstantiationException, IllegalAccessException,
//			InvocationTargetException, NoSuchMethodException, IOException
//	{
//		LexLocation.absoluteToStringLocation = false;
//		Properties.recordTestResults = true;
//		String name = "Type_Check_RT_Classes_TestSuite_External";
//		File root = ExternalTestSettings.getBasePath("rttest/tc");
//		TestSuite test = null;
//		if (root != null && root.exists())
//		{
//			test = createTestCompleteFile(name, root.getAbsolutePath(), NewClassRtTestCase.class);
//		} else
//		{
//			test = new TestSuite("Empty Skipped Test Suite");
//		}
//		return test;
//	}
//}
