package org.overture.interpreter.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.pattern.AConcatenationPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AIdentifierPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AIgnorePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapPatternMapletAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapUnionPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ARecordPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASeqPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ATuplePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AUnionPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.PPatternListAssistantInterpreter;

public class ConstrainedPatternChecker extends AnswerAdaptor<Boolean>
{
	protected IInterpreterAssistantFactory af;
	
	public ConstrainedPatternChecker(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public Boolean caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		//return AConcatenationPatternAssistantInterpreter.isConstrained(pattern);
//		return PPatternAssistantInterpreter.isConstrained(pattern.getLeft())
//				|| PPatternAssistantInterpreter.isConstrained(pattern.getRight());
		return pattern.getLeft().apply(THIS) || pattern.getRight().apply(THIS);
	}
	
	@Override
	public Boolean caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		//return AIdentifierPatternAssistantInterpreter.isConstrained(pattern);
		return pattern.getConstrained(); // The variable may be constrained to be the same as another occurrence
	}
	
	@Override
	public Boolean caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		//return AIgnorePatternAssistantInterpreter.isConstrained(pattern);
		return false;
	}
	
	@Override
	public Boolean caseAMapPattern(AMapPattern pattern) throws AnalysisException
	{
		//return AMapPatternAssistantInterpreter.isConstrained(pattern);
		for (AMapletPatternMaplet p : pattern.getMaplets())
		{
			//if (AMapPatternMapletAssistantInterpreter.isConstrained(p))
			if(p.apply(THIS))
			{
				return true;
			}
		}

		return false;
	}
	
	@Override
	public Boolean caseAMapUnionPattern(AMapUnionPattern pattern)
			throws AnalysisException
	{
		//return AMapUnionPatternAssistantInterpreter.isConstrained(pattern);
//		return PPatternAssistantInterpreter.isConstrained(pattern.getLeft())
//				|| PPatternAssistantInterpreter.isConstrained(pattern.getRight());
		return pattern.getLeft().apply(THIS) || pattern.getRight().apply(THIS);
	}
	
	@Override
	public Boolean caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		//return ARecordPatternAssistantInterpreter.isConstrained(pattern);
		return PPatternListAssistantInterpreter.isConstrained(pattern.getPlist());
		
	}
	@Override
	public Boolean caseASeqPattern(ASeqPattern pattern) throws AnalysisException
	{
		return ASeqPatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseASetPattern(ASetPattern pattern) throws AnalysisException
	{
		//return ASetPatternAssistantInterpreter.isConstrained(pattern);
		if (af.createPTypeAssistant().isUnion(af.createPPatternListAssistant().getPossibleType(pattern.getPlist(), pattern.getLocation())))
		{
			return true; // Set types are various, so we must permute
		}

		return PPatternListAssistantInterpreter.isConstrained(pattern.getPlist());
	}
	
	@Override
	public Boolean caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		//return ATuplePatternAssistantInterpreter.isConstrained(pattern);
		return PPatternListAssistantInterpreter.isConstrained(pattern.getPlist());
	}
	
	@Override
	public Boolean caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		//return AUnionPatternAssistantInterpreter.isConstrained(pattern);
//		return PPatternAssistantInterpreter.isConstrained(pattern.getLeft())
//				|| PPatternAssistantInterpreter.isConstrained(pattern.getRight());
		return pattern.getLeft().apply(THIS) || pattern.getRight().apply(THIS);
	}
	
	@Override
	public Boolean defaultPPattern(PPattern node) throws AnalysisException
	{
		return true;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return true;
	}

}
