package org.overturetool.cgisa;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;

public class IsaChecks
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

	public boolean isFieldRHS(INode node)
	{
		return node.getAncestor(AFieldDeclCG.class) != null;
	}
}
