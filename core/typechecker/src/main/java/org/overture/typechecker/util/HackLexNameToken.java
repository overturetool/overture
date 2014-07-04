package org.overture.typechecker.util;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.LexNameTokenAssistant;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

//FIXME Remove this class when we figure out how to compare lexNametoken without using types
public class HackLexNameToken
{
	protected static ITypeCheckerAssistantFactory af = new TypeCheckerAssistantFactory();

	@SuppressWarnings("static-access")
	public HackLexNameToken(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static boolean isEqual(ILexNameToken one, Object other)
	{
		return af.getLexNameTokenAssistant().isEqual(one, other);
	}
	
	public static LexNameTokenAssistant getStaticLexNameTokenAssistant(){
		return af.getLexNameTokenAssistant();
	}

}
