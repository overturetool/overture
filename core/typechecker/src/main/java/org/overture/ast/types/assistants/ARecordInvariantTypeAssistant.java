package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;



public class ARecordInvariantTypeAssistant {

	public static AFieldField findField(ARecordInvariantType rec, String tag) {
		for (AFieldField f: rec.getFields())
		{
			if (f.getTag().equals(tag))
			{
				return f;
			}
		}

		return null;
	}

	public static void unResolve(ARecordInvariantType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }

		for (AFieldField f: type.getFields())
		{
			AFieldFieldAssistant.unResolve(f);
		}
		
	}

	public static PType typeResolve(ARecordInvariantType type,
			ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved())
		{
			return type;
		}
		else
		{
			type.setResolved(true);
			type.setInfinite(false);
		}

		for (AFieldField f: type.getFields())
		{
			if (root != null)
				root.setInfinite(false);

			AFieldFieldAssistant.typeResolve(f, root, rootVisitor, question);

			if (root != null)
				type.setInfinite(type.getInfinite() || root.getInfinite());
		}

		if (root != null) root.setInfinite(type.getInfinite());
		return type;
	}

}
