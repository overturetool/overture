package org.overture.codegen.tests.output;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.codegen.tests.output.util.PpSpecificationTest;
import org.overture.codegen.utils.GeneratedData;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.codegen.vdm2java.JavaSettings;

public abstract class Pp2JavaSpecificationTest extends PpSpecificationTest
{
	public Pp2JavaSpecificationTest(String nameParameter, String inputParameter, String resultParameter)
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

		return getJavaGen().generateJavaFromVdm(classes);
	}
}
