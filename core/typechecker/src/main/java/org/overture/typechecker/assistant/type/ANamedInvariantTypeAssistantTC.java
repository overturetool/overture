package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.type.ANamedInvariantTypeAssistant;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
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
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;

public class ANamedInvariantTypeAssistantTC extends ANamedInvariantTypeAssistant{

	public static void unResolve(ANamedInvariantType type) {
		if (!type.getResolved()) return; else { type.setResolved(false); }
		PTypeAssistantTC.unResolve(type.getType());
	}

	public static PType typeResolve(ANamedInvariantType type, ATypeDefinition root, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		if (type.getResolved()) return type; else type.setResolved(true);

		try
		{
			type.setType( PTypeAssistantTC.typeResolve(type.getType(),root, rootVisitor, question));
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
		return PTypeAssistantTC.isProduct(type.getType(),size);
	}

	public static AProductType getProduct(ANamedInvariantType type, int size) {
		return PTypeAssistantTC.getProduct(type.getType(),size);
	}

	public static boolean isProduct(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistantTC.isProduct(type.getType());
	}

	public static AProductType getProduct(ANamedInvariantType type) {
		return PTypeAssistantTC.getProduct(type.getType());
	}

	public static boolean isType(ANamedInvariantType b,
			Class<? extends PType> typeclass) {
		if (b.getOpaque()) return false;
		return PTypeAssistantTC.isType(b.getType(), typeclass);
		
	}

	public static PType isType(ANamedInvariantType exptype, String typename) {
		if (exptype.getOpaque()) return null;
		return PTypeAssistantTC.isType(exptype.getType(), typename);
	}

	public static boolean equals(ANamedInvariantType type, Object other) {
		other = PTypeAssistantTC.deBracket(other);

		if (other instanceof ANamedInvariantType)
		{
			ANamedInvariantType nother = (ANamedInvariantType)other;
			return type.getName().equals(nother.getName());
		}

		return false;
	}

	public static boolean isUnion(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistantTC.isUnion(type.getType());
	}
	
	public static AUnionType getUnion(ANamedInvariantType type) {		
		return PTypeAssistantTC.getUnion(type.getType());
	}

	public static boolean isFunction(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistantTC.isFunction(type.getType());
	}

	public static AFunctionType getFunction(ANamedInvariantType type) {		
		return PTypeAssistantTC.getFunction(type.getType());
	}

	public static boolean isOperation(ANamedInvariantType type) {
		if(type.getOpaque()) return false;
		return PTypeAssistantTC.isOperation(type.getType());
	}
	
	public static AOperationType getOperation(ANamedInvariantType type) {		
		return PTypeAssistantTC.getOperation(type.getType());
	}

	public static boolean isSeq(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistantTC.isSeq(type.getType());
	}
	
	public static SSeqType getSeq(ANamedInvariantType type) {		
		return PTypeAssistantTC.getSeq(type.getType());
	}

	
	
	

	public static boolean isMap(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistantTC.isMap(type.getType());
	}
	
	public static SMapType getMap(ANamedInvariantType type) {
		return PTypeAssistantTC.getMap(type.getType());
	}

	public static boolean isSet(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistantTC.isSet(type.getType());
	}
	
	public static ASetType getSet(ANamedInvariantType type) {
		return PTypeAssistantTC.getSet(type.getType());
	}

	public static boolean isRecord(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistantTC.isRecord(type.getType());
	}
	
	public static ARecordInvariantType getRecord(ANamedInvariantType type) {
		return PTypeAssistantTC.getRecord(type.getType());
	}

	public static boolean isClass(ANamedInvariantType type) {
		if (type.getOpaque()) return false;
		return PTypeAssistantTC.isClass(type.getType());
	}
	
	public static AClassType getClassType(ANamedInvariantType type) {
		return PTypeAssistantTC.getClassType(type.getType());
	}
	
	public static boolean narrowerThan(ANamedInvariantType type,
			AAccessSpecifierAccessSpecifier accessSpecifier) {		
		
		if (type.getInNarrower())
		{
			return false;
		}

		type.setInNarrower(true);
		boolean result = false;
		
		
		if (type.getDefinitions().size() == 0)
		{
			result = PTypeAssistantTC.narrowerThan(type, accessSpecifier) || PTypeAssistantTC.narrowerThanBaseCase(type, accessSpecifier);
		}
		else
		{
			for (PDefinition d : type.getDefinitions())
			{
				result = result || PAccessSpecifierAssistantTC.narrowerThan(d.getAccess(), accessSpecifier);
			}
			
		}
		
		type.setInNarrower(false);
		return result;
	}

}
