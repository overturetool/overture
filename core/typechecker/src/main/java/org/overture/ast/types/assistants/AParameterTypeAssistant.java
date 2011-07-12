package org.overture.ast.types.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.typechecker.NameScope;


public class AParameterTypeAssistant {

	public static PType typeResolve(AParameterType type, ATypeDefinition root,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (type.getResolved()) return type; else type.setResolved(true);

		PDefinition p = question.env.findName(type.getName(), NameScope.NAMES);

		if (p == null || !(p.getType() instanceof AParameterType))
		{
			TypeCheckerErrors.report(3433, "Parameter type @" + type.getName() + " not defined",p.getLocation(),p);
		}

		return type;
	}

}
