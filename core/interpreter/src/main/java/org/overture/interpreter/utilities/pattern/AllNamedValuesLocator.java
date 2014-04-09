package org.overture.interpreter.utilities.pattern;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.assistant.pattern.ABooleanPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ACharacterPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AConcatenationPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AExpressionPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AIdentifierPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AIgnorePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AIntegerPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AMapUnionPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ANilPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AQuotePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ARealPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ARecordPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASeqPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ASetPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AStringPatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.ATuplePatternAssistantInterpreter;
import org.overture.interpreter.assistant.pattern.AUnionPatternAssistantInterpreter;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.Value;

public class AllNamedValuesLocator extends QuestionAnswerAdaptor<AllNamedValuesLocator.Newquestion, List<NameValuePairList>>
{
	public static class Newquestion
	{
		Value expval;
		Context ctxt;
		
		public Newquestion(Value expval, Context ctxt)
		{
			this.expval = expval;
			this.ctxt = ctxt;
		}
	}
	
	protected IInterpreterAssistantFactory af;
	
	public AllNamedValuesLocator(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public List<NameValuePairList> caseABooleanPattern(ABooleanPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return ABooleanPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}

	@Override
	public List<NameValuePairList> caseACharacterPattern(
			ACharacterPattern pattern, Newquestion question)
			throws AnalysisException
	{
		return ACharacterPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAConcatenationPattern(
			AConcatenationPattern pattern, Newquestion question)
			throws AnalysisException
	{
		return AConcatenationPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAExpressionPattern(
			AExpressionPattern pattern, Newquestion question)
			throws AnalysisException
	{
		return AExpressionPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAIdentifierPattern(
			AIdentifierPattern pattern, Newquestion question)
			throws AnalysisException
	{
		return AIdentifierPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAIgnorePattern(AIgnorePattern pattern,
			Newquestion question) throws AnalysisException
	{
		return AIgnorePatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAIntegerPattern(AIntegerPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return AIntegerPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAMapPattern(AMapPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return AMapPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAMapUnionPattern(AMapUnionPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return AMapUnionPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseANilPattern(ANilPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return ANilPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAQuotePattern(AQuotePattern pattern,
			Newquestion question) throws AnalysisException
	{
		return AQuotePatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseARealPattern(ARealPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return ARealPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseARecordPattern(ARecordPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return ARecordPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseASeqPattern(ASeqPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return ASeqPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseASetPattern(ASetPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return ASetPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAStringPattern(AStringPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return AStringPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	@Override
	public List<NameValuePairList> caseATuplePattern(ATuplePattern pattern,
			Newquestion question) throws AnalysisException
	{
		return ATuplePatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> caseAUnionPattern(AUnionPattern pattern,
			Newquestion question) throws AnalysisException
	{
		return AUnionPatternAssistantInterpreter.getAllNamedValues(pattern, question.expval, question.ctxt);
	}
	
	@Override
	public List<NameValuePairList> defaultPPattern(PPattern pattern,
			Newquestion question) throws AnalysisException
	{
		assert false : "Should not happen!";
		return null;
	}
	@Override
	public List<NameValuePairList> createNewReturnValue(INode node,
			Newquestion question) throws AnalysisException
	{
		assert false : "Should not happen!";
		return null;
	}

	@Override
	public List<NameValuePairList> createNewReturnValue(Object node,
			Newquestion question) throws AnalysisException
	{
		assert false : "Should not happen!";
		return null;
	}

}
