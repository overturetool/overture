package org.overture.typechecker.assistant.definition;

import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMutexSyncDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AMutexSyncDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PDefinition findName(AMutexSyncDefinition d,
			ILexNameToken sought, NameScope scope)
	{
		return null;
	}

	public static LexNameList getVariableNames(AMutexSyncDefinition d)
	{
		return new LexNameList();
	}


}
