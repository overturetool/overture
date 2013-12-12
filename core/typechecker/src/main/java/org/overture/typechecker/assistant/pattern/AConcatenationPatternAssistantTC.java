package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AConcatenationPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AConcatenationPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

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

//	public static void unResolve(AConcatenationPattern pattern)
//	{
//		af.createPPatternAssistant().unResolve(pattern.getLeft());
//		af.createPPatternAssistant().unResolve(pattern.getRight());
//		pattern.setResolved(false);
//
//	}

//	public static List<PDefinition> getAllDefinitions(AConcatenationPattern rp,
//			PType ptype, NameScope scope)
//	{
//		List<PDefinition> list = PPatternAssistantTC.getDefinitions(rp.getLeft(), ptype, scope);
//		list.addAll(PPatternAssistantTC.getDefinitions(rp.getRight(), ptype, scope));
//		return list;
//
//	}

//	public static PType getPossibleType(AConcatenationPattern pattern)
//	{
//		return AstFactory.newASeqSeqType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation()));
//	}

//	public static PExp getMatchingExpression(AConcatenationPattern ccp)
//	{
//		LexToken op = new LexKeywordToken(VDMToken.CONCATENATE, ccp.getLocation());
//		PExp le = PPatternAssistantTC.getMatchingExpression(ccp.getLeft());
//		PExp re = PPatternAssistantTC.getMatchingExpression(ccp.getRight());
//		return AstFactory.newASeqConcatBinaryExp(le, op, re);
//	}

//	public static boolean isSimple(AConcatenationPattern p)
//	{
//		return PPatternAssistantTC.isSimple(p.getLeft())
//				&& PPatternAssistantTC.isSimple(p.getRight());
//	}

}
