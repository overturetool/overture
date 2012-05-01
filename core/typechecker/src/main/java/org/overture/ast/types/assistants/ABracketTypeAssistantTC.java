package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.ABracketType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;


public class ABracketTypeAssistantTC {

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

	public static AFunctionType getFunction(ABracketType type) {
		return PTypeAssistant.getFunction(type.getType());
	}

	public static boolean isOperation(ABracketType type) {
		return PTypeAssistant.isOperation(type.getType());
	}
	
	public static AOperationType getOperation(ABracketType type) {
		return PTypeAssistant.getOperation(type.getType());
	}

	public static boolean isSeq(ABracketType type) {
		return PTypeAssistant.isSeq(type.getType());
	}
	public static SSeqType getSeq(ABracketType type) {
		return PTypeAssistant.getSeq(type.getType());
	}

	public static boolean isNumeric(ABracketType type) {
		return PTypeAssistant.isNumeric(type.getType());
		
	}

	public static SNumericBasicType getNumeric(ABracketType type) {
		return PTypeAssistant.getNumeric(type.getType());
	}

	public static boolean isMap(ABracketType type) {
		return PTypeAssistant.isMap(type.getType());
	}
	
	public static SMapType getMap(ABracketType type){
		return PTypeAssistant.getMap(type.getType());
	}

	public static boolean isSet(ABracketType type) {
		return PTypeAssistant.isSet(type.getType());
	}
	
	public static ASetType getSet(ABracketType type) {
		return PTypeAssistant.getSet(type.getType());
	}

	public static boolean isRecord(ABracketType type) {
		return PTypeAssistant.isRecord(type.getType());
	}

	public static ARecordInvariantType getRecord(ABracketType type) {
		return PTypeAssistant.getRecord(type.getType());
	}

	public static AUnionType getUnion(ABracketType type) {
		return PTypeAssistant.getUnion(type.getType());
	}

	public static boolean narrowerThan(ABracketType type,
			AAccessSpecifierAccessSpecifier accessSpecifier) {
		return PTypeAssistant.narrowerThan(type.getType(), accessSpecifier);
	}
	
	

}
