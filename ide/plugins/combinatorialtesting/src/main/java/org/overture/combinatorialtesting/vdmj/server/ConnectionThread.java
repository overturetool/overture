/*******************************************************************************
 * Copyright (c) 2009 Fujitsu Services Ltd. Author: Nick Battle This file is part of VDMJ. VDMJ is free software: you
 * can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version. VDMJ is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a
 * copy of the GNU General Public License along with VDMJ. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.overture.combinatorialtesting.vdmj.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.overture.combinatorialtesting.vdmj.server.common.Utils;
import org.overture.combinatorialtesting.vdmj.server.xml.XMLNode;
import org.overture.combinatorialtesting.vdmj.server.xml.XMLParser;
import org.overture.combinatorialtesting.vdmj.server.xml.XMLTagNode;

public class ConnectionThread extends Thread
{
	private final boolean principal;
	private final Socket socket;
	private final BufferedInputStream input;
	private final BufferedOutputStream output;

	private String id = "";
	// private long xid = 0;

	private boolean connected;
	private static boolean trace = false;
	private static boolean quiet = false;
	// private static ConnectionThread focus = null;

	private final IClientMonitor monitor;

	public ConnectionThread(ThreadGroup group, Socket conn, boolean principal,
			IClientMonitor monitor) throws IOException
	{
		super(group, null, "DBGp Connection");

		this.socket = conn;
		this.input = new BufferedInputStream(conn.getInputStream());
		this.output = new BufferedOutputStream(conn.getOutputStream());
		this.principal = principal;
		this.monitor = monitor;

		setDaemon(true);
	}

	public String getIdeId()
	{
		return id;
	}

	public static synchronized boolean setTrace()
	{
		trace = !trace;
		return trace;
	}

	public static synchronized boolean setQuiet()
	{
		quiet = !quiet;
		return quiet;
	}


	@Override
	public void run()
	{
		connected = true;
		try
		{
			if (!principal)
			{
				// runme(); // Send run command to start new thread
			}

			while (connected)
			{
				if(!receive()) // Blocking
				{
					return;
				}
			}
		} catch (SocketException e)
		{e.printStackTrace();
			monitor.traceError("Connection error: " + e.getMessage());
			// Caused by die(), and VDMJ death
		} catch (IOException e)
		{
			System.out.println("Connection exception: " + e.getMessage());

		} finally
		{
			die();
		}

		if (!principal && !quiet)
		{
			System.out.println("Thread stopped: " + this);
		}
	}

	public synchronized void die()
	{
		try
		{
			connected = false;
			socket.close();
		} catch (IOException e)
		{
			// ?
		}
	}


	private boolean receive() throws IOException
	{
		// <ascii length> \0 <XML data> \0

		int c = input.read();
		int length = 0;

		while (c >= '0' && c <= '9')
		{
			length = length * 10 + c - '0';
			c = input.read();
		}

		if (c == -1)
		{
			connected = false; // End of thread
			return false;
		}

		if (c != 0)
		{
			throw new IOException("Malformed DBGp count on " + this);
		}

		byte[] data = new byte[length];
		int offset = 0;
		int remaining = length;
		int retries = 10;
		int done = input.read(data, offset, remaining);

		while (done < remaining && --retries > 0)
		{
			Utils.milliPause(100);
			remaining -= done;
			offset += done;
			done = input.read(data, offset, remaining);
		}

		if (retries == 0)
		{
			throw new IOException("Timeout DBGp reply on thread " + this.id
					+ ", got [" + new String(data) + "]");
		}

		if (done != remaining)
		{
			throw new IOException("Short DBGp reply on thread " + this.id
					+ ", got [" + new String(data) + "]");
		}

		if (input.read() != 0)
		{
			throw new IOException("Malformed DBGp terminator on thread "
					+ this.id + ", got [" + new String(data) + "]");
		}

	return	process(data);
	}

	private boolean process(byte[] data) throws IOException
	{
		// System.out.println(new String(data));
		XMLParser parser = new XMLParser(data);
		XMLNode node = parser.readNode();

		if (trace)
		{
			System.err.println("[" + id + "] " + node); // diags!
		}

		try
		{
			XMLTagNode tagnode = (XMLTagNode) node;

			if (tagnode.tag.equals("init"))
			{
				processInit(tagnode);
				return true;
			} else
			{
				return processResponse(tagnode);
			}
		} catch (Exception e)
		{
			throw new IOException("Unexpected XML response: " + node);
		}
	}

	private boolean processResponse(XMLTagNode tagnode)
	{
		String traceName = tagnode.getAttr("tracename");
		String progress = tagnode.getAttr("progress");
		String status = tagnode.getAttr("status");

		if (monitor == null)
		{
			System.out.println("PROGRESS: " + traceName + " " + progress);
		} else if (status.equals("combinatorialtestingtart"))
		{
			monitor.traceStart(traceName);
		} else if (status.equals("progress"))
		{
			Integer p = Integer.parseInt(progress);
			monitor.progress(traceName, p);
		} else if (status.equals("error"))
		{
			String errorMessage = tagnode.getAttr("message");
			monitor.traceError(errorMessage);
		} else if (status.equals("completed"))
		{
			try
			{
				output.write("exit".getBytes());
				output.flush();
			} catch (IOException e)
			{
			}
			monitor.completed();
			monitor.terminating();
			return false;

		} 
		return true;
	}

	private void processInit(XMLTagNode tagnode)
	{
		String module = tagnode.getAttr("module");
		if (monitor == null)
		{
			System.out.println("INIT: " + module);
		} else
		{
			monitor.initialize(module);
		}
	}

}
