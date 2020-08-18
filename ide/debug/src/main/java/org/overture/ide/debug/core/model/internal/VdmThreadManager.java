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
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.overture.ide.debug.core.DebugOption;
import org.overture.ide.debug.core.IDebugOptions;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.dbgp.IDbgpFeature;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.IDbgpStreamFilter;
import org.overture.ide.debug.core.dbgp.IDbgpStreamListener;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpBreakpoint;
import org.overture.ide.debug.core.dbgp.breakpoints.IDbgpLineBreakpoint;
import org.overture.ide.debug.core.dbgp.commands.IDbgpFeatureCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.DebugEventHelper;
import org.overture.ide.debug.core.model.IVdmDebugThreadConfigurator;
import org.overture.ide.debug.core.model.IVdmStackFrame;
import org.overture.ide.debug.core.model.IVdmThread;
import org.overture.ide.debug.core.model.internal.operations.DbgpDebugger;

public class VdmThreadManager implements IVdmThreadManager, IDbgpStreamListener
{
	private static final boolean DEBUG = VdmDebugPlugin.DEBUG;
	private boolean errorState = false;
	private IVdmDebugThreadConfigurator configurator = null;

	// Helper methods
	private interface IThreadBoolean
	{
		boolean get(IThread thread);
	}

	private boolean getThreadBoolean(IThreadBoolean b)
	{
		synchronized (threads)
		{
			IThread[] ths = getThreads();

			if (ths.length == 0)
			{
				return false;
			}

			for (int i = 0; i < ths.length; ++i)
			{
				if (!b.get(ths[i]))
				{
					return false;
				}
			}

			return true;
		}
	}

	private final ListenerList listeners = new ListenerList(ListenerList.IDENTITY);

	private final List<VdmThread> threads = new ArrayList<VdmThread>();

	private volatile boolean waitingForThreads = true;

	private final VdmDebugTarget target;

