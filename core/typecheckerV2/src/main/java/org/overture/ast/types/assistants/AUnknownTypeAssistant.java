package org.overture.ast.types.assistants;

import java.util.Vector;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.NodeList;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PAccessSpecifier;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overturetool.vdmjV2.lex.LexLocation;
import org.overturetool.vdmjV2.lex.LexNameList;
import org.overturetool.vdmjV2.lex.LexNameToken;

public class AUnknownTypeAssistant {

	public static AProductType getProduct(AUnknownType type, int n) {
		
		NodeList<PType> tl = new NodeList<PType>(null);

		for (int i=0; i<n; i++)
		{
			tl.add(new AUnknownType(type.getLocation(),false));
		}

		return new AProductType(type.getLocation(),false,null, tl);
	}

	public static AProductType getProduct(AUnknownType type) {
		return new AProductType(type.getLocation(),false,null, new NodeList<PType>(null));
	}

	public static boolean isType(AUnknownType b,
			Class<? extends PType> typeclass) {
		return true;
	}

	public static PType isType(AUnknownType exptype, String typename) {
		return null;	// Isn't any particular type?
	}

	public static boolean equals(AUnknownType type, PType other) {
		return true;
	}

	public static boolean isFunction(AUnknownType type) {
		return true;
	}

	public static AFunctionType getFunction(AUnknownType type) {
		return new AFunctionType(
				type.getLocation(),false, null, true, new NodeList<PType>(null), new AUnknownType(type.getLocation(),false));
	}

	public static boolean isOperation(AUnknownType type) {
		return true;
	}
	
	public static AOperationType getOperation(AUnknownType type) {
		return new AOperationType(
			type.getLocation(),false, null, new PTypeList(), new AUnknownType(type.getLocation(),false));
	}

	public static boolean isSeq(AUnknownType type) {
		return true;
	}
	
	public static SSeqType getSeq(AUnknownType type)
	{
		ASeqSeqType res = new ASeqSeqType(type.getLocation(),false,  true);	// empty
		res.setSeqof(new AUnknownType(type.getLocation(), false));
		return res;
	}

	public static boolean isNumeric(AUnknownType type) {
		return true;
	}
	
	public static ARealNumericBasicType getNumeric(AUnknownType type) {
		return new ARealNumericBasicType(type.getLocation(), false);
	}

	public static boolean isMap(AUnknownType type) {
		return true;
	}

	public static SMapType getMap(AUnknownType type) {
		AMapMapType res = new AMapMapType(type.getLocation(), false,  true);
		res.setFrom(new AUnknownType(type.getLocation(), false));
		res.setTo(new AUnknownType(type.getLocation(), false));
		return res;
	}

	public static boolean isSet(AUnknownType type) {
		return true;
	}

	public static ASetType getSet(AUnknownType type) {
		return new ASetType(type.getLocation(), false, null,new AUnknownType(type.getLocation(), false), true, false);
	}

	public static boolean isClass(AUnknownType type) {
		return true;
	}
	
	public static AClassType getClassType(AUnknownType type){
		LexNameToken name = new LexNameToken("CLASS", "DEFAULT", new LexLocation());
		return new AClassType(type.getLocation(),false, null,name, 
				new AClassClassDefinition(
						new LexLocation(),
						name,
				null, 
				false, 
				null, 
				null, 
				null, 
				null, new LexNameList(), new Vector<PDefinition>(), null, null, null, null, null, null, null, null, null, null, null, null, null));
	}

	public static boolean narrowerThan(AUnknownType type,
			PAccessSpecifier accessSpecifier) {		
		return false;
	}

	public static ARecordInvariantType getRecord(AUnknownType type) {
		return new ARecordInvariantType(type.getLocation(), false,  new LexNameToken("?", "?", type.getLocation()), new Vector<AFieldField>()); 
	}
	
}
