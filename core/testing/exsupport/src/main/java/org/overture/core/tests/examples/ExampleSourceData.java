/*
 * #%~
 * Overture Testing Framework
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
package org.overture.core.tests.examples;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;

/**
 * Raw test data of an Overture example. This class holds the name and sources (the VDM model encoded as a single
 * {@link String}). It also holds the {@link Release} and {@link Dialect} the example is in.
 * 
 * @author ldc
 */
public class ExampleSourceData implements Serializable
{

	private static final long serialVersionUID = 1L;

	String name;
	Dialect dialect;
	Release release;
	List<File> source;

	public ExampleSourceData(String name, Dialect dialect, Release release,
			List<File> source)
	{
		this.name = name;
		this.dialect = dialect;
		this.source = source;
		this.release = release;
	}

	public ExampleSourceData(String name, Dialect dialect, Release release,
			File source)
	{
		this(name, dialect, release, Arrays.asList(new File[] { source }));
	}

	public Release getRelease()
	{
		return release;
	}

	public String getName()
	{
		return name;
	}

	public Dialect getDialect()
	{
		return dialect;
	}

	public List<File> getSource()
	{
		return source;
	}
	
	@Override
	public String toString()
	{
	return this.dialect + " > " + this.name;
	}

}
