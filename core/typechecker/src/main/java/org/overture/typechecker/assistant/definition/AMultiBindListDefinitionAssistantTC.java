package org.overture.typechecker.assistant.definition;

import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMultiBindListDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMultiBindListDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PDefinition findName(AMultiBindListDefinition d,
			ILexNameToken sought, NameScope scope)
	{

		if (d.getDefs() != null)
		{
			PDefinition def = PDefinitionListAssistantTC.findName(d.getDefs(), sought, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public static void unusedCheck(AMultiBindListDefinition d)
	{
		if (d.getDefs() != null)
		{
			PDefinitionListAssistantTC.unusedCheck(d.getDefs());
		}

	}

	public static LexNameList getVariableNames(AMultiBindListDefinition d)
	{

		return d.getDefs() == null ? new LexNameList()
				: PDefinitionListAssistantTC.getVariableNames(d.getDefs());
	}

	public static PType getType(AMultiBindListDefinition def)
	{
		PTypeList types = new PTypeList();

		for (PDefinition definition : def.getDefs())
		{
			types.add(definition.getType());
		}

		AUnionType result = AstFactory.newAUnionType(def.getLocation(), types);

		return result;
	}


}
