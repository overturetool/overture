package org.overture.ast.util.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.assistant.pattern.PPatternAssistant;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;

public class AllVariableNameLocator extends AnswerAdaptor<LexNameList>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public AllVariableNameLocator(IAstAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public LexNameList caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));

		return list;
	}
	
	@Override
	public LexNameList caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();
		list.add(pattern.getName());
		return list;
	}
	
	@Override
	public LexNameList caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}

		return list;
	}
//	public static LexNameList getAllVariableNames(AConcatenationPattern pattern)
//			throws InvocationAssistantException
//	{

//	}
//
//	public static LexNameList getAllVariableNames(AIdentifierPattern pattern)
//	{

//	}
//
//	public static LexNameList getAllVariableNames(ARecordPattern pattern)
//			throws InvocationAssistantException
//	{

//
//	}
//
	@Override
	public LexNameList caseASeqPattern(ASeqPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}

		return list;
	}
//	public static LexNameList getAllVariableNames(ASeqPattern pattern)
//			throws InvocationAssistantException
//	{

//	}
//
	@Override
	public LexNameList caseASetPattern(ASetPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}

		return list;
	}
//	public static LexNameList getAllVariableNames(ASetPattern pattern)
//			throws InvocationAssistantException
//	{

//	}
//
	@Override
	public LexNameList caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getAllVariableNames(p));
		}

		return list;
	}
//	public static LexNameList getAllVariableNames(ATuplePattern pattern)
//			throws InvocationAssistantException
//	{

//	}
//
	@Override
	public LexNameList caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getLeft()));
		list.addAll(PPatternAssistant.getAllVariableNames(pattern.getRight()));

		return list;
	}
//	public static LexNameList getAllVariableNames(AUnionPattern pattern)
//			throws InvocationAssistantException
//	{

//	}
//	@Override
//	public LexNameList defaultPPattern(PPattern pattern) throws AnalysisException
//	{
//		return PPatternAssistant.getVariableNamesBaseCase(pattern);
//	}
//	public static LexNameList getVariableNames(PPattern pattern)
//			throws InvocationAssistantException
//	{
//		
//	}
	
	@Override
	public LexNameList createNewReturnValue(INode node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LexNameList createNewReturnValue(Object node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
