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
package org.overture.typechecker.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.AObjectPattern;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.visitor.TypeCheckVisitor;

/**
 * This class set a pattern to resolved.
 * 
 * @author kel
 */

public class PatternResolver extends
		QuestionAdaptor<PatternResolver.NewQuestion>
{
	public static class NewQuestion
	{
		IQuestionAnswer<TypeCheckInfo, PType> rootVisitor;
		TypeCheckInfo question;

		public NewQuestion(IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
				TypeCheckInfo question)
		{
			this.rootVisitor = rootVisitor;
			this.question = question;
		}
	}

	protected final ITypeCheckerAssistantFactory af;
	protected final String fromModule;

	public PatternResolver(ITypeCheckerAssistantFactory af, String fromModule)
	{
		this.af = af;
		this.fromModule = fromModule;
	}

	@Override
	public void caseAConcatenationPattern(AConcatenationPattern pattern,
			NewQuestion question) throws AnalysisException
	{

		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			pattern.getLeft().apply(THIS, question);
			pattern.getRight().apply(THIS, question);
		} catch (TypeCheckException e)
		{
			af.createPPatternAssistant(fromModule).unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void caseAExpressionPattern(AExpressionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		// af.createAExpressionPatternAssistant().typeResolve(pattern, question.rootVisitor, question.question);
		// Have to ask how is it done.
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			question.question.qualifiers = null;
			question.question.scope = NameScope.NAMESANDSTATE;
			pattern.getExp().apply(new TypeCheckVisitor(), question.question);
		} catch (TypeCheckException e)
		{
			af.createPPatternAssistant(fromModule).unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void caseARecordPattern(ARecordPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			af.createPPatternListAssistant().typeResolve(pattern.getPlist(), question.rootVisitor, question.question);
			pattern.setType(af.createPTypeAssistant().typeResolve(pattern.getType(), null, question.rootVisitor, question.question));
		} catch (TypeCheckException e)
		{
			af.createPPatternAssistant(fromModule).unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void caseASeqPattern(ASeqPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			af.createPPatternListAssistant().typeResolve(pattern.getPlist(), question.rootVisitor, question.question);
		} catch (TypeCheckException e)
		{
			af.createPPatternAssistant(fromModule).unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void caseASetPattern(ASetPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			af.createPPatternListAssistant().typeResolve(pattern.getPlist(), question.rootVisitor, question.question);
		} catch (TypeCheckException e)
		{
			af.createPPatternAssistant(fromModule).unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void caseATuplePattern(ATuplePattern pattern, NewQuestion question)
			throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			af.createPPatternListAssistant().typeResolve(pattern.getPlist(), question.rootVisitor, question.question);
		} catch (TypeCheckException e)
		{
			af.createPPatternAssistant(fromModule).unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void caseAUnionPattern(AUnionPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			pattern.getLeft().apply(THIS, question);
			pattern.getRight().apply(THIS, question);
		} catch (TypeCheckException e)
		{
			// AUnionPatternAssistantTC.unResolve(pattern);
			af.createPPatternAssistant(fromModule).unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void caseAMapPattern(AMapPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			for (AMapletPatternMaplet mp : pattern.getMaplets())
			{
				// af.createAMapletPatternMapletAssistant().typeResolve(mp, question.rootVisitor, question.question);
				mp.apply(THIS, question);
			}
		} catch (TypeCheckException e)
		{
			// af.createAMapPatternAssistant().unResolve(pattern);
			pattern.apply(af.getPatternUnresolver());
			throw e;
		}
	}

	@Override
	public void caseAMapUnionPattern(AMapUnionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		} else
		{
			pattern.setResolved(true);
		}

		try
		{
			pattern.getLeft().apply(THIS, question);
			pattern.getRight().apply(THIS, question);
		} catch (TypeCheckException e)
		{
			// af.createAMapUnionPatternAssistant().unResolve(pattern);
			pattern.apply(af.getPatternUnresolver());
			throw e;
		}
	}

	@Override
	public void caseAObjectPattern(AObjectPattern pattern, NewQuestion question) throws AnalysisException
	{
		if (pattern.getResolved())
		{
			return;
		}
		else
		{
			pattern.setResolved(true);
		}

		try
		{
			af.createPPatternListAssistant().typeResolvePairs(pattern.getFields(), question.rootVisitor, question.question);
			pattern.setType(af.createPTypeAssistant().typeResolve(pattern.getType(), null, question.rootVisitor, question.question));
		}
		catch (TypeCheckException e)
		{
			af.createPPatternAssistant(fromModule).unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void defaultPPattern(PPattern pattern, NewQuestion question)
			throws AnalysisException
	{
		pattern.setResolved(true);
	}
}
