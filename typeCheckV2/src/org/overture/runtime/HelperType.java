package org.overture.runtime;

import org.overture.ast.types.AClassType;

public class HelperType {

	public static boolean hasSupertype(AClassType cto, AClassType other) {
		return HelperDefinition.hasSupertype(cto.getClassdef(),other);
	}

}
