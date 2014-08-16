package org.overture.codegen.trans.patterns;

import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;

public class PatternBlockData
{
	private SPatternCG pattern;
	private AVarLocalDeclCG successVarDecl;
	private AIdentifierVarExpCG successVar;
	private ABlockStmCG declBlock;
	private MismatchHandling mismatchHandling;

	public PatternBlockData(SPatternCG pattern, ABlockStmCG declBlock, MismatchHandling mismatchHandling)
	{
		this.pattern = pattern;
		this.declBlock = declBlock;
		this.mismatchHandling = mismatchHandling;
	}
	
	public PatternBlockData(MismatchHandling mismatchHandling)
	{
		this(null, null, mismatchHandling);
	}
	
	public boolean IsRootPattern(SPatternCG pattern)
	{
		return this.pattern == pattern;
	}

	public SPatternCG getPattern()
	{
		return pattern;
	}

	public void setPattern(SPatternCG pattern)
	{
		this.pattern = pattern;
	}

	public AVarLocalDeclCG getSuccessVarDecl()
	{
		return successVarDecl;
	}

	public void setSuccessVarDecl(AVarLocalDeclCG successVarDecl)
	{
		this.successVarDecl = successVarDecl;
	}

	public AIdentifierVarExpCG getSuccessVar()
	{
		return successVar;
	}

	public void setSuccessVar(AIdentifierVarExpCG successVar)
	{
		this.successVar = successVar;
	}

	public ABlockStmCG getDeclBlock()
	{
		return declBlock;
	}

	public void setDeclBlock(ABlockStmCG declBlock)
	{
		this.declBlock = declBlock;
	}
	
	public MismatchHandling getMismatchHandling()
	{
		return mismatchHandling;
	}
}
