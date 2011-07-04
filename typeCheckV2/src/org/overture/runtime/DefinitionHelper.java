package org.overture.runtime;

import java.util.Collection;
import java.util.List;

import org.overture.ast.definitions.PDefinition;

import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;

public class DefinitionHelper {

	public static LexNameList getVariableName(List<PDefinition> list) {
		LexNameList variableNames = new LexNameList();

		for (PDefinition d: list)
		{
			variableNames.addAll(getVariableNames(d));
		}

		return variableNames;
	}

	private static Collection<? extends LexNameToken> getVariableNames(
			PDefinition d) {
		switch (d.kindPDefinition()) {
		
		}
		
		System.out.println("DefinitionHelper : getVariableName(PDefinition)");
		return null;
	}
	

}
