package org.overture.vdm2jml.tests;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.logging.Logger;

public class ModuleStateInvComplexTests extends StateTestBase
{
	@BeforeClass
	public static void init() throws AnalysisException,
			UnsupportedModelingException
	{
		List<AClassDeclCG> classes = getClasses("ModuleStateInvComplex");

		for (AClassDeclCG clazz : classes)
		{
			if (clazz.getName().equals("M"))
			{
				genModule = clazz;
			} else if (clazz.getName().equals("St"))
			{
				genStateType = clazz;
			}
		}
		
		if (genModule == null || genStateType == null)
		{
			Logger.getLog().printErrorln("Problems generating the module "
					+ "and state type in ModuleStateInvComplexTests");
		}
	}

	@Test
	public void seqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("seqField", true, true);
	}

	@Test
	public void mapField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("mapField", true, true);
	}

	@Test
	public void fieldSeqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("fieldSeqField", true, true);
	}

	@Test
	public void seqSeqField()
			throws org.overture.codegen.cgast.analysis.AnalysisException
	{
		ModuleStateInvTests.checkAssertion("seqSeqField", true, true);
	}
}
