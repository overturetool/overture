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

package org.overturetool.vdmj;

import org.overturetool.vdmj.definitions.BUSClassDefinition;
import org.overturetool.vdmj.definitions.CPUClassDefinition;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.MessageException;

public class VDMRT extends VDMPP
{
	public VDMRT()
	{
		Settings.dialect = Dialect.VDM_RT;
	}

	@Override
	public ExitStatus typeCheck()
	{
		try
		{
			classes.add(new CPUClassDefinition());
  			classes.add(new BUSClassDefinition());
		}
		catch (Exception e)
		{
			throw new MessageException("Internal 0011: CPU or BUS creation failure");
		}

		return super.typeCheck();
	}
}