	protected void fireThreadAccepted(IVdmThread thread, boolean first)
	{
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i)
		{
			((IVdmThreadManagerListener) list[i]).threadAccepted(thread, first);
		}
	}

	protected void fireAllThreadsTerminated()
	{
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i)
		{
			((IVdmThreadManagerListener) list[i]).allThreadsTerminated();
		}
	}

	public void addListener(IVdmThreadManagerListener listener)
	{
		listeners.add(listener);
	}

	public void removeListener(IVdmThreadManagerListener listener)
	{
		listeners.remove(listener);
	}

	public boolean isWaitingForThreads()
	{
		return waitingForThreads;
	}

	public boolean hasThreads()
	{
		synchronized (threads)
		{
			return !threads.isEmpty();
		}
	}

	public IVdmThread[] getThreads()
	{
		synchronized (threads)
		{

			return (IVdmThread[]) threads.toArray(new IVdmThread[threads.size()]);
		}
	}

	public VdmThreadManager(VdmDebugTarget target)
	{
		if (target == null)
		{
			throw new IllegalArgumentException();
		}

		this.target = target;
	}

	private IDbgpStreamFilter[] streamFilters = null;

	/**
	 * @param data
	 * @param stdout
	 * @return
	 */
	private String filter(String data, int stream)
	{
		if (streamFilters != null)
		{
			for (int i = 0; i < streamFilters.length; ++i)
			{
				data = streamFilters[i].filter(data, stream);
				if (data == null)
				{
					return null;
				}
			}
		}
		return data;
	}

	public void stdoutReceived(String data)
	{
		final IVdmStreamProxy proxy = target.getStreamProxy();
		if (proxy != null)
		{
			data = filter(data, IDbgpStreamFilter.STDOUT);
			if (data != null)
			{
				proxy.writeStdout(data);
			}
		}
		if (DEBUG)
		{
			//			System.out.println("Received (stdout): " + data); //$NON-NLS-1$
		}
	}

	public void stderrReceived(String data)
	{

		final IVdmStreamProxy proxy = target.getStreamProxy();

		if (proxy != null)
		{
			data = filter(data, IDbgpStreamFilter.STDERR);
			if (data != null)
			{
				proxy.writeStderr(data);
			}
		}
		if (DEBUG)
		{
			System.out.println("Received (stderr): " + data); //$NON-NLS-1$
		}

		DebugEventHelper.fireChangeEvent(target);
	}

	void setStreamFilters(IDbgpStreamFilter[] streamFilters)
	{
		this.streamFilters = streamFilters;
	}

	/**
	 * Tests if the specified thread has breakpoint at the same line
	 * 
	 * @param thread
	 * @return
	 */
	private static boolean hasBreakpointAtCurrentPosition(VdmThread thread)
	{
		try
		{
			thread.updateStack();
			if (thread.hasStackFrames())
			{
				final IStackFrame top = thread.getTopStackFrame();
				if (top instanceof IVdmStackFrame && top.getLineNumber() > 0)
				{
					final IVdmStackFrame frame = (IVdmStackFrame) top;
					if (frame.getSourceURI() != null)
					{
						final String location = frame.getSourceURI().getPath();
						final IDbgpBreakpoint[] breakpoints = thread.getDbgpSession().getCoreCommands().getBreakpoints();
						for (int i = 0; i < breakpoints.length; ++i)
						{
							if (breakpoints[i] instanceof IDbgpLineBreakpoint)
							{
								final IDbgpLineBreakpoint bp = (IDbgpLineBreakpoint) breakpoints[i];
								if (frame.getLineNumber() == bp.getLineNumber())
								{
									try
									{
										if (new URI(bp.getFilename()).getPath().equals(location))
										{
											return true;
										}
									} catch (URISyntaxException e)
									{
										if (VdmDebugPlugin.DEBUG)
										{
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (DebugException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				e.printStackTrace();
			}
		} catch (DbgpException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Tests if the specified thread has valid current stack. In some cases it is better to skip first internal
	 * location.
	 * 
	 * @param thread
	 * @return
	 */
	private static boolean isValidStack(VdmThread thread)
	{
		final IDebugOptions debugOptions = thread.getDbgpSession().getDebugOptions();
		if (debugOptions.get(DebugOption.ENGINE_VALIDATE_STACK))
		{
			thread.updateStack();
			if (thread.hasStackFrames())
			{
				return thread.isValidStack();
			}
		}
		return true;
	}

	// IDbgpThreadAcceptor
	public void acceptDbgpThread(IDbgpSession session, IProgressMonitor monitor)
	{
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try
		{
			DbgpException error = session.getInfo().getError();
			if (error != null)
			{
				throw error;
			}
			session.configure(target.getOptions());
			session.getStreamManager().addListener(this);

			final boolean breakOnFirstLine = // target.breakOnFirstLineEnabled()
			// ||
			isAnyThreadInStepInto();
			VdmThread thread = new VdmThread(target, session, this);
			thread.initialize(sub.newChild(25));
			addThread(thread);

			final boolean isFirstThread = waitingForThreads;
			if (isFirstThread)
			{
				waitingForThreads = false;
			}
			if (isFirstThread || !isSupportsThreads(thread))
			{
				SubMonitor child = sub.newChild(25);
				target.breakpointManager.initializeSession(thread.getDbgpSession(), child);
				child = sub.newChild(25);
				if (configurator != null)
				{
					configurator.initializeBreakpoints(thread, child);
				}
			}

			DebugEventHelper.fireCreateEvent(thread);

			final boolean stopBeforeCode = thread.getDbgpSession().getDebugOptions().get(DebugOption.ENGINE_STOP_BEFORE_CODE);
			boolean executed = false;
			if (!breakOnFirstLine)
			{
				if (stopBeforeCode || !hasBreakpointAtCurrentPosition(thread))
				{
					thread.resumeAfterAccept();
					executed = true;
				}
			} else
			{
				if (stopBeforeCode || !isValidStack(thread))
				{
					thread.initialStepInto();
					executed = true;
				}
			}
			if (!executed)
			{
				if (!thread.isStackInitialized())
				{
					thread.updateStack();
				}
				DebugEventHelper.fireChangeEvent(thread);
				DebugEventHelper.fireSuspendEvent(thread, DebugEvent.CLIENT_REQUEST);
			}
			sub.worked(25);
			fireThreadAccepted(thread, isFirstThread);
		} catch (Exception e)
		{
			try
			{
				target.terminate();
			} catch (DebugException e1)
			{
			}
			VdmDebugPlugin.log(e);
		} finally
		{
			sub.done();
		}
	}

	private static boolean isSupportsThreads(IVdmThread thread)
	{
		try
		{
			final IDbgpFeature feature = thread.getDbgpSession().getCoreCommands().getFeature(IDbgpFeatureCommands.LANGUAGE_SUPPORTS_THREADS);
			return feature != null
					&& IDbgpFeature.ONE_VALUE.equals(feature.getValue());
		} catch (DbgpException e)
		{
			if (VdmDebugPlugin.DEBUG)
			{
				e.printStackTrace();
			}
			return false;
		}
	}

	private boolean isAnyThreadInStepInto()
	{
		synchronized (threads)
		{
			for (Iterator<VdmThread> i = threads.iterator(); i.hasNext();)
			{
				VdmThread thread = (VdmThread) i.next();
				if (thread.isStepInto())
				{
					return true;
				}
			}
		}
		return false;
	}

	private void addThread(VdmThread thread)
	{
		synchronized (threads)
		{
			threads.add(thread);
		}
	}

	public void terminateThread(IVdmThread thread)
	{
		synchronized (threads)
		{
			threads.remove(thread);
		}
		DebugEventHelper.fireTerminateEvent(thread);
		final IDbgpSession session = ((VdmThread) thread).getDbgpSession();
		session.getStreamManager().removeListener(this);
		target.breakpointManager.removeSession(thread.getDbgpSession());
		if (!hasThreads())
		{
			fireAllThreadsTerminated();
		}
	}

	// ITerminate
	public boolean canTerminate()
	{
		synchronized (threads)
		{
			IThread[] ths = getThreads();

			if (ths.length == 0)
			{
				if (waitingForThreads)
				{
					return true;
				} else
				{
					return false;
				}
			}

			for (int i = 0; i < ths.length; ++i)
			{
				if (!ths[i].canTerminate())
				{
					return false;
				}
			}

			return true;
		}
	}

	public boolean isTerminated()
	{
		if (!hasThreads())
		{
			return !isWaitingForThreads();
		}

		return getThreadBoolean(new IThreadBoolean()
		{
			public boolean get(IThread thread)
			{
				return thread.isTerminated();
			}
		});
	}

	public void terminate() throws DebugException
	{
		target.terminate();
	}

	public void sendTerminationRequest() throws DebugException
	{
		synchronized (threads)
		{
			IVdmThread[] threads = getThreads();
			if (threads.length > 0)
			{
				waitingForThreads = false;
			}
			for (int i = 0; i < threads.length; ++i)
			{
				threads[i].sendTerminationRequest();
			}

		}
	}

	public boolean canResume()
	{
		if (errorState)
		{
			return false;
		}
		return getThreadBoolean(new IThreadBoolean()
		{
			public boolean get(IThread thread)
			{
				return thread.canResume();
			}
		});
	}

	public boolean canSuspend()
	{
		return getThreadBoolean(new IThreadBoolean()
		{
			public boolean get(IThread thread)
			{
				return thread.canSuspend();
			}
		});
	}

	public boolean isSuspended()
	{
		return getThreadBoolean(new IThreadBoolean()
		{
			public boolean get(IThread thread)
			{
				return thread.isSuspended();
			}
		});
	}

	public void resume() throws DebugException
	{
		synchronized (threads)
		{
			IThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i)
			{
				((VdmThread) threads[i]).resumeInner();
			}
		}
	}

	public void stepInto() throws DebugException
	{
		synchronized (threads)
		{
			IThread[] threads = getThreads();
			// System.out.println("Thread number:" + threads.length );
			for (int i = 0; i < threads.length; ++i)
			{
				((VdmThread) threads[i]).stepIntoInner();
				// System.out.println("Step Thread: " + i);
			}
		}
	}

	public void stepOver() throws DebugException
	{
		synchronized (threads)
		{
			IThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i)
			{
				((VdmThread) threads[i]).stepOverInner();
			}
		}
	}

	public void stepReturn() throws DebugException
	{
		synchronized (threads)
		{
			IThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i)
			{
				((VdmThread) threads[i]).stepReturnInner();
			}
		}
	}

	public void suspend() throws DebugException
	{
		synchronized (threads)
		{
			IThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i)
			{
				threads[i].suspend();
			}
		}
	}

	public void refreshThreads()
	{
		synchronized (threads)
		{
			IThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i)
			{
				((IVdmThread) threads[i]).updateStackFrames();
			}
		}
	}

	public void setVdmThreadConfigurator(
			IVdmDebugThreadConfigurator configurator)
	{
		this.configurator = configurator;
	}

	public void configureThread(DbgpDebugger engine, VdmThread scriptThread)
	{
		if (configurator != null)
		{
			configurator.configureThread(engine, scriptThread);
		}
	}

	public Boolean handleCustomTerminationCommands()
	{
		synchronized (threads)
		{
			if (threads.size() == 1)
			{
				return target.handleCustomTerminationCommands(threads.get(0).getDbgpSession());
			}else return false;
		}

	}


}
