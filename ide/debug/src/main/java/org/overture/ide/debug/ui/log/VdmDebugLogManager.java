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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.overture.ide.debug.core.ExtendedDebugEventDetails;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpRawListener;
import org.overture.ide.debug.core.dbgp.IDbgpRawPacket;
import org.overture.ide.debug.core.dbgp.internal.IDbgpDebugingEngine;
import org.overture.ide.debug.core.model.IVdmDebugTarget;
import org.overture.ide.debug.core.model.IVdmThread;

public class VdmDebugLogManager implements ILaunchListener,
		IDebugEventSetListener, IDbgpRawListener
{

	private static VdmDebugLogManager instance;

	private VdmDebugLogView view;

	private VdmDebugLogManager()
	{
		// empty constructor
	}

	public static synchronized VdmDebugLogManager getInstance()
	{
		if (instance == null)
		{
			instance = new VdmDebugLogManager();
		}

		return instance;
	}

	/*
	 * @see org.eclipse.dltk.dbgp.IDbgpRawListener#dbgpPacketReceived(java.lang.String )
	 */
	public void dbgpPacketReceived(int sessionId, IDbgpRawPacket content)
	{
		append(new VdmDebugLogItem(Messages.ItemType_Input, sessionId, content));
	}

	/*
	 * @see org.eclipse.dltk.dbgp.IDbgpRawListener#dbgpPacketSent(java.lang.String)
	 */
	public void dbgpPacketSent(int sessionId, IDbgpRawPacket content)
	{
		append(new VdmDebugLogItem(Messages.ItemType_Output, sessionId, content));
	}

	/*
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse .debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events)
	{
		if (view == null)
		{
			return;
		}

		for (int i = 0; i < events.length; ++i)
		{
			DebugEvent event = events[i];

			append(new VdmDebugLogItem(Messages.ItemType_Event, getDebugEventKind(event)
					+ " from " + event.getSource().getClass().getName()));//$NON-NLS-1$

			if (event.getKind() == DebugEvent.CREATE)
			{
				handleCreateEvent(event);
			} else if (event.getKind() == DebugEvent.MODEL_SPECIFIC
					&& event.getDetail() == ExtendedDebugEventDetails.DGBP_NEW_CONNECTION)
			{
				if (event.getSource() instanceof IDbgpDebugingEngine)
				{
					((IDbgpDebugingEngine) event.getSource()).addRawListener(this);
				}
			} else if (event.getKind() == DebugEvent.TERMINATE)
			{
				handleTerminateEvent(event);
			}
		}
	}

	/*
	 * @see org.eclipse.debug.core.ILaunchListener#launchAdded(org.eclipse.debug. core.ILaunch)
	 */
	public void launchAdded(ILaunch launch)
	{
		// empty implementation
	}

	/*
	 * @see org.eclipse.debug.core.ILaunchListener#launchChanged(org.eclipse.debug .core.ILaunch)
	 */
	public void launchChanged(ILaunch launch)
	{
		IDebugTarget target = launch.getDebugTarget();
		boolean loggingEnabled = false;
		try
		{
			if (launch.getLaunchConfiguration() != null)
			{
				loggingEnabled = launch.getLaunchConfiguration().getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_ENABLE_LOGGING, false);
			}

		} catch (CoreException e1)
		{

		}

		// bail if we're not a VdmDebugTarget or logging isn't enabled
		if (!(target instanceof IVdmDebugTarget && loggingEnabled))
		{
			return;
		}

		final IWorkbench wb = PlatformUI.getWorkbench();
		if (wb.getWorkbenchWindowCount() > 0)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{

					IWorkbenchPage page = wb.getWorkbenchWindows()[0].getActivePage();// VdmDebugPlugin.getActivePage();

					if (page != null)
					{
						try
						{
							view = (VdmDebugLogView) page.showView(VdmDebugLogView.VIEW_ID);

							DebugPlugin.getDefault().addDebugEventListener(VdmDebugLogManager.this);
						} catch (PartInitException e)
						{
							VdmDebugPlugin.log(e);
						}
					}
				}
			});
		}
	}

	/*
	 * @see org.eclipse.debug.core.ILaunchListener#launchRemoved(org.eclipse.debug .core.ILaunch)
	 */
	public void launchRemoved(ILaunch launch)
	{
		// empty implementation
	}

	protected void append(final VdmDebugLogItem item)
	{
		view.append(item);
	}

	private static String getDebugEventKind(DebugEvent event)
	{
		switch (event.getKind())
		{
			case DebugEvent.CREATE:
				return Messages.EventKind_Create;
			case DebugEvent.TERMINATE:
				return Messages.EventKind_Terminate;
			case DebugEvent.CHANGE:
				return Messages.EventKind_Change;
			case DebugEvent.SUSPEND:
				return Messages.EventKind_Suspend;
			case DebugEvent.RESUME:
				return Messages.EventKind_Resume;
			case DebugEvent.MODEL_SPECIFIC:
				return Messages.EventKind_ModelSpecific + '/'
						+ event.getDetail();
		}
		return Messages.EventKind_Unknown + '(' + event.getKind() + ')';
	}

	private void handleCreateEvent(DebugEvent event)
	{
		if (event.getSource() instanceof IVdmThread)
		{
			((IVdmThread) event.getSource()).getDbgpSession().addRawListener(this);
		}
	}

	private void handleTerminateEvent(DebugEvent event)
	{
		if (event.getSource() instanceof IVdmThread)
		{
			((IVdmThread) event.getSource()).getDbgpSession().removeRawListenr(this);
			DebugPlugin.getDefault().removeDebugEventListener(this);
		}
	}

}
