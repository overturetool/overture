package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.patterns.PMultipleBindCG;
import org.overture.codegen.ooast.OoAstInfo;

public class MultipleBindVisitorCG extends AbstractVisitorCG<OoAstInfo, PMultipleBindCG>
{
	
	@Override
	public PMultipleBindCG caseASetMultipleBind(ASetMultipleBind node,
			OoAstInfo question) throws AnalysisException
	{
		LinkedList<PPattern> patterns = node.getPlist();
		PExp set = node.getSet();
		
		LinkedList<AIdentifierPatternCG> patternsCg = new LinkedList<AIdentifierPatternCG>();
		
		for(PPattern pattern : patterns)
		{
			if(!(pattern instanceof AIdentifierPattern))
			{
				question.addUnsupportedNode(node, "Generation of a multiple set bind only supports identifier patterns. Got: " + pattern);
				return null;
			}
			
			AIdentifierPattern id = (AIdentifierPattern) pattern;
			
			AIdentifierPatternCG idCg = new AIdentifierPatternCG();
			idCg.setName(id.getName().getName());
			
			patternsCg.add(idCg);
		}
		
		PExpCG setCg = set.apply(question.getExpVisitor(), question);
		
		ASetMultipleBindCG multipleSetBind = new ASetMultipleBindCG();
		
		multipleSetBind.setPatterns(patternsCg);
		multipleSetBind.setSet(setCg);
		
		return multipleSetBind;
	}
	
}
