package org.overture.typechecker.assistant.type;

import java.util.Vector;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;

public class AUnknownTypeAssistantTC {

	public static AProductType getProduct(AUnknownType type, int n) {
		
		NodeList<PType> tl = new NodeList<PType>(null);

		for (int i=0; i<n; i++)
		{
			tl.add(AstFactory.newAUnknownType(type.getLocation()));
		}

		return AstFactory.newAProductType(type.getLocation(), tl);
	}

	public static AProductType getProduct(AUnknownType type) {
		return AstFactory.newAProductType(type.getLocation(), new NodeList<PType>(null));
	}

	public static boolean isType(AUnknownType b,
			Class<? extends PType> typeclass) {
		return true;
	}

	public static PType isType(AUnknownType exptype, String typename) {
		return null;	// Isn't any particular type?
	}

	public static boolean equals(AUnknownType type, Object other) {
		return true;
	}

	public static boolean isFunction(AUnknownType type) {
		return true;
	}

	public static AFunctionType getFunction(AUnknownType type) {
		return AstFactory.newAFunctionType(type.getLocation(), true, new NodeList<PType>(null), AstFactory.newAUnknownType(type.getLocation()));
	}

	public static boolean isOperation(AUnknownType type) {
		return true;
	}
	
	public static AOperationType getOperation(AUnknownType type) {
		return AstFactory.newAOperationType(type.getLocation(),new PTypeList(), AstFactory.newAUnknownType(type.getLocation()));
	}

	public static boolean isSeq(AUnknownType type) {
		return true;
	}
	
	public static SSeqType getSeq(AUnknownType type)
	{
		return AstFactory.newASeqSeqType(type.getLocation()); // empty
	}

	

	public static boolean isMap(AUnknownType type) {
		return true;
	}

	public static SMapType getMap(AUnknownType type) {
		return AstFactory.newAMapMapType(type.getLocation()); // Unknown |-> Unknown
	}

	public static boolean isSet(AUnknownType type) {
		return true;
	}

	public static ASetType getSet(AUnknownType type) {
		return AstFactory.newASetType(type.getLocation()); // empty
	}

	public static boolean isClass(AUnknownType type) {
		return true;
	}
	
	public static AClassType getClassType(AUnknownType type){
		return AstFactory.newAClassType(type.getLocation(), AstFactory.newAClassClassDefinition());
	}

	public static boolean narrowerThan(AUnknownType type,
			PAccessSpecifier accessSpecifier) {		
		return false;
	}

	public static ARecordInvariantType getRecord(AUnknownType type) {
		return AstFactory.newARecordInvariantType(type.getLocation(), new Vector<AFieldField>()); 
	}
	
}
