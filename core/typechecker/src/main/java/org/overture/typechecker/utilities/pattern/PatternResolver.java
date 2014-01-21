package org.overture.typechecker.utilities.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.patterns.ARecordPattern;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.pattern.AMapPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapUnionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AMapletPatternMapletAssistantTC;
import org.overture.typechecker.assistant.pattern.ASeqPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ASetPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.ATuplePatternAssistantTC;
import org.overture.typechecker.assistant.pattern.AUnionPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;

/**
 * This class set a pattern to resolved.
 * 
 * @author kel
 */

public class PatternResolver extends
		QuestionAdaptor<PatternResolver.NewQuestion>
{
	/**
	 * 
	 */
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

	protected ITypeCheckerAssistantFactory af;

	public PatternResolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
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
			af.createPPatternAssistant().unResolve(pattern);
			throw e;
		}
	}

	@Override
	public void caseAExpressionPattern(AExpressionPattern pattern,
			NewQuestion question) throws AnalysisException
	{
		af.createAExpressionPatternAssistant().typeResolve(pattern, question.rootVisitor, question.question);
		//Have to ask how is it done.
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
			af.createPPatternAssistant().unResolve(pattern);
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
			af.createASeqPatternAssistant().unResolve(pattern);
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
			af.createASetPatternAssistant().unResolve(pattern);
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
			af.createATuplePatternAssistant().unResolve(pattern);
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
			//AUnionPatternAssistantTC.unResolve(pattern);
			af.createPPatternAssistant().unResolve(pattern);
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
				af.createAMapletPatternMapletAssistant().typeResolve(mp, question.rootVisitor, question.question);
			}
		} catch (TypeCheckException e)
		{
			af.createAMapPatternAssistant().unResolve(pattern);
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
			af.createAMapUnionPatternAssistant().unResolve(pattern);
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
