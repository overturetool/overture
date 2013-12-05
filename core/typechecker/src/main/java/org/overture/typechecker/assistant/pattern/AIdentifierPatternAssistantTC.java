package org.overture.typechecker.assistant.pattern;

import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AIdentifierPatternAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AIdentifierPatternAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

//	public static PType getPossibleTypes(AIdentifierPattern pattern)
//	{
//		return AstFactory.newAUnknownType(pattern.getLocation());
//	}

//	public static List<PDefinition> getAllDefinitions(AIdentifierPattern rp,
//			PType ptype, NameScope scope)
//	{
//		List<PDefinition> defs = new ArrayList<PDefinition>();
//		defs.add(AstFactory.newALocalDefinition(rp.getLocation(), rp.getName().clone(), scope, ptype));
//		return defs;
//	}

//	public static PExp getMatchingExpression(AIdentifierPattern idp)
//	{
//		return AstFactory.newAVariableExp(idp.getName().clone());
//	}

//	public static boolean isSimple(AIdentifierPattern p)
//	{
//		return false;
//	}

}
