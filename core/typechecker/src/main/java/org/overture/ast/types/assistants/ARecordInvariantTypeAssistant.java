package org.overture.ast.types.assistants;

import org.overture.ast.types.AFieldField;
import org.overture.ast.types.ARecordInvariantType;



public class ARecordInvariantTypeAssistant {

	public static AFieldField findField(ARecordInvariantType rec, String tag) {
		for (AFieldField f: rec.getFields())
		{
			if (f.getTag().equals(tag))
			{
				return f;
			}
		}

		return null;
	}

}
