package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AExternalDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AExternalDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PDefinition findName(AExternalDefinition d,
			ILexNameToken sought, NameScope scope)
	{

		if (sought.getOld())
		{
			return (sought.equals(d.getOldname())) ? d : null;
		}

		return (sought.equals(d.getState().getName())) ? d : null;
	}

	public static void markUsed(AExternalDefinition d)
	{
		d.setUsed(true);
		PDefinitionAssistantTC.markUsed(d.getState());

	}

//	public static LexNameList getVariableNames(AExternalDefinition d)
//	{
//		return PDefinitionAssistantTC.getVariableNames(d.getState());
//	}

	public static PType getType(AExternalDefinition def)
	{
		return af.createPDefinitionAssistant().getType(def.getState());
	}

	public static boolean isUsed(AExternalDefinition u)
	{
		return PDefinitionAssistantTC.isUsed(u.getState());

	}

}
