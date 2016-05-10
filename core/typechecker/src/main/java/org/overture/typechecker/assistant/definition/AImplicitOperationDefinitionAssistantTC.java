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
package org.overture.typechecker.assistant.definition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AOperationType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AImplicitOperationDefinitionAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AImplicitOperationDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@SuppressWarnings("unchecked")
	public AExplicitFunctionDefinition getPostDefinition(
			AImplicitOperationDefinition d, Environment base)
	{

		List<List<PPattern>> parameters = new Vector<>();
		List<PPattern> plist = new Vector<PPattern>();

		for (APatternListTypePair pl : (LinkedList<APatternListTypePair>) d.getParameterPatterns())
		{
			plist.addAll((Collection<PPattern>) pl.getPatterns().clone());
		}

		if (d.getResult() != null)
		{
			plist.add(d.getResult().getPattern().clone());
		}

		AStateDefinition state = d.getState();

		if (state != null)
		{
			plist.add(AstFactory.newAIdentifierPattern(state.getName().getOldName()));
			plist.add(AstFactory.newAIdentifierPattern(state.getName().clone()));
		} else if (base.isVDMPP())
		{
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName().getOldName()));

			if (!af.createPAccessSpecifierAssistant().isStatic(d.getAccess()))
			{
				plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName()));
			}
		}

		parameters.add(plist);
		PExp postop = AstFactory.newAPostOpExp(d.getName().clone(), d.getPrecondition(), d.getPostcondition(), d.getErrors(), d.getState());

		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPostName(d.getPostcondition().getLocation()), NameScope.GLOBAL, null, af.createAOperationTypeAssistant().getPostType((AOperationType) d.getType(), state, d.getClassDefinition(), af.createPAccessSpecifierAssistant().isStatic(d.getAccess())), parameters, postop, null, null, false, null);

		// Operation postcondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(af.createPAccessSpecifierAssistant().getStatic(d, false));
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	@SuppressWarnings("unchecked")
	public AExplicitFunctionDefinition getPreDefinition(
			AImplicitOperationDefinition d, Environment base)
	{

		List<List<PPattern>> parameters = new Vector<>();
		List<PPattern> plist = new Vector<PPattern>();

		for (APatternListTypePair pl : (LinkedList<APatternListTypePair>) d.getParameterPatterns())
		{
			plist.addAll((Collection<PPattern>) pl.getPatterns().clone());
		}

		AStateDefinition state = d.getState();

		if (state != null)
		{
			plist.add(AstFactory.newAIdentifierPattern(state.getName().clone()));
		} else if (base.isVDMPP()
				&& !af.createPAccessSpecifierAssistant().isStatic(d.getAccess()))
		{
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName()));
		}

		parameters.add(plist);
		PExp preop = AstFactory.newAPreOpExp(d.getName().clone(), d.getPrecondition(), d.getErrors(), d.getState());

		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, null, af.createAOperationTypeAssistant().getPreType((AOperationType) d.getType(), state, d.getClassDefinition(), af.createPAccessSpecifierAssistant().isStatic(d.getAccess())), parameters, preop, null, null, false, null);

		// Operation precondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(af.createPAccessSpecifierAssistant().getStatic(d, false));
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	public List<PPattern> getParamPatternList(
			AImplicitOperationDefinition definition)
	{
		List<PPattern> plist = new ArrayList<PPattern>();

		for (APatternListTypePair pl : definition.getParameterPatterns())
		{
			plist.addAll(pl.getPatterns());
		}

		return plist;
	}

	public List<List<PPattern>> getListParamPatternList(
			AImplicitOperationDefinition func)
	{
		List<List<PPattern>> parameters = new ArrayList<>();
		List<PPattern> plist = new ArrayList<PPattern>();

		for (APatternListTypePair pl : func.getParameterPatterns())
		{
			plist.addAll(pl.getPatterns());
		}

		parameters.add(plist);
		return parameters;
	}

}
