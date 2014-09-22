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
package org.overture.ide.debug.core.dbgp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.overture.ide.debug.core.dbgp.internal.utils.Base64Helper;
import org.overture.ide.debug.utils.StrUtils;

public class DbgpRequest implements IDbgpRawPacket
{
	private final Map<String, String> options;

	private final String command;
	private final boolean async;

	private String data;

	public DbgpRequest(String command)
	{
		this(command, false);
	}

	public DbgpRequest(String command, boolean async)
	{
		this.command = command;
		this.async = async;
		this.options = new HashMap<String, String>();
	}

	public String getCommand()
	{
		return command;
	}

	public void addOption(String optionNmae, int optionValue)
	{
		addOption(optionNmae, new Integer(optionValue));
	}

	public void addOption(String optionName, Object optionValue)
	{
		if (optionValue == null)
		{
			throw new IllegalArgumentException();
		}

		options.put(optionName, optionValue.toString());
	}

	public String getOption(String optionName)
	{
		return (String) options.get(optionName);
	}

	public boolean hasOption(String optionName)
	{
		return options.containsKey(optionName);
	}

	public int optionCount()
	{
		return options.size();
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public String getData()
	{
		return this.data;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer(command);

		Iterator<Entry<String, String>> it = options.entrySet().iterator();

		while (it.hasNext())
		{
			Entry<String, String> entry = it.next();

			sb.append(' ');
			sb.append(entry.getKey());
			sb.append(' ');
			sb.append(entry.getValue());
		}

		if (data != null)
		{
			sb.append(" -- "); //$NON-NLS-1$
			sb.append(Base64Helper.encodeString(data));
		}

		return sb.toString();
	}

	public boolean equals(Object o)
	{
		if (o instanceof DbgpRequest)
		{
			DbgpRequest request = (DbgpRequest) o;

			return command.equals(request.command) && async == request.async
					&& options.equals(request.options)
					&& StrUtils.equals(data, request.data);
		}

		return false;
	}

	/**
	 * @return the async
	 */
	public boolean isAsync()
	{
		return async;
	}

	public void writeTo(OutputStream output) throws IOException
	{
		// TODO optimize - send directly to stream without string
		output.write(toString().getBytes("ASCII")); //$NON-NLS-1$
	}

	public String getPacketAsString()
	{
		return toString();
	}
}
