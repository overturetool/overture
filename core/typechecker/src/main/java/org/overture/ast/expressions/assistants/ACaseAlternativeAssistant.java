package org.overture.ast.expressions.assistants;

import java.util.ArrayList;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.assistants.PPatternAssistantTC;
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
			PPatternAssistantTC.typeResolve(c.getPattern(),rootVisitor,question);

			if (c.getPattern() instanceof AExpressionPattern)
			{
				// Only expression patterns need type checking...
				AExpressionPattern ep = (AExpressionPattern)c.getPattern();
				ep.getExp().apply(rootVisitor, question);
			}

			PPatternAssistantTC.typeResolve(c.getPattern(),rootVisitor,question);
			c.getDefs().addAll(PPatternAssistantTC.getDefinitions(c.getPattern(),expType, NameScope.LOCAL));
		}

		PDefinitionListAssistant.typeCheck(c.getDefs(),rootVisitor,question);
		Environment local = new FlatCheckedEnvironment(c.getDefs(), question.env, question.scope);
		question  = new TypeCheckInfo(local, question.scope,question.qualifiers);
		c.setType(c.getResult().apply(rootVisitor, question).clone());
		local.unusedCheck();
		return c.getType();
	}

}
