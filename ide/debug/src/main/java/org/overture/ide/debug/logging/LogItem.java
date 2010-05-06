package org.overture.ide.debug.logging;

import org.overture.ide.debug.utils.xml.XMLNode;

public class LogItem
{
	public final XMLNode node;
	public final String data;

	/**
	 * True for Request, False for response
	 */
	public final Boolean output;

	public final Integer threadId;

	public final String sessionId;

	public final Boolean isError;

	public final String type;

	public LogItem(String sessionId, String type, Integer threadId,
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

	public LogItem(String sessionId, String type, Integer threadId, Boolean output, XMLNode node,
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

	public LogItem(String sessionId, String type, Integer threadId, Boolean output, String data)
	{
		this.sessionId= sessionId;
		this.type = type;
		this.threadId = threadId;
		this.output = output;
		this.node = null;
		this.data = data;
		this.isError = false;
	}

	public LogItem(String sessionId, String type, Integer threadId, Boolean output, String data,
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
