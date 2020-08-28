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
package org.overture.ide.debug.core.model.internal;

import java.net.URI;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.osgi.util.NLS;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.debug.core.DebugOption;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.breakpoints.DbgpBreakpointConfig;
import org.overture.ide.debug.core.dbgp.commands.IDbgpBreakpointCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCoreCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.dbgp.internal.utils.Util;
import org.overture.ide.debug.core.model.IVdmBreakpoint;
import org.overture.ide.debug.core.model.IVdmBreakpointPathMapper;
import org.overture.ide.debug.core.model.IVdmDebugTarget;
import org.overture.ide.debug.core.model.IVdmLineBreakpoint;
import org.overture.ide.debug.core.model.IVdmMethodEntryBreakpoint;
import org.overture.ide.debug.core.model.IVdmWatchpoint;

public class VdmBreakpointManager implements IBreakpointListener,
		IBreakpointManagerListener
{

	final IVdmBreakpointPathMapper bpPathMapper;

	private static final IDbgpSession[] NO_SESSIONS = new IDbgpSession[0];

	private IDbgpSession[] sessions;

	// Utility methods
	protected static IBreakpointManager getBreakpointManager()
	{
		return DebugPlugin.getDefault().getBreakpointManager();
	}

	protected static DbgpBreakpointConfig createBreakpointConfig(
			IVdmBreakpoint breakpoint) throws CoreException
	{
		// Enabled
		boolean enabled = breakpoint.isEnabled()
				&& getBreakpointManager().isEnabled();

		DbgpBreakpointConfig config = new DbgpBreakpointConfig(enabled);

		// Hit value
		config.setHitValue(breakpoint.getHitValue());

		// Hit condition
		config.setHitCondition(breakpoint.getHitCondition());

		// Expression
		if (breakpoint.getExpressionState())
		{
			config.setExpression(breakpoint.getExpression());
		}

		if (breakpoint instanceof IVdmLineBreakpoint
				&& !(breakpoint instanceof IVdmMethodEntryBreakpoint))
		{
			IVdmLineBreakpoint lineBreakpoint = (IVdmLineBreakpoint) breakpoint;
			config.setLineNo(lineBreakpoint.getLineNumber());
		}
		return config;
	}

	protected static String makeWatchpointExpression(IVdmWatchpoint watchpoint)
			throws CoreException
	{
		// final IDLTKDebugToolkit debugToolkit = VdmDebugManager.getInstance()
		// .getDebugToolkitByDebugModel(watchpoint.getModelIdentifier());
		// if (debugToolkit.isAccessWatchpointSupported()) {
		// return watchpoint.getFieldName()
		// + (watchpoint.isAccess() ? '1' : '0')
		// + (watchpoint.isModification() ? '1' : '0');
		// } else {
		return watchpoint.getFieldName();
		// }
	}

	// Adding, removing, updating
	protected void addBreakpoint(final IDbgpSession session,
			IVdmBreakpoint breakpoint) throws CoreException, DbgpException
	{
		final IDbgpCoreCommands commands = session.getCoreCommands();
		DbgpBreakpointConfig config = createBreakpointConfig(breakpoint);

		String id = null;
		URI bpUri = null;

		// map the outgoing uri if we're a line breakpoint
		if (breakpoint instanceof IVdmLineBreakpoint)
		{
			IVdmLineBreakpoint bp = (IVdmLineBreakpoint) breakpoint;
			bpUri = bpPathMapper.map(bp.getResourceURI());
		}

		// Type specific
		if (breakpoint instanceof IVdmWatchpoint)
		{
			IVdmWatchpoint watchpoint = (IVdmWatchpoint) breakpoint;
			config.setExpression(makeWatchpointExpression(watchpoint));

			id = commands.setWatchBreakpoint(bpUri, watchpoint.getLineNumber(), config);
		} else if (breakpoint instanceof IVdmMethodEntryBreakpoint)
		{
			IVdmMethodEntryBreakpoint entryBreakpoint = (IVdmMethodEntryBreakpoint) breakpoint;

			if (entryBreakpoint.breakOnExit())
			{
				final String exitId = commands.setReturnBreakpoint(bpUri, entryBreakpoint.getMethodName(), config);

				entryBreakpoint.setExitBreakpointId(exitId);
			}

			if (entryBreakpoint.breakOnEntry())
			{
				final String entryId = commands.setCallBreakpoint(bpUri, entryBreakpoint.getMethodName(), config);

				entryBreakpoint.setEntryBreakpointId(entryId);
			}
		} else if (breakpoint instanceof IVdmLineBreakpoint)
		{
			IVdmLineBreakpoint lineBreakpoint = (IVdmLineBreakpoint) breakpoint;

			if (VdmBreakpointUtils.isConditional(lineBreakpoint))
			{
				id = commands.setConditionalBreakpoint(bpUri, lineBreakpoint.getLineNumber(), config);
			} else
			{
				id = commands.setLineBreakpoint(bpUri, lineBreakpoint.getLineNumber(), config);
			}
		}

		// else if (breakpoint instanceof IVdmExceptionBreakpoint) {
		// IVdmExceptionBreakpoint lineBreakpoint = (IVdmExceptionBreakpoint) breakpoint;
		// id = commands.setExceptionBreakpoint(lineBreakpoint.getTypeName(),
		// config);
		// }

		// Identifier
		breakpoint.setId(session, id);
	}

	// private void addSpawnpoint(final IDbgpSession session,
	// IVdmSpawnpoint spawnpoint) throws DbgpException, CoreException {
	// final IDbgpSpawnpointCommands commands = (IDbgpSpawnpointCommands) session
	// .get(IDbgpSpawnpointCommands.class);
	// final IDbgpSpawnpoint p = commands.setSpawnpoint(bpPathMapper
	// .map(spawnpoint.getResourceURI()), spawnpoint.getLineNumber(),
	// spawnpoint.isEnabled());
	// if (p != null) {
	// spawnpoint.setId(session, p.getId());
	// }
	// }

	protected void changeBreakpoint(final IDbgpSession session,
			IVdmBreakpoint breakpoint) throws DbgpException, CoreException
	{
		final IDbgpBreakpointCommands commands = session.getCoreCommands();
		URI bpUri = null;

		// map the outgoing uri if we're a line breakpoint
		if (breakpoint instanceof IVdmLineBreakpoint)
		{
			IVdmLineBreakpoint bp = (IVdmLineBreakpoint) breakpoint;
			bpUri = bpPathMapper.map(bp.getResourceURI());
		}

		if (breakpoint instanceof IVdmMethodEntryBreakpoint)
		{
			DbgpBreakpointConfig config = createBreakpointConfig(breakpoint);
			IVdmMethodEntryBreakpoint entryBreakpoint = (IVdmMethodEntryBreakpoint) breakpoint;

			String entryId = entryBreakpoint.getEntryBreakpointId();
			if (entryBreakpoint.breakOnEntry())
			{
				if (entryId == null)
				{
					// Create entry breakpoint
					entryId = commands.setCallBreakpoint(bpUri, entryBreakpoint.getMethodName(), config);
					entryBreakpoint.setEntryBreakpointId(entryId);
				} else
				{
					// Update entry breakpoint
					commands.updateBreakpoint(entryId, config);
				}
			} else
			{
				if (entryId != null)
				{
					// Remove existing entry breakpoint
					commands.removeBreakpoint(entryId);
					entryBreakpoint.setEntryBreakpointId(null);
				}
			}

			String exitId = entryBreakpoint.getExitBreakpointId();
			if (entryBreakpoint.breakOnExit())
			{
				if (exitId == null)
				{
					// Create exit breakpoint
					exitId = commands.setReturnBreakpoint(bpUri, entryBreakpoint.getMethodName(), config);
					entryBreakpoint.setExitBreakpointId(exitId);
				} else
				{
					// Update exit breakpoint
					commands.updateBreakpoint(exitId, config);
				}
			} else
			{
				if (exitId != null)
				{
					// Remove exit breakpoint
					commands.removeBreakpoint(exitId);
					entryBreakpoint.setExitBreakpointId(null);
				}
			}
		} else
		{
			// All other breakpoints
			final String id = breakpoint.getId(session);
			if (id != null)
			{
				final DbgpBreakpointConfig config = createBreakpointConfig(breakpoint);
				if (breakpoint instanceof IVdmWatchpoint)
				{
					config.setExpression(makeWatchpointExpression((IVdmWatchpoint) breakpoint));
				}
				commands.updateBreakpoint(id, config);
			}
		}
	}

	protected static void removeBreakpoint(IDbgpSession session,
			IVdmBreakpoint breakpoint) throws DbgpException, CoreException
	{
		final IDbgpBreakpointCommands commands = session.getCoreCommands();
		final String id = breakpoint.removeId(session);
		if (id != null)
		{
			commands.removeBreakpoint(id);
		}

		if (breakpoint instanceof IVdmMethodEntryBreakpoint)
		{
			IVdmMethodEntryBreakpoint entryBreakpoint = (IVdmMethodEntryBreakpoint) breakpoint;

			final String entryId = entryBreakpoint.getEntryBreakpointId();
			if (entryId != null)
			{
				commands.removeBreakpoint(entryId);
			}

			final String exitId = entryBreakpoint.getExitBreakpointId();
			if (exitId != null)
			{
				commands.removeBreakpoint(exitId);
			}
		}
	}

	private static final int NO_CHANGES = 0;
	private static final int MINOR_CHANGE = 1;
	private static final int MAJOR_CHANGE = 2;

	private int hasBreakpointChanges(IMarkerDelta delta,
			IVdmBreakpoint breakpoint)
	{
		final String[] attrs = breakpoint.getUpdatableAttributes();
		try
		{
			final IMarker marker = delta.getMarker();
			for (int i = 0; i < attrs.length; ++i)
			{
				final String attr = attrs[i];

				final Object oldValue = delta.getAttribute(attr);
				final Object newValue = marker.getAttribute(attr);

				if (oldValue == null)
				{
					if (newValue != null)
					{
						return classifyBreakpointChange(delta, breakpoint, attr);
					}
					continue;
				}
				if (newValue == null)
				{
					return classifyBreakpointChange(delta, breakpoint, attr);
				}
				if (!oldValue.equals(newValue))
				{
					return classifyBreakpointChange(delta, breakpoint, attr);
				}
			}
		} catch (CoreException e)
		{
			VdmDebugPlugin.log(e);
		}
		return NO_CHANGES;
	}

	// private static int hasSpawnpointChanges(IMarkerDelta delta,
	// IVdmBreakpoint breakpoint) {
	// final String[] attrs = breakpoint.getUpdatableAttributes();
	// try {
	// final IMarker marker = delta.getMarker();
	// for (int i = 0; i < attrs.length; ++i) {
	// final String attr = attrs[i];
	// if (IBreakpoint.ENABLED.equals(attr)
	// || IMarker.LINE_NUMBER.equals(attr)) {
	// final Object oldValue = delta.getAttribute(attr);
	// final Object newValue = marker.getAttribute(attr);
	// if (oldValue == null) {
	// if (newValue != null) {
	// return IMarker.LINE_NUMBER.equals(attr) ? MAJOR_CHANGE
	// : MINOR_CHANGE;
	// }
	// continue;
	// }
	// if (newValue == null) {
	// return IMarker.LINE_NUMBER.equals(attr) ? MAJOR_CHANGE
	// : MINOR_CHANGE;
	// }
	// if (!oldValue.equals(newValue)) {
	// return IMarker.LINE_NUMBER.equals(attr) ? MAJOR_CHANGE
	// : MINOR_CHANGE;
	// }
	// }
	// }
	// } catch (CoreException e) {
	// VdmDebugPlugin.log(e);
	// }
	// return NO_CHANGES;
	// }

	private int classifyBreakpointChange(IMarkerDelta delta,
			IVdmBreakpoint breakpoint, String attr) throws CoreException
	{
		final boolean conditional = VdmBreakpointUtils.isConditional(breakpoint);
		if (conditional && AbstractVdmBreakpoint.EXPRESSION.equals(attr))
		{
			return MAJOR_CHANGE;
		}
		final boolean oldExprState = delta.getAttribute(AbstractVdmBreakpoint.EXPRESSION_STATE, false);
		final String oldExpr = delta.getAttribute(AbstractVdmBreakpoint.EXPRESSION, null);
		if (VdmBreakpointUtils.isConditional(oldExprState, oldExpr) != conditional)
		{
			return MAJOR_CHANGE;
		}
		if (IMarker.LINE_NUMBER.equals(attr)
				&& !target.getOptions().get(DebugOption.DBGP_BREAKPOINT_UPDATE_LINE_NUMBER))
		{
			return MAJOR_CHANGE;
		}
		return MINOR_CHANGE;
	}

	// DebugTarget
	private final IVdmDebugTarget target;

	// private void changeSpawnpoint(final IDbgpSession session,
	// IVdmSpawnpoint spawnpoint) throws DbgpException, CoreException {
	// final IDbgpSpawnpointCommands commands = (IDbgpSpawnpointCommands) session
	// .get(IDbgpSpawnpointCommands.class);
	// if (commands != null) {
	// final String id = spawnpoint.getId(session);
	// if (id != null) {
	// commands.updateSpawnpoint(id, spawnpoint.isEnabled());
	// }
	// }
	// }

	// protected void removeSpawnpoint(final IDbgpSession session,
	// IVdmSpawnpoint spawnpoint) throws DbgpException, CoreException {
	// final IDbgpSpawnpointCommands commands = (IDbgpSpawnpointCommands) session
	// .get(IDbgpSpawnpointCommands.class);
	// if (commands != null) {
	// final String id = spawnpoint.getId(session);
	// if (id != null) {
	// commands.removeSpawnpoint(id);
	// spawnpoint.setId(session, null);
	// }
	// }
	// }

	public VdmBreakpointManager(IVdmDebugTarget target,
			IVdmBreakpointPathMapper pathMapper)
	{
		this.target = target;
		this.bpPathMapper = pathMapper;
		this.sessions = NO_SESSIONS;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint)
	{
		if (breakpoint instanceof IVdmBreakpoint)
		{
			return StrUtils.equals(breakpoint.getModelIdentifier(), target.getModelIdentifier());
		}

		return false;
	}

	private void threadAccepted()
	{
		IBreakpointManager manager = getBreakpointManager();

		manager.addBreakpointListener(target);
		manager.addBreakpointManagerListener(this);
	}

	public void threadTerminated()
	{
		IBreakpointManager manager = getBreakpointManager();

		manager.removeBreakpointListener(target);
		manager.removeBreakpointManagerListener(this);

		if (bpPathMapper instanceof IVdmBreakpointPathMapperExtension)
		{
			((IVdmBreakpointPathMapperExtension) bpPathMapper).clearCache();
		}
	}

	synchronized IDbgpSession[] getSessions()
	{
		return sessions;
	}

	private synchronized boolean addSession(IDbgpSession session)
	{
		for (int i = 0; i < sessions.length; ++i)
		{
			if (session.equals(sessions[i]))
			{
				return false;
			}
		}
		final IDbgpSession[] temp = new IDbgpSession[sessions.length + 1];
		System.arraycopy(sessions, 0, temp, 0, sessions.length);
		temp[sessions.length] = session;
		sessions = temp;
		return true;
	}

	synchronized boolean removeSession(IDbgpSession session)
	{
		for (int i = 0; i < sessions.length; ++i)
		{
			if (session.equals(sessions[i]))
			{
				if (sessions.length == 1)
				{
					sessions = NO_SESSIONS;
				} else
				{
					final IDbgpSession[] temp = new IDbgpSession[sessions.length - 1];
					if (i > 0)
					{
						System.arraycopy(sessions, 0, temp, 0, i);
					}
					++i;
					if (i < sessions.length)
					{
						System.arraycopy(sessions, i, temp, i - 1, sessions.length
								- i);
					}
					sessions = temp;
				}
				return true;
			}
		}
		return false;
	}

	public void initializeSession(IDbgpSession session, IProgressMonitor monitor)
	{
		if (!addSession(session))
		{
			return;
		}
		if (!target.getLaunch().getLaunchMode().equals(ILaunchManager.DEBUG_MODE))
		{
			return;
		}
		if (!DebugPlugin.getDefault().getBreakpointManager().isEnabled())
		{
			return;
		}

		IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(target.getModelIdentifier());
		IVdmProject vdmProject = this.target.getVdmProject();

		monitor.beginTask(Util.EMPTY_STRING, breakpoints.length);

		for (int i = 0; i < breakpoints.length; i++)
		{
			try
			{
				final IBreakpoint breakpoint = breakpoints[i];
				if (breakpoint.getMarker().getResource().getProject().getName().equals(vdmProject.getName()))
				{
					addBreakpoint(session, (IVdmBreakpoint) breakpoint);
				}

			} catch (Exception e)
			{
				VdmDebugPlugin.logWarning(NLS.bind("ErrorSetupDeferredBreakpoints", e.getMessage()), e);
				if (VdmDebugPlugin.DEBUG)
				{
					e.printStackTrace();
				}
			}
			monitor.worked(1);
		}
		threadAccepted();
		monitor.done();
	}

	private static class TemporaryBreakpoint implements IDebugEventSetListener
	{
		final VdmBreakpointManager manager;
		final Map<IDbgpSession, String> ids = new IdentityHashMap<IDbgpSession, String>(1);

		/**
		 * @param manager
		 * @param uri
		 * @param line
		 */
		public TemporaryBreakpoint(VdmBreakpointManager manager, URI uri,
				int line)
		{
			this.manager = manager;
			final IDbgpSession[] sessions = manager.getSessions();
			for (int i = 0; i < sessions.length; ++i)
			{
				DbgpBreakpointConfig config = new DbgpBreakpointConfig(true);
				try
				{
					final String id = sessions[i].getCoreCommands().setLineBreakpoint(uri, line, config);
					if (id != null)
					{
						ids.put(sessions[i], id);
					}
				} catch (DbgpException e)
				{
					VdmDebugPlugin.log(e);
				}
			}
		}

		public void handleDebugEvents(DebugEvent[] events)
		{
			for (int i = 0; i < events.length; ++i)
			{
				DebugEvent event = events[i];
				if (event.getKind() == DebugEvent.SUSPEND)
				{
					removeBreakpoint();
					DebugPlugin.getDefault().removeDebugEventListener(this);
					break;
				}
			}
		}

		private void removeBreakpoint()
		{
			try
			{
				final IDbgpSession[] sessions = manager.getSessions();
				for (int i = 0; i < sessions.length; ++i)
				{
					final IDbgpSession session = sessions[i];
					final String id = (String) ids.remove(session);
					if (id != null)
					{
						session.getCoreCommands().removeBreakpoint(id);
					}
				}
			} catch (DbgpException e)
			{
				VdmDebugPlugin.log(e);
			}
		}

	}

	public void setBreakpointUntilFirstSuspend(URI uri, int line)
	{
		final TemporaryBreakpoint temp = new TemporaryBreakpoint(this, uri, line);
		if (!temp.ids.isEmpty())
		{
			DebugPlugin.getDefault().addDebugEventListener(temp);
		}
	}

	// IBreakpointListener
	public void breakpointAdded(IBreakpoint breakpoint)
	{
		if (!supportsBreakpoint(breakpoint))
		{
			return;
		}
		try
		{
			final IDbgpSession[] sessions = getSessions();
			for (int i = 0; i < sessions.length; ++i)
			{

				addBreakpoint(sessions[i], (IVdmBreakpoint) breakpoint);

			}
		} catch (Exception e)
		{
			VdmDebugPlugin.log(e);
		}
	}

	/**
	 * @see IBreakpointListener#breakpointChanged(IBreakpoint, IMarkerDelta)
	 * @param breakpoint
	 * @param delta
	 *            if delta is <code>null</code> then there was a call to
	 *            BreakPointManager.fireBreakpointChanged(IBreakpoint breakpoint), so see it as a major change.
	 */
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		if (!supportsBreakpoint(breakpoint))
		{
			return;
		}
		try
		{

			final IVdmBreakpoint sbp = (IVdmBreakpoint) breakpoint;
			final int changes = delta != null ? hasBreakpointChanges(delta, sbp)
					: MAJOR_CHANGE;
			if (changes != NO_CHANGES)
			{
				final IDbgpSession[] sessions = getSessions();
				if (changes == MAJOR_CHANGE)
				{
					for (int i = 0; i < sessions.length; ++i)
					{
						removeBreakpoint(sessions[i], sbp);
						addBreakpoint(sessions[i], sbp);
					}
				} else
				{
					for (int i = 0; i < sessions.length; ++i)
					{
						changeBreakpoint(sessions[i], sbp);
					}
				}
			}

		} catch (Exception e)
		{
			VdmDebugPlugin.log(e);
		}
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta)
	{
		if (!supportsBreakpoint(breakpoint))
		{
			return;
		}
		try
		{
			final IDbgpSession[] sessions = getSessions();

			for (int i = 0; i < sessions.length; ++i)
			{
				removeBreakpoint(sessions[i], (IVdmBreakpoint) breakpoint);
			}

		} catch (Exception e)
		{
			VdmDebugPlugin.log(e);
		}
	}

	// IBreakpointManagerListener
	public void breakpointManagerEnablementChanged(boolean enabled)
	{
		IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(target.getModelIdentifier());

		final IDbgpSession[] sessions = getSessions();
		for (int i = 0; i < breakpoints.length; ++i)
		{
			try
			{
				final IBreakpoint breakpoint = breakpoints[i];
				if (breakpoint instanceof IVdmBreakpoint)
				{
					for (int j = 0; j < sessions.length; ++j)
					{
						changeBreakpoint(sessions[j], (IVdmBreakpoint) breakpoint);
					}
				}
			} catch (Exception e)
			{
				VdmDebugPlugin.log(e);
			}
		}
	}

}
