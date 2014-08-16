/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.utility;

import java.util.List;
import java.util.Vector;

import org.overture.ast.lex.Dialect;


public class Language implements ILanguage
{
	private String name;
	private String nature;
	private final List<String> contentTypes = new Vector<String>();
	private Dialect dialect = null;

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setNature(String nature)
	{
		this.nature = nature;
	}

	public String getNature()
	{
		return nature;
	}

	public void addContentType(String contentType)
	{
		this.contentTypes.add( contentType);
	}

	public List<String> getContentTypes()
	{
		return contentTypes;
	}

	public void setDialect(Dialect dialect)
	{
		this.dialect = dialect;
	}

	public Dialect getDialect()
	{
		return dialect;
	}

}
