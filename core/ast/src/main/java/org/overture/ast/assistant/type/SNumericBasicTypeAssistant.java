/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ast.assistant.type;

import org.overture.ast.assistant.IAstAssistant;
import org.overture.ast.assistant.IAstAssistantFactory;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.ARationalNumericBasicType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.SNumericBasicType;

public class SNumericBasicTypeAssistant implements IAstAssistant
{

	protected static IAstAssistantFactory af;

	@SuppressWarnings("static-access")
	public SNumericBasicTypeAssistant(IAstAssistantFactory af)
	{
		this.af = af;
	}

	public int getWeight(SNumericBasicType subn)
	{
		if (subn instanceof AIntNumericBasicType)
		{
			return 2;
		} else if (subn instanceof ANatNumericBasicType)
		{
			return 1;
		} else if (subn instanceof ANatOneNumericBasicType)
		{
			return 0;
		} else if (subn instanceof ARationalNumericBasicType)
		{
			return 3;
		} else if (subn instanceof ARealNumericBasicType)
		{
			return 4;
		}
		return -1;
	}

}
