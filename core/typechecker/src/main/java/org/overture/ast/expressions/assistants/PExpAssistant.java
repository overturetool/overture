package org.overture.ast.expressions.assistants;

import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.AFuncInstatiationExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;

public class PExpAssistant {

	public static String getPreName(PExp root) {
		String result = null;
		switch (root.kindPExp()) {
		case FUNCINSTATIATION: {
			AFuncInstatiationExp func = AFuncInstatiationExp.class.cast(root);
			result = getPreName(func.getFunction());
		}
			break;
		case VARIABLE: {
			AVariableExp var = AVariableExp.class.cast(root);

			PDefinition def = var.getVardef();
			
			//TODO: This will not work if the functions is renamed more than one time, can this occur??
			if (def instanceof ARenamedDefinition)
				def = ((ARenamedDefinition) def).getDef();
			else if (def instanceof AInheritedDefinition)
				def = ((AInheritedDefinition) def).getSuperdef();
			
			if (def instanceof AExplicitFunctionDefinition) {
				AExplicitFunctionDefinition ex = AExplicitFunctionDefinition.class
						.cast(def);
				PDefinition predef = ex.getPredef();
				result = predef == null ? "" : predef.getName().name;

			} else if (def instanceof AImplicitFunctionDefinition) {
				AImplicitFunctionDefinition im = AImplicitFunctionDefinition.class
						.cast(def);
				PDefinition predef = im.getPredef();
				result = predef == null ? "" : predef.getName().name;
			}
			break;
		}
		}
		return result;
	}
}
