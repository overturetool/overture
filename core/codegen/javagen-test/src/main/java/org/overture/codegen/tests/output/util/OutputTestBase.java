package org.overture.codegen.tests.output.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.config.Release;
import org.overture.config.Settings;

abstract public class OutputTestBase extends SpecificationTestBase
{
	public OutputTestBase(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	@Before
	public void init()
	{
		File f = new File(modelPath);

		if (GeneralCodeGenUtils.isVdmPpSourceFile(f))
		{
			Settings.dialect = Dialect.VDM_PP;
		} else if (GeneralCodeGenUtils.isVdmSlSourceFile(f))
		{
			Settings.dialect = Dialect.VDM_SL;
		} else if (GeneralCodeGenUtils.isVdmRtSourceFile(f))
		{
			Settings.dialect = Dialect.VDM_RT;
		} else
		{
			Assert.fail("The file does not have a valid VDM source file extension. Got: "
					+ modelPath);
		}

		Settings.release = Release.VDM_10;
	}

	public List<AModuleModules> buildModulesList(List<INode> ast)
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
		return modules;
	}

	public List<SClassDefinition> buildClassList(List<INode> ast)
	{
		List<SClassDefinition> classes = new LinkedList<SClassDefinition>();

		for (INode c : ast)
		{
			if (c instanceof SClassDefinition)
			{
				classes.add((SClassDefinition) c);
			} else
			{
				Assert.fail("Expected only classes got " + c);
			}
		}
		return classes;
	}

	abstract protected String getUpdatePropertyString();
}
