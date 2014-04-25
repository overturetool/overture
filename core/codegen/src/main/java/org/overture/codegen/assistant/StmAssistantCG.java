package org.overture.codegen.assistant;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.ALetBeStExpCG;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ALetBeStStmCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.ooast.OoAstInfo;

public class StmAssistantCG extends AssistantBase
{
	public StmAssistantCG(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public ALetDefStmCG convertToLetDefStm(ALetDefExpCG letDefExp)
	{
		AReturnStmCG returnStm = new AReturnStmCG();
		returnStm.setExp(letDefExp.getExp());

		
		ALetDefStmCG letDefStm = new ALetDefStmCG();
		
		letDefStm.setLocalDefs(letDefExp.getLocalDefs());
		letDefStm.setStm(returnStm);
		
		return letDefStm;
	}
	
	public ALetBeStStmCG convertToLetBeStStm(ALetBeStExpCG letBeStExp)
	{
		AReturnStmCG returnStm = new AReturnStmCG();
		returnStm.setExp(letBeStExp.getValue());
		
		ALetBeStStmCG letBeStStm = new ALetBeStStmCG();
		letBeStStm.setHeader(letBeStExp.getHeader());
		letBeStStm.setStatement(returnStm);
		
		return letBeStStm;
	}
	
	public void injectDeclAsStm(ABlockStmCG block, AVarLocalDeclCG decl)
	{
		ABlockStmCG wrappingBlock = new ABlockStmCG();
		
		wrappingBlock.getLocalDefs().add(decl);
		
		block.getStatements().add(wrappingBlock);
	}
}
