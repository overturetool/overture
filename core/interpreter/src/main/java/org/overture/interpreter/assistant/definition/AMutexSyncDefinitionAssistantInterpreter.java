package org.overture.interpreter.assistant.definition;

import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.typechecker.assistant.definition.AMutexSyncDefinitionAssistantTC;

public class AMutexSyncDefinitionAssistantInterpreter extends
		AMutexSyncDefinitionAssistantTC
{

	public static PExp getExpression(AMutexSyncDefinition sync,
			LexNameToken excluding)
	{
		LexNameList list = null;

		if (sync.getOperations().size() == 1)
		{
			list =new LexNameList();
			list.addAll(sync.getOperations());
		}
		else
		{
			list = new LexNameList();
			list.addAll(sync.getOperations());
			list.remove(excluding);
		}

		return AstFactory.newAEqualsBinaryExp(AstFactory.newAHistoryExp(sync.getLocation(), new LexToken(sync.getLocation(),VDMToken.ACTIVE), list), 
    		new LexKeywordToken(VDMToken.EQUALS, sync.getLocation()),
    		AstFactory.newAIntLiteralExp(new LexIntegerToken(0, sync.getLocation())));
	}

}
