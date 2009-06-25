/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmjc.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.overturetool.vdmjc.config.Config;

public class ConnectionListener extends Thread
{
	private ServerSocket socket;
	private ThreadGroup group;
	private long connectionId = 0;
	private ConnectionThread principal = null;

	public ConnectionListener() throws IOException
	{
		socket = new ServerSocket();
		socket.bind(null);

		group = new ThreadGroup("Connections");
		setDaemon(true);
		setName("ConnectionListener");
	}

	public int getPort()
	{
		return socket.getLocalPort();
	}

	public ConnectionThread getPrincipal()
	{
		return principal;
	}

	@Override
	public void run()
	{
		try
		{
			while (true)
			{
				Socket conn = socket.accept();

				if (group.activeCount() >= Config.listener_connection_limit)
				{
					CommandLine.message("Too many DBGp connections");
					conn.close();
					continue;
				}

				ConnectionThread worker =
					new ConnectionThread(group, conn, ++connectionId, (principal == null));

				if (principal == null)
				{
					principal = worker;		// The main connection
				}

				worker.start();
			}
		}
		catch (SocketException e)
		{
			// Killed by die()
		}
		catch (IOException e)
		{
			CommandLine.message("Connection listener has died");
		}
	}

	public void die()
	{
		try
		{
			socket.close();

			for (ConnectionThread ct: getConnections())
			{
				ct.die();
			}
		}
		catch (IOException e)
		{
			CommandLine.message("Cannot stop connection listener");
		}
	}

	public ConnectionThread findConnection(long id)
	{
		if (id == 0)
		{
			return principal;
		}

		for (ConnectionThread ct: getConnections())
		{
			if (ct.getId() == id)
			{
				return ct;
			}
		}

		return null;
	}

	public ConnectionThread[] getConnections()
	{
		ConnectionThread[] all = null;

		do
		{
			all = new ConnectionThread[group.activeCount()];
		}
		while (group.enumerate(all) != all.length);

		return all;
	}
}
