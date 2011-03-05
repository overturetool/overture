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
	/**
	 * If true VDMJ can be launched from outside VDMJC in debug mode.
	 * VDMJC will print the arguments to VDMJ with DBGPReader as main class
	 * and wait for it to connect.
	 */
	public static boolean VDMJ_DEBUG = false;
	private final Dialect dialect;
	private final Release release;
	private final List<File> files;
	private final String expression;
	private File logFile = null;

	private ConnectionListener listener;
	private Process process;
	private int exitCode;

	public ProcessListener(Dialect dialect, Release release, List<File> files, String expression)
	{
		this.dialect = dialect;
		this.release = release;
		this.files = files;
		this.expression = expression;

		setName("Process Listener");
		setDaemon(true);
	}

	public ProcessListener(Dialect dialect, Release release, List<File> files, String expression, File logFile)
	{
		this(dialect, release, files, expression);
		this.logFile = logFile;
	}

	@Override
    public void run()
	{
		try
		{
			listener = new ConnectionListener();
			listener.start();

			List<String> pargs = new Vector<String>();

			if (!VDMJ_DEBUG)
			{
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
			}

			pargs.add("-h");
			pargs.add("localhost");
			pargs.add("-p");
			pargs.add(Integer.toString(listener.getPort()));
			pargs.add("-k");
			pargs.add("12345678");
			pargs.add(dialect.getArgstring());
			pargs.add("-e");
			pargs.add(expression);
			if (logFile != null && dialect== Dialect.VDM_RT)
			{
				pargs.add("-log");
				pargs.add(logFile.toURI().toASCIIString());
			}
			if (release != null)
			{
				pargs.add("-r");
				pargs.add(release.toString());
			}

			for (File file: files)
			{
				pargs.add(file.toURI().toString());
			}

			if (VDMJ_DEBUG)
			{
				blockOnRemoteLaunch(pargs);
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

	private void blockOnRemoteLaunch(List<String> pargs) throws IOException,
			InterruptedException
	{
		StringBuilder sb = new StringBuilder();
		for (String string : pargs)
		{
			sb.append(string);
			sb.append(" ");
		}
		System.out.println(sb.toString());

		ProcessBuilder pb = new ProcessBuilder("help");
		process = pb.start();


		while(true)
		{
			Thread.sleep(1000);
		}
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
		if (VDMJ_DEBUG)
		{
			return false;
		}

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

	public ConnectionThread findConnection(String id)
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
