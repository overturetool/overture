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

package org.overturetool.vdmjc.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmjc.common.Utils;
import org.overturetool.vdmjc.config.Config;


public class ProcessListener extends Thread
{
	private final Dialect dialect;
	private final List<File> files;
	private final String expression;

	private ConnectionListener listener;
	private Process process;
	private int exitCode;

	public ProcessListener(Dialect dialect, List<File> files, String expression)
	{
		this.dialect = dialect;
		this.files = files;
		this.expression = expression;

		setName("Process Listener");
		setDaemon(true);
	}

	@Override
    public void run()
	{
		try
		{
			listener = new ConnectionListener();
			listener.start();

			List<String> pargs = new Vector<String>();

			pargs.add("java");
			pargs.add("-cp");
			pargs.add(Config.vdmj_jar);

			if (Config.vdmj_jvm.length() > 0)
			{
    			for (String a: Config.vdmj_jvm.split("\\s+"))
    			{
    				pargs.add(a);
    			}
			}

			pargs.add("org.overturetool.vdmj.debug.DBGPReader");

			pargs.add("-h");
			pargs.add("localhost");
			pargs.add("-p");
			pargs.add(Integer.toString(listener.getPort()));
			pargs.add("-k");
			pargs.add("12345678");
			pargs.add(dialect.argstring);
			pargs.add("-e");
			pargs.add(expression);

			for (File file: files)
			{
				pargs.add(file.toURI().toString());
			}

    		ProcessBuilder pb = new ProcessBuilder(pargs);
			process = pb.start();

			BufferedInputStream stdout =
				new BufferedInputStream(process.getInputStream());
			BufferedInputStream stderr =
				new BufferedInputStream(process.getErrorStream());

			StringBuilder stdoutline = new StringBuilder();
			StringBuilder stderrline = new StringBuilder();

			while (!hasEnded())
			{
				poll(stdout, stdoutline);
				poll(stderr, stderrline);
				Utils.milliPause(100);
			}

			poll(stdout, stdoutline);
			poll(stderr, stderrline);
		}
		catch (SocketException e)
		{
			// Killed by die() or VDMJ crashed
		}
		catch (IOException e)
		{
			if (!e.getMessage().equals("Stream closed"))
			{
				CommandLine.message("VDMJ process exception: " + e);
			}
		}
		catch (Exception e)
		{
			CommandLine.message("VDMJ process exception: " + e);
		}

		CommandLine.message("");	// Refresh prompt
		die();
	}

	public synchronized boolean waitStarted()
	{
		while (process == null)
		{
			Utils.milliPause(100);
		}

		return !hasEnded();
	}

	public synchronized int waitEnded()
	{
		while (!hasEnded())
		{
			Utils.pause(1);
		}

		return exitCode;
	}

	public synchronized boolean hasEnded()
	{
		if (process == null)
		{
			return true;
		}
		else
		{
			try
            {
	            exitCode = process.exitValue();
	            return true;
            }
            catch (IllegalThreadStateException e)
            {
            	return false;
            }
		}
	}

	public synchronized void die()
	{
		if (process != null)
		{
			process.destroy();
			process = null;
		}

		if (listener != null)
		{
			listener.die();
			listener = null;
		}
	}

	private void poll(InputStream is, StringBuilder line) throws IOException
	{
		while (is.available() > 0)
		{
			int c = is.read();

			if (c == '\n')
			{
				CommandLine.message(line.toString());
				line.setLength(0);
			}
			else if (c != -1 && c != '\r')
			{
				line.append((char)c);
			}
		}
	}

	public ConnectionThread findConnection(long id)
	{
		return hasEnded() ? null : listener.findConnection(id);
	}

	public ConnectionThread[] getConnections()
	{
		return listener == null ?
			new ConnectionThread[0] : listener.getConnections();
	}

	public ConnectionThread getPrincipal()
	{
		return listener == null ?
			null : listener.getPrincipal();
	}
}
