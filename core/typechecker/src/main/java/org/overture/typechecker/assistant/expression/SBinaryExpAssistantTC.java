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
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.SBooleanBinaryExp;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class SBinaryExpAssistantTC implements IAstAssistant
{

	protected ITypeCheckerAssistantFactory af;

	public SBinaryExpAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	//FIXME only used once. move it
	public ABooleanBasicType binaryCheck(SBooleanBinaryExp node,
			ABooleanBasicType expected,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!af.createPTypeAssistant().isType(node.getLeft().getType(), expected.getClass()))
		{
			TypeCheckerErrors.report(3065, "Left hand of " + node.getOp()
					+ " is not " + expected, node.getLocation(), node);
		}

		if (!af.createPTypeAssistant().isType(node.getRight().getType(), expected.getClass()))
		{
			TypeCheckerErrors.report(3066, "Right hand of " + node.getOp()
					+ " is not " + expected, node.getLocation(), node);
		}

		node.setType(expected);
		return (ABooleanBasicType) node.getType();

	}

}
