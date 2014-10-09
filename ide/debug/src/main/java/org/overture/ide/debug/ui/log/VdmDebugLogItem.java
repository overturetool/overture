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
package org.overture.ide.debug.ui.log;

import org.overture.ide.debug.core.dbgp.IDbgpRawPacket;

public class VdmDebugLogItem
{

	private final long timestamp;
	private final String type;
	private final int sessionId;
	private final String message;

	public VdmDebugLogItem(String type, String message)
	{
		this.timestamp = System.currentTimeMillis();
		this.type = type;
		this.sessionId = 0;
		this.message = message;
	}

	public VdmDebugLogItem(String type, int sessionId, IDbgpRawPacket message)
	{
		this(System.currentTimeMillis(), type, sessionId, message);
	}

	/**
	 * @param message
	 * @param timestamp
	 * @param type
	 */
	public VdmDebugLogItem(long timestamp, String type, int sessionId,
			IDbgpRawPacket message)
	{
		this.timestamp = timestamp;
		this.type = type;
		this.sessionId = sessionId;
		this.message = message.getPacketAsString();
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public String getType()
	{
		return type;
	}

	public int getSessionId()
	{
		return sessionId;
	}

	public String getMessage()
	{
		return message;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return type + '\t' + message;
	}

}
