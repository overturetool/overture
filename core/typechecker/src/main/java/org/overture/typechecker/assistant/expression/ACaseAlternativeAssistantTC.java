package org.overture.typechecker.assistant.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.TypeComparator;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;

public class ACaseAlternativeAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ACaseAlternativeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public PType typeCheck(ACaseAlternative c,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question, PType expType) throws AnalysisException
	{

		if (c.getDefs().size() == 0)
		{
			// c.setDefs(new ArrayList<PDefinition>());
			af.createPPatternAssistant().typeResolve(c.getPattern(), rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env));

			if (c.getPattern() instanceof AExpressionPattern)
			{
				// Only expression patterns need type checking...
				AExpressionPattern ep = (AExpressionPattern) c.getPattern();
				PType ptype = ep.getExp().apply(rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env, question.scope));

				if (!TypeComparator.compatible(ptype, expType))
				{
					TypeCheckerErrors.report(3311, "Pattern cannot match", c.getPattern().getLocation(), c.getPattern());
				}
			}

			try
			{
				PPatternAssistantTC.typeResolve(c.getPattern(), rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env));
				c.getDefs().addAll(PPatternAssistantTC.getDefinitions(c.getPattern(), expType, NameScope.LOCAL));
			} catch (TypeCheckException e)
			{
				c.getDefs().clear();
				throw e;
			}
		}

		af.createPDefinitionListAssistant().typeCheck(c.getDefs(), rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env, question.scope));

		if (!af.createPPatternAssistant().matches(c.getPattern(), expType))
		{
			TypeCheckerErrors.report(3311, "Pattern cannot match", c.getPattern().getLocation(), c.getPattern());
		}

		Environment local = new FlatCheckedEnvironment(af, c.getDefs(), question.env, question.scope);
		question = new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers);
		c.setType(c.getResult().apply(rootVisitor, question));
		local.unusedCheck();
		return c.getType();
	}

}
