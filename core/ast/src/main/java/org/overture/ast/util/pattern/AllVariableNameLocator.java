/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ast.util.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ANamePatternPair;
import org.overture.ast.patterns.AObjectPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;

/**
 * Locates all variable names in a pattern and add them to a list.
 * 
 * @author gkanos
 */
public class AllVariableNameLocator extends AnswerAdaptor<LexNameList>
{
	protected final IAstAssistantFactory af;
	protected final String fromModule;

	public AllVariableNameLocator(IAstAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	@Override
	public LexNameList caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(pattern.getLeft()));
		list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(pattern.getRight()));

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
			list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(p));
		}

		return list;
	}

	@Override
	public LexNameList caseASeqPattern(ASeqPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(p));
		}

		return list;
	}

	@Override
	public LexNameList caseASetPattern(ASetPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(p));
		}

		return list;
	}

	@Override
	public LexNameList caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (PPattern p : pattern.getPlist())
		{
			list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(p));
		}

		return list;
	}

	@Override
	public LexNameList caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(pattern.getLeft()));
		list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(pattern.getRight()));

		return list;
	}

	@Override
	public LexNameList caseAObjectPattern(AObjectPattern pattern)
			throws AnalysisException
	{
		LexNameList list = new LexNameList();

		for (ANamePatternPair npp : pattern.getFields())
		{
			list.addAll(af.createPPatternAssistant(fromModule).getAllVariableNames(npp.getPattern()));
		}

		return list;
	}

	@Override
	public LexNameList defaultPPattern(PPattern pattern)
			throws AnalysisException
	{
		return new LexNameList();
	}

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
