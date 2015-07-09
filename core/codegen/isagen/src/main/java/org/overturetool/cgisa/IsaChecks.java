/*
 * #%~
 * The VDM to Isabelle Translator
 * %%
 * Copyright (C) 2008 - 2015 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overturetool.cgisa;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;

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
	public boolean isRootRec(AApplyExpCG node)
	{
		return (node.getRoot() instanceof AFieldExpCG);
	}

	public boolean isFieldRHS(INode node)
	{
		return node.getAncestor(AFieldDeclCG.class) != null;
	}

}
