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
import java.util.Vector;
import java.util.ArrayList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.utilities.type.QualifiedDefinition;

/**
 * Visitor to search for is_() expressions and return a list of definitions with qualified type information for the
 * variable(s) concerned.
 */
public class QualificationVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, List<QualifiedDefinition>>
{
	public List<QualifiedDefinition> caseAIsExp(AIsExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		List<QualifiedDefinition> result = new ArrayList<QualifiedDefinition>();

		if (node.getTest() instanceof AVariableExp)
		{
			AVariableExp exp = (AVariableExp) node.getTest();
			PDefinition existing = question.env.findName(exp.getName(), NameScope.NAMESANDSTATE);

			if (existing != null
					&& existing.getNameScope().matches(NameScope.NAMES))
			{
				if (node.getBasicType() != null)
				{
					result.add(new QualifiedDefinition(existing, node.getBasicType()));
				} else if (node.getTypeName() != null)
				{
					if (node.getTypedef() == null)
					{
						PDefinition typedef = question.env.findType(node.getTypeName(), node.getLocation().getModule());
						
						if (typedef != null)
						{
							node.setTypedef(typedef.clone());
						}
					}

					if (node.getTypedef() != null)
					{
						result.add(new QualifiedDefinition(existing, node.getTypedef().getType()));
					}
				}
			}
		}

		return result;
	}

	public List<QualifiedDefinition> caseAPreOpExp(APreOpExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		return node.getExpression().apply(THIS, question);
	}

	public List<QualifiedDefinition> caseAAndBooleanBinaryExp(
			AAndBooleanBinaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		List<QualifiedDefinition> result = node.getLeft().apply(THIS, question);
		result.addAll(node.getRight().apply(THIS, question));
		return result;
	}

	@Override
	public List<QualifiedDefinition> createNewReturnValue(INode node,
			TypeCheckInfo question) throws AnalysisException
	{
		return new ArrayList<QualifiedDefinition>();
	}

	@Override
	public List<QualifiedDefinition> createNewReturnValue(Object node,
			TypeCheckInfo question) throws AnalysisException
	{
		return new ArrayList<QualifiedDefinition>();
	}
}
