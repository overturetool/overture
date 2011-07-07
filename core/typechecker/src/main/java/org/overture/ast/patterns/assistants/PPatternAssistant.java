package org.overture.ast.patterns.assistants;

import java.util.ArrayList;
import java.util.List;

import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;

import org.overturetool.vdmj.typechecker.NameScope;

public class PPatternAssistant {

	public static List<PDefinition> getDefinitions(PPattern rp,
			PType ptype, NameScope scope) {		
		switch (rp.kindPPattern()) {		
			case IDENTIFIER:
				if(rp instanceof AIdentifierPattern)
				{
					AIdentifierPattern idPattern = (AIdentifierPattern) rp;					
					List<PDefinition> defs = new ArrayList<PDefinition>();
					defs.add(new ALocalDefinition(idPattern.getLocation(), idPattern.getName(), scope, false, null, ptype));
					return defs;
				}
				break;
			default:
				System.out.println("HelperPattern : getDefinitions not implemented");
				break;
		}

		return null;
	}

}
