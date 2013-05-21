package org.overture.codegen.lookup;

import java.util.HashMap;

import org.overture.ast.types.ACharBasicType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
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
		
		//Basic
		//typeMap.put(ABooleanBasicType.class, value)
		typeMap.put(ACharBasicType.class, new ACharBasicTypeCG());
		
		//Basic numeric
		typeMap.put(AIntNumericBasicType.class, new AIntNumericBasicTypeCG());
		typeMap.put(ANatOneNumericBasicType.class, new AIntNumericBasicTypeCG());
		typeMap.put(ANatNumericBasicType.class, new AIntNumericBasicTypeCG());
		typeMap.put(ARationalNumericBasicType.class, new ARealNumericBasicTypeCG());
		typeMap.put(ARealNumericBasicType.class, new ARealNumericBasicTypeCG());
	}
	
	public PTypeCG getType(PType type)
	{
		PTypeCG result = typeMap.get(type.getClass());
		
		if(result == null)
			System.out.println();
		
		return result;
	}
}
