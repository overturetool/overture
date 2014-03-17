package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.PPattern;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetBindCG;
import org.overture.codegen.cgast.patterns.PBindCG;
import org.overture.codegen.ooast.OoAstInfo;

public class BindVisitorCG  extends AbstractVisitorCG<OoAstInfo, PBindCG>
{
	@Override
	public PBindCG caseASetBind(ASetBind node, OoAstInfo question)
			throws AnalysisException
	{
		PPattern pattern = node.getPattern();
		
		if(!(pattern instanceof AIdentifierPattern))
		{
			question.addUnsupportedNode(node, "Generation of a set bind only supports identifier patterns. Got: " + pattern);
			return null;
		}

		AIdentifierPattern id = (AIdentifierPattern) pattern; 
		
		AIdentifierPatternCG idCg = new AIdentifierPatternCG();
		idCg.setName(id.getName().getName());
		
		PExp set = node.getSet();
		PExpCG setCg = set.apply(question.getExpVisitor(), question);
		
		ASetBindCG setBind = new ASetBindCG();
		setBind.setPattern(idCg);
		setBind.setSet(setCg);
		
		return setBind;
	}
}
