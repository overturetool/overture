package org.overture.codegen.lookup;

import java.util.HashMap;

import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;

public class TypeLookup
{
	private HashMap<Class<? extends PType>, PTypeCG> typeMap;
	
	public TypeLookup()
	{
		initTypeMap();
	}
	
	private void initTypeMap()
	{
	
		typeMap = new HashMap<Class<? extends PType>, PTypeCG>();
		
		//VDM strings are sequences of characters
		typeMap.put(ASeq1SeqType.class, new AStringTypeCG());
		typeMap.put(ASeq1SeqType.class, new AStringTypeCG());
		
		//Basic
		typeMap.put(ABooleanBasicType.class, new ABoolBasicTypeCG());
		typeMap.put(ACharBasicType.class, new ACharBasicTypeCG());
		
		//Basic numeric
		typeMap.put(AIntNumericBasicType.class, new AIntNumericBasicTypeCG());
		typeMap.put(ANatOneNumericBasicType.class, new AIntNumericBasicTypeCG());
		typeMap.put(ANatNumericBasicType.class, new AIntNumericBasicTypeCG());
		typeMap.put(ARationalNumericBasicType.class, new ARealNumericBasicTypeCG());
		typeMap.put(ARealNumericBasicType.class, new ARealNumericBasicTypeCG());
		
		//TODO: Used for something?
		//Collections
		typeMap.put(ASeqSeqType.class, new ASeqSeqTypeCG());
		typeMap.put(ASeq1SeqType.class, new ASeqSeqTypeCG());
	}
	
	public PTypeCG getType(PType type)
	{
		return typeMap.get(type.getClass());
	}
}
