package org.overture.ast.types.assistants;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;

import org.overturetool.vdmj.typechecker.NameScope;

public class APatternListTypePairAssistant {

	public static Collection<? extends PDefinition> getDefinitions(
			APatternListTypePair pltp, NameScope scope) {
		List<PDefinition> list = new Vector<PDefinition>();

		for (PPattern p: pltp.getPatterns())
		{
			list.addAll(PPatternAssistant.getDefinitions(p, pltp.getType(), scope));
		}

		return list;
	}

	
	
}
