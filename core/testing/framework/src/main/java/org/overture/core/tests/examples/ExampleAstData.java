package org.overture.core.tests.examples;

import java.util.List;

import org.overture.ast.node.INode;

/**
 * Processed test data of an Overture example. This class holds the name and AST (List of {@link INode}) of an Overture
 * example.
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

	@Override
	public String toString()
	{
		return this.exampleName;
	}

}
