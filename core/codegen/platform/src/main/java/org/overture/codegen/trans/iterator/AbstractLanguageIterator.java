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

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public abstract class AbstractLanguageIterator implements ILanguageIterator
{
	protected TransAssistantIR transAssistant;
	protected IterationVarPrefixes iteVarPrefixes;

	public AbstractLanguageIterator(TransAssistantIR transformationAssistant,
			IterationVarPrefixes iteVarPrefixes)
	{
		this.transAssistant = transformationAssistant;
		this.iteVarPrefixes = iteVarPrefixes;
	}

	@Override
	abstract public AVarDeclIR getForLoopInit(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern);

	@Override
	abstract public SExpIR getForLoopCond(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern)
			throws AnalysisException;

	@Override
	abstract public SExpIR getForLoopInc(AIdentifierVarExpIR setVar,
			List<SPatternIR> patterns, SPatternIR pattern);

	@Override
	abstract public AVarDeclIR getNextElementDeclared(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns,
			SPatternIR pattern) throws AnalysisException;

	@Override
	abstract public ALocalPatternAssignmentStmIR getNextElementAssigned(
			AIdentifierVarExpIR setVar, List<SPatternIR> patterns,
			SPatternIR pattern, AVarDeclIR successVarDecl,
			AVarDeclIR nextElementDecl) throws AnalysisException;
}
