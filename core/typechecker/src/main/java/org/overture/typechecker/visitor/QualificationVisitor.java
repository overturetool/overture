package org.overture.typechecker.visitor;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AIsExp;
import org.overture.ast.expressions.APreOpExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.utilities.type.QualifiedDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.typechecker.NameScope;

/**
 * Visitor to search for is_() expressions and return a list of definitions with
 * qualified type information for the variable(s) concerned.
 */
public class QualificationVisitor extends QuestionAnswerAdaptor<TypeCheckInfo, List<QualifiedDefinition>>
{
	public List<QualifiedDefinition> caseAIsExp(AIsExp node, TypeCheckInfo question) throws AnalysisException
	{
		List<QualifiedDefinition> result = new Vector<QualifiedDefinition>();

		if (node.getTest() instanceof AVariableExp)
		{
			AVariableExp exp = (AVariableExp)node.getTest();
			PDefinition existing = question.env.findName(exp.getName(), NameScope.NAMESANDSTATE);

			if (existing != null && existing.getNameScope().matches(NameScope.NAMES))
			{
				if (node.getBasicType() != null)
				{
					result.add(new QualifiedDefinition(existing, node.getBasicType()));
				}
				else if (node.getTypeName() != null && node.getTypedef() != null)
				{
					result.add(new QualifiedDefinition(existing, node.getTypedef().getType()));
				}
			}
		}

		return result;
	}

	public List<QualifiedDefinition> caseAPreOpExp(APreOpExp node, TypeCheckInfo question) throws AnalysisException
	{
		return node.getExpression().apply(THIS, question);
	}

	public List<QualifiedDefinition> caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node, TypeCheckInfo question) throws AnalysisException
	{
		List<QualifiedDefinition> result = node.getLeft().apply(THIS, question);
		result.addAll(node.getRight().apply(THIS, question));
		return result;
	}

	@Override
	public List<QualifiedDefinition> createNewReturnValue(INode node, TypeCheckInfo question) throws AnalysisException
	{
		return new Vector<QualifiedDefinition>();
	}

	@Override
	public List<QualifiedDefinition> createNewReturnValue(Object node, TypeCheckInfo question) throws AnalysisException
	{
		return new Vector<QualifiedDefinition>();
	}
}
