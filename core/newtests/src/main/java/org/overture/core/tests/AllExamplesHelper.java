package org.overture.core.tests;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

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
	 * Returns the sources for the Overture examples. Only examples that are supposed to parse and TC are returned. The
	 * source for each example is a single string of the entire model, including any libraries.
	 * 
	 * @return a collection of {@link ExampleTestData}, each representing one example.
	 * @throws IOException
	 */
	static public Collection<ExampleTestData> getExamplesSources()
			throws IOException
	{
		return ExampleTestUtils.getCorrectExamplesSources();
	}

	/**
	 * Returns the ASTs for the Overture examples. Only examples that are supposed to parse and TC are returned.
	 * 
	 * @return a collection of {@link ExampleAstData}, each representing one example.
	 * @throws ParserException
	 * @throws LexException
	 * @throws IOException
	 */
	static public Collection<ExampleAstData> getExamplesAsts()
			throws ParserException, LexException, IOException
	{
		Collection<ExampleAstData> r = new Vector<ExampleAstData>();

		Collection<ExampleTestData> examples = ExampleTestUtils.getCorrectExamplesSources();

		for (ExampleTestData e : examples)
		{
			r.add(ParseTcFacade.parseExample(e));
		}

		return r;
	}

}
