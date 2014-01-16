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

package org.overture.combinatorialtesting.vdmj.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.overture.ide.debug.core.VdmDebugPlugin;

public class ConnectionListener extends Thread
{
	private ServerSocket socket;
	private ThreadGroup group;
	private boolean listening;
	private ConnectionThread principal = null;
	private IClientMonitor monitor;

	public ConnectionListener(int port, IClientMonitor monitor)
			throws IOException
	{
		socket = new ServerSocket(port);
		socket.setSoTimeout(VdmDebugPlugin.getDefault().getConnectionTimeout());

		this.monitor = monitor;

		group = new ThreadGroup("Connections");
		setDaemon(true);
		setName("Connection Listener");
	}

	public int getPort()
	{
		return socket.getLocalPort();
	}

	public synchronized ConnectionThread getPrincipal()
	{
		return principal;
	}

	@Override
	public void run()
	{
		listening = true;

		try
		{
			while (listening)
			{
				try
				{
					Socket conn = socket.accept();

					if (group.activeCount() >= 1)
					{
						System.out.println("Too many DBGp connections");
						conn.close();
						continue;
					}

					ConnectionThread worker = new ConnectionThread(group, conn, principal == null, monitor);

					if (principal == null)
					{
						principal = worker; // The main connection
					}

					worker.start();
				} catch (SocketTimeoutException e)
				{
					// System.out.println("Listener timed out: " + e.getMessage());
				}
			}
		} catch (SocketException e)
		{
			// Killed by die() or VDMJ crash
		} catch (SocketTimeoutException e)
		{
			System.out.println("Listener timed out: " + e.getMessage());
		} catch (IOException e)
		{
			System.out.println("Listener exception: " + e.getMessage());
		}

		die();
	}

	public synchronized void die()
	{
		try
		{
			listening = false;
			socket.close();

			for (ConnectionThread ct : getConnections())
			{
				ct.die();
			}
		} catch (IOException e)
		{
			System.out.println("Cannot stop connection listener");
		}

		monitor.terminating();
	}

	public ConnectionThread findConnection(String id)
	{
		if (id == null)
		{
			return principal;
		}

		for (ConnectionThread ct : getConnections())
		{
			if (ct.getIdeId() != null && ct.getIdeId().equals(id))
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
		} while (group.enumerate(all) != all.length);

		return all;
	}
}
