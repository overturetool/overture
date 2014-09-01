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
package org.overture.ide.debug.core.launching.debug;

/**
 * Data object to hold host/port/sessionId and static methods to load from/save to {@link InterpreterConfig}
 */
public class DbgpConnectionConfig
{
	private final String host;
	private final int port;
	private final String sessionId;

	private DbgpConnectionConfig(String host, int port, String sessionId)
	{
		this.host = host;
		this.port = port;
		this.sessionId = sessionId;
	}

	public String getHost()
	{
		return host;
	}

	public int getPort()
	{
		return port;
	}

	public String getSessionId()
	{
		return sessionId;
	}

	// public static DbgpConnectionConfig load(InterpreterConfig config) {
	// String host = (String) config.getProperty(DbgpConstants.HOST_PROP);
	// String port = (String) config.getProperty(DbgpConstants.PORT_PROP);
	// String sessionId = (String) config
	// .getProperty(DbgpConstants.SESSION_ID_PROP);
	// return new DbgpConnectionConfig(host, Integer.parseInt(port), sessionId);
	// }
	//
	// public static void save(InterpreterConfig config, String host, int port,
	// String sessionId) {
	// config.setProperty(DbgpConstants.HOST_PROP, host);
	// config.setProperty(DbgpConstants.PORT_PROP, Integer.toString(port));
	// config.setProperty(DbgpConstants.SESSION_ID_PROP, sessionId);
	// }
}
