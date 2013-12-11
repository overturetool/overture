package org.overture.typechecker.assistant.type;

import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ARecordInvariantTypeAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARecordInvariantTypeAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static AFieldField findField(ARecordInvariantType rec, String tag)
	{
		for (AFieldField f : rec.getFields())
		{
			if (f.getTag().equals(tag))
			{
				return f;
			}
		}

		return null;
	}

}
