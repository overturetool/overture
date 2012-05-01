package org.overture.ast.statements.assistants;

import java.util.Set;

import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;

public class ABlockSimpleBlockStmAssistantTC {

	public static void addOne(Set<PType> rtypes, PType add)
	{
		if (add instanceof AVoidReturnType)
		{
			rtypes.add(new AVoidType(add.getLocation(), false));
		}
		else if (!(add instanceof AVoidType))
		{
			rtypes.add(add);
		}
	}
	
}
