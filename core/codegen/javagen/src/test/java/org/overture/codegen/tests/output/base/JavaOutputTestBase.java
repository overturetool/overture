package org.overture.codegen.tests.output.base;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.CodeGenBase;
import org.overture.codegen.tests.output.util.OutputTestBase;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;
import org.overture.config.Settings;

public abstract class JavaOutputTestBase extends OutputTestBase
{
	public JavaOutputTestBase(String nameParameter, String inputParameter,
			String resultParameter)
	{
		super(nameParameter, inputParameter, resultParameter);
	}

	public JavaSettings getJavaSettings()
	{
		JavaSettings javaSettings = new JavaSettings();
		javaSettings.setDisableCloning(false);

		return javaSettings;
	}

	public JavaCodeGen getJavaGen()
	{
		JavaCodeGen javaGen = new JavaCodeGen();
		javaGen.setSettings(getIrSettings());
		javaGen.setJavaSettings(getJavaSettings());

		return javaGen;
	}

	public GeneratedData genCode(List<INode> ast) throws AnalysisException
	{
		if (Settings.dialect == Dialect.VDM_SL)
		{
			List<AModuleModules> modules = buildModulesList(ast);

			return getJavaGen().generate(CodeGenBase.getNodes(modules));
		} else
		{
			List<SClassDefinition> classes = buildClassList(ast);

			return getJavaGen().generate(CodeGenBase.getNodes(classes));
		}
	}
}
