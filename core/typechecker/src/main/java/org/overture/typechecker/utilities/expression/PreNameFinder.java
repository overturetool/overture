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
package org.overture.typechecker.utilities.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * Used to find the Name of an expression or a definition.
 * 
 * @author kel
 */

public class PreNameFinder extends AnswerAdaptor<ILexNameToken>
{
	protected ITypeCheckerAssistantFactory af;
	// A LexNameToken to indicate that a function has no precondition name, rather than
	// that it is not a pure function (indicated by null).
	public final static LexNameToken NO_PRECONDITION = new LexNameToken("", "", null);

	public PreNameFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public ILexNameToken caseAFuncInstatiationExp(
			AFuncInstatiationExp expression) throws AnalysisException
	{
		AFuncInstatiationExp func = AFuncInstatiationExp.class.cast(expression);
		return func.getFunction().apply(this); // getPreName(func.getFunction());
	}

	@Override
	public ILexNameToken caseAVariableExp(AVariableExp expression)
			throws AnalysisException
	{
		ILexNameToken result = null;

		AVariableExp var = AVariableExp.class.cast(expression);
		PDefinition def = af.createPDefinitionAssistant().deref(var.getVardef());
		if (def instanceof AExplicitFunctionDefinition)
		{
			AExplicitFunctionDefinition ex = AExplicitFunctionDefinition.class.cast(def);
			PDefinition predef = ex.getPredef();
			result = predef == null ? NO_PRECONDITION : predef.getName();

		} else if (def instanceof AImplicitFunctionDefinition)
		{
			AImplicitFunctionDefinition im = AImplicitFunctionDefinition.class.cast(def);
			PDefinition predef = im.getPredef();
			result = predef == null ? NO_PRECONDITION : predef.getName();
		}
		return result;
	}

	@Override
	public ILexNameToken createNewReturnValue(INode node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ILexNameToken createNewReturnValue(Object node)
			throws AnalysisException
	{
		// TODO Auto-generated method stub
		return null;
	}
}
