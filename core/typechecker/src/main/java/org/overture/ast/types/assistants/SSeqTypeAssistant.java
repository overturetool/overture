package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;

public class SSeqTypeAssistant {

	public static void unResolve(SSeqType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }
		PTypeAssistant.unResolve(type.getSeqof());
		
	}

	public static PType typeResolve(SSeqType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		if (type.getResolved()) return type; else { type.setResolved(true); }

		try
		{
			type.setSeqof(PTypeAssistant.typeResolve(type.getSeqof(), root, rootVisitor, question));
			if (root != null) root.setInfinite(false);	// Could be empty
			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

}
