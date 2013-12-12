package org.overture.typechecker.utilities.pattern;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.AMapletPatternMapletAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

/**
 * This class set a pattern to unresolved.
 * 
 * @author kel
 */
public class PatternUnresolver extends AnalysisAdaptor
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public PatternUnresolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public void caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		pattern.getLeft().apply(THIS);
		pattern.getRight().apply(THIS);
		pattern.setResolved(false);
	}
	
	@Override
	public void caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		pattern.getType().apply(THIS);
		pattern.setResolved(false);
	}
	
	@Override
	public void caseASeqPattern(ASeqPattern pattern) throws AnalysisException
	{
		af.createPPatternListAssistant().unResolve(pattern.getPlist());
		pattern.setResolved(false);
	}
	
	@Override
	public void caseASetPattern(ASetPattern pattern) throws AnalysisException
	{
		af.createPPatternListAssistant().unResolve(pattern.getPlist());
		pattern.setResolved(false);
	}
	
	@Override
	public void caseATuplePattern(ATuplePattern pattern) throws AnalysisException
	{
		af.createPPatternListAssistant().unResolve(pattern.getPlist());
		pattern.setResolved(false);
	}
	
	@Override
	public void caseAUnionPattern(AUnionPattern pattern) throws AnalysisException
	{
		pattern.getLeft().apply(THIS);
		pattern.getRight().apply(THIS);
		pattern.setResolved(false);
	}
	
	@Override
	public void caseAMapUnionPattern(AMapUnionPattern pattern)
			throws AnalysisException
	{
		pattern.getLeft().apply(THIS);
		pattern.getRight().apply(THIS);
		pattern.setResolved(false);
	}
	
	@Override
	public void caseAMapPattern(AMapPattern pattern) throws AnalysisException
	{
		for (AMapletPatternMaplet mp : pattern.getMaplets())
		{
			af.createAMapletPatternMapletAssistant().unResolve(mp);
		}
		
		pattern.setResolved(false);
	}
	
	@Override
	public void defaultPPattern(PPattern pattern) throws AnalysisException
	{
		pattern.setResolved(false);
	}
}
