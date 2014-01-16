package org.overture.typechecker.assistant.pattern;

<<<<<<< HEAD
=======
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
>>>>>>> origin/pvj/main
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AConcatenationPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AConcatenationPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

<<<<<<< HEAD
//	public static void typeResolve(AConcatenationPattern pattern,
//			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question) throws AnalysisException
//	{
//
//		if (pattern.getResolved())
//			return;
//		else
//		{
//			pattern.setResolved(true);
//		}
//
//		try
//		{
//			af.createPPatternAssistant().typeResolve(pattern.getLeft(), rootVisitor, question);
//			af.createPPatternAssistant().typeResolve(pattern.getRight(), rootVisitor, question);
//		} catch (TypeCheckException e)
//		{
//			unResolve(pattern);
//			throw e;
//		}
//
//	}
=======
	public static void typeResolve(AConcatenationPattern pattern,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
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
			PPatternAssistantTC.typeResolve(pattern.getLeft(), rootVisitor, question);
			PPatternAssistantTC.typeResolve(pattern.getRight(), rootVisitor, question);
		} catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}

	}

	public static void unResolve(AConcatenationPattern pattern)
	{
		PPatternAssistantTC.unResolve(pattern.getLeft());
		PPatternAssistantTC.unResolve(pattern.getRight());
		pattern.setResolved(false);
>>>>>>> origin/pvj/main

//	public static void unResolve(AConcatenationPattern pattern)
//	{
//		af.createPPatternAssistant().unResolve(pattern.getLeft());
//		af.createPPatternAssistant().unResolve(pattern.getRight());
//		pattern.setResolved(false);
//
//	}

}
