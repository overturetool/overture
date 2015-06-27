package org.overture.codegen.tests.output;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneratedData;
import org.overture.config.Release;
import org.overture.config.Settings;

abstract public class OutputTestPp extends OutputTestBase
{
	public OutputTestPp(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}
	
	@Before
	public void init()
	{
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
		Logger.getLog().setSilent(true);

		vdmCodGen.clear();
		vdmCodGen.setSettings(getIrSettings());
		vdmCodGen.setJavaSettings(getJavaSettings());
	}

	public GeneratedData genCode(List<INode> ast) throws AnalysisException,
			UnsupportedModelingException
	{
		List<SClassDefinition> classes = new LinkedList<SClassDefinition>();

		for (INode c : ast)
		{
			if (c instanceof SClassDefinition)
			{
				classes.add((SClassDefinition) c);
			}
			else
			{
				Assert.fail("Expected only classes got " + c);
			}
		}

		return vdmCodGen.generateJavaFromVdm(classes);
	}

	abstract protected String getUpdatePropertyString();
}
