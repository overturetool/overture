package org.overture.typechecker.assistant.type;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexIdentifierToken;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AClassTypeAssistantTC
{

	protected ITypeCheckerAssistantFactory af;

	public AClassTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public LexNameToken getMemberName(AClassType cls,
			ILexIdentifierToken id)
	{
		// Note: not explicit
		return new LexNameToken(cls.getName().getName(), id.getName(), id.getLocation(), false, false);
	}

	public PDefinition findName(AClassType cls, ILexNameToken tag,
			NameScope scope)
	{
		return af.createPDefinitionAssistant().findName(cls.getClassdef(), tag, scope);
	}

	public boolean hasSupertype(AClassType sclass, PType other)
	{
		return af.createSClassDefinitionAssistant().hasSupertype(sclass.getClassdef(), other);
	}

}
