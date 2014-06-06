package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.PPattern;
import org.overture.codegen.cgast.SBindCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetBindCG;
import org.overture.codegen.ir.IRInfo;

public class BindVisitorCG  extends AbstractVisitorCG<IRInfo, SBindCG>
{
	@Override
	public SBindCG caseASetBind(ASetBind node, IRInfo question)
			throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		SPatternCG patternTempCg = pattern.apply(question.getPatternVisitor(), question);
		
		if(!(patternTempCg instanceof AIdentifierPatternCG))
		{
			question.addUnsupportedNode(node, "Generation of a set bind only supports identifier patterns. Got: " + patternTempCg);
			return null;
		}
		
		AIdentifierPatternCG patternCg = (AIdentifierPatternCG) patternTempCg;
		
		PExp set = node.getSet();
		SExpCG setCg = set.apply(question.getExpVisitor(), question);
		
		ASetBindCG setBind = new ASetBindCG();
		setBind.setPattern(patternCg);
		setBind.setSet(setCg);
		
		return setBind;
	}
}
