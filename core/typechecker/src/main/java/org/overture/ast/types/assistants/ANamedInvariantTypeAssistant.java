package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;

public class ANamedInvariantTypeAssistant {

	public static void unResolve(ANamedInvariantType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }
		PTypeAssistant.unResolve(type.getType());
	}

	public static PType typeResolve(ANamedInvariantType type, ATypeDefinition root, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		if (type.getResolved()) return type; else type.setResolved(true);

		try
		{
			type.setType( PTypeAssistant.typeResolve(type.getType(),root, rootVisitor, question));
			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

}
