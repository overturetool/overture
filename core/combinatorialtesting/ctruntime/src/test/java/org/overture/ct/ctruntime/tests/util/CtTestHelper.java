/*
 * #%~
 * Combinatorial Testing Runtime
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
package org.overture.ct.ctruntime.tests.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CtTestHelper
{
	public String[] buildArgs(final String traceName, final int port,
			File traceFolder, File specfile)

	{
		// Passing 'null' indicates no trace reduction
		return buildArgs(traceName, port, traceFolder, specfile, null);
	}

	public String[] buildArgs(final String traceName, final int port,
			File traceFolder, File specfile, TraceReductionInfo info)
	{
		if (info == null)
		{
			info = new TraceReductionInfo();
		}

		String[] args = new String[] {
				"-h",
				"localhost",
				"-p",
				port + "",
				"-k",
				"whatever",
				"-e",
				"DEFAULT",
				"-vdmsl",
				"-r",
				"vdm10",
				"-t",
				traceName,
				"-tracefolder",
				traceFolder.toURI().toASCIIString(),
				specfile.toURI().toASCIIString(),
				"-traceReduction",
				"{" + info.getSubset() + ","
						+ info.getReductionType().toString() + ","
						+ info.getSeed() + "}" };
		return args;
	}

	public Thread consCtClientThread(final ServerSocket socket, final Data data)
	{
		Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Socket conn = null;
					try
					{
						conn = socket.accept();
						BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));

						String line = null;
						while ((line = input.readLine()) != null)
						{
							line = line.trim();

							if (!line.isEmpty())
							{
								data.setMessage(line);
							}

							final String TRACE_COMPLETE_MESSAGE = "status=\"completed\" progress=\"100\"";

							if (line.contains(TRACE_COMPLETE_MESSAGE))
							{
								final OutputStream out = conn.getOutputStream();
								final String SIGNAL_EXIT = "exit\n";
								out.write(SIGNAL_EXIT.getBytes());
								out.flush();
								socket.close();
							}
						}
					} catch (IOException e)
					{
						// e.printStackTrace();
					} finally
					{
						try
						{
							if (conn != null)
							{
								conn.close();
							}
						} catch (IOException e)
						{
							// e.printStackTrace();
						}
						try
						{
							socket.close();
						} catch (IOException e)
						{
							// e.printStackTrace();
						}
					}

				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

		return t;
	}
}
