package org.overture.core.tests.examples;

import java.util.List;

import org.overture.ast.node.INode;

	/**
	 * Simple wrapper class for examples data. Contains the AST (as a list of {@link INode}) and name of an example.
	 * 
	 * @author ldc
	 */
	public class ExampleAstData
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
