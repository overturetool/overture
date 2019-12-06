/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.interpreter;

import org.overture.ast.factory.AstFactoryTC;
import org.overture.ast.lex.Dialect;
import org.overture.ast.messages.InternalException;
import org.overture.config.Settings;
import org.overture.interpreter.util.ExitStatus;

public class VDMRT extends VDMPP
{
	public VDMRT()
	{
		Settings.dialect = Dialect.VDM_RT;
		System.setProperty("VDM_RT", "1");
	}

	@Override
	public ExitStatus typeCheck()
	{
		try
		{
			classes.add(AstFactoryTC.newACpuClassDefinition(assistantFactory));
			classes.add(AstFactoryTC.newABusClassDefinition(assistantFactory));
		} catch (Exception e)
		{
			throw new InternalException(11, "CPU or BUS creation failure");
		}

		return super.typeCheck();
	}
}
