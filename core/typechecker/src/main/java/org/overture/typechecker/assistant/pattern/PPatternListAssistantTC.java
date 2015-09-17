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
package org.overture.typechecker.assistant.pattern;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.patterns.ANamePatternPair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PPatternListAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public PPatternListAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public void typeResolve(List<PPattern> pp,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		for (PPattern pattern : pp)
		{
			af.createPPatternAssistant().typeResolve(pattern, rootVisitor, question);
		}
	}

	public void typeResolvePairs(List<ANamePatternPair> npplist,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		for (ANamePatternPair npp : npplist)
		{
			af.createPPatternAssistant().typeResolve(npp.getPattern(), rootVisitor, question);
		}
	}

	public void unResolve(List<PPattern> pp)
	{

		for (PPattern pPattern : pp)
		{
			af.createPPatternAssistant().unResolve(pPattern);
		}
	}

	public PType getPossibleType(LinkedList<PPattern> plist,
			ILexLocation location)
	{

		switch (plist.size())
		{
			case 0:
				return AstFactory.newAUnknownType(location);

			case 1:
				return af.createPPatternAssistant().getPossibleType(plist.get(0));

			default:
				PTypeSet list = new PTypeSet(af);

				for (PPattern p : plist)
				{
					list.add(af.createPPatternAssistant().getPossibleType(p));
				}

				return list.getType(location); // NB. a union of types
		}
	}

	public boolean isSimple(LinkedList<PPattern> p)
	{
		for (PPattern pattern : p)
		{

			if (!af.createPPatternAssistant().isSimple(pattern))
			{

				return false; // NB. AND
			}
		}

		return true;
	}

	public boolean alwaysMatches(List<PPattern> pl)
	{
		for (PPattern p : pl)
		{

			if (!af.createPPatternAssistant().alwaysMatches(p))
			{

				return false; // NB. AND
			}
		}

		return true;
	}

}
