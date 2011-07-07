package org.overture.ast.definitions.assistants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;

import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;

public class PDefinitionAssistant {

	public static LexNameList getVariableName(List<PDefinition> list) {
		LexNameList variableNames = new LexNameList();

		for (PDefinition d : list) {
			variableNames.addAll(getVariableNames(d));
		}

		return variableNames;
	}

	private static Collection<? extends LexNameToken> getVariableNames(
			PDefinition d) {
		List<LexNameToken> result = new ArrayList<LexNameToken>();

		switch (d.kindPDefinition()) {
		case EXPLICITFUNCTION:
			if(d instanceof AExplicitFunctionDefinition)
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition) d;
				result.addAll(AExplicitFunctionDefinitionAssistant.getVariableNames(efd));
			}
			break;
		case LOCAL:
			if(d instanceof ALocalDefinition)
			{	
				ALocalDefinition ld = (ALocalDefinition) d;
				result.addAll(ALocalDefinitionAssistant.getVariableNames(ld));
			}
			break;
		default:
			System.out
					.println("DefinitionHelper : getVariableName(PDefinition)");
			break;
		}

		return result;
	}

}
