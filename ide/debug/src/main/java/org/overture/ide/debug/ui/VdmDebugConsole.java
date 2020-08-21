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
package org.overture.ide.debug.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IFlushableStreamMonitor;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.IConsoleColorProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.progress.UIJob;
import org.overture.ide.debug.core.VdmDebugPlugin;

public class VdmDebugConsole extends IOConsole implements
		IDebugEventSetListener
{

	/**
	 * @since 2.0
	 */
	public static final String TYPE = VdmDebugPlugin.PLUGIN_ID
			+ ".ScriptDebugConsoleType"; //$NON-NLS-1$

	private final ILaunch launch;
	private final IConsoleColorProvider fColorProvider;

	public ILaunch getLaunch()
	{
		return launch;
	}

	/**
	 * @since 2.0
	 */
	public IProcess getProcess()
	{
		final IProcess[] processes = launch.getProcesses();
		if (processes.length != 0)
		{
			return processes[0];
		} else
		{
			return null;
		}
	}

	/**
	 * @since 2.0
	 */
	public VdmDebugConsole(ILaunch launch, String name,
			ImageDescriptor imageDescriptor, String encoding,
			IConsoleColorProvider colorProvider)
	{
		super(name, TYPE, imageDescriptor, encoding, true);
		this.launch = launch;
		this.fColorProvider = colorProvider;
		// this.addPatternMatchListener(new ScriptDebugConsoleTraceTracker());
	}

	@Override
	public void matcherFinished()
	{
		super.matcherFinished();
	}

	@Override
	public void partitionerFinished()
	{
		super.partitionerFinished();
	}

	/*
	 * Increase visibility
	 */
	@Override
	protected void setName(String name)
	{
		super.setName(name);
	}

	@Override
	protected void dispose()
	{
		closeStreams();
		disposeStreams();
		super.dispose();
	}

	private Set<IProcess> connectedProcesses;

	/**
	 * @param process
	 * @since 2.0
	 */
	public synchronized void connect(IProcess process)
	{
		if (connectedProcesses == null)
		{
			connectedProcesses = new HashSet<IProcess>();
		}
		if (connectedProcesses.add(process))
		{
			final IStreamsProxy proxy = process.getStreamsProxy();
			if (proxy == null)
			{
				return;
			}
			connect(proxy);
		}
	}

	/**
	 * @since 2.0
	 */
	public void connect(final IStreamsProxy proxy)
	{
		IStreamMonitor streamMonitor = proxy.getErrorStreamMonitor();
		if (streamMonitor != null)
		{
			connect(streamMonitor, IDebugUIConstants.ID_STANDARD_ERROR_STREAM);
		}
		streamMonitor = proxy.getOutputStreamMonitor();
		if (streamMonitor != null)
		{
			connect(streamMonitor, IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM);
		}

		IOConsoleInputStream input = getInputStream();
		if (input != null)
		{
			getInputStream().setColor(fColorProvider.getColor(IDebugUIConstants.ID_STANDARD_INPUT_STREAM));
		}

	}

	private List<StreamListener> fStreamListeners = new ArrayList<StreamListener>();

	/**
	 * @param streamMonitor
	 * @param idStandardErrorStream
	 */
	private void connect(IStreamMonitor streamMonitor, String streamIdentifier)
	{
		synchronized (streamMonitor)
		{
			IOConsoleOutputStream stream = newOutputStream();
			stream.setActivateOnWrite(true);
			stream.setColor(fColorProvider.getColor(streamIdentifier));
			StreamListener listener = new StreamListener(streamMonitor, stream);
			fStreamListeners.add(listener);
		}
	}

	/**
	 * cleanup method to close all of the open stream to this console
	 */
	private synchronized void closeStreams()
	{
		for (StreamListener listener : fStreamListeners)
		{
			listener.closeStream();
		}
	}

	/**
	 * disposes of the listeners for each of the stream associated with this console
	 */
	private synchronized void disposeStreams()
	{
		for (StreamListener listener : fStreamListeners)
		{
			listener.dispose();
		}
	}

	/**
	 * This class listens to a specified IO stream
	 */
	private class StreamListener implements IStreamListener
	{
		private IOConsoleOutputStream fStream;
		private IStreamMonitor fStreamMonitor;
		private boolean fFlushed = false;
		private boolean fListenerRemoved = false;

		public StreamListener(IStreamMonitor monitor,
				IOConsoleOutputStream stream)
		{
			this.fStream = stream;
			this.fStreamMonitor = monitor;
			fStreamMonitor.addListener(this);
			// fix to bug 121454. Ensure that output to fast processes is
			// processed.
			streamAppended(null, monitor);
		}

		public void streamAppended(String text, IStreamMonitor monitor)
		{
			String encoding = getEncoding();
			if (fFlushed)
			{
				try
				{
					if (fStream != null)
					{
						if (encoding == null)
						{
							fStream.write(text);
						} else
						{
							fStream.write(text.getBytes(encoding));
						}
					}
				} catch (IOException e)
				{
					VdmDebugPlugin.log(e);
				}
			} else
			{
				String contents = null;
				synchronized (fStreamMonitor)
				{
					fFlushed = true;
					contents = fStreamMonitor.getContents();
					if (fStreamMonitor instanceof IFlushableStreamMonitor)
					{
						IFlushableStreamMonitor m = (IFlushableStreamMonitor) fStreamMonitor;
						m.flushContents();
						m.setBuffered(false);
					}
				}
				try
				{
					if (contents != null && contents.length() > 0)
					{
						if (fStream != null)
						{
							fStream.write(contents);
						}
					}
				} catch (IOException e)
				{
					VdmDebugPlugin.log(e);
				}
			}
		}

		public void closeStream()
		{
			if (fStreamMonitor == null)
			{
				return;
			}
			synchronized (fStreamMonitor)
			{
				fStreamMonitor.removeListener(this);
				if (!fFlushed)
				{
					String contents = fStreamMonitor.getContents();
					streamAppended(contents, fStreamMonitor);
				}
				fListenerRemoved = true;
			}
		}

		public void dispose()
		{
			if (!fListenerRemoved)
			{
				closeStream();
			}
			fStreamMonitor = null;
			fStream = null;
		}
	}

	/**
	 * Notify listeners when name changes.
	 * 
	 * @see org.eclipse.debug.core.IDebugEventSetListener#handleDebugEvents(org.eclipse.debug.core.DebugEvent[])
	 */
	public void handleDebugEvents(DebugEvent[] events)
	{
		for (int i = 0; i < events.length; i++)
		{
			DebugEvent event = events[i];
			if (event.getSource().equals(getProcess()))
			{

				if (event.getKind() == DebugEvent.TERMINATE)
				{
					closeStreams();
					DebugPlugin.getDefault().removeDebugEventListener(this);
				}

				resetName();
			}
		}
	}

	/**
	 * resets the name of this console to the original computed name
	 */
	private void resetName()
	{

		UIJob job = new UIJob("Activating Console") { //$NON-NLS-1$
			public IStatus runInUIThread(IProgressMonitor monitor)
			{

				warnOfContentChange();
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();

	}

	/**
	 * send notification of a change of content in this console
	 */
	private void warnOfContentChange()
	{
		IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
		for (IConsole iConsole : consoles)
		{
			if (iConsole instanceof VdmDebugConsole)
			{
				VdmDebugConsole vdmC = (VdmDebugConsole) iConsole;
				vdmC.activate();
			}
		}

		// if (warn != null) {
		// ConsolePlugin.getDefault().getConsoleManager()
		// .warnOfContentChange(warn);
		// }
	}

}
