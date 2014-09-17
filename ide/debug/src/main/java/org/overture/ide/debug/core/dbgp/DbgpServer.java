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
import java.net.ServerSocket;
import java.net.Socket;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.internal.DbgpDebugingEngine;
import org.overture.ide.debug.core.dbgp.internal.DbgpSession;
import org.overture.ide.debug.core.dbgp.internal.DbgpWorkingThread;

public class DbgpServer extends DbgpWorkingThread
{
	private final int port;
	private ServerSocket server;
	private final int clientTimeout;

	public static int findAvailablePort(int fromPort, int toPort)
	{
		if (fromPort > toPort)
		{
			throw new IllegalArgumentException("startPortShouldBeLessThanOrEqualToEndPort");
		}

		int port = fromPort;
		ServerSocket socket = null;
		while (port <= toPort)
		{
			try
			{
				socket = new ServerSocket(port);
				return port;
			} catch (IOException e)
			{
				++port;
			} finally
			{
				if (socket != null)
				{
					try
					{
						socket.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return -1;
	}

	private static final int STATE_NONE = 0;
	private static final int STATE_STARTED = 1;
	private static final int STATE_CLOSED = 2;

	private final Object stateLock = new Object();
	private int state = STATE_NONE;

	public boolean isStarted()
	{
		synchronized (stateLock)
		{
			return state == STATE_STARTED;
		}
	}

	public boolean waitStarted()
	{
		return waitStarted(15000);
	}

	public boolean waitStarted(long timeout)
	{
		synchronized (stateLock)
		{
			if (state == STATE_STARTED)
			{
				return true;
			} else if (state == STATE_CLOSED)
			{
				return false;
			}
			try
			{
				stateLock.wait(timeout);
			} catch (InterruptedException e)
			{
				// ignore
			}
			return state == STATE_STARTED;
		}
	}

	protected void workingCycle() throws Exception, IOException
	{
		try
		{
			server = new ServerSocket(port);
			synchronized (stateLock)
			{
				state = STATE_STARTED;
				stateLock.notifyAll();
			}
			while (!server.isClosed())
			{
				final Socket client = server.accept();
				client.setSoTimeout(clientTimeout);
				createSession(client);
			}
		} finally
		{
			if (server != null && !server.isClosed())
			{
				server.close();
			}
			synchronized (stateLock)
			{
				state = STATE_CLOSED;
				stateLock.notifyAll();
			}
		}
	}

	private static final class DbgpSessionJob extends Job
	{
		private final Socket client;
		private final IDbgpServerListener listener;

		private DbgpSessionJob(Socket client, IDbgpServerListener listener)
		{
			super("acceptingDebuggingEngineConnection");
			this.client = client;
			this.listener = listener;
			setSystem(true);
		}

		public boolean shouldSchedule()
		{
			return listener != null;
		}

		protected IStatus run(IProgressMonitor monitor)
		{
			DbgpDebugingEngine engine = null;
			try
			{
				engine = new DbgpDebugingEngine(client);
				DbgpSession session = new DbgpSession(engine);
				listener.clientConnected(session);
			} catch (Exception e)
			{
				VdmDebugPlugin.log(e);
				if (engine != null)
				{
					engine.requestTermination();
				}
			}
			return Status.OK_STATUS;
		}
	}

	private void createSession(final Socket client)
	{
		Job job = new DbgpSessionJob(client, listener);
		job.schedule();
	}

	public DbgpServer(int port, int clientTimeout)
	{
		super("DbgpServer"); //$NON-NLS-1$

		this.port = port;
		this.clientTimeout = clientTimeout;
	}

	/**
	 * @param port
	 * @param serverTimeout
	 * @param clientTimeout
	 * @deprecated use {@link #DbgpServer(int, int)}
	 */
	public DbgpServer(int port, int serverTimeout, int clientTimeout)
	{
		this(port, clientTimeout);
	}

	public void requestTermination()
	{
		try
		{
			if (server != null)
			{
				server.close();
			}
		} catch (IOException e)
		{
			VdmDebugPlugin.log(e);
		}
		super.requestTermination();
	}

	private IDbgpServerListener listener;

	public void setListener(IDbgpServerListener listener)
	{
		this.listener = listener;
	}
}
