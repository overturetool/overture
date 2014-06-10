package org.overture.ast.assistant.type;

import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.SNumericBasicType;

public class SNumericBasicTypeAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public SNumericBasicTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public int getWeight(SNumericBasicType subn)
	{
		if (subn instanceof AIntNumericBasicType) {
			return 2;
		} else if (subn instanceof ANatNumericBasicType) {
			return 1;
		} else if (subn instanceof ANatOneNumericBasicType) {
			return 0;
		} else if (subn instanceof ARationalNumericBasicType) {
			return 3;
		} else if (subn instanceof ARealNumericBasicType) {
			return 4;
		}
		return -1;
	}

}
