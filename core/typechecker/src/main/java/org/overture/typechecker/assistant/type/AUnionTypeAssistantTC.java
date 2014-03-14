package org.overture.typechecker.assistant.type;

import org.overture.ast.assistant.type.AUnionTypeAssistant;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AUnionTypeAssistantTC extends AUnionTypeAssistant
{
	protected ITypeCheckerAssistantFactory af;

	public AUnionTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		super(af);
		this.af = af;
	}

	public boolean isUnknown(AUnionType type)
	{
		for (PType t : type.getTypes())
		{
			if (af.createPTypeAssistant().isUnknown(t))
			{
				return true;
			}
		}

		return false;
	}
}
