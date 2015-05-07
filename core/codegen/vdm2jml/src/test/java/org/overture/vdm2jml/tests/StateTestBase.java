package org.overture.vdm2jml.tests;

import java.util.List;

import org.junit.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2java.JavaFormat;

abstract public class StateTestBase extends AnnotationTestsBase
{
	// The IR class that is used to represent the type of the module state
	protected static AClassDeclCG genStateType;
	
	public static void init(String fileName) throws AnalysisException, UnsupportedModelingException
	{
		List<AClassDeclCG> classes = getClasses(fileName);

		// Two classes are expected - one for the module and
		// another class to represent the state of the module
		if (classes.size() == 2)
		{
			if (classes.get(0).getPackage() == null)
			{
				genModule = classes.get(0);
				genStateType = classes.get(1);
			} else
			{
				genModule = classes.get(1);
				genStateType = classes.get(0);
			}

			// We expect the generated class to be in the default package
		}
		else
		{
			Logger.getLog().printErrorln("Expected two classes for this type of test");
		}
	}
	
	public static void validateGenModuleAndStateType()
	{
		validGeneratedModule();
		
		Assert.assertTrue("State type was not generated", genStateType != null);
		String stateClassPackage = genModule.getName()
				+ JavaFormat.TYPE_DECL_PACKAGE_SUFFIX;
		Assert.assertEquals("Generated state type is located in a wrong package", stateClassPackage, genStateType.getPackage());
	}
}
