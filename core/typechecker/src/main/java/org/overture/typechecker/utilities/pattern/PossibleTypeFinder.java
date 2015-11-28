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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ABooleanPattern;
import org.overture.ast.patterns.ACharacterPattern;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.AIntegerPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.ANilPattern;
import org.overture.ast.patterns.AObjectPattern;
import org.overture.ast.patterns.AQuotePattern;
import org.overture.ast.patterns.ARealPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to get if a possible type out of a pattern.
 * 
 * @author kel
 */
public class PossibleTypeFinder extends AnswerAdaptor<PType>
{
	protected ITypeCheckerAssistantFactory af;

	public PossibleTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PType caseABooleanPattern(ABooleanPattern pattern)
			throws AnalysisException
	{
		return AstFactory.newABooleanBasicType(pattern.getLocation());
	}

	@Override
	public PType caseACharacterPattern(ACharacterPattern pattern)
			throws AnalysisException
	{
		return AstFactory.newACharBasicType(pattern.getLocation());
	}

	@Override
	public PType caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		PTypeSet set = new PTypeSet(af);

		set.add(af.createPPatternAssistant().getPossibleType(pattern.getLeft()));
		set.add(af.createPPatternAssistant().getPossibleType(pattern.getRight()));

		PType s = set.getType(pattern.getLocation());

		return af.createPTypeAssistant().isUnknown(s) ?
			AstFactory.newASeqSeqType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation())) : s;
	}

	@Override
	public PType caseAExpressionPattern(AExpressionPattern pattern)
			throws AnalysisException
	{
		return AstFactory.newAUnknownType(pattern.getLocation());
	}

	@Override
	public PType caseAIdentifierPattern(AIdentifierPattern pattern)
			throws AnalysisException
	{
		return AstFactory.newAUnknownType(pattern.getLocation());
	}

	@Override
	public PType caseAIgnorePattern(AIgnorePattern pattern)
			throws AnalysisException
	{
		return AstFactory.newAUnknownType(pattern.getLocation());
	}

	@Override
	public PType caseAIntegerPattern(AIntegerPattern pattern)
			throws AnalysisException
	{
		return typeOf(pattern.getValue().getValue(), pattern.getLocation());
	}

	@Override
	public PType caseANilPattern(ANilPattern pattern) throws AnalysisException
	{
		return AstFactory.newAOptionalType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation()));
	}

	@Override
	public PType caseAQuotePattern(AQuotePattern pattern)
			throws AnalysisException
	{
		return AstFactory.newAQuoteType(((AQuotePattern) pattern).getValue().clone());
	}

	@Override
	public PType caseARealPattern(ARealPattern pattern)
			throws AnalysisException
	{
		return AstFactory.newARealNumericBasicType(pattern.getLocation());
	}

	@Override
	public PType caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		return pattern.getType();
	}

	@Override
	public PType caseASetPattern(ASetPattern pattern) throws AnalysisException
	{
		return AstFactory.newASetType(pattern.getLocation(), af.createPPatternListAssistant().getPossibleType(pattern.getPlist(), pattern.getLocation()));
	}

	@Override
	public PType caseASeqPattern(ASeqPattern pattern) throws AnalysisException
	{
		return AstFactory.newASeqSeqType(pattern.getLocation(), af.createPPatternListAssistant().getPossibleType(pattern.getPlist(), pattern.getLocation()));
	}

	@Override
	public PType caseAStringPattern(AStringPattern pattern)
			throws AnalysisException
	{
		ASeqSeqType t = AstFactory.newASeqSeqType(pattern.getLocation(), AstFactory.newACharBasicType(pattern.getLocation()));
		return t;
	}

	@Override
	public PType caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		PTypeList list = new PTypeList();

		for (PPattern p : pattern.getPlist())
		{
			list.add(af.createPPatternAssistant().getPossibleType(p));
		}

		return list.getType(pattern.getLocation());
	}

	@Override
	public PType caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		PTypeSet set = new PTypeSet(af);

		set.add(af.createPPatternAssistant().getPossibleType(pattern.getLeft()));
		set.add(af.createPPatternAssistant().getPossibleType(pattern.getRight()));

		PType s = set.getType(pattern.getLocation());

		return af.createPTypeAssistant().isUnknown(s) ? AstFactory.newASetType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation()))
				: s;
	}

	@Override
	public PType caseAMapPattern(AMapPattern pattern) throws AnalysisException
	{
		return AstFactory.newAMapMapType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation()), AstFactory.newAUnknownType(pattern.getLocation()));
	}

	@Override
	public PType caseAObjectPattern(AObjectPattern pattern)
			throws AnalysisException
	{
		return pattern.getType();
	}

	@Override
	public PType defaultPPattern(PPattern pattern) throws AnalysisException
	{
		return null;
	}

	@Override
	public PType createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	private PType typeOf(long value, ILexLocation location)
	{
		if (value > 0)
		{
			return AstFactory.newANatOneNumericBasicType(location);
		} else if (value >= 0)
		{
			return AstFactory.newANatNumericBasicType(location);
		} else
		{
			return AstFactory.newAIntNumericBasicType(location);
		}
	}

}
