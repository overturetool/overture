package org.overture.typechecker.assistant.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMapUnionPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapUnionPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static void unResolve(AMapUnionPattern pattern)
	{

		af.createPPatternAssistant().unResolve(pattern.getLeft());
		af.createPPatternAssistant().unResolve(pattern.getRight());
		pattern.setResolved(false);
	}

	public static void typeResolve(AMapUnionPattern pattern,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		if (pattern.getResolved())
			return;
		else
		{
			pattern.setResolved(true);
		}

		try
		{
			af.createPPatternAssistant().typeResolve(pattern.getLeft(), rootVisitor, question);
			af.createPPatternAssistant().typeResolve(pattern.getRight(), rootVisitor, question);
		} catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}

	}

//	public static List<PDefinition> getAllDefinitions(AMapUnionPattern rp,
//			PType ptype, NameScope scope)
//	{
//
//		List<PDefinition> defs = new Vector<PDefinition>();
//
//		if (!PTypeAssistantTC.isMap(ptype))
//		{
//			TypeCheckerErrors.report(3315, "Matching expression is not a map type", rp.getLocation(), rp);
//		}
//
//		defs.addAll(PPatternAssistantTC.getDefinitions(rp.getLeft(), ptype, scope));
//		defs.addAll(PPatternAssistantTC.getDefinitions(rp.getRight(), ptype, scope));
//
//		return defs;
//	}

//	public static boolean isSimple(AMapUnionPattern p)
//	{
//		return PPatternAssistantTC.isSimple(p.getLeft())
//				&& PPatternAssistantTC.isSimple(p.getRight());
//	}

}
