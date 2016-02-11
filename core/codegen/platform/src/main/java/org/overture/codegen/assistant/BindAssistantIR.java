/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.assistant;

import org.overture.codegen.ir.SBindIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.patterns.ASetBindIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.patterns.ATypeBindIR;
import org.overture.codegen.ir.patterns.ATypeMultipleBindIR;
import org.overture.codegen.logging.Logger;

public class BindAssistantIR extends AssistantBase
{
	public BindAssistantIR(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public SMultipleBindIR convertToMultipleBind(SBindIR bind)
	{
		SMultipleBindIR result = null;
		
		if(bind instanceof ASetBindIR)
		{
			ASetBindIR setBind = (ASetBindIR) bind;
			
			ASetMultipleBindIR multipleSetBind = new ASetMultipleBindIR();
			
			multipleSetBind.getPatterns().add(bind.getPattern());
			multipleSetBind.setSet(setBind.getSet());
			
			result = multipleSetBind;
		}
		else if(bind instanceof ATypeBindIR)
		{
			ATypeBindIR typeBind = (ATypeBindIR) bind;
			
			ATypeMultipleBindIR multipleTypeBind = new ATypeMultipleBindIR();
			
			multipleTypeBind.getPatterns().add(bind.getPattern());
			multipleTypeBind.setType(typeBind.getType());
			
			result = multipleTypeBind; 
		}
		
		if(result != null)
		{
			result.setTag(bind.getTag());
			result.setSourceNode(bind.getSourceNode());
			result.setMetaData(bind.getMetaData());
		}
		else
		{
			Logger.getLog().printErrorln("Expected set or type bind in '" + this.getClass().getSimpleName() + "'");
		}
		
		return result; 
	}
}
