package org.overturetool.cgisa;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.IrNodeInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;
import org.overture.core.tests.ParseTcFacade;

public class AdHocTest
{
	@Test
	public void testQuick() throws AnalysisException, org.overture.codegen.cgast.analysis.AnalysisException
	{
		
		File f = new File("src/test/resources/test.vdmpp");
		List<File> files = new LinkedList<>();
		files.add(f);
		
		List<INode> ast = ParseTcFacade.typedAstNoRetry(files, "Quick", Dialect.VDM_PP);
		
		IsaCodeGen gen = new IsaCodeGen();
		
		List<SClassDefinition> classes = new LinkedList<>();
		
		for(INode n : ast)
		{
			classes.add((SClassDefinition) n);
		}
		
		List<GeneratedModule> result = gen.generateIsabelleSyntax(classes);
		
		for (GeneratedModule generatedClass : result)
		{
			Logger.getLog().println("**********");

			if (generatedClass.hasMergeErrors())
			{
				Logger.getLog().println(String.format("Class %s could not be merged. Following merge errors were found:", generatedClass.getName()));

				JavaCodeGenUtil.printMergeErrors(generatedClass.getMergeErrors());
			} else if (!generatedClass.canBeGenerated())
			{
				Logger.getLog().println("Could not generate class: "
						+ generatedClass.getName() + "\n");
				
				if(generatedClass.hasUnsupportedIrNodes())
				{
					Logger.getLog().println("Following VDM constructs are not supported by the IR:");
					JavaCodeGenUtil.printUnsupportedIrNodes(generatedClass.getUnsupportedInIr());
				}
				
				if(generatedClass.hasUnsupportedTargLangNodes())
				{
					Logger.getLog().println("Following IR constructs are not supported by the backend/target languages:");
					JavaCodeGenUtil.printUnsupportedNodes(generatedClass.getUnsupportedInTargLang());
				}
				
			} else
			{
				Logger.getLog().println(generatedClass.getContent());
				
				Set<IrNodeInfo> warnings = generatedClass.getTransformationWarnings();
				
				if(!warnings.isEmpty())
				{
					Logger.getLog().println("Following transformation warnings were found:");
					JavaCodeGenUtil.printUnsupportedNodes(generatedClass.getTransformationWarnings());
				}
			}

			Logger.getLog().println("\n");
		}
		
	}
}
