package org.overture.codegen.tests.exec.base;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.tests.exec.util.CheckerTestBase;
import org.overture.codegen.tests.exec.util.testhandlers.ExecutableSpecTestHandler;
import org.overture.codegen.tests.exec.util.testhandlers.ExpressionTestHandler;
import org.overture.codegen.tests.exec.util.testhandlers.TestHandler;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.utils.Generated;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Settings;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public abstract class JavaGenTestBase extends CheckerTestBase
{
	public JavaGenTestBase(File vdmSpec, TestHandler testHandler)
	{
		super(vdmSpec, testHandler);
	}

	public JavaCodeGen getJavaGen()
	{
		JavaCodeGen javaCg = new JavaCodeGen();
		javaCg.setJavaSettings(getJavaSettings());
		javaCg.setSettings(getIrSettings());

		return javaCg;
	}

	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);
		javaSettings.setMakeClassesSerializable(true);
		javaSettings.setFormatCode(false);

		return javaSettings;
	}

	public static GeneratedData genData(JavaCodeGen javaCg, List<File> files)
			throws AnalysisException, ParserException, LexException
	{
		GeneratedData data = null;
		if (Settings.dialect == Dialect.VDM_SL)
		{
			TypeCheckResult<List<AModuleModules>> tcResult = checkTcResult(TypeCheckerUtil.typeCheckSl(files));
			data = javaCg.generate(CodeGenBase.getNodes(tcResult.result));

		} else if (Settings.dialect == Dialect.VDM_PP)
		{
			TypeCheckResult<List<SClassDefinition>> tcResult = checkTcResult(TypeCheckerUtil.typeCheckPp(files));
			data = javaCg.generate(CodeGenBase.getNodes(tcResult.result));
		} else if (Settings.dialect == Dialect.VDM_RT)
		{
			TypeCheckResult<List<SClassDefinition>> tcResult = checkTcResult(TypeCheckerUtil.typeCheckRt(files));
			data = javaCg.generate(CodeGenBase.getNodes(tcResult.result));
		}
		return data;
	}

	public void genJavaSources(File vdmSource)
	{
		JavaCodeGen javaCg = getJavaGen();

		try
		{
			if (testHandler instanceof ExpressionTestHandler)
			{
				Generated s = JavaCodeGenUtil.generateJavaFromExp(GeneralUtils.readFromFile(vdmSource), javaCg, Settings.dialect);
				((ExpressionTestHandler) testHandler).injectArgIntoMainClassFile(outputDir, s.getContent(), javaCg.getJavaSettings().getJavaRootPackage());
			} else
			{
				List<File> files = new LinkedList<File>();
				files.add(vdmSource);

				GeneratedData data = genData(javaCg, files);

				if (data == null)
				{
					Assert.fail("Problems encountered when trying to code generate VDM model!");
				}

				javaCg.genJavaSourceFiles(outputDir, data.getClasses());

				if (data.getQuoteValues() != null
						&& !data.getQuoteValues().isEmpty())
				{
					javaCg.genJavaSourceFiles(outputDir, data.getQuoteValues());
				}

				if (testHandler instanceof ExecutableSpecTestHandler)
				{
					ExecutableSpecTestHandler ex = (ExecutableSpecTestHandler) testHandler;
					ex.writeMainClass(outputDir, getJavaSettings().getJavaRootPackage());
				}
			}
		} catch (AnalysisException | IOException e)
		{
			Assert.fail("Got unexpected exception when attempting to generate Java code: "
					+ e.getMessage());
			e.printStackTrace();
		}
	}

	public void genSourcesAndCompile()
	{
		genJavaSources(file);
		compile(consCpFiles());
	}
}
