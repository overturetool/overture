package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.typecheck.TypeCheckInfo;

public class AOptionalTypeAssistantTC {

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


	public static AFunctionType getFunction(AOptionalType type) {
		return PTypeAssistant.getFunction(type.getType());
	}


	public static boolean isOperation(AOptionalType type) {
		return PTypeAssistant.isOperation(type.getType());
	}
	
	public static AOperationType getOperation(AOptionalType type) {
		return PTypeAssistant.getOperation(type.getType());
	}


	public static boolean isSeq(AOptionalType type) {
		return PTypeAssistant.isSeq(type.getType());
	}
	
	public static SSeqType getSeq(AOptionalType type) {
		return PTypeAssistant.getSeq(type.getType());
	}


	public static boolean isNumeric(AOptionalType type) {
		return PTypeAssistant.isNumeric(type.getType());
	}
	
	public static SNumericBasicType getNumeric(AOptionalType type) {
		return PTypeAssistant.getNumeric(type.getType());
	}


	public static boolean isMap(AOptionalType type) {
		return PTypeAssistant.isMap(type.getType());
	}
	
	public static SMapType getMap(AOptionalType type) {
		return PTypeAssistant.getMap(type.getType());
	}


	public static boolean isSet(AOptionalType type) {
		return PTypeAssistant.isSet(type.getType());
	}
	
	public static ASetType getSet(AOptionalType type) {
		return PTypeAssistant.getSet(type.getType());
	}


	public static boolean isRecord(AOptionalType type) {
		return PTypeAssistant.isRecord(type.getType());
	}

	public static ARecordInvariantType getRecord(AOptionalType type) {
		return PTypeAssistant.getRecord(type.getType());
	}


	public static boolean isClass(AOptionalType type) {
		return PTypeAssistant.isClass(type.getType());
	}

	public static AClassType getClassType(AOptionalType type) {
		return PTypeAssistant.getClassType(type.getType());
	}


	public static boolean narrowerThan(AOptionalType type,
			AAccessSpecifierAccessSpecifier accessSpecifier) {
		return PTypeAssistant.narrowerThan(type.getType(), accessSpecifier);
	}
	
}
