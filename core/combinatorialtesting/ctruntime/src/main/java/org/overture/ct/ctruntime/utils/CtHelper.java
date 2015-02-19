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
package org.overture.ct.ctruntime.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;

public class CtHelper
{
	public static class CtTestData
	{
		public final String traceName;
		public final int port;
		public final File traceFolder;
		public final File specFile;
		public TraceReductionInfo reduction;

		public CtTestData(String traceName, int port, File traceFolder,
				File specFile, TraceReductionInfo second)
		{
			this.traceName = traceName;
			this.port = port;
			this.traceFolder = traceFolder;
			this.specFile = specFile;
			this.reduction = second;

		}
	}

	public String[] buildArgs(Dialect dialect, Release release, CtTestData data)
	{
		if (data.reduction == null)
		{
			data.reduction= new TraceReductionInfo();
		}

		String[] args = new String[] {
				"-h",
				"localhost",
				"-p",
				data.port + "",
				"-k",
				"whatever",
				"-e",
				dialect == Dialect.VDM_SL ? "DEFAULT" : "Entry",
				dialect.getArgstring(),
				"-r",
				"vdm10",
				"-t",
				data.traceName,
				"-tracefolder",
				data.traceFolder.toURI().toASCIIString(),
				data.specFile.toURI().toASCIIString(),
				"-traceReduction",
				"{" + data.reduction.getSubset() + ","
						+ data.reduction.getReductionType().toString() + ","
						+ data.reduction.getSeed() + "}" };
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
