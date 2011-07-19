package org.overture.ast.types.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;

public class AProductTypeAssistant {

	public static PType typeResolve(AProductType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else { type.setResolved(true);}

		try
		{
			List<PType> fixed = new Vector<PType>();

			for (PType t: type.getTypes())
			{
				PType rt = PTypeAssistant.typeResolve(t, root, rootVisitor, question);
				fixed.add(rt);
			}

			type.setTypes(fixed);
			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

	public static void unResolve(AProductType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }

		for (PType t: type.getTypes())
		{
			PTypeAssistant.unResolve(t);
		}		
	}

}
