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

import java.util.List;
import java.util.Vector;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

//FIXME: only used in 1 class. move it.
public class AStateDefinitionAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AStateDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public AExplicitFunctionDefinition getInitDefinition(AStateDefinition d)
	{
		ILexLocation loc = d.getInitPattern().getLocation();
		List<PPattern> params = new Vector<PPattern>();
		params.add(d.getInitPattern().clone());

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		parameters.add(params);

		PTypeList ptypes = new PTypeList();
		ptypes.add(AstFactory.newAUnresolvedType(d.getName()));
		AFunctionType ftype = AstFactory.newAFunctionType(loc, false, ptypes, AstFactory.newABooleanBasicType(loc));

		PExp body = AstFactory.newAStateInitExp(d);

		AExplicitFunctionDefinition def = AstFactory.newAExplicitFunctionDefinition(d.getName().getInitName(loc), NameScope.GLOBAL, null, ftype, parameters, body, null, null, false, null);

		return def;
	}

	public AExplicitFunctionDefinition getInvDefinition(AStateDefinition d)
	{

		ILexLocation loc = d.getInvPattern().getLocation();
		List<PPattern> params = new Vector<PPattern>();
		params.add(d.getInvPattern().clone());

		List<List<PPattern>> parameters = new Vector<List<PPattern>>();
		parameters.add(params);

		PTypeList ptypes = new PTypeList();
		ptypes.add(AstFactory.newAUnresolvedType(d.getName()));
		AFunctionType ftype = AstFactory.newAFunctionType(loc, false, ptypes, AstFactory.newABooleanBasicType(loc));

		return AstFactory.newAExplicitFunctionDefinition(d.getName().getInvName(loc), NameScope.GLOBAL, null, ftype, parameters, d.getInvExpression(), null, null, true, null);
	}

}
