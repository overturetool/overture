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
package org.overture.codegen.ir;

import org.overture.codegen.analysis.vdm.AbstractAnalysis;
import org.overture.codegen.analysis.vdm.QuoteAnalysis;
import org.overture.codegen.analysis.vdm.UtilAnalysis;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;

public class IRAnalysis
{
	public static boolean usesQuoteLiterals(ADefaultClassDeclCG classDecl)
	{
		return hasDependency(classDecl, new QuoteAnalysis());
	}

	public static boolean usesUtils(ADefaultClassDeclCG classDecl)
	{
		return hasDependency(classDecl, new UtilAnalysis());
	}

	private static boolean hasDependency(ADefaultClassDeclCG classDecl,
			AbstractAnalysis analysis)
	{
		try
		{
			classDecl.apply(analysis);
		} catch (AnalysisException e)
		{
			// If we find what we are looking for an exception will
			// be thrown to terminate the visitor analysis
		}

		return analysis.isFound();
	}
}
