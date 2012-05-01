package org.overture.ast.statements.assistants;

import org.overture.ast.types.AVoidReturnType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeSet;

public class ANonDeterministicSimpleBlockStmAssistantTC
{
	public static boolean addOne(PTypeSet rtypes, PType add)
	{
		if (add instanceof AVoidReturnType)
		{
			rtypes.add(new AVoidType(add.getLocation(),false));
			return true;
		}
		else if (!(add instanceof AVoidType))
		{
			rtypes.add(add);
			return true;
		}
		else
		{
			rtypes.add(add);
			return false;
		}
	}
}
