package org.overture.interpreter.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
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
import org.overture.interpreter.assistant.pattern.AMapUnionPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ARecordPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASeqPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ATuplePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AUnionPatternAssistantInterpreter;

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
		return AConcatenationPatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		return AIdentifierPatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		return AIgnorePatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseAMapPattern(AMapPattern pattern) throws AnalysisException
	{
		return AMapPatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseAMapUnionPattern(AMapUnionPattern pattern)
			throws AnalysisException
	{
		return AMapUnionPatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		return ARecordPatternAssistantInterpreter.isConstrained(pattern);
	}
	@Override
	public Boolean caseASeqPattern(ASeqPattern pattern) throws AnalysisException
	{
		return ASeqPatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseASetPattern(ASetPattern pattern) throws AnalysisException
	{
		return ASetPatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		return ATuplePatternAssistantInterpreter.isConstrained(pattern);
	}
	
	@Override
	public Boolean caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		return AUnionPatternAssistantInterpreter.isConstrained(pattern);
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
