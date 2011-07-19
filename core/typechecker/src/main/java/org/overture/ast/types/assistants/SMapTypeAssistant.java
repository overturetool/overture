package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;


public class SMapTypeAssistant {

	public static void unResolve(SMapType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }

		if (!type.getEmpty())
		{
			PTypeAssistant.unResolve(type.getFrom());
			PTypeAssistant.unResolve(type.getTo());
		}
		
	}

	public static PType typeResolve(SMapType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		if (type.getResolved()) return type; else { type.setResolved(true); }

		try
		{
			if (!type.getEmpty())
			{
				type.setFrom(PTypeAssistant.typeResolve(type.getFrom(), root, rootVisitor, question));
				type.setTo(PTypeAssistant.typeResolve(type.getTo(), root, rootVisitor, question));
			}

			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

}
