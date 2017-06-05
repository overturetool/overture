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
package org.overture.codegen.printer;

public class DefaultConsolePrinter extends AbstractPrinter
{

	private static DefaultConsolePrinter log;

	public static DefaultConsolePrinter getDefaultLogger()
	{
		if (log == null)
		{
			log = new DefaultConsolePrinter();
		}

		return log;
	}

	protected boolean silent;

	private DefaultConsolePrinter()
	{
		super();
	}
	
	@Override
	public void println(String msg)
	{
		if (silent)
		{
			return;
		}

		System.out.println(msg);
	}

	@Override
	public void print(String msg)
	{
		if (silent)
		{
			return;
		}

		System.out.print(msg);
	}

	@Override
	public void errorln(String msg)
	{
		if (silent)
		{
			return;
		}

		System.err.println(msg);
	}

	@Override
	public void error(String msg)
	{
		if (silent)
		{
			return;
		}

		System.err.print(msg);
	}

}
