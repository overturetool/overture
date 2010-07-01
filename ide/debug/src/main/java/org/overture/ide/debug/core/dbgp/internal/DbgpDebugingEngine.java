/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.overture.ide.debug.core.dbgp.internal;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.eclipse.core.runtime.ListenerList;
import org.overture.ide.debug.core.ExtendedDebugEventDetails;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.DbgpRequest;
import org.overture.ide.debug.core.dbgp.IDbgpRawListener;
import org.overture.ide.debug.core.dbgp.IDbgpRawPacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpNotifyPacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpPacketReceiver;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpPacketSender;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpResponsePacket;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpStreamPacket;
import org.overture.ide.debug.core.dbgp.internal.packets.IDbgpRawLogger;
import org.overture.ide.debug.core.model.internal.DebugEventHelper;

public class DbgpDebugingEngine extends DbgpTermination implements
		IDbgpDebugingEngine, IDbgpTerminationListener
{
	private final Socket socket;

	private final DbgpPacketReceiver receiver;

	private final DbgpPacketSender sender;

	private final Object terminatedLock = new Object();
	private boolean terminated = false;

	private final int id;

	private static int lastId = 0;
	private static final Object idLock = new Object();

	// FIXME [OutOfMemory]
	private static boolean outOfMemory = false;

	public DbgpDebugingEngine(Socket socket) throws IOException
	{
		this.socket = socket;
		synchronized (idLock)
		{
			id = ++lastId;
		}

		receiver = new DbgpPacketReceiver(new BufferedInputStream(socket.getInputStream()));

		receiver.setLogger(new IDbgpRawLogger()
		{
			public void log(IDbgpRawPacket output)
			{
				firePacketReceived(output);
			}
		});

		receiver.addTerminationListener(this);

		// FIXME [OutOfMemory] The start is skipped if a out of memory exception have occurred. The memory here is the
		// native memory used for threads. See http://blogs.msdn.com/b/oldnewthing/archive/2005/07/29/444912.aspx
		try
		{
			if (!outOfMemory)
			{
				receiver.start();
			}
		} catch (OutOfMemoryError e)
		{
			outOfMemory = true;
			throw e;
		}
		sender = new DbgpPacketSender(new BufferedOutputStream(socket.getOutputStream()));

		sender.setLogger(new IDbgpRawLogger()
		{
			public void log(IDbgpRawPacket output)
			{
				firePacketSent(output);
			}
		});
		/*
		 * FIXME this event is delivered on the separate thread, so sometimes logging misses a few initial packets.
		 */
		DebugEventHelper.fireExtendedEvent(this, ExtendedDebugEventDetails.DGBP_NEW_CONNECTION);
	}

	public DbgpStreamPacket getStreamPacket() throws IOException,
			InterruptedException
	{
		return receiver.getStreamPacket();
	}

	public DbgpNotifyPacket getNotifyPacket() throws IOException,
			InterruptedException
	{
		return receiver.getNotifyPacket();
	}

	public DbgpResponsePacket getResponsePacket(int transactionId, int timeout)
			throws IOException, InterruptedException
	{
		return receiver.getResponsePacket(transactionId, timeout);
	}

	public void sendCommand(DbgpRequest command) throws IOException
	{
		sender.sendCommand(command);
	}

	// IDbgpTerminataion
	public void requestTermination()
	{
		// always just close the socket
		try
		{
			socket.close();
		} catch (IOException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				e.printStackTrace();
			}
		}
	}

	public void waitTerminated() throws InterruptedException
	{
		synchronized (terminatedLock)
		{
			if (terminated)
			{
				return;
			}

			receiver.waitTerminated();
		}
	}

	public void objectTerminated(Object object, Exception e)
	{
		synchronized (terminatedLock)
		{
			if (terminated)
				return;

			receiver.removeTerminationListener(this);
			try
			{
				receiver.waitTerminated();
			} catch (InterruptedException e1)
			{
				// OK, interrupted
			}

			terminated = true;
		}

		fireObjectTerminated(e);
	}

	private final ListenerList listeners = new ListenerList();

	protected void firePacketReceived(IDbgpRawPacket content)
	{
		Object[] list = listeners.getListeners();

		for (int i = 0; i < list.length; ++i)
		{
			((IDbgpRawListener) list[i]).dbgpPacketReceived(id, content);
		}
	}

	protected void firePacketSent(IDbgpRawPacket content)
	{
		Object[] list = listeners.getListeners();

		for (int i = 0; i < list.length; ++i)
		{
			((IDbgpRawListener) list[i]).dbgpPacketSent(id, content);
		}
	}

	public void addRawListener(IDbgpRawListener listener)
	{
		listeners.add(listener);
	}

	public void removeRawListenr(IDbgpRawListener listener)
	{
		listeners.remove(listener);
	}
}
