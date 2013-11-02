package org.overture.codegen.assistant;

import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;

public class StmAssistantCG
{
	public static ALetDefStmCG convertToLetDefStm(ALetDefExpCG letDefExp)
	{
		ALetDefStmCG letDefStm = new ALetDefStmCG();
		letDefStm.setLocalDefs(letDefExp.getLocalDefs());
		
		AReturnStmCG returnStm = new AReturnStmCG();
		returnStm.setExp(letDefExp.getExp());
		letDefStm.setStm(returnStm);
		
		return letDefStm;
	}
}
