/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
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
package org.overture.codegen.analysis.vdm;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.expressions.ATupleExpCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.ir.IRConstants;

public class UtilAnalysis extends AbstractAnalysis
{
	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		if (node instanceof AClassTypeCG)
		{
			AClassTypeCG classType = (AClassTypeCG) node;

			String className = classType.getName();

			for (int i = 0; i < IRConstants.UTIL_NAMES.length; i++)
			{
				if (className.equals(IRConstants.UTIL_NAMES[i]))
				{
					setFound();
					throw new AnalysisException();
				}
			}
		} else if (node instanceof ARecordDeclCG)
		{
			setFound();
			throw new AnalysisException();
		} else if (node instanceof ATupleTypeCG || node instanceof ATupleExpCG)
		{
			setFound();
			throw new AnalysisException();
		}
	}
}
