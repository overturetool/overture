/*
 * #%~
 * Test Framework for Overture
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
package org.overture.interpreter.tests.newtests;

import org.overture.test.framework.results.IMessage;

public class Message
{
	private int number;

	private int line;

	private int col;

	private String message;

	private String resource;

	public Message(){
		
	}
	
	public Message(String resource, int number, int line, int col,
			String message)
	{
		this.resource = resource;
		this.number = number;
		this.line = line;
		this.col = col;
		this.message = message;
	}

	public void setNumber(int number)
	{
		this.number = number;
	}

	public int getNumber()
	{
		return number;
	}

	public void setLine(int line)
	{
		this.line = line;
	}

	public int getLine()
	{
		return line;
	}

	public void setCol(int col)
	{
		this.col = col;
	}

	public int getCol()
	{
		return col;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getMessage()
	{
		return message;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof IMessage)
		{
			IMessage other = (IMessage) obj;
			return // this.resource.equals(other.getResource()) && //removed the resource
			this.number == other.getNumber() && this.col == other.getCol()
					&& this.line == other.getLine()
					&& this.message.equals(other.getMessage());
		}
		return super.equals(obj);
	}

	@Override
	public String toString()
	{
		return number + " " + resource + " at " + line + ":" + col + " "
				+ message;
	}

	public void setResource(String resource)
	{
		this.resource = resource;
	}

	public String getResource()
	{
		return resource;
	}

}
