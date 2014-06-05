package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.pattern.PPatternCG;
import org.overture.codegen.ir.IRInfo;

public class PatternVisitorCG extends AbstractVisitorCG<IRInfo, PPatternCG>
{
	@Override
	public PPatternCG caseAIdentifierPattern(AIdentifierPattern node,
			IRInfo question) throws AnalysisException
	{
		String name = node.getName().getName();
		
		AIdentifierPatternCG idCg = new AIdentifierPatternCG();
		idCg.setName(name);
		
		return idCg;
	}
}
