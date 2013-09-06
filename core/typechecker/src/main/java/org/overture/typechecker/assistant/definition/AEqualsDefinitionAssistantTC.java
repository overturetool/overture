package org.overture.typechecker.assistant.definition;

import java.util.List;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AEqualsDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AEqualsDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PDefinition findName(AEqualsDefinition d,
			ILexNameToken sought, NameScope scope)
	{

		List<PDefinition> defs = d.getDefs();

		if (defs != null)
		{
			PDefinition def = PDefinitionListAssistantTC.findName(defs, sought, scope);

			if (def != null)
			{
				return def;
			}
		}
		return null;
	}

	public static void unusedCheck(AEqualsDefinition d)
	{

		if (d.getDefs() != null)
		{
			PDefinitionListAssistantTC.unusedCheck(d.getDefs());
		}

	}

	public static PType getType(AEqualsDefinition def)
	{
		return def.getDefType() != null ? def.getDefType()
				: AstFactory.newAUnknownType(def.getLocation());
	}


}
