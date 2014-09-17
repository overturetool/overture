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
package org.overture.ide.debug.core.dbgp.internal.packets;

import java.io.IOException;
import java.io.OutputStream;

import org.overture.ide.debug.core.dbgp.DbgpRequest;

public class DbgpPacketSender
{
	private final Object lock = new Object();

	private final OutputStream output;

	private IDbgpRawLogger logger;

	public DbgpPacketSender(OutputStream output)
	{
		if (output == null)
		{
			throw new IllegalArgumentException();
		}

		this.output = output;
	}

	public void setLogger(IDbgpRawLogger logger)
	{
		this.logger = logger;
	}

	public void sendCommand(DbgpRequest command) throws IOException
	{
		if (logger != null)
		{
			logger.log(command);
		}

		synchronized (lock)
		{
			command.writeTo(output);
			output.write(0);
			output.flush();
		}
	}
}
