package org.overture.pog.utility;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;

public class POException extends AnalysisException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	INode errorNode;

	public POException(INode errorNode, String message)
	{
		super(message);
		this.errorNode = errorNode;
	}

	public INode getErrorNode()
	{
		return errorNode;
	}

	public void setErrorNode(INode errorNode)
	{
		this.errorNode = errorNode;
	}

}
