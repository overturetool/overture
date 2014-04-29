package org.overture.typechecker.assistant.statement;

import java.util.Set;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ABlockSimpleBlockStmAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public ABlockSimpleBlockStmAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public void addOne(Set<PType> rtypes, PType add)
	{
		if (add instanceof AVoidReturnType)
		{
			rtypes.add(AstFactory.newAVoidType(add.getLocation()));
		} else if (!(add instanceof AVoidType))
		{
			rtypes.add(add);
		}
	}

}
