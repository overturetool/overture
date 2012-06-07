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
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;


public class ABracketTypeAssistantTC extends ABracketTypeAssistant{

	public static PType typeResolve(ABracketType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		
		if (type.getResolved()) return type; else { type.setResolved(true); }

		try
		{
			do
			{
				type.setType(PTypeAssistantTC.typeResolve(type.getType(), root, rootVisitor, question));
			}
			while (type.getType() instanceof ABracketType);

			type.setType(PTypeAssistantTC.typeResolve(type.getType(), root, rootVisitor, question));
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
		PTypeAssistantTC.unResolve(type);
		
	}

	public static String toDisplay(ABracketType exptype) {
		return "(" + exptype.getType() + ")";
	}

	public static boolean isProduct(ABracketType type, int size) {
		return PTypeAssistantTC.isProduct(type.getType(), size);
	}

	public static AProductType getProduct(ABracketType type, int size) {
		return PTypeAssistantTC.getProduct(type.getType(), size);
	}

	public static boolean isProduct(ABracketType type) {
		return PTypeAssistantTC.isProduct(type.getType());
	}

	public static AProductType getProduct(ABracketType type) {
		return PTypeAssistantTC.getProduct(type.getType());
	}

	public static boolean isType(ABracketType b, Class<? extends PType> typeclass) {
		return PTypeAssistantTC.isType(b.getType(), typeclass);
	}

	public static PType isType(ABracketType exptype, String typename) {
		return PTypeAssistantTC.isType(exptype.getType(), typename);
	}

	public static boolean equals(ABracketType type, Object other) {
		return PTypeAssistantTC.equals(type.getType(),other);
	}

	public static boolean isUnion(ABracketType type) {
		return PTypeAssistantTC.isUnion(type.getType());
	}

	public static boolean isFunction(ABracketType type) {
		return PTypeAssistantTC.isFunction(type.getType());
	}

	public static AFunctionType getFunction(ABracketType type) {
		return PTypeAssistantTC.getFunction(type.getType());
	}

	public static boolean isOperation(ABracketType type) {
		return PTypeAssistantTC.isOperation(type.getType());
	}
	
	public static AOperationType getOperation(ABracketType type) {
		return PTypeAssistantTC.getOperation(type.getType());
	}

	public static boolean isSeq(ABracketType type) {
		return PTypeAssistantTC.isSeq(type.getType());
	}
	public static SSeqType getSeq(ABracketType type) {
		return PTypeAssistantTC.getSeq(type.getType());
	}

	
	

	public static boolean isMap(ABracketType type) {
		return PTypeAssistantTC.isMap(type.getType());
	}
	
	public static SMapType getMap(ABracketType type){
		return PTypeAssistantTC.getMap(type.getType());
	}

	public static boolean isSet(ABracketType type) {
		return PTypeAssistantTC.isSet(type.getType());
	}
	
	public static ASetType getSet(ABracketType type) {
		return PTypeAssistantTC.getSet(type.getType());
	}

	public static boolean isRecord(ABracketType type) {
		return PTypeAssistantTC.isRecord(type.getType());
	}

	public static ARecordInvariantType getRecord(ABracketType type) {
		return PTypeAssistantTC.getRecord(type.getType());
	}

	public static AUnionType getUnion(ABracketType type) {
		return PTypeAssistantTC.getUnion(type.getType());
	}

	public static boolean narrowerThan(ABracketType type,
			AAccessSpecifierAccessSpecifier accessSpecifier) {
		return PTypeAssistantTC.narrowerThan(type.getType(), accessSpecifier);
	}
	
	

}
