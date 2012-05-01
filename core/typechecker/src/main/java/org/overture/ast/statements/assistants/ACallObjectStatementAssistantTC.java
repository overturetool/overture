package org.overture.ast.statements.assistants;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.ACallObjectStm;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeSet;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overture.typecheck.TypeComparator;


public class ACallObjectStatementAssistantTC {

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
			TypeCheckerErrors.report(3211, "Expecting " + ptypes.size() + " arguments",	type.getLocation(), type);
		}
		else
		{
			int i=0;

			for (PType atype: atypes)
			{
				PType ptype = ptypes.get(i++);

				if (!TypeComparator.compatible(ptype, atype))
				{
					TypeCheckerErrors.report(3212, "Unexpected type for argument " + i, atype.getLocation(), atype);
					TypeCheckerErrors.detail2("Expected", ptype, "Actual", atype);
				}
			}
		}
	}

	public static PTypeSet exitCheck(ACallObjectStm statement) {
		// TODO We don't know what an operation call will raise
		return new PTypeSet(new AUnknownType(statement.getLocation(),false));
	}
}
