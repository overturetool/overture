package org.overture.typechecker.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.AConcatenationPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AExpressionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapUnionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ARecordPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASeqPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASetPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ATuplePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AUnionPatternAssistantTC;

public class PatternResolver extends QuestionAdaptor<PatternResolver.NewQuestion>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected ITypeCheckerAssistantFactory af;

	public PatternResolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	public static class NewQuestion
	{
		IQuestionAnswer<TypeCheckInfo, PType> rootVisitor;
		TypeCheckInfo question;
		
		public NewQuestion(IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
				TypeCheckInfo question)
		{
			this.rootVisitor = rootVisitor;
			this.question = question;
		}
	}
	
	@Override
	public void caseAConcatenationPattern(AConcatenationPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		AConcatenationPatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}
	@Override
	public void caseAExpressionPattern(AExpressionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		AExpressionPatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseARecordPattern(ARecordPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		ARecordPatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}
	@Override
	public void caseASeqPattern(ASeqPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		ASeqPatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseASetPattern(ASetPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		ASetPatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}

	@Override
	public void caseATuplePattern(ATuplePattern pattern, NewQuestion question)
			throws AnalysisException
	{
		ATuplePatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseAUnionPattern(AUnionPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		AUnionPatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseAMapPattern(AMapPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		AMapPatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseAMapUnionPattern(AMapUnionPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		AMapUnionPatternAssistantTC.typeResolve(pattern, question.rootVisitor, question.question);
	}
	
	@Override
	public void defaultPPattern(PPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		pattern.setResolved(true);
	}
}
