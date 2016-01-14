package org.overture.codegen.tests.output;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.codegen.tests.output.util.SlSpecificationTest;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;

public abstract class Sl2JavaSpecificationTest extends SlSpecificationTest
{
	public Sl2JavaSpecificationTest(String nameParameter, String inputParameter, String resultParameter)
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
		List<AModuleModules> modules = buildModulesList(ast);
		
		return getJavaGen().generateJavaFromVdmModules(modules);
	}
}
