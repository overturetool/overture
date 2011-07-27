package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.AProductType;
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
			type.setType( PTypeAssistant.typeResolve(type.getType(),root, rootVisitor, question).clone());
			return type;
		}
		catch (TypeCheckException e)
		{
			unResolve(type);
			throw e;
		}
	}

	public static String toDisplay(ANamedInvariantType exptype) {
		return exptype.getName().toString();
	}

	public static boolean isProduct(ANamedInvariantType type, int size) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isProduct(type.getType(),size);
	}

	public static AProductType getProduct(ANamedInvariantType type, int size) {
		return PTypeAssistant.getProduct(type.getType(),size);
	}

	public static boolean isProduct(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isProduct(type.getType());
	}

	public static AProductType getProduct(ANamedInvariantType type) {
		return PTypeAssistant.getProduct(type.getType());
	}

	public static boolean isType(ANamedInvariantType b,
			Class<? extends PType> typeclass) {
		if (b.getOpaque()) return false;
		return PTypeAssistant.isType(b.getType(), typeclass);
		
	}

	public static PType isType(ANamedInvariantType exptype, String typename) {
		if (exptype.getOpaque()) return null;
		return PTypeAssistant.isType(exptype.getType(), typename);
	}

	public static boolean equals(ANamedInvariantType type, PType other) {
		other = PTypeAssistant.deBracket(other);

		if (other instanceof ANamedInvariantType)
		{
			ANamedInvariantType nother = (ANamedInvariantType)other;
			return type.getName().equals(nother.getName());
		}

		return false;
	}

	public static boolean isUnion(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isUnion(type.getType());
	}

	public static boolean isFunction(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isFunction(type.getType());
	}

}
