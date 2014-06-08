package org.overture.codegen.transform;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;

public class BaseTransformationAssistant
{
	public void replaceNodeWith(INode original, INode replacement)
	{
		if (original != replacement)
		{
			replace(original, replacement);
		}
	}

	public void replaceNodeWithRecursively(INode original, INode replacement, DepthFirstAnalysisAdaptor analysis) throws AnalysisException
	{
		if(original != replacement)
		{
			replaceNodeWith(original, replacement);
			replacement.apply(analysis);
		}
	}

	private void replace(INode original, INode replacement)
	{
		INode parent = original.parent();
		parent.replaceChild(original, replacement);
		original.parent(null);
	}
}
