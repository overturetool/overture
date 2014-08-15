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
package org.overture.ide.debug.core.dbgp.internal;

import java.io.IOException;

import org.overture.ide.debug.core.IDebugOptions;
import org.overture.ide.debug.core.dbgp.IDbgpCommunicator;
import org.overture.ide.debug.core.dbgp.IDbgpNotificationManager;
import org.overture.ide.debug.core.dbgp.IDbgpRawListener;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.IDbgpSessionInfo;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCoreCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpExtendedCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpOvertureCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpSpawnpointCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.commands.DbgpCoreCommands;
import org.overture.ide.debug.core.dbgp.internal.commands.DbgpDebuggingEngineCommunicator;
import org.overture.ide.debug.core.dbgp.internal.commands.DbgpExtendedCommands;
import org.overture.ide.debug.core.dbgp.internal.commands.DbgpOvertureCommands;
import org.overture.ide.debug.core.dbgp.internal.commands.DbgpSpawnpointCommands;
import org.overture.ide.debug.core.dbgp.internal.managers.DbgpNotificationManager;
import org.overture.ide.debug.core.dbgp.internal.managers.DbgpStreamManager;
import org.overture.ide.debug.core.dbgp.internal.managers.IDbgpStreamManager;
import org.overture.ide.debug.core.dbgp.internal.packets.DbgpResponsePacket;
import org.overture.ide.debug.core.dbgp.internal.utils.DbgpXmlEntityParser;
import org.overture.ide.debug.core.model.DefaultDebugOptions;

public class DbgpSession extends DbgpTermination implements IDbgpSession,
		IDbgpTerminationListener
{
	private final IDbgpDebugingEngine engine;

	private final IDbgpCoreCommands coreCommands;

	private final IDbgpExtendedCommands extendedCommands;
	private final IDbgpSpawnpointCommands spawnpointCommands;
	private final IDbgpOvertureCommands overtureCommands;
	private final DbgpNotificationManager notificationManager;

	private final DbgpStreamManager streamManager;

	private IDbgpSessionInfo info;

	private final Object terminatedLock = new Object();
	private boolean terminated = false;

	private DbgpDebuggingEngineCommunicator communicator;

	private void requestTerminateImpl(Object object)
	{
		if (object != engine)
		{
			engine.requestTermination();
		}

		if (object != streamManager && streamManager != null)
		{
			streamManager.requestTermination();
		}

		if (object != notificationManager && notificationManager != null)
		{
			notificationManager.requestTermination();
		}
	}

	private void waitTerminatedImpl(Object object) throws InterruptedException
	{
		if (object != engine)
		{
			engine.waitTerminated();
		}

		if (streamManager != null && object != streamManager)
		{
			streamManager.waitTerminated();
		}

		if (notificationManager != null && object != notificationManager)
		{
			notificationManager.waitTerminated();
		}
	}

	public DbgpSession(IDbgpDebugingEngine engine) throws DbgpException,
			IOException
	{
		if (engine == null)
		{
			throw new IllegalArgumentException();
		}

		this.engine = engine;

		try
		{
			DbgpResponsePacket responsePacket = engine.getResponsePacket(-1, 0);
			if (responsePacket == null)
			{
				throw new DbgpException();
			}
			info = DbgpXmlEntityParser.parseSession(responsePacket.getContent());
		} catch (InterruptedException e)
		{
		}

		// Engine
		this.engine.addTerminationListener(this);

		// Notification manager
		this.notificationManager = new DbgpNotificationManager(engine);
		this.notificationManager.addTerminationListener(this);

		// Stream manager
		this.streamManager = new DbgpStreamManager(engine, "DBGP - Stream manager"); //$NON-NLS-1$
		this.streamManager.addTerminationListener(this);

		communicator = new DbgpDebuggingEngineCommunicator(engine, DefaultDebugOptions.getDefaultInstance());

		this.coreCommands = new DbgpCoreCommands(communicator);
		this.extendedCommands = new DbgpExtendedCommands(communicator);
		this.spawnpointCommands = new DbgpSpawnpointCommands(communicator, this);
		this.overtureCommands = new DbgpOvertureCommands(communicator);

		// Starting all
		this.notificationManager.start();
		this.streamManager.start();
	}

	public IDbgpSessionInfo getInfo()
	{
		return info;
	}

	public String toString()
	{
		return "Session. appid: " + info.getApplicationId(); //$NON-NLS-1$
	}

	public IDbgpCoreCommands getCoreCommands()
	{
		return coreCommands;
	}

	public IDbgpExtendedCommands getExtendedCommands()
	{
		return extendedCommands;
	}

	public IDbgpOvertureCommands getOvertureCommands()
	{
		return overtureCommands;
	}

	public IDbgpStreamManager getStreamManager()
	{
		return streamManager;
	}

	public IDbgpNotificationManager getNotificationManager()
	{
		return notificationManager;
	}

	// IDbgpTermination
	public void requestTermination()
	{
		synchronized (terminatedLock)
		{
			if (terminated)
			{
				return;
			}

			requestTerminateImpl(null);
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

			waitTerminatedImpl(null);
		}
	}

	// IDbgpTerminationListener
	public void objectTerminated(Object object, Exception e)
	{
		// Allows to unblock all terminating threads
		synchronized (terminatedLock)
		{
			if (terminated)
			{
				return;
			}
			terminated = true;
		}

		engine.removeTerminationListener(this);
		if (streamManager != null)
		{
			streamManager.removeTerminationListener(this);
		}
		if (notificationManager != null)
		{
			notificationManager.removeTerminationListener(this);
		}

		// Request terminate
		requestTerminateImpl(object);

		try
		{
			waitTerminatedImpl(object);
		} catch (InterruptedException ex)
		{
			// OK, interrrputed
		}
		fireObjectTerminated(e);
	}

	public void addRawListener(IDbgpRawListener listener)
	{
		engine.addRawListener(listener);

	}

	public void removeRawListenr(IDbgpRawListener listener)
	{
		engine.removeRawListenr(listener);
	}

	public IDbgpCommunicator getCommunicator()
	{
		return this.communicator;
	}

	/*
	 * @see org.eclipse.dltk.debug.core.IDebugConfigurable#getDebugOptions()
	 */
	public IDebugOptions getDebugOptions()
	{
		return communicator.getDebugOptions();
	}

	public void configure(IDebugOptions debugOptions)
	{
		communicator.configure(debugOptions);
	}

	public Object get(@SuppressWarnings("rawtypes") Class type)
	{
		if (type == IDbgpSpawnpointCommands.class)
		{
			return spawnpointCommands;
		} else if (type == IDbgpCoreCommands.class)
		{
			return coreCommands;
		} else if (type == IDbgpExtendedCommands.class)
		{
			return extendedCommands;
		}
		return null;
	}
}
