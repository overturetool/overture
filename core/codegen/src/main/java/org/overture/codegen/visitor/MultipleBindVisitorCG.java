package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SMultipleBindCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.ir.IRInfo;

public class MultipleBindVisitorCG extends AbstractVisitorCG<IRInfo, SMultipleBindCG>
{
	
	@Override
	public SMultipleBindCG caseASetMultipleBind(ASetMultipleBind node,
			IRInfo question) throws AnalysisException
	{
		LinkedList<PPattern> patterns = node.getPlist();
		PExp set = node.getSet();
		
		LinkedList<AIdentifierPatternCG> patternsCg = new LinkedList<AIdentifierPatternCG>();
		
		for(PPattern pattern : patterns)
		{
			SPatternCG patternTempCg = pattern.apply(question.getPatternVisitor(), question);
			
			if(!(patternTempCg instanceof AIdentifierPatternCG))
			{
				question.addUnsupportedNode(node, "Generation of a multiple set bind only supports identifier patterns. Got: " + patternTempCg);
				return null;
			}
			
			AIdentifierPatternCG idCg = (AIdentifierPatternCG) patternTempCg;
			patternsCg.add(idCg);
		}
		
		SExpCG setCg = set.apply(question.getExpVisitor(), question);
		
		ASetMultipleBindCG multipleSetBind = new ASetMultipleBindCG();
		
		multipleSetBind.setPatterns(patternsCg);
		multipleSetBind.setSet(setCg);
		
		return multipleSetBind;
	}
	
}
