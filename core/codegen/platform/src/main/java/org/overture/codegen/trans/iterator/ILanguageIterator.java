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
package org.overture.codegen.trans.iterator;

import java.util.List;

import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SPatternCG;
import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmCG;

public interface ILanguageIterator
{
	public List<SStmCG> getPreForLoopStms(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern);
	
	public AVarDeclCG getForLoopInit(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern);

	public SExpCG getForLoopCond(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException;

	public SExpCG getForLoopInc(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern);

	public AVarDeclCG getNextElementDeclared(AIdentifierVarExpCG setVar,
			List<SPatternCG> patterns, SPatternCG pattern)
			throws AnalysisException;

	public ALocalPatternAssignmentStmCG getNextElementAssigned(
			AIdentifierVarExpCG setVar, List<SPatternCG> patterns,
			SPatternCG pattern, AVarDeclCG successVarDecl,
			AVarDeclCG nextElementDecl) throws AnalysisException;
	
	public SExpCG consNextElementCall(AIdentifierVarExpCG setVar) throws AnalysisException;

}
