/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.utilities.pattern;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
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
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to check if a pattern is a simple value.
 * 
 * @author kel
 */
public class SimplePatternChecker extends AnswerAdaptor<Boolean>
{
	protected ITypeCheckerAssistantFactory af;

	public SimplePatternChecker(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public Boolean caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		return pattern.getLeft().apply(THIS) && pattern.getRight().apply(THIS);
	}

	@Override
	public Boolean caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean caseAMapUnionPattern(AMapUnionPattern pattern)
			throws AnalysisException
	{
		return pattern.getLeft().apply(THIS) && pattern.getRight().apply(THIS);
	}

	@Override
	public Boolean caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().isSimple(pattern.getPlist());
	}

	@Override
	public Boolean caseASeqPattern(ASeqPattern pattern)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().isSimple(pattern.getPlist());
	}

	@Override
	public Boolean caseASetPattern(ASetPattern pattern)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().isSimple(pattern.getPlist());
	}

	@Override
	public Boolean caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().isSimple(pattern.getPlist());
	}

	@Override
	public Boolean caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		return pattern.getLeft().apply(THIS) && pattern.getRight().apply(THIS);
	}

	@Override
	public Boolean caseAMapPattern(AMapPattern pattern)
			throws AnalysisException
	{
		for (AMapletPatternMaplet mp : pattern.getMaplets())
		{

			// if (!AMapletPatternMapletAssistantTC.isSimple(mp)) // Original code.
			if (!mp.apply(THIS))
			{
				return false;
			}

		}
		return true;
	}

	@Override
	public Boolean caseAObjectPattern(AObjectPattern pattern)
			throws AnalysisException
	{
		LinkedList<PPattern> list = new LinkedList<PPattern>();
		
		for (ANamePatternPair npp: pattern.getFields())
		{
			list.add(npp.getPattern());
		}
		
		return af.createPPatternListAssistant().isSimple(list);
	}

	@Override
	public Boolean defaultPPattern(PPattern pattern) throws AnalysisException
	{
		/*
		 * True if the pattern is a simple value that can match only one value for certain. Most pattern types are like
		 * this, but any that include variables or ignore patterns are not.
		 */
		return true;
	}

	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		// TODO Auto-generated method stub
		return false;
	}

}
