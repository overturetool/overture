package org.overture.runtime;

import java.util.List;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.typechecker.NameScope;

public class HelperPattern {

	public static List<PDefinition> getDefinitions(PPattern rp,
			PType expectedResult, NameScope names) {
		System.out.println("HelperPattern : getDefinitions not implemented");
		return null;
	}

}
