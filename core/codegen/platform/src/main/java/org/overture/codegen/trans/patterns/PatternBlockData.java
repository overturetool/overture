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
package org.overture.codegen.trans.patterns;

import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;

public class PatternBlockData
{
	private SPatternIR pattern;
	private AVarDeclIR successVarDecl;
	private AIdentifierVarExpIR successVar;
	private ABlockStmIR declBlock;
	private AIdentifierVarExpIR rootPatternVar;
	private MismatchHandling mismatchHandling;

	public PatternBlockData(SPatternIR pattern, ABlockStmIR declBlock,
			MismatchHandling mismatchHandling)
	{
		this.pattern = pattern;
		this.declBlock = declBlock;
		this.rootPatternVar = null;
		this.mismatchHandling = mismatchHandling;
	}

	public PatternBlockData(MismatchHandling mismatchHandling)
	{
		this(null, null, mismatchHandling);
	}

	public boolean IsRootPattern(SPatternIR pattern)
	{
		return this.pattern == pattern;
	}

	public SPatternIR getPattern()
	{
		return pattern;
	}

	public void setPattern(SPatternIR pattern)
	{
		this.pattern = pattern;
	}

	public AVarDeclIR getSuccessVarDecl()
	{
		return successVarDecl;
	}

	public void setSuccessVarDecl(AVarDeclIR successVarDecl)
	{
		this.successVarDecl = successVarDecl;
	}

	public AIdentifierVarExpIR getSuccessVar()
	{
		return successVar;
	}

	public void setSuccessVar(AIdentifierVarExpIR successVar)
	{
		this.successVar = successVar;
	}

	public ABlockStmIR getDeclBlock()
	{
		return declBlock;
	}

	public void setDeclBlock(ABlockStmIR declBlock)
	{
		this.declBlock = declBlock;
	}

	public void setRootPatternVar(AIdentifierVarExpIR var)
	{
		// There can only be one root pattern variable
		if (this.rootPatternVar == null)
		{
			this.rootPatternVar = var;
		}
	}

	public AIdentifierVarExpIR getRootPatternVar()
	{
		return rootPatternVar;
	}

	public void setMismatchHandling(MismatchHandling mismatchHandling)
	{
		this.mismatchHandling = mismatchHandling;
	}

	public MismatchHandling getMismatchHandling()
	{
		return mismatchHandling;
	}

	@Override
	public String toString()
	{
		return String.format("Pattern %s\nSuccess var: %s\n"
				+ "Mismatch handling: %s\nDeclaration block: %s", pattern, successVar, mismatchHandling, declBlock);
	}
}
