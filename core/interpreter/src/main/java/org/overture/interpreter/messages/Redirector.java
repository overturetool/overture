/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
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

package org.overture.interpreter.messages;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.debug.DBGPRedirect;

abstract public class Redirector extends PrintWriter
{
	protected DBGPRedirect type;
	protected DBGPReader dbgp;

	public Redirector(OutputStreamWriter out)
	{
		super(out, true);
		this.type = DBGPRedirect.DISABLE;
		this.dbgp = null;
	}

	public void redirect(DBGPRedirect t, DBGPReader d)
	{
		this.type = t;
		this.dbgp = d;
	}

	@Override
	public void println(String line)
	{
		print(line + "\n");
		flush();
	}

	@Override
	public PrintWriter printf(String format, Object... args)
	{
		print(String.format(format, args));
		flush();
		return this;
	}
	
	
	@Override
	public void println()
	{
		print("\n");
		flush();
	}
}
