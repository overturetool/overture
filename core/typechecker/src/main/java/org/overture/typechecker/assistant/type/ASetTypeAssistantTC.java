package org.overture.typechecker.assistant.type;

import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASetTypeAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}


	public static PType polymorph(ASetType type, ILexNameToken pname,
			PType actualType)
	{
		return AstFactory.newASetType(type.getLocation(), PTypeAssistantTC.polymorph(type.getSetof(), pname, actualType));
	}

}
