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

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class set a pattern to unresolved.
 * 
 * @author kel
 */
public class PatternUnresolver extends AnalysisAdaptor
{
	protected ITypeCheckerAssistantFactory af;

	public PatternUnresolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public void caseAConcatenationPattern(AConcatenationPattern pattern)
			throws AnalysisException
	{
		pattern.getLeft().apply(THIS);
		pattern.getRight().apply(THIS);
		pattern.setResolved(false);
	}

	@Override
	public void caseARecordPattern(ARecordPattern pattern)
			throws AnalysisException
	{
		pattern.getType().apply(THIS);
		pattern.setResolved(false);
	}

	@Override
	public void caseASeqPattern(ASeqPattern pattern) throws AnalysisException
	{
		af.createPPatternListAssistant().unResolve(pattern.getPlist());
		pattern.setResolved(false);
	}

	@Override
	public void caseASetPattern(ASetPattern pattern) throws AnalysisException
	{
		af.createPPatternListAssistant().unResolve(pattern.getPlist());
		pattern.setResolved(false);
	}

	@Override
	public void caseATuplePattern(ATuplePattern pattern)
			throws AnalysisException
	{
		af.createPPatternListAssistant().unResolve(pattern.getPlist());
		pattern.setResolved(false);
	}

	@Override
	public void caseAUnionPattern(AUnionPattern pattern)
			throws AnalysisException
	{
		pattern.getLeft().apply(THIS);
		pattern.getRight().apply(THIS);
		pattern.setResolved(false);
	}

	@Override
	public void caseAMapUnionPattern(AMapUnionPattern pattern)
			throws AnalysisException
	{
		pattern.getLeft().apply(THIS);
		pattern.getRight().apply(THIS);
		pattern.setResolved(false);
	}

	@Override
	public void caseAMapPattern(AMapPattern pattern) throws AnalysisException
	{
		for (AMapletPatternMaplet mp : pattern.getMaplets())
		{
			// af.createAMapletPatternMapletAssistant().unResolve(mp);
			mp.apply(THIS);
		}
		pattern.setResolved(false);
	}

	@Override
	public void defaultPPattern(PPattern pattern) throws AnalysisException
	{
		pattern.setResolved(false);
	}
}
