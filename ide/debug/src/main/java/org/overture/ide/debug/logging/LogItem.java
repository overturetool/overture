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
package org.overture.ide.debug.logging;

import org.overture.ide.debug.core.dbgp.IDbgpSessionInfo;
import org.overture.ide.debug.utils.xml.XMLNode;

public class LogItem
{
	public final XMLNode node;
	public final String data;

	/**
	 * True for Request, False for response
	 */
	public final Boolean output;

	public final String threadId;

	public final String sessionId;

	public final Boolean isError;

	public final String type;

	public LogItem(String sessionId, String type, String threadId,
			Boolean output, XMLNode node)
	{
		this.sessionId = sessionId;
		this.type = type;
		this.threadId = threadId;
		this.output = output;
		this.node = node;
		this.data = null;
		this.isError = false;
	}

	public LogItem(String sessionId, String type, String threadId,
			Boolean output, XMLNode node, Boolean isError)
	{
		this.sessionId = sessionId;
		this.type = type;
		this.threadId = threadId;
		this.output = output;
		this.node = node;
		this.data = null;
		this.isError = isError;
	}

	public LogItem(String sessionId, String type, String threadId,
			Boolean output, String data)
	{
		this.sessionId = sessionId;
		this.type = type;
		this.threadId = threadId;
		this.output = output;
		this.node = null;
		this.data = data;
		this.isError = false;
	}

	public LogItem(String sessionId, String type, String threadId,
			Boolean output, String data, Boolean isError)
	{
		this.sessionId = sessionId;
		this.type = type;
		this.threadId = threadId;
		this.output = output;
		this.data = data;
		this.isError = isError;
		this.node = null;
	}

	public LogItem(IDbgpSessionInfo info, String type, boolean output,
			String string2)
	{
		this.sessionId = info.getSession();
		this.type = type;
		this.threadId = info.getThreadId().split(" ")[0];
		this.output = output;
		this.data = string2;
		this.isError = false;
		this.node = null;
	}

	public String getData()
	{
		if (node == null)
		{
			return data;
		} else
		{
			return node.toString();
		}
	}
}
