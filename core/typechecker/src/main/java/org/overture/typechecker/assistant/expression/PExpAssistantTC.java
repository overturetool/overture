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
package org.overture.typechecker.assistant.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class PExpAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;
	// A LexNameToken to indicate that a function has no precondition name, rather than
	// that it is not a pure function (indicated by null).
	public final static LexNameToken NO_PRECONDITION = new LexNameToken("", "", null);

	public PExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	//FIXME only used in 1 class. move it
	public ILexNameToken getPreName(PExp expression)
	{
		try
		{
			return expression.apply(af.getPreNameFinder());// FIXME: should we handle exceptions like this
		} catch (AnalysisException e)
		{
			return null;
		}
	}

}
