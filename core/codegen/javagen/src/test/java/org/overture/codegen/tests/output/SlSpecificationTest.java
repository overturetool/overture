package org.overture.codegen.tests.output;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.utils.GeneratedData;
import org.overture.config.Release;
import org.overture.config.Settings;

abstract public class SlSpecificationTest extends SpecificationTestBase
{
	public SlSpecificationTest(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Before
	public void init()
	{
		super.init();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	public GeneratedData genCode(List<INode> ast) throws AnalysisException,
			UnsupportedModelingException
	{
		List<AModuleModules> modules = new LinkedList<AModuleModules>();

		for (INode c : ast)
		{
			if (c instanceof AModuleModules)
			{
				modules.add((AModuleModules) c);
			} else
			{
				Assert.fail("Expected only modules got " + c);
			}
		}

		return vdmCodGen.generateJavaFromVdmModules(modules);
	}
}
