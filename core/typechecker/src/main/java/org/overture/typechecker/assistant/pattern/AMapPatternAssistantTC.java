package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMapPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMapPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public void unResolve(AMapPattern pattern)
//	{
//
//		for (AMapletPatternMaplet mp : pattern.getMaplets())
//		{
//			af.createAMapletPatternMapletAssistant().unResolve(mp);
//		}
//
//		pattern.setResolved(false);
//	}

//	public void typeResolve(AMapPattern pattern,
//			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
//			TypeCheckInfo question) throws AnalysisException
//	{
//
//		if (pattern.getResolved())
//		{
//			return;
//		} else
//		{
//			pattern.setResolved(true);
//		}
//
//		try
//		{
//			for (AMapletPatternMaplet mp : pattern.getMaplets())
//			{
//				af.createAMapletPatternMapletAssistant().typeResolve(mp, rootVisitor, question);
//			}
//		} catch (TypeCheckException e)
//		{
//			unResolve(pattern);
//			throw e;
//		}
//
//	}

//	public List<PDefinition> getAllDefinitions(AMapPattern rp,
//			PType ptype, NameScope scope)
//	{
//
//		List<PDefinition> defs = new Vector<PDefinition>();
//
//		if (!PTypeAssistantTC.isMap(ptype))
//		{
//			TypeCheckerErrors.report(3314, "Map pattern is not matched against map type", rp.getLocation(), rp);
//			TypeCheckerErrors.detail("Actual type", ptype);
//		} else
//		{
//			SMapType map = af.createPTypeAssistant().getMap(ptype);
//
//			if (!map.getEmpty())
//			{
//				for (AMapletPatternMaplet p : rp.getMaplets())
//				{
//					defs.addAll(af.createAMapletPatternMapletAssistant().getDefinitions(p, map, scope));
//				}
//			}
//		}
//
//		return defs;
//	}

//	public boolean isSimple(AMapPattern p)
//	{
//		for (AMapletPatternMaplet mp : p.getMaplets())
//		{
//
//			if (!af.createAMapletPatternMapletAssistant().isSimple(mp))
//			{
//				return false;
//			}
//
//		}
//		return true;
//	}

}
