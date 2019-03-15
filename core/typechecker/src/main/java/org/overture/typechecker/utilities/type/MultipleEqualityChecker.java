package org.overture.typechecker.utilities.type;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.node.INode;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by ldc on 05/06/17.
 */
public class MultipleEqualityChecker
		extends QuestionAdaptor<TypeCheckInfo >
{

	@Override public void caseANamedInvariantType(ANamedInvariantType node,
			TypeCheckInfo question) throws AnalysisException
	{
		if (node.getEqDef() != null)
		{
			return;        // eqdef will override others anyway
		}

		AUnionType members = question.assistantFactory.createPTypeAssistant().getUnion(node, question.fromModule);
		List<PType> equality = new LinkedList<>();

		for (PType m : members.getTypes())
		{
			if (m.apply(new AnswerAdaptor<Boolean>()
			{
				@Override public Boolean createNewReturnValue(INode node)
						throws AnalysisException
				{
					return false;
				}

				@Override public Boolean createNewReturnValue(Object node)
						throws AnalysisException
				{
					return false;
				}

				@Override public Boolean defaultSInvariantType(
						SInvariantType node) throws AnalysisException
				{
					return (node.getEqDef() != null);
				}
			}))
			{
			equality.add(m);
			}
		}

		if (equality.size() > 1)
		{
			TypeChecker.warning(5021, "Multiple union members define eq clauses, " + equality, node.getLocation());
		}

	}

}
