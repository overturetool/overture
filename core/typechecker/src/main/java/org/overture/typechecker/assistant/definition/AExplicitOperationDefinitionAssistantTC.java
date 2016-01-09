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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.APostOpExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AExplicitOperationDefinitionAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AExplicitOperationDefinitionAssistantTC(
			ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public List<? extends PDefinition> getParamDefinitions(
			AExplicitOperationDefinition node)
	{

		List<PDefinition> defs = new Vector<PDefinition>();
		Iterator<PType> titer = ((AOperationType) node.getType()).getParameters().iterator();

		for (PPattern p : node.getParameterPatterns())
		{
			defs.addAll(af.createPPatternAssistant().getDefinitions(p, titer.next(), NameScope.LOCAL));
		}

		return af.createPDefinitionAssistant().checkDuplicatePatterns(node, defs);
	}

	@SuppressWarnings("unchecked")
	public AExplicitFunctionDefinition getPostDefinition(
			AExplicitOperationDefinition d, Environment base)
	{

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();
		plist.addAll((List<PPattern>) d.getParameterPatterns().clone());

		if (!(((AOperationType) d.getType()).getResult() instanceof AVoidType))
		{
			LexNameToken result = new LexNameToken(d.getName().getModule(), "RESULT", d.getLocation());
			plist.add(AstFactory.newAIdentifierPattern(result));
		}

		AStateDefinition state = d.getState();

		if (state != null) // Two args, called Sigma~ and Sigma
		{
			plist.add(AstFactory.newAIdentifierPattern(state.getName().getOldName()));
			plist.add(AstFactory.newAIdentifierPattern(state.getName().clone()));
		} else if (base.isVDMPP())
		{
			// Two arguments called "self~" and "self"
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName().getOldName()));

			if (!af.createPAccessSpecifierAssistant().isStatic(d.getAccess()))
			{
				plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName()));
			}
		}

		parameters.add(plist);
		APostOpExp postop = AstFactory.newAPostOpExp(d.getName().clone(), d.getPrecondition(), d.getPostcondition(), null, d.getState());

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
			AExplicitOperationDefinition d, Environment base)
	{

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		List<PPattern> plist = new Vector<PPattern>();
		plist.addAll((List<PPattern>) d.getParameterPatterns().clone());

		if (d.getState() != null)
		{
			plist.add(AstFactory.newAIdentifierPattern(d.getState().getName().clone()));
		} else if (base.isVDMPP()
				&& !af.createPAccessSpecifierAssistant().isStatic(d.getAccess()))
		{
			plist.add(AstFactory.newAIdentifierPattern(d.getName().getSelfName()));
		}

		parameters.add(plist);
		APreOpExp preop = AstFactory.newAPreOpExp(d.getName().clone(), d.getPrecondition(), null, d.getState());

		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getPreName(d.getPrecondition().getLocation()), NameScope.GLOBAL, null, af.createAOperationTypeAssistant().getPreType((AOperationType) d.getType(), d.getState(), d.getClassDefinition(), af.createPAccessSpecifierAssistant().isStatic(d.getAccess())), parameters, preop, null, null, false, null);

		// Operation precondition functions are effectively not static as
		// their expression can directly refer to instance variables, even
		// though at runtime these are passed via a "self" parameter.

		def.setAccess(af.createPAccessSpecifierAssistant().getStatic(def, false));
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

	public List<List<PPattern>> getParamPatternList(
			AExplicitOperationDefinition func)
	{
		List<List<PPattern>> parameters = new ArrayList<List<PPattern>>();
		List<PPattern> plist = new ArrayList<PPattern>();

		for (PPattern p : func.getParameterPatterns())
		{
			plist.add(p);
		}

		parameters.add(plist);
		return parameters;
	}

}
