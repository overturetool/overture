/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.core.dbgp.internal;

import java.net.URI;

import org.overture.ide.debug.core.dbgp.IDbgpStackLevel;
import org.overture.ide.debug.utils.StrUtils;

public class DbgpStackLevel implements IDbgpStackLevel
{

	private final int level;

	private final int lineNumber;

	private final int beginLine;
	private final int beginColumn;

	private final int endLine;
	private final int endColumn;

	private final URI fileUri;

	private final String where;

	public DbgpStackLevel(URI fileUri, String where, int level, int lineNumber,
			int beginLine, int endLine)
	{
		this(fileUri, where, level, lineNumber, beginLine, -1, endLine, -1);
	}

	public DbgpStackLevel(URI fileUri, String where, int level, int lineNumber,
			int beginLine, int beginColumn, int endLine, int endColumn)
	{
		this.fileUri = fileUri;
		this.level = level;
		this.lineNumber = lineNumber;
		this.beginLine = beginLine;
		this.beginColumn = beginColumn;
		this.endLine = endLine;
		this.endColumn = endColumn;
		this.where = where;
	}

	public String getWhere()
	{
		return where;
	}

	public int getLevel()
	{
		return level;
	}

	public int getLineNumber()
	{
		return lineNumber;
	}

	public int getBeginLine()
	{
		return beginLine;
	}

	public int getBeginColumn()
	{
		return beginColumn;
	}

	public int getEndLine()
	{
		return endLine;
	}

	public int getEndColumn()
	{
		return endColumn;
	}

	public URI getFileURI()
	{
		return fileUri;
	}

	public String toString()
	{
		return "DbgpStackLevel(level: " + level + ", line: " + lineNumber //$NON-NLS-1$ //$NON-NLS-2$
				+ ", begin: " + beginLine + ", end: " + endLine + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (fileUri == null ? 0 : fileUri.hashCode());
		result = prime * result + level;
		result = prime * result + beginLine;
		result = prime * result + endLine;
		result = prime * result + lineNumber;
		result = prime * result + (where == null ? 0 : where.hashCode());
		return result;
	}

	private static boolean equals(URI u1, URI u2)
	{
		if (u1 == null)
		{
			return u2 == null;
		} else
		{
			return u1.equals(u2);
		}
	}

	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof DbgpStackLevel))
		{
			return false;
		}
		final DbgpStackLevel other = (DbgpStackLevel) obj;
		if (!equals(fileUri, other.fileUri))
		{
			return false;
		}
		if (level != other.level)
		{
			return false;
		}
		if (beginLine != other.beginLine)
		{
			return false;
		}
		if (beginColumn != other.beginColumn)
		{
			return false;
		}
		if (endLine != other.endLine)
		{
			return false;
		}
		if (endColumn != other.endColumn)
		{
			return false;
		}
		if (lineNumber != other.lineNumber)
		{
			return false;
		}
		if (!StrUtils.equals(where, other.where))
		{
			return false;
		}
		return true;
	}

	public boolean isSameMethod(IDbgpStackLevel other)
	{
		return equals(fileUri, other.getFileURI())
				&& StrUtils.equals(where, other.getWhere());
	}
}
