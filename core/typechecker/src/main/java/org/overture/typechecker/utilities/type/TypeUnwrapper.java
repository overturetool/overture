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
package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AOptionalType;

public abstract class TypeUnwrapper<Q, A> extends QuestionAnswerAdaptor<Q, A>
{
	@Override
	public A caseABracketType(ABracketType node, Q question) throws AnalysisException
	{
		return node.getType().apply(THIS, question);
	}

	@Override
	public A caseAOptionalType(AOptionalType node, Q question) throws AnalysisException
	{
		return node.getType().apply(THIS, question);
	}

	@Override
	public A createNewReturnValue(INode node, Q question)
	{
		assert false : "should not happen";
		return null;
	}

	@Override
	public A createNewReturnValue(Object node, Q question)
	{
		assert false : "should not happen";
		return null;
	}
}
