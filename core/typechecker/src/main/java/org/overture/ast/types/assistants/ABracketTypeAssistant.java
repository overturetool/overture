package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.PType;
import org.overture.runtime.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;


public class ABracketTypeAssistant {

	public static PType typeResolve(ABracketType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		
		if (type.getResolved()) return type; else { type.setResolved(true); }

		try
		{
			do
			{
				type.setType(PTypeAssistant.typeResolve(type.getType(), root, rootVisitor, question));
			}
			while (type.getType() instanceof ABracketType);

			type.setType(PTypeAssistant.typeResolve(type.getType(), root, rootVisitor, question));
			return type.getType();
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

	public static void unResolve(ABracketType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }
		PTypeAssistant.unResolve(type);
		
	}

}
