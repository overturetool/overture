package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;


public class ALocalDefinitionAssistant {

	

	public static LexNameList getVariableNames(
			ALocalDefinition ld) {
		return new LexNameList(ld.getName());
	}

	public static void setValueDefinition(ALocalDefinition ld) {
		ld.setValueDefinition(true);
		
	}

	public static List<PDefinition> getDefinitions(ALocalDefinition d) {
		List<PDefinition> res = new Vector<PDefinition>();
		res.add(d);
		return res;
	}

	public static void typeResolve(ALocalDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (d.getType() != null)
   		{
   			d.setType(PTypeAssistant.typeResolve(PDefinitionAssistantTC.getType(d), null, rootVisitor, question));
   		}
		
	}

	public static boolean isFunction(ALocalDefinition def) {
		return (def.getValueDefinition() || PTypeAssistant.isType(PDefinitionAssistantTC.getType(def),AParameterType.class)) ? false : PTypeAssistant.isFunction(PDefinitionAssistantTC.getType(def));
	}

	

}
