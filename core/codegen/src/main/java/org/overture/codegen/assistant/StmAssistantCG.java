package org.overture.codegen.assistant;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.codegen.cgast.expressions.ALetDefExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.ACallStmCG;
import org.overture.codegen.cgast.statements.ALetDefStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.ooast.OoAstInfo;

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
	
	public static void generateArguments(List<PExp> args, ACallStmCG callStm, OoAstInfo question) throws AnalysisException
	{
		for (int i = 0; i < args.size(); i++)
		{
			PExpCG arg = args.get(i).apply(question.getExpVisitor(), question);
			callStm.getArgs().add(arg);
		}
	}
}
