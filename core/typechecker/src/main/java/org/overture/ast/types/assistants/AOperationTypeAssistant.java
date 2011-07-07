package org.overture.ast.types.assistants;

import java.util.ArrayList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.expressions.AApplyExp;
import org.overture.ast.expressions.assistants.PExpAssistant;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.runtime.Environment;
import org.overture.runtime.TypeCheckException;
import org.overture.runtime.TypeComparator;
import org.overture.typecheck.TypeCheckInfo;


public class AOperationTypeAssistant {

	public static AOperationType typeResolve(AOperationType ot, Environment env,
			ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (ot.getResolved()) return ot; else { ot.setResolved(true); }

		try
		{
			List<PType> fixed = new ArrayList<PType>();

			for (PType type: ot.getParameters())
			{
				fixed.add(PTypeAssistant.typeResolve(type, env, root, rootVisitor, question));
			}

			ot.setParameters(fixed);
			ot.setResult(PTypeAssistant.typeResolve(ot.getResult(), env, root, rootVisitor, question));
			return ot;
		}
		catch (TypeCheckException e)
		{
			unResolve(ot);
			throw e;
		}		
	}
	
	public static void unResolve(AOperationType ot)
	{
		if (!ot.getResolved()) return; else { ot.setResolved(false); }

		for (PType type: ot.getParameters())
		{
			PTypeAssistant.unResolve(type);
		}

		PTypeAssistant.unResolve(ot.getResult());
	}

	

}
