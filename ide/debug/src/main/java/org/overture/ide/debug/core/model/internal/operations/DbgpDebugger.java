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
package org.overture.ide.debug.core.model.internal.operations;

import org.overture.ide.debug.core.dbgp.IDbgpFeature;
import org.overture.ide.debug.core.dbgp.IDbgpSession;
import org.overture.ide.debug.core.dbgp.IDbgpStatus;
import org.overture.ide.debug.core.dbgp.commands.IDbgpCoreCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpExtendedCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpFeatureCommands;
import org.overture.ide.debug.core.dbgp.commands.IDbgpStreamCommands;
import org.overture.ide.debug.core.dbgp.exceptions.DbgpException;
import org.overture.ide.debug.core.model.IVdmThread;

public class DbgpDebugger
{
	// Operations
	private final DbgpStepIntoOperation stepIntoOperation;

	private final DbgpStepOverOperation stepOverOperation;

	private final DbgpStepReturnOperation stepReturnOperation;

	private DbgpSuspendOperation suspendOperation;

	private final DbgpResumeOperation resumeOperation;

	private final DbgpTerminateOperation terminateOperation;

	private final IDbgpSession session;

	public DbgpDebugger(IVdmThread thread, final IDbgpDebuggerFeedback end)
	{

		this.session = thread.getDbgpSession();

		/*
		 * FIXME should use single command queue here to guarantee we handle responses in the same sequences as we send
		 * requests
		 */
		stepIntoOperation = new DbgpStepIntoOperation(thread, new DbgpOperation.IResultHandler()
		{
			public void finish(IDbgpStatus status, DbgpException e)
			{
				end.endStepInto(e, status);
			}
		});

		stepOverOperation = new DbgpStepOverOperation(thread, new DbgpOperation.IResultHandler()
		{
			public void finish(IDbgpStatus status, DbgpException e)
			{
				end.endStepOver(e, status);
			}
		});

		stepReturnOperation = new DbgpStepReturnOperation(thread, new DbgpOperation.IResultHandler()
		{
			public void finish(IDbgpStatus status, DbgpException e)
			{
				end.endStepReturn(e, status);
			}
		});

		suspendOperation = new DbgpSuspendOperation(thread, new DbgpOperation.IResultHandler()
		{
			public void finish(IDbgpStatus status, DbgpException e)
			{
				end.endSuspend(e, status);
			}
		});

		resumeOperation = new DbgpResumeOperation(thread, new DbgpOperation.IResultHandler()
		{
			public void finish(IDbgpStatus status, DbgpException e)
			{
				end.endResume(e, status);
			}
		});

		terminateOperation = new DbgpTerminateOperation(thread, new DbgpOperation.IResultHandler()
		{
			public void finish(IDbgpStatus status, DbgpException e)
			{
				end.endTerminate(e, status);
			}
		});
	}

	public void stepInto()
	{
		stepIntoOperation.schedule();
	}

	public void stepOver()
	{
		stepOverOperation.schedule();
	}

	public void stepReturn()
	{
		stepReturnOperation.schedule();
	}

	public void suspend()
	{
		suspendOperation.schedule();
	}

	public void resume()
	{
		resumeOperation.schedule();
	}

	public void terminate()
	{
		terminateOperation.schedule();
	}

	// Feature helper methods
	public IDbgpFeature getFeature(String name) throws DbgpException
	{
		IDbgpCoreCommands core = session.getCoreCommands();
		return core.getFeature(name);
	}

	public void setFeature(String name, String value) throws DbgpException
	{
		IDbgpCoreCommands core = session.getCoreCommands();
		core.setFeature(name, value);
	}

	public boolean isFeatureSupported(String name) throws DbgpException
	{
		return getFeature(name).isSupported();
	}

	public boolean getFeatureBoolean(String name) throws DbgpException
	{
		return getFeature(name).getValue().equals(IDbgpFeature.ONE_VALUE);
	}

	public void setFeatureBoolean(String name, boolean value)
			throws DbgpException
	{
		setFeature(name, value ? IDbgpFeature.ONE_VALUE
				: IDbgpFeature.ZERO_VALUE);
	}

	public int getFeatureInteger(String name) throws DbgpException
	{
		return Integer.parseInt(getFeature(name).getValue());
	}

	public void setFeatureInteger(String name, int value) throws DbgpException
	{
		setFeature(name, Integer.toString(value));
	}

