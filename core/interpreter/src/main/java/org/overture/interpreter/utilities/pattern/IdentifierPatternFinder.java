package org.overture.interpreter.utilities.pattern;

import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ANamePatternPair;
import org.overture.ast.patterns.AObjectPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;

/***************************************
 * This class implement a way to find identifier for patterns in a pattern type
 * 
 * @author gkanos
 ****************************************/
public class IdentifierPatternFinder extends
		AnswerAdaptor<List<AIdentifierPattern>>
{
	protected IInterpreterAssistantFactory af;

	public IdentifierPatternFinder(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public List<AIdentifierPattern> caseAConcatenationPattern(
			AConcatenationPattern pattern) throws AnalysisException
	{

		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		list.addAll(pattern.getLeft().apply(THIS));
		list.addAll(pattern.getRight().apply(THIS));
		return list;
	}

	@Override
	public List<AIdentifierPattern> caseAIdentifierPattern(
			AIdentifierPattern pattern) throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();
		list.add(pattern);
		return list;
	}

	@Override
	public List<AIdentifierPattern> caseAMapPattern(AMapPattern pattern)
			throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		for (AMapletPatternMaplet p : pattern.getMaplets())
		{
			list.addAll(af.createAMapPatternMapletAssistant().findIdentifiers(p));
		}

		return list;
	}

	@Override
	public List<AIdentifierPattern> caseAMapUnionPattern(
			AMapUnionPattern pattern) throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		list.addAll(pattern.getLeft().apply(THIS));
		list.addAll(pattern.getRight().apply(THIS));
		return list;
	}

	@Override
	public List<AIdentifierPattern> caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(p.apply(THIS));
		}

		return list;
	}

	@Override
	public List<AIdentifierPattern> caseASeqPattern(ASeqPattern pattern)
			throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(p.apply(THIS));
		}

		return list;
	}

	@Override
	public List<AIdentifierPattern> caseASetPattern(ASetPattern pattern)
			throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(p.apply(THIS));
		}

		return list;
	}

	@Override
	public List<AIdentifierPattern> caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(p.apply(THIS));
		}

		return list;
	}

	@Override
	public List<AIdentifierPattern> caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		list.addAll(pattern.getLeft().apply(THIS));
		list.addAll(pattern.getRight().apply(THIS));
		return list;
	}

	@Override
	public List<AIdentifierPattern> caseAObjectPattern(AObjectPattern pattern)
			throws AnalysisException
	{
		List<AIdentifierPattern> list = new ArrayList<AIdentifierPattern>();

		for (ANamePatternPair npp : pattern.getFields())
		{
			list.addAll(npp.getPattern().apply(THIS));
		}

		return list;
	}

	@Override
	public List<AIdentifierPattern> defaultPPattern(PPattern node)
			throws AnalysisException
	{
		return new ArrayList<AIdentifierPattern>(); // Most have none
	}

	@Override
	public List<AIdentifierPattern> createNewReturnValue(INode node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AIdentifierPattern> createNewReturnValue(Object node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
