package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;


public class ALocalDefinitionAssistantTC {

	

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
   			d.setType(PTypeAssistantTC.typeResolve(PDefinitionAssistantTC.getType(d), null, rootVisitor, question));
   		}
		
	}

	public static boolean isFunction(ALocalDefinition def) {
		return (def.getValueDefinition() || PTypeAssistantTC.isType(PDefinitionAssistantTC.getType(def),AParameterType.class)) ? false : PTypeAssistantTC.isFunction(PDefinitionAssistantTC.getType(def));
	}

	

}
