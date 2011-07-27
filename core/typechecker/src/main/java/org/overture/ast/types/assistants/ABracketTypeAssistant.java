package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
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

	public static String toDisplay(ABracketType exptype) {
		return "(" + exptype.getType() + ")";
	}

	public static boolean isProduct(ABracketType type, int size) {
		return PTypeAssistant.isProduct(type.getType(), size);
	}

	public static AProductType getProduct(ABracketType type, int size) {
		return PTypeAssistant.getProduct(type.getType(), size);
	}

	public static boolean isProduct(ABracketType type) {
		return PTypeAssistant.isProduct(type.getType());
	}

	public static AProductType getProduct(ABracketType type) {
		return PTypeAssistant.getProduct(type.getType());
	}

	public static boolean isType(ABracketType b, Class<? extends PType> typeclass) {
		return PTypeAssistant.isType(b.getType(), typeclass);
	}

	public static PType isType(ABracketType exptype, String typename) {
		return PTypeAssistant.isType(exptype.getType(), typename);
	}

	public static boolean equals(ABracketType type, PType other) {
		return PTypeAssistant.equals(type.getType(),other);
	}

	public static boolean isUnion(ABracketType type) {
		return PTypeAssistant.isUnion(type.getType());
	}

	public static boolean isFunction(ABracketType type) {
		return PTypeAssistant.isFunction(type.getType());
	}

}
