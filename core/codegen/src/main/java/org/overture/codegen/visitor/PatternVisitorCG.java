package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.ir.IRInfo;

public class PatternVisitorCG extends AbstractVisitorCG<IRInfo, SPatternCG>
{
	@Override
	public SPatternCG caseAIdentifierPattern(AIdentifierPattern node,
			IRInfo question) throws AnalysisException
	{
		String name = node.getName().getName();
		
		AIdentifierPatternCG idCg = new AIdentifierPatternCG();
		idCg.setName(name);
		
		return idCg;
	}
	
	@Override
	public SPatternCG caseAIgnorePattern(AIgnorePattern node, IRInfo question)
			throws AnalysisException
	{
		return new AIgnorePatternCG();
	}
}
