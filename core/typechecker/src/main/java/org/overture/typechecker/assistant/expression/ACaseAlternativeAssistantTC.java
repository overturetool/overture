/*
 * #%~
 * The VDM Type Checker
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.typechecker.assistant.expression;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.expressions.ACaseAlternative;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ACaseAlternativeAssistantTC implements IAstAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public ACaseAlternativeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	//FIXME only used once. move it
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

				if (!af.getTypeComparator().compatible(ptype, expType))
				{
					TypeCheckerErrors.report(3311, "Pattern cannot match", c.getPattern().getLocation(), c.getPattern());
				}
			}

			try
			{
				af.createPPatternAssistant().typeResolve(c.getPattern(), rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env));
				c.getDefs().addAll(af.createPPatternAssistant().getDefinitions(c.getPattern(), expType, NameScope.LOCAL));
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
		question = question.newInfo(local);
		c.setType(c.getResult().apply(rootVisitor, question));
		local.unusedCheck();
		return c.getType();
	}

}
