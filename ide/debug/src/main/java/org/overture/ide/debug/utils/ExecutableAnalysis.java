package org.overture.ide.debug.utils;

import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;

public class ExecutableAnalysis extends DepthFirstAnalysisAdaptor
{
	protected boolean executable = false;
	private final int searchLine;

	private ExecutableAnalysis(int searchLine)
	{
		this.searchLine = searchLine;
	}

	public static boolean isExecutable(INode node, int line)
	{
		ExecutableAnalysis analysis = new ExecutableAnalysis(line);

		node.apply(analysis);

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
