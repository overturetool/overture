package org.overture.codegen.tests.output;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.analysis.violations.InvalidNamesResult;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.config.Release;
import org.overture.config.Settings;

abstract public class PpSpecificationTest extends OutputTestBase
{
	public PpSpecificationTest(String nameParameter, String inputParameter,
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
	
	@Override
	public String processModel(List<INode> ast)
	{
		try
		{
			StringBuilder generatedCode = new StringBuilder();
			GeneratedData data = genCode(ast);
			List<GeneratedModule> classes = data.getClasses();

			for (GeneratedModule classCg : classes)
			{
				generatedCode.append(classCg.getContent());
				generatedCode.append(MODULE_DELIMITER);
			}

			List<GeneratedModule> quoteData = data.getQuoteValues();

			if (quoteData != null && !quoteData.isEmpty())
			{
				generatedCode.append(QUOTE_INDICATOR + LINE_SEPARATOR);
				for (int i = 0; i < quoteData.size(); i++)
				{
					GeneratedModule q = quoteData.get(i);

					generatedCode.append(q.getName());

					if (i + 1 < quoteData.size())
					{
						generatedCode.append(QUOTE_SEPARATOR);
					}

				}
				generatedCode.append(MODULE_DELIMITER);
			}

			InvalidNamesResult invalidNames = data.getInvalidNamesResult();

			if (invalidNames != null && !invalidNames.isEmpty())
			{
				generatedCode.append(NAME_VIOLATION_INDICATOR + LINE_SEPARATOR);
				generatedCode.append(LINE_SEPARATOR
						+ GeneralCodeGenUtils.constructNameViolationsString(invalidNames));
				generatedCode.append(MODULE_DELIMITER);
			}

			return generatedCode.toString();

		} catch (UnsupportedModelingException | AnalysisException e)
		{
			Assert.fail("Unexpected problem encountered when attempting to code generate VDM model: "
					+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
