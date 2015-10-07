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

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.pattern.PPatternAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionSet;
import org.overture.typechecker.utilities.pattern.AllDefinitionLocator;
import org.overture.typechecker.utilities.pattern.PatternResolver;
import org.overture.typechecker.visitor.TypeCheckerPatternVisitor;

public class PPatternAssistantTC extends PPatternAssistant implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public PPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	/**
	 * Get a set of definitions for the pattern's variables. Note that if the pattern includes duplicate variable names,
	 * these are collapse into one.
	 * 
	 * @param rp
	 * @param ptype
	 * @param scope
	 * @return
	 */
	public List<PDefinition> getDefinitions(PPattern rp, PType ptype,
			NameScope scope)
	{
		PDefinitionSet set = af.createPDefinitionSet();
		set.addAll(af.createPPatternAssistant().getAllDefinitions(rp, ptype, scope));
		List<PDefinition> result = new Vector<PDefinition>(set);
		return result;
	}

	/**
	 * Get a complete list of all definitions, including duplicates. This method should only be used only by PP
	 */
	private List<PDefinition> getAllDefinitions(PPattern pattern, PType ptype,
			NameScope scope)
	{
		try
		{
			return pattern.apply(af.getAllDefinitionLocator(), new AllDefinitionLocator.NewQuestion(ptype, scope));
		} catch (AnalysisException e)
		{
			return null;
		}

	}

	public void typeResolve(PPattern pattern,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		try
		{
			pattern.apply(af.getPatternResolver(), new PatternResolver.NewQuestion(rootVisitor, question));
		}
		catch (AnalysisException e)
		{

		}
	}
	
	public void typeCheck(PPattern pattern, TypeCheckInfo question, IQuestionAnswer<TypeCheckInfo, PType> typeCheckVisitor)
	{
		try
		{
			pattern.apply(new TypeCheckerPatternVisitor(typeCheckVisitor), new TypeCheckInfo(question.assistantFactory, question.env, question.scope));
		}
		catch (AnalysisException e)
		{
		}
	}

	public void unResolve(PPattern pattern)
	{
		try
		{
			pattern.apply(af.getPatternUnresolver());
		} catch (AnalysisException e)
		{

		}
	}

	public PType getPossibleType(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getPossibleTypeFinder());
		} catch (AnalysisException e)
		{
			return null;
		}
	}

	public boolean matches(PPattern pattern, PType expType)
	{
		return af.getTypeComparator().compatible(af.createPPatternAssistant().getPossibleType(pattern), expType);
	}


	public boolean isSimple(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getSimplePatternChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

	public boolean alwaysMatches(PPattern pattern)
	{
		try
		{
			return pattern.apply(af.getAlwaysMatchingPatternChecker());
		} catch (AnalysisException e)
		{
			return false;
		}
	}

}
