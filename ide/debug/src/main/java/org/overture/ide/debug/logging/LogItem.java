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

	public LogItem(String sessionId, String type, String threadId, Boolean output, XMLNode node,
			Boolean isError)
	{
		this.sessionId= sessionId;
		this.type = type;
		this.threadId = threadId;
		this.output = output;
		this.node = node;
		this.data = null;
		this.isError = isError;
	}

	public LogItem(String sessionId, String type, String threadId, Boolean output, String data)
	{
		this.sessionId= sessionId;
		this.type = type;
		this.threadId = threadId;
		this.output = output;
		this.node = null;
		this.data = data;
		this.isError = false;
	}

	public LogItem(String sessionId, String type, String threadId, Boolean output, String data,
			Boolean isError)
	{
		this.sessionId= sessionId;
		this.type = type;
		this.threadId = threadId;
		this.output = output;
		this.data = data;
		this.isError = isError;
		this.node = null;
	}

	public LogItem(IDbgpSessionInfo info, String type, boolean output,
			String string2) {
		this.sessionId = info.getSession();
		this.type = type;
		this.threadId = (info.getThreadId().split(" ")[0]);
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
