package org.overture.ast.expressions.assistants;

import java.util.ArrayList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistant;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.types.PType;
import org.overture.runtime.Environment;
import org.overture.runtime.FlatCheckedEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.typechecker.NameScope;

public class ACaseAlternativeAssistant {

	public static PType typeCheck(ACaseAlternative c,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question, PType expType) {

		if (c.getDefs() == null)
		{
			c.setDefs(new ArrayList<PDefinition>());
			PPatternAssistant.typeResolve(c.getPattern(),question.env);

			if (c.getPattern() instanceof AExpressionPattern)
			{
				// Only expression patterns need type checking...
				AExpressionPattern ep = (AExpressionPattern)c.getPattern();
				ep.getExp().apply(rootVisitor, question);
			}

			PPatternAssistant.typeResolve(c.getPattern(),question.env);
			c.getDefs().addAll(PPatternAssistant.getDefinitions(c.getPattern(),expType, NameScope.LOCAL));
		}

		PDefinitionAssistant.typeCheck(c.getDefs(),rootVisitor,question);
		Environment local = new FlatCheckedEnvironment(c.getDefs(), question.env, question.scope);
		question.env = local;
		c.setType(c.getResult().apply(rootVisitor, question));
		local.unusedCheck();
		return c.getType();
	}

}
