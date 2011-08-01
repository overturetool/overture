package org.overture.ast.expressions.assistants;

import java.util.ArrayList;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.types.PType;
import org.overture.typecheck.Environment;
import org.overture.typecheck.FlatCheckedEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.typechecker.NameScope;

public class ACaseAlternativeAssistant {

	public static PType typeCheck(ACaseAlternative c,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question, PType expType) {

		if (c.getDefs().size() == 0)
		{
			//c.setDefs(new ArrayList<PDefinition>());
			PPatternAssistant.typeResolve(c.getPattern(),rootVisitor,question);

			if (c.getPattern() instanceof AExpressionPattern)
			{
				// Only expression patterns need type checking...
				AExpressionPattern ep = (AExpressionPattern)c.getPattern();
				ep.getExp().apply(rootVisitor, question);
			}

			PPatternAssistant.typeResolve(c.getPattern(),rootVisitor,question);
			c.getDefs().addAll(PPatternAssistant.getDefinitions(c.getPattern(),expType, NameScope.LOCAL));
		}

		PDefinitionListAssistant.typeCheck(c.getDefs(),rootVisitor,question);
		Environment local = new FlatCheckedEnvironment(c.getDefs(), question.env, question.scope);
		question.env = local;
		c.setType(c.getResult().apply(rootVisitor, question).clone());
		local.unusedCheck();
		return c.getType();
	}

}
