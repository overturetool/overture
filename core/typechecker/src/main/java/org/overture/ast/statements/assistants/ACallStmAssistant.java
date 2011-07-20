package org.overture.ast.statements.assistants;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.types.PType;
import org.overture.runtime.TypeComparator;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;

public class ACallStmAssistant {
	
	
	public static List<PType> getArgTypes(LinkedList<PExp> args,QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question)
	{
		List<PType> types = new LinkedList<PType>();

		for (PExp e :  args)
		{
			types.add(e.apply(rootVisitor, question));
		}

		return types;
	}
	
	public static void checkArgTypes(PType type, List<PType> ptypes, List<PType> atypes)
	{
		if (ptypes.size() != atypes.size())
		{
			TypeCheckerErrors.report(3216, "Expecting " + ptypes.size() + " arguments", type.getLocation(), type);
		}
		else
		{
			int i=0;

			for (PType atype: atypes)
			{
				PType ptype = ptypes.get(i++);

				if (!TypeComparator.compatible(ptype, atype))
				{
					TypeCheckerErrors.report(3217, "Unexpected type for argument " + i, type.getLocation(), type);
					TypeCheckerErrors.detail2("Expected", ptype, "Actual", atype);
				}
			}
		}
	}

}
