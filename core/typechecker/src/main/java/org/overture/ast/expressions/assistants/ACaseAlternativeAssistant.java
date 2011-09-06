package org.overture.ast.expressions.assistants;


import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.assistants.PDefinitionListAssistant;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.assistants.PPatternAssistantTC;
import org.overture.ast.types.PType;
import org.overture.typecheck.Environment;
import org.overture.typecheck.FlatCheckedEnvironment;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.typechecker.NameScope;

public class ACaseAlternativeAssistant {

	public static PType typeCheck(ACaseAlternative c,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question, PType expType) {

		if (c.getDefs().size() == 0)
		{
			//c.setDefs(new ArrayList<PDefinition>());
			PPatternAssistantTC.typeResolve(c.getPattern(),rootVisitor,new TypeCheckInfo(question.env));

			if (c.getPattern() instanceof AExpressionPattern)
			{
				// Only expression patterns need type checking...
				AExpressionPattern ep = (AExpressionPattern)c.getPattern();
				ep.getExp().apply(rootVisitor, new TypeCheckInfo(question.env, question.scope));
			}

			PPatternAssistantTC.typeResolve(c.getPattern(),rootVisitor,new TypeCheckInfo(question.env));
			c.getDefs().addAll(PPatternAssistantTC.getDefinitions(c.getPattern(),expType, NameScope.LOCAL));
		}

		PDefinitionListAssistant.typeCheck(c.getDefs(),rootVisitor,new TypeCheckInfo(question.env, question.scope));
		
		if(!PPatternAssistantTC.matches(c.getPattern(),expType))
		{
			TypeCheckerErrors.report(3311, "Pattern cannot match", c.getPattern().getLocation(), c.getPattern());
		}
		
		Environment local = new FlatCheckedEnvironment(c.getDefs(), question.env, question.scope);
		question  = new TypeCheckInfo(local, question.scope,question.qualifiers);
		c.setType(c.getResult().apply(rootVisitor, question).clone());
		local.unusedCheck();
		return c.getType();
	}

}
