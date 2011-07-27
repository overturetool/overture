package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

public class AOptionalTypeAssistant {

	public static PType typeResolve(AOptionalType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else { type.setResolved(true); }
		type.setType(PTypeAssistant.typeResolve(type.getType(), root, rootVisitor, question));
		
		if (root != null)  root.setInfinite(false);	// Could be nil
		return type;
	}
	
	
	public static void unResolve(AOptionalType type)
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }
		PTypeAssistant.unResolve(type.getType());
	}


	public static String toDisplay(AOptionalType exptype) {
		return "[" + exptype.getType() + "]";
	}


	public static boolean isProduct(AOptionalType type, int size) {
		return PTypeAssistant.isProduct(type.getType(),size);
	}


	public static AProductType getProduct(AOptionalType type, int size) {
		return PTypeAssistant.getProduct(type.getType(),size);
	}


	public static boolean isProduct(AOptionalType type) {
		return PTypeAssistant.isProduct(type.getType());
	}


	public static AProductType getProduct(AOptionalType type) {
		return PTypeAssistant.getProduct(type.getType());
	}


	public static boolean isType(AOptionalType b,
			Class<? extends PType> typeclass) {
		return PTypeAssistant.isType(b.getType(), typeclass);
	}


	public static PType isType(AOptionalType exptype, String typename) {
		return PTypeAssistant.isType(exptype.getType(), typename);
	}


	public static boolean equals(AOptionalType type, PType other) {
		if (other instanceof AOptionalType)
		{
			AOptionalType oo = (AOptionalType)other;
			return  PTypeAssistant.equals(type.getType(),oo.getType());
		}
		
		return false;
	}


	public static boolean isFunction(AOptionalType type) {
		return PTypeAssistant.isFunction(type.getType());
	}

}
