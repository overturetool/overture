package org.overture.typechecker.utilities.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
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
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.AConcatenationPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AIdentifierPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapUnionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ARecordPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASeqPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASetPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ATuplePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AUnionPatternAssistantTC;

/**
 * Get a complete list of all definitions, including duplicates. This method should only be used only by PP
 * 
 * @author gkanos
 */

public class AllDefinitionLocator extends QuestionAnswerAdaptor<AllDefinitionLocator.NewQuestion, List<PDefinition>>
{

	public static class NewQuestion
	{
		PType ptype;
		NameScope scope;
		
		public NewQuestion(PType ptype,
				NameScope scope)
		{
			this.ptype = ptype;
			this.scope = scope;
		}
	}
	
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public AllDefinitionLocator(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public List<PDefinition> caseAIdentifierPattern(AIdentifierPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return AIdentifierPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}
	
	@Override
	public List<PDefinition> caseABooleanPattern(ABooleanPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}
	
	@Override
	public List<PDefinition> caseACharacterPattern(ACharacterPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAExpressionPattern(AExpressionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}
	
	@Override
	public List<PDefinition> caseAIgnorePattern(AIgnorePattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}
	
	@Override
	public List<PDefinition> caseAIntegerPattern(AIntegerPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseANilPattern(ANilPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}
	
	@Override
	public List<PDefinition> caseAQuotePattern(AQuotePattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}
	
	@Override
	public List<PDefinition> caseARealPattern(ARealPattern node,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}

	@Override
	public List<PDefinition> caseAStringPattern(AStringPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return new Vector<PDefinition>();
	}
	
	@Override
	public List<PDefinition> caseAConcatenationPattern(
			AConcatenationPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		return AConcatenationPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}
	
	@Override
	public List<PDefinition> caseARecordPattern(ARecordPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return ARecordPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}
	
	@Override
	public List<PDefinition> caseASeqPattern(ASeqPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return ASeqPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}

	@Override
	public List<PDefinition> caseASetPattern(ASetPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return ASetPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}
	
	@Override
	public List<PDefinition> caseATuplePattern(ATuplePattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return ATuplePatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}
	
	@Override
	public List<PDefinition> caseAUnionPattern(AUnionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return AUnionPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}
	
	@Override
	public List<PDefinition> caseAMapUnionPattern(AMapUnionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return AMapUnionPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}
	
	@Override
	public List<PDefinition> caseAMapPattern(AMapPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		return AMapPatternAssistantTC.getAllDefinitions(pattern, question.ptype, question.scope);
	}

	@Override
	public List<PDefinition> createNewReturnValue(INode node,
			NewQuestion question) throws AnalysisException
	{
		assert false : "PPatternAssistant.getDefinitions - should not hit this case";
		return null;
	}

	@Override
	public List<PDefinition> createNewReturnValue(Object node,
			NewQuestion question) throws AnalysisException
	{
		assert false : "PPatternAssistant.getDefinitions - should not hit this case";
		return null;
	}
}
