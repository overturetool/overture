package org.overture.codegen.transform;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.PTypeCG;

public abstract class AbstractIterationStrategy
{
	abstract public List<ALocalVarDeclCG> getOuterBlockDecls(List<AIdentifierPatternCG> ids) throws AnalysisException;
	
	abstract public PExpCG getForLoopCond(String iteratorName) throws AnalysisException;

	abstract public ABlockStmCG getForLoopBody(PTypeCG setElementType, AIdentifierPatternCG id, String iteratorName) throws AnalysisException;

	abstract public List<PStmCG> getLastForLoopStms();
	
	abstract public List<PStmCG> getOuterBlockStms();
	
	protected boolean firstBind;
	protected boolean lastBind;

	public void setFirstBind(boolean firstBind)
	{
		this.firstBind = firstBind;
	}

	public void setLastBind(boolean lastBind)
	{
		this.lastBind = lastBind;
	}
	
	protected List<PStmCG> packStm(PStmCG stm)
	{
		List<PStmCG> stms = new LinkedList<PStmCG>();
		
		stms.add(stm);
		
		return stms;
	}
	
	protected List<ALocalVarDeclCG> packDecl(ALocalVarDeclCG decl)
	{
		List<ALocalVarDeclCG> decls = new LinkedList<ALocalVarDeclCG>();
		
		decls.add(decl);
		
		return decls;
	}
}
