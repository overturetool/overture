package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.type.AOptionalTypeAssistant;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.AAccessSpecifierAccessSpecifier;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.typechecker.TypeCheckInfo;

public class AOptionalTypeAssistantTC extends AOptionalTypeAssistant {

	public static PType typeResolve(AOptionalType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else { type.setResolved(true); }
		type.setType(PTypeAssistantTC.typeResolve(type.getType(), root, rootVisitor, question));
		
		if (root != null)  root.setInfinite(false);	// Could be nil
		return type;
	}
	
	
	public static void unResolve(AOptionalType type)
	{
		if (!type.getResolved()) return; else { type.setResolved(false); }
		PTypeAssistantTC.unResolve(type.getType());
	}


	public static String toDisplay(AOptionalType exptype) {
		return "[" + exptype.getType() + "]";
	}


	public static boolean isProduct(AOptionalType type, int size) {
		return PTypeAssistantTC.isProduct(type.getType(),size);
	}


	public static AProductType getProduct(AOptionalType type, int size) {
		return PTypeAssistantTC.getProduct(type.getType(),size);
	}


	public static boolean isProduct(AOptionalType type) {
		return PTypeAssistantTC.isProduct(type.getType());
	}


	public static AProductType getProduct(AOptionalType type) {
		return PTypeAssistantTC.getProduct(type.getType());
	}


	public static boolean isType(AOptionalType b,
			Class<? extends PType> typeclass) {
		
		if(typeclass.equals(AVoidType.class))
		{
			return false; // Optionals are never void
		}
		
		return PTypeAssistantTC.isType(b.getType(), typeclass);
	}


	public static PType isType(AOptionalType exptype, String typename) {
		return PTypeAssistantTC.isType(exptype.getType(), typename);
	}


	public static boolean equals(AOptionalType type, Object other) {
		if (other instanceof AOptionalType)
		{
			AOptionalType oo = (AOptionalType)other;
			return  PTypeAssistantTC.equals(type.getType(),oo.getType());
		}
		
		return false;
	}


	public static boolean isFunction(AOptionalType type) {
		return PTypeAssistantTC.isFunction(type.getType());
	}


	public static AFunctionType getFunction(AOptionalType type) {
		return PTypeAssistantTC.getFunction(type.getType());
	}


	public static boolean isOperation(AOptionalType type) {
		return PTypeAssistantTC.isOperation(type.getType());
	}
	
	public static AOperationType getOperation(AOptionalType type) {
		return PTypeAssistantTC.getOperation(type.getType());
	}


	public static boolean isSeq(AOptionalType type) {
		return PTypeAssistantTC.isSeq(type.getType());
	}
	
	public static SSeqType getSeq(AOptionalType type) {
		return PTypeAssistantTC.getSeq(type.getType());
	}


	public static boolean isMap(AOptionalType type) {
		return PTypeAssistantTC.isMap(type.getType());
	}
	
	public static SMapType getMap(AOptionalType type) {
		return PTypeAssistantTC.getMap(type.getType());
	}


	public static boolean isSet(AOptionalType type) {
		return PTypeAssistantTC.isSet(type.getType());
	}
	
	public static ASetType getSet(AOptionalType type) {
		return PTypeAssistantTC.getSet(type.getType());
	}


	public static boolean isRecord(AOptionalType type) {
		return PTypeAssistantTC.isRecord(type.getType());
	}

	public static ARecordInvariantType getRecord(AOptionalType type) {
		return PTypeAssistantTC.getRecord(type.getType());
	}


	public static boolean isClass(AOptionalType type) {
		return PTypeAssistantTC.isClass(type.getType());
	}

	public static AClassType getClassType(AOptionalType type) {
		return PTypeAssistantTC.getClassType(type.getType());
	}


	public static boolean narrowerThan(AOptionalType type,
			AAccessSpecifierAccessSpecifier accessSpecifier) {
		return PTypeAssistantTC.narrowerThan(type.getType(), accessSpecifier);
	}


	public static PType polymorph(AOptionalType type, ILexNameToken pname,
			PType actualType) {		
		return AstFactory.newAOptionalType(type.getLocation(), PTypeAssistantTC.polymorph(type.getType(),pname, actualType));
	}
	
}
