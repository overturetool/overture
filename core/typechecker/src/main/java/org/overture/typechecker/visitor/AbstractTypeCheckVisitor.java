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
package org.overture.typechecker.visitor;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.node.INode;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.utilities.type.QualifiedDefinition;

public class AbstractTypeCheckVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType>
{

	public AbstractTypeCheckVisitor(
			IQuestionAnswer<TypeCheckInfo, PType> visitor)
	{
		super(visitor);
	}

	public AbstractTypeCheckVisitor()
	{
		super();
	}

	@Override
	public PType createNewReturnValue(INode node, TypeCheckInfo question)
	{
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node, TypeCheckInfo question)
	{
		return null;
	}

	@Override
	public PType defaultINode(INode node, TypeCheckInfo question)
			throws AnalysisException
	{
		return THIS.defaultINode(node, question);
	}

	protected PType typeCheckIf(ILexLocation ifLocation, PExp testExp,
			INode thenNode, List<? extends INode> elseIfNodeList,
			INode elseNode, TypeCheckInfo question) throws AnalysisException
	{
		boolean isExpression = testExp.parent() instanceof PExp;

		question.qualifiers = null;

		PType test = testExp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isType(test, ABooleanBasicType.class))
		{
			TypeCheckerErrors.report((isExpression ? 3108 : 3224), "If expression is not boolean", testExp.getLocation(), testExp);
		}

		List<QualifiedDefinition> qualified = testExp.apply(question.assistantFactory.getQualificationVisitor(), question);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.qualifyType();
		}

		PTypeSet rtypes = new PTypeSet(question.assistantFactory);
		question.qualifiers = null;
		rtypes.add(thenNode.apply(THIS, question));

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		if (elseIfNodeList != null)
		{
			for (INode stmt : elseIfNodeList)
			{
				question.qualifiers = null;
				rtypes.add(stmt.apply(THIS, question));
			}
		}

		if (elseNode != null)
		{
			question.qualifiers = null;
			rtypes.add(elseNode.apply(THIS, question));
		} else
		{
			// If the else case is empty then it is a statement and its type is void
			rtypes.add(AstFactory.newAVoidType(ifLocation));
		}

		return rtypes.getType(ifLocation);

	}
	
	/**
	 * Type checks a AElseIf node
	 * @param elseIfNode
	 * @param elseIfLocation
	 * @param test
	 * @param thenNode
	 * @param question
	 * @return
	 * @throws AnalysisException
	 */
	PType typeCheckAElseIf(INode elseIfNode, ILexLocation elseIfLocation,INode test, INode thenNode, TypeCheckInfo question)
			throws AnalysisException
	{
		if (!question.assistantFactory.createPTypeAssistant().isType(test.apply(THIS, question.newConstraint(null)), ABooleanBasicType.class))
		{
			boolean isExpression = elseIfNode.parent() instanceof PExp;
			TypeCheckerErrors.report((isExpression ? 3086 : 3218), "Expression is not boolean", elseIfLocation, elseIfNode);
		}

		List<QualifiedDefinition> qualified = test.apply(question.assistantFactory.getQualificationVisitor(), question);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.qualifyType();
		}

		PType type = thenNode.apply(THIS, question);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		return type;
	}

}
