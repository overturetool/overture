package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
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
	
	public static AUnionType getUnion(ANamedInvariantType type) {		
		return PTypeAssistant.getUnion(type.getType());
	}

	public static boolean isFunction(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isFunction(type.getType());
	}

	public static AFunctionType getFunction(ANamedInvariantType type) {		
		return PTypeAssistant.getFunction(type.getType());
	}

	public static boolean isOperation(ANamedInvariantType type) {
		if(type.getOpaque()) return false;
		return PTypeAssistant.isOperation(type.getType());
	}
	
	public static AOperationType getOperation(ANamedInvariantType type) {		
		return PTypeAssistant.getOperation(type.getType());
	}

	public static boolean isSeq(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isSeq(type.getType());
	}
	
	public static SSeqType getSeq(ANamedInvariantType type) {		
		return PTypeAssistant.getSeq(type.getType());
	}

	public static boolean isNumeric(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isNumeric(type.getType());
	}
	
	public static SNumericBasicType getNumeric(ANamedInvariantType type) {
		return PTypeAssistant.getNumeric(type.getType());
	}

	public static boolean isMap(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isMap(type.getType());
	}
	
	public static SMapType getMap(ANamedInvariantType type) {
		return PTypeAssistant.getMap(type.getType());
	}

	public static boolean isSet(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isSet(type.getType());
	}
	
	public static ASetType getSet(ANamedInvariantType type) {
		return PTypeAssistant.getSet(type.getType());
	}

	public static boolean isRecord(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isRecord(type.getType());
	}
	
	public static ARecordInvariantType getRecord(ANamedInvariantType type) {
		return PTypeAssistant.getRecord(type.getType());
	}

	public static boolean isClass(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistant.isClass(type.getType());
	}
	
	public static AClassType getClassType(ANamedInvariantType type) {
		return PTypeAssistant.getClassType(type.getType());
	}
	

}