	// Must available features
	public boolean isSupportsThreads() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.LANGUAGE_SUPPORTS_THREADS).getValue().equals(IDbgpFeature.ONE_VALUE);
	}

	public String getLanguageName() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.LANGUAGE_NAME).getValue();
	}

	public String getLanguageVersion() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.LANGUAGE_VERSION).getValue();
	}

	public String getEncoding() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.ENCODING).getValue();
	}

	public String getDataEncoding() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.DATA_ENCODING).getValue();
	}

	public String getProtocolVersion() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.PROTOCOL_VERSION).getValue();
	}

	public boolean isSupportsAsync() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.SUPPORTS_ASYNC).getValue().equals(IDbgpFeature.ONE_VALUE);
	}

	public String getBreakpointLanguages() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.BREAKPOINT_LANGUAGES).getValue();
	}

	public String getBreakpointTypes() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.BREAKPOINT_TYPES).getValue();
	}

	// Multiple sessions
	public boolean getMultipleSessions() throws DbgpException
	{
		return getFeature(IDbgpFeatureCommands.MULTIPLE_SESSIONS).getValue().equals(IDbgpFeature.ONE_VALUE);
	}

	public void setMultipleSessions(boolean value) throws DbgpException
	{
		setFeature(IDbgpFeatureCommands.MULTIPLE_SESSIONS, value ? IDbgpFeature.ONE_VALUE
				: IDbgpFeature.ZERO_VALUE);
	}

	// Max children
	public int getMaxChildren() throws DbgpException
	{
		return getFeatureInteger(IDbgpFeatureCommands.MAX_CHILDREN);
	}

	public void setMaxChildren(int value) throws DbgpException
	{
		setFeatureInteger(IDbgpFeatureCommands.MAX_CHILDREN, value);
	}

	// Max data
	public int getMaxData() throws DbgpException
	{
		return getFeatureInteger(IDbgpFeatureCommands.MAX_DATA);
	}

	public void setMaxData(int value) throws DbgpException
	{
		setFeatureInteger(IDbgpFeatureCommands.MAX_DATA, value);
	}

	// Max depth
	public int getMaxDepth() throws DbgpException
	{
		return getFeatureInteger(IDbgpFeatureCommands.MAX_DEPTH);
	}

	public void setMaxDepth(int value) throws DbgpException
	{
		setFeatureInteger(IDbgpFeatureCommands.MAX_DEPTH, value);
	}

	// Optional features

	// Post morten
	public boolean isPostMortenSupported() throws DbgpException
	{
		return isFeatureSupported(IDbgpFeatureCommands.SUPPORTS_POSTMORTEN);
	}

	public boolean getPostMorten() throws DbgpException
	{
		return getFeatureBoolean(IDbgpFeatureCommands.SUPPORTS_POSTMORTEN);
	}

	// Show hidden
	public boolean isShowHiddenSupported() throws DbgpException
	{
		return isFeatureSupported(IDbgpFeatureCommands.SHOW_HIDDEN);
	}

	public boolean getShowHidden() throws DbgpException
	{
		return getFeatureBoolean(IDbgpFeatureCommands.SHOW_HIDDEN);
	}

	public void setShowHidden(boolean value) throws DbgpException
	{
		setFeatureBoolean(IDbgpFeatureCommands.SHOW_HIDDEN, value);
	}

	// Notfy ok
	public boolean isNotifyOkSupported() throws DbgpException
	{
		return isFeatureSupported(IDbgpFeatureCommands.NOTIFY_OK);
	}

	public boolean getNotifyOk() throws DbgpException
	{
		return getFeatureBoolean(IDbgpFeatureCommands.NOTIFY_OK);
	}

	public void setNotifyOk(boolean value) throws DbgpException
	{
		setFeatureBoolean(IDbgpFeatureCommands.NOTIFY_OK, value);
	}

	protected void configureStdout(int value) throws DbgpException
	{
		IDbgpCoreCommands core = session.getCoreCommands();
		// TODO: error handling
		core.configureStdout(value);
	}

	// Stdout
	public void disableStdout() throws DbgpException
	{
		configureStdout(IDbgpStreamCommands.DISABLE);
	}

	public void copyStdout() throws DbgpException
	{
		configureStdout(IDbgpStreamCommands.COPY);
	}

	public void redirectStdout() throws DbgpException
	{
		configureStdout(IDbgpStreamCommands.REDIRECT);
	}

	// Stderr
	protected void configureStderr(int value) throws DbgpException
	{
		IDbgpCoreCommands core = session.getCoreCommands();
		// TODO: error handling
		core.configureStderr(value);
	}

	public void disableStderr() throws DbgpException
	{
		configureStderr(IDbgpStreamCommands.DISABLE);
	}

	public void copyStderr() throws DbgpException
	{
		configureStderr(IDbgpStreamCommands.COPY);
	}

	public void redirectStderr() throws DbgpException
	{
		configureStderr(IDbgpStreamCommands.REDIRECT);
	}

	// Stdin
	protected void configureStdin(int value) throws DbgpException
	{
		IDbgpExtendedCommands extended = session.getExtendedCommands();
		// TODO: handling
		extended.configureStdin(value);
	}

	public void disableStdin() throws DbgpException
	{
		configureStdin(IDbgpExtendedCommands.DISABLE);
	}

	public void redirectStdin() throws DbgpException
	{
		configureStdin(IDbgpExtendedCommands.REDIRECT);
	}

	public static void printEngineInfo(DbgpDebugger d) throws DbgpException
	{
		// TODO: to debug log
		System.out.println(IDbgpFeatureCommands.LANGUAGE_SUPPORTS_THREADS
				+ ": " + d.isSupportsThreads()); //$NON-NLS-1$
		System.out.println(IDbgpFeatureCommands.LANGUAGE_NAME + ": " //$NON-NLS-1$
				+ d.getLanguageName());
		System.out.println(IDbgpFeatureCommands.LANGUAGE_VERSION + ": " //$NON-NLS-1$
				+ d.getLanguageVersion());
		System.out.println(IDbgpFeatureCommands.ENCODING + ": " //$NON-NLS-1$
				+ d.getEncoding());
		System.out.println(IDbgpFeatureCommands.DATA_ENCODING + ": " //$NON-NLS-1$
				+ d.getDataEncoding());
		System.out.println(IDbgpFeatureCommands.PROTOCOL_VERSION + ": " //$NON-NLS-1$
				+ d.getProtocolVersion());
		System.out.println(IDbgpFeatureCommands.SUPPORTS_ASYNC + ": " //$NON-NLS-1$
				+ d.isSupportsAsync());
		System.out.println(IDbgpFeatureCommands.BREAKPOINT_LANGUAGES + ": " //$NON-NLS-1$
				+ d.getBreakpointLanguages());
		System.out.println(IDbgpFeatureCommands.BREAKPOINT_TYPES + ": " //$NON-NLS-1$
				+ d.getBreakpointTypes());
		System.out.println(IDbgpFeatureCommands.MULTIPLE_SESSIONS + ": " //$NON-NLS-1$
				+ d.getMultipleSessions());
		System.out.println(IDbgpFeatureCommands.MAX_CHILDREN + ": " //$NON-NLS-1$
				+ d.getMaxChildren());
		System.out.println(IDbgpFeatureCommands.MAX_DATA + ": " //$NON-NLS-1$
				+ d.getMaxData());
		System.out.println(IDbgpFeatureCommands.MAX_DEPTH + ": " //$NON-NLS-1$
				+ d.getMaxDepth());

		if (d.isPostMortenSupported())
		{
			System.out.println("Support of " //$NON-NLS-1$
					+ IDbgpFeatureCommands.SUPPORTS_POSTMORTEN
					+ ": true, value: " + d.getPostMorten()); //$NON-NLS-1$
		} else
		{
			System.out.println("Support of " //$NON-NLS-1$
					+ IDbgpFeatureCommands.SUPPORTS_POSTMORTEN + ": false"); //$NON-NLS-1$
		}

		if (d.isShowHiddenSupported())
		{
			System.out.println("Support of " + IDbgpFeatureCommands.SHOW_HIDDEN //$NON-NLS-1$
					+ ": true, value: " + d.getShowHidden()); //$NON-NLS-1$
		} else
		{
			System.out.println("Support of " + IDbgpFeatureCommands.SHOW_HIDDEN //$NON-NLS-1$
					+ ": false"); //$NON-NLS-1$
		}

		if (d.isNotifyOkSupported())
		{
			System.out.println("Support of " + IDbgpFeatureCommands.NOTIFY_OK //$NON-NLS-1$
					+ ": true, value: " + d.getNotifyOk()); //$NON-NLS-1$
		} else
		{
			System.out.println("Support of " + IDbgpFeatureCommands.NOTIFY_OK //$NON-NLS-1$
					+ ": false"); //$NON-NLS-1$
		}
	}

	public IDbgpSession getSession()
	{
		return this.session;
	}
}
