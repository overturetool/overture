package org.overture.ast.expressions.assistants;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

public class ACaseAlternativeAssistant {

	public static PType typeCheck(ACaseAlternative c,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question, PType expType) {

		if (c.getDefs() == null)
		{
			defs = new DefinitionList();
			pattern.typeResolve(base);

			if (pattern instanceof ExpressionPattern)
			{
				// Only expression patterns need type checking...
				ExpressionPattern ep = (ExpressionPattern)pattern;
				ep.exp.typeCheck(base, null, scope);
			}

			pattern.typeResolve(base);
			defs.addAll(pattern.getDefinitions(expType, NameScope.LOCAL));
		}

		defs.typeCheck(base, scope);
		Environment local = new FlatCheckedEnvironment(defs, base, scope);
		Type r = result.typeCheck(local, null, scope);
		local.unusedCheck();
		return r;
	}

}
