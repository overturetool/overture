package org.overture.typechecker.assistant.expression;


import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

public class ACaseAlternativeAssistantTC {

	public static PType typeCheck(ACaseAlternative c,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question, PType expType) throws AnalysisException {

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

		PDefinitionListAssistantTC.typeCheck(c.getDefs(),rootVisitor,new TypeCheckInfo(question.env, question.scope));
		
		if(!PPatternAssistantTC.matches(c.getPattern(),expType))
		{
			TypeCheckerErrors.report(3311, "Pattern cannot match", c.getPattern().getLocation(), c.getPattern());
		}
		
		Environment local = new FlatCheckedEnvironment(c.getDefs(), question.env, question.scope);
		question  = new TypeCheckInfo(local, question.scope,question.qualifiers);
		c.setType(c.getResult().apply(rootVisitor, question));
		local.unusedCheck();
		return c.getType();
	}

	public static LexNameList getOldNames(ACaseAlternative c)
	{
		return PExpAssistantTC.getOldNames(c.getResult());
	}
	
}
