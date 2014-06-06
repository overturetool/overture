package org.overture.codegen.transform;

import org.overture.codegen.cgast.INode;

public class BaseTransformationAssistant
{
	public void replaceNodeWith(INode original, INode replacement)
	{
		INode parent = original.parent();
		parent.replaceChild(original, replacement);
		original.parent(null);
	}
}
