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
package org.overture.ide.debug.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.model.IDebugTarget;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.IDbgpThreadAcceptor;
import org.overture.ide.debug.core.dbgp.internal.utils.Util;
import org.overture.ide.debug.core.model.IVdmDebugTargetListener;
import org.overture.ide.debug.core.model.internal.VdmDebugTarget;

public class DebugSessionAcceptor implements IDbgpThreadAcceptor,
		IVdmDebugTargetListener
{

	private static class NopLaunchStatusHandler implements
			ILaunchStatusHandler, ILaunchStatusHandlerExtension
	{

		public void initialize(IDebugTarget target, IProgressMonitor monitor)
		{
			// empty
		}

		public void updateElapsedTime(long elapsedTime)
		{
			// empty
		}

		public void dispose()
		{
			// empty
		}

		public boolean isCanceled()
		{
			return true;
		}

	}

	private final VdmDebugTarget target;
	private IProgressMonitor parentMonitor;
	private boolean initialized = false;
	private boolean connected = false;
	private ILaunchStatusHandler statusHandler = null;

	public DebugSessionAcceptor(VdmDebugTarget target, IProgressMonitor monitor)
	{
		this.target = target;
		this.parentMonitor = monitor;
		target.addListener(this);
		target.getDbgpService().registerAcceptor(target.getSessionId(), this);
	}

	/*
	 * @see IVdmDebugTargetListener#targetInitialized()
	 */
	public void targetInitialized()
	{
		synchronized (this)
		{
			initialized = true;
			notify();
		}
	}

	public void targetTerminating()
	{
		target.getDbgpService().unregisterAcceptor(target.getSessionId());
		disposeStatusHandler();
	}

	public void disposeStatusHandler()
	{
		if (statusHandler != null)
		{
			statusHandler.dispose();
			statusHandler = null;
		}
	}

	private static final int WAIT_CHUNK = 1000;

	public boolean waitConnection(final int timeout) throws CoreException
	{
		final SubProgressMonitor sub = new SubProgressMonitor(parentMonitor, 1);
		sub.beginTask(Util.EMPTY_STRING, timeout / WAIT_CHUNK);
		try
		{
			sub.setTaskName("Waiting for Connection");
			final long start = System.currentTimeMillis();
			try
			{
				long waitStart = start;
				for (;;)
				{
					synchronized (this)
					{
						if (connected)
						{
							return true;
						}
					}
					if (target.isTerminated() || sub.isCanceled())
					{
						break;
					}
					abortIfProcessTerminated();
					synchronized (this)
					{
						wait(WAIT_CHUNK);
					}
					final long now = System.currentTimeMillis();
					if (timeout != 0 && now - start > timeout)
					{
						if (statusHandler == null)
						{
							statusHandler = createStatusHandler();
						}
						if (statusHandler instanceof ILaunchStatusHandlerExtension
								&& ((ILaunchStatusHandlerExtension) statusHandler).isCanceled())
						{
							return false;
						}
						statusHandler.updateElapsedTime(now - start);
					}
					sub.worked((int) ((now - waitStart) / WAIT_CHUNK));
					waitStart = now;
				}
			} catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
			return false;
		} finally
		{
			sub.done();
		}
	}

	private void abortIfProcessTerminated() throws CoreException
	{
		if (target.getLaunch().getLaunchConfiguration().getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG, false))
		{
			return;
		}

		if (target.getProcess() != null && target.getProcess().isTerminated())
		{
			throw new CoreException(new Status(IStatus.ERROR, VdmDebugPlugin.PLUGIN_ID, IDebugConstants.ERR_DEBUGGER_PROCESS_TERMINATED, "DebuggerUnexpectedlyTerminated", null));
		}
	}

	/**
	 * @return
	 */
	private ILaunchStatusHandler createStatusHandler()
	{
		final String extensionPointId = VdmDebugPlugin.PLUGIN_ID
				+ ".launchStatusHandler"; //$NON-NLS-1$
		final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(extensionPointId);
		for (int i = 0; i < elements.length; ++i)
		{
			try
			{
				final ILaunchStatusHandler handler = (ILaunchStatusHandler) elements[i].createExecutableExtension("class"); //$NON-NLS-1$
				handler.initialize(target, parentMonitor);
				return handler;
			} catch (Exception e)
			{
				VdmDebugPlugin.logWarning(e);
			}
		}
		final ILaunchStatusHandler handler = new NopLaunchStatusHandler();
		handler.initialize(target, parentMonitor);
		return handler;
	}

	public void acceptDbgpThread(IDbgpSession session, IProgressMonitor monitor)
	{
		final boolean isFirst;
		synchronized (this)
		{
			isFirst = !connected;
			if (!connected)
			{
				connected = true;
				notify();
			}
		}
		if (isFirst)
		{
			IProgressMonitor sub = getInitializeMonitor();
			try
			{
				target.getDbgpThreadAcceptor().acceptDbgpThread(session, sub);
			} finally
			{
				sub.done();
			}
		} else
		{
			target.getDbgpThreadAcceptor().acceptDbgpThread(session, new NullProgressMonitor());
		}
	}

	private IProgressMonitor initializeMonitor = null;

	private synchronized IProgressMonitor getInitializeMonitor()
	{
		if (initializeMonitor == null)
		{
			initializeMonitor = new SubProgressMonitor(parentMonitor, 1);
			initializeMonitor.beginTask(Util.EMPTY_STRING, 100);
			initializeMonitor.setTaskName("waitInitialization");
		}
		return initializeMonitor;
	}

	public boolean waitInitialized(final int timeout) throws CoreException
	{
		final IProgressMonitor sub = getInitializeMonitor();
		try
		{
			final long start = System.currentTimeMillis();
			try
			{
				for (;;)
				{
					synchronized (this)
					{
						if (initialized)
						{
							return true;
						}
					}
					if (target.isTerminated() || sub.isCanceled())
					{
						break;
					}
					abortIfProcessTerminated();
					synchronized (this)
					{
						wait(WAIT_CHUNK);
					}
					final long now = System.currentTimeMillis();
					if (timeout != 0 && now - start > timeout)
					{
						break;
					}
				}
			} catch (InterruptedException e)
			{
				Thread.interrupted();
			}
			return false;
		} finally
		{
			sub.done();
		}
	}

}
