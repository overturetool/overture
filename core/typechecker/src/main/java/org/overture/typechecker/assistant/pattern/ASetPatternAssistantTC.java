package org.overture.typechecker.assistant.pattern;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASetPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static void typeResolve(ASetPattern pattern,
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
			PPatternListAssistantTC.typeResolve(pattern.getPlist(), rootVisitor, question);
		} catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}

	}

	public static void unResolve(ASetPattern pattern)
	{
		PPatternListAssistantTC.unResolve(pattern.getPlist());
		pattern.setResolved(false);

	}


	// public static LexNameList getVariableNames(ASetPattern pattern) {
	// LexNameList list = new LexNameList();
	//
	// for (PPattern p: pattern.getPlist())
	// {
	// list.addAll(PPatternTCAssistant.getVariableNames(p));
	// }
	//
	// return list;
	// }

//	public static List<PDefinition> getAllDefinitions(ASetPattern rp,
//			PType type, NameScope scope)
//	{
//
//		List<PDefinition> defs = new Vector<PDefinition>();
//
//		if (!PTypeAssistantTC.isSet(type))
//		{
//			TypeCheckerErrors.report(3204, "Set pattern is not matched against set type", rp.getLocation(), rp);
//			TypeCheckerErrors.detail("Actual type", type);
//		} else
//		{
//			ASetType set = PTypeAssistantTC.getSet(type);
//
//			if (!set.getEmpty())
//			{
//				for (PPattern p : rp.getPlist())
//				{
//					defs.addAll(PPatternAssistantTC.getDefinitions(p, set.getSetof(), scope));
//				}
//			}
//		}
//
//		return defs;
//	}

//	public static List<PDefinition> getAllDefinitions(ASetPattern rp,
//			PType type, NameScope scope)
//	{
//
//		List<PDefinition> defs = new Vector<PDefinition>();
//
//		if (!PTypeAssistantTC.isSet(type))
//		{
//			TypeCheckerErrors.report(3204, "Set pattern is not matched against set type", rp.getLocation(), rp);
//			TypeCheckerErrors.detail("Actual type", type);
//		} else
//		{
//			ASetType set = PTypeAssistantTC.getSet(type);
//
//			if (!set.getEmpty())
//			{
//				for (PPattern p : rp.getPlist())
//				{
//					defs.addAll(PPatternAssistantTC.getDefinitions(p, set.getSetof(), scope));
//				}
//			}
//		}
//
//		return defs;
//	}


}
