package org.overture.ide.debug.utils;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;

public class ExecutableAnalysis extends DepthFirstAnalysisAdaptor
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5353696074294132014L;
	protected boolean executable = false;
	private final int searchLine;

	private ExecutableAnalysis(int searchLine)
	{
		this.searchLine = searchLine;
	}

	public static boolean isExecutable(INode node, int line)
	{
		ExecutableAnalysis analysis = new ExecutableAnalysis(line);

		try
		{
			node.apply(analysis);
		} catch (Throwable e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return analysis.executable;
	}

	public void defaultInPStm(PStm node)
	{
		if (node.getLocation().startLine == searchLine)
		{
			executable = true;
			return;
		}
	}

	public void defaultInPExp(PExp node)
	{
		if (node.getLocation().startLine == searchLine)
		{
			executable = true;
			return;
		}
	}
}
