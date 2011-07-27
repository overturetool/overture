package org.overture.ast.types.assistants;

import org.overture.ast.node.NodeList;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;

public class AUnknownTypeAssistant {

	public static AProductType getProduct(AUnknownType type, int n) {
		
		NodeList<PType> tl = new NodeList<PType>(null);

		for (int i=0; i<n; i++)
		{
			tl.add(new AUnknownType(type.getLocation(),false));
		}

		return new AProductType(type.getLocation(),false, tl);
	}

	public static AProductType getProduct(AUnknownType type) {
		return new AProductType(type.getLocation(),false, new NodeList<PType>(null));
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

}
