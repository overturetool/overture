package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AImportedDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AImportedDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PDefinition findName(AImportedDefinition d,
			ILexNameToken sought, NameScope scope)
	{

		PDefinition def = PDefinitionAssistantTC.findName(d.getDef(), sought, scope);

		if (def != null)
		{
			PDefinitionAssistantTC.markUsed(d);
		}

		return def;
	}

	public static void markUsed(AImportedDefinition d)
	{
		d.setUsed(true);
		PDefinitionAssistantTC.markUsed(d.getDef());

	}

	 public static boolean isUsed(AImportedDefinition u) {
	 return PDefinitionAssistantTC.isUsed(u.getDef());
	
	 }

}
