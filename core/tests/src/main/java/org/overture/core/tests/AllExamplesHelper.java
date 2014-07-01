package org.overture.core.tests;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;
import org.overture.ast.node.INode;
import org.overture.parser.lex.LexException;
import org.overture.parser.syntax.ParserException;
import org.overture.tools.examplepackager.util.ExampleTestData;
import org.overture.tools.examplepackager.util.ExampleTestUtils;

/**
 * Special class to help handle testing of the Overture examples. This class provides the examples but because these
 * examples are Overture multi-file projects, a special method to help parse and TC them is available.
 * 
 * @author ldc
 */
public class AllExamplesHelper
{
	/**
	 * Simple wrapper class for examples data. Contains the AST (as a list of {@link INode}) and name of an example.
	 * 
	 * @author ldc
	 */
	static public class ExampleAstData
	{

		String exampleName;
		List<INode> model;

		public ExampleAstData(String exampleName, List<INode> model)
		{
			this.exampleName = exampleName;
			this.model = model;
		}

		public String getExampleName()
		{
			return exampleName;
		}

		public List<INode> getModel()
		{
			return model;
		}

	}

	/**
	 * Returns the ASTs for the Overture examples. Only examples that are supposed to parse and TC are returned.
	 * 
	 * @return a collection of {@link ExampleAstData}, each representing one example.
	 * @throws ParserException
	 * @throws LexException
	 */
	static public Collection<ExampleAstData> getExamplesAsts()
			throws ParserException, LexException
	{
		Collection<ExampleAstData> r = new Vector<ExampleAstData>();

		Collection<ExampleTestData> examples = ExampleTestUtils.getCorrectExamplesSources();

		for (ExampleTestData e : examples)
		{
			r.add(parseExample(e));
		}

		return r;
	}

	private static ExampleAstData parseExample(ExampleTestData e)
			throws ParserException, LexException
	{
		List<INode> ast = new LinkedList<INode>();
		switch (e.getDialect())
		{
			case VDM_SL:
				ast = InputProcessor.typedAst(e.getSource(), Dialect.VDM_SL);
				break;

			default:
				break;
		}
		return new ExampleAstData(e.getName(), ast);

	}

}
