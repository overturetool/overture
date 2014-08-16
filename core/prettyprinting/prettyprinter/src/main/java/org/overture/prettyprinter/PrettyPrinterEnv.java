/*
 * #%~
 * The VDM Pretty Printer
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
package org.overture.prettyprinter;

public class PrettyPrinterEnv
{
	StringBuilder sb = new StringBuilder();
	String className = "";

	public void setClassName(String name)
	{
		this.className = name;
	}

	public String getClassName()
	{
		return this.className;
	}

	public String increaseIdent()
	{
		sb.append("  ");
		return getIdent();
	}

	public String decreaseIdent()
	{
		if (sb.length() > 0)
		{
			sb.setLength(sb.length() - 2);
		}
		return getIdent();
	}

	public String getIdent()
	{
		return sb.toString();
	}
}
