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
import org.overture.ast.node.INode;
import org.overture.ast.patterns.ASeqMultipleBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PossibleBindTypeFinder extends AnswerAdaptor<PType>
{
	protected ITypeCheckerAssistantFactory af;

	public PossibleBindTypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public PType caseASetMultipleBind(ASetMultipleBind mb)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().getPossibleType(mb.getPlist(), mb.getLocation());
	}

	@Override
	public PType caseASeqMultipleBind(ASeqMultipleBind mb)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().getPossibleType(mb.getPlist(), mb.getLocation());
	}

	@Override
	public PType caseATypeMultipleBind(ATypeMultipleBind mb)
			throws AnalysisException
	{
		return af.createPPatternListAssistant().getPossibleType(mb.getPlist(), mb.getLocation());
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
}
