package org.overture.isapog;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.core.tests.ParseTcFacade;

public class QuickIsaPog
{
	String modelPath = "src/test/resources/adhoc/isapog.vdmsl";
	String thysPath = "src/test/resources/adhoc/";

	@Test
	public void fileWriteTest() throws IOException, AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		List<INode> nodelist = ParseTcFacade.typedAst(modelPath, "AdHoc");
		INode ast = nodelist.get(0);

		if (ast instanceof AModuleModules)
		{
			AModuleModules module = (AModuleModules) ast;
			IsaPog isapo = new IsaPog(module);
			if (isapo.hasErrors())
			{
				fail(isapo.getErrorMessage());
			}
			isapo.writeThyFiles(thysPath);
		}

		else
		{
			fail();
		}
	}

	@Test
	public void printTest() throws IOException, AnalysisException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		List<INode> nodelist = ParseTcFacade.typedAst(modelPath, "AdHoc");
		INode ast = nodelist.get(0);

		if (ast instanceof AModuleModules)
		{
			AModuleModules module = (AModuleModules) ast;
			IsaPog isapo = new IsaPog(module);

			if (isapo.hasErrors())
			{
				fail(isapo.getErrorMessage());
			}

			System.out.println("(** Model THY **)");
			System.out.println(isapo.getModelThyString());
			System.out.println();
			System.out.println("(** POs THY **)");
			System.out.println(isapo.getPosThyString());
		}

		else
		{
			fail();
		}
	}

}
