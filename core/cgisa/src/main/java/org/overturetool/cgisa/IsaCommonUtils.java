package org.overturetool.cgisa;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.expressions.AApplyExpCG;

public class IsaCommonUtils
{
	public boolean isRoot(INode node)
	{
		if (node.parent() instanceof AApplyExpCG)
		{
			AApplyExpCG par = (AApplyExpCG) node.parent();
			if (par.getRoot() == node)
			{
				return true;
			}
		}
		return false;
	}
}
