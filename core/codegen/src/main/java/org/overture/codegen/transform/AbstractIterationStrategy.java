package org.overture.codegen.transform;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.SSetTypeCG;

public abstract class AbstractIterationStrategy
{
	abstract public List<ALocalVarDeclCG> getOuterBlockDecls();
	
	abstract public PExpCG getForLoopCond(SSetTypeCG setType, String iteratorName) throws AnalysisException;
	
	abstract public List<PStmCG> getCurrentForLoopStms();
	
	abstract public List<PStmCG> getOuterBlockStms();
	
	
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
