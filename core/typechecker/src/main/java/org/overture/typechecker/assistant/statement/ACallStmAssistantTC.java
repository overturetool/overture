package org.overture.typechecker.assistant.statement;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.statements.ACallStm;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.TypeComparator;

public class ACallStmAssistantTC {
	
	
	public static List<PType> getArgTypes(LinkedList<PExp> args,QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question)
	{
		List<PType> types = new LinkedList<PType>();

		for (PExp e :  args)
		{
			types.add(e.apply(rootVisitor, question));
		}

		return types;
	}
	
	public static void checkArgTypes(ACallStm node, PType type, List<PType> ptypes, List<PType> atypes)
	{
		if (ptypes.size() != atypes.size())
		{
			TypeCheckerErrors.report(3216, "Expecting " + ptypes.size() + " arguments", node.getLocation(), node);
		}
		else
		{
			int i=0;

			for (PType atype: atypes)
			{
				PType ptype = ptypes.get(i++);

				if (!TypeComparator.compatible(ptype, atype))
				{
					TypeCheckerErrors.report(3217, "Unexpected type for argument " + i, node.getLocation(), type);
					TypeCheckerErrors.detail2("Expected", ptype, "Actual", atype);
				}
			}
		}
	}

	public static PTypeSet exitCheck(ACallStm statement) {
		// TODO We don't know what an operation call will raise
		return new PTypeSet(AstFactory.newAUnknownType(statement.getLocation()));
	}

}
