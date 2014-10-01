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
package org.overture.test.framework;

import java.io.File;

import junit.framework.TestCase;

public abstract class BaseTestCase extends TestCase
{
	protected File file;
	protected String name;
	protected String content;

	protected enum ContentModed
	{
		File, String, None
	};

	protected final ContentModed mode;

	public BaseTestCase()
	{
		super("skip");
		mode = ContentModed.None;
	}

	public BaseTestCase(File file)
	{
		super("test");
		this.file = file;
		this.content = file.getName();
		mode = ContentModed.File;
	}

	public BaseTestCase(File rootSource, String name, String content)
	{
		super("test");
		this.content = content;
		this.name = name;
		this.file = rootSource;
		mode = ContentModed.String;
	}

	@Override
	public String getName()
	{
		if (name != null)
		{
			return name;
		} else if (file != null)
		{
			String newName = null;
			String name = file.getName();
			if (name.contains("."))
			{
				newName = name.substring(0, name.indexOf("."));
			}
			if (newName == null)
			{
				newName = file.getName();
			}

			return newName + " <" + file.getParentFile().getName() + ">";

		}
		return "Generic Base Test";
	}

	public abstract void test() throws Exception;

	public static String pad(String text, int length)
	{
		if (text == null)
		{
			text = "null";
		}
		while (text.length() < length)
		{
			text += " ";
		}
		return text;
	}

	public void skip()
	{
	};
}
