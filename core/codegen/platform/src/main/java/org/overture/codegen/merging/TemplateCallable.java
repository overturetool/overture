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
package org.overture.codegen.merging;

public class TemplateCallable
{
	private String key;
	private Object callable;

	public TemplateCallable(String key, Object callable)
	{
		this.key = key;
		this.callable = callable;
	}

	public String getKey()
	{
		return key;
	}

	public Object getCallable()
	{
		return callable;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((callable == null) ? 0 : callable.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{		
		if(!(obj instanceof TemplateCallable))
		{
			return false;
		}
		
		TemplateCallable other = (TemplateCallable) obj;
		
		return key.equals(other.getKey());
	}
}
