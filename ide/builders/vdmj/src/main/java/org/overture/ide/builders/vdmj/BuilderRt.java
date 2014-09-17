/*
 * #%~
 * org.overture.ide.builders.vdmj
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
package org.overture.ide.builders.vdmj;

import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.lex.Dialect;
import org.overture.ast.messages.InternalException;
import org.overture.config.Settings;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

/***
 * VDM RT builder
 * 
 * @author kela <extension<br>
 *         point="org.overture.ide.builder"><br>
 *         <builder<br>
 *         class="org.overture.ide.builders.vdmj.BuilderRt"><br>
 *         </builder><br>
 *         </extension><br>
 */
public class BuilderRt extends BuilderPp {

	public BuilderRt() {
		super();
		Settings.dialect = Dialect.VDM_RT;
	}

	

	
	
	@Override
	public ExitStatus typeCheck()
	{
		try
		{
			ITypeCheckerAssistantFactory factory = new TypeCheckerAssistantFactory();
			
			classes.add(AstFactoryTC.newACpuClassDefinition(factory));
  			classes.add(AstFactoryTC.newABusClassDefinition(factory));
		}
		catch (Exception e)
		{
			throw new InternalException(11, "CPU or BUS creation failure");
		}

		return super.typeCheck();
	}

}
