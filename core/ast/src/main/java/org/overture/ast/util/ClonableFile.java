/*
 * #%~
 * The Overture Abstract Syntax Tree
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
package org.overture.ast.util;

import java.io.File;
import java.net.URI;

import org.overture.ast.node.ExternalNode;

public class ClonableFile extends File implements ExternalNode
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClonableFile(String pathname)
	{
		super(pathname);
	}

	public ClonableFile(String parent, String child)
	{
		super(parent, child);
	}

	public ClonableFile(URI uri)
	{

		super(uri);
	}

	public ClonableFile(File file)
	{
		super(file.getPath());
	}

	public Object clone()
	{
		return new ClonableFile(this.getPath());
	}

}
