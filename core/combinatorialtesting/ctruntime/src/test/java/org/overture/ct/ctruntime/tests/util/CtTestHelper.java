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
		//Passing 'null' indicates no trace reduction
		return buildArgs(traceName, port, traceFolder, specfile, null);
	}
	
	public String[] buildArgs(final String traceName, final int port,
			File traceFolder, File specfile, TraceReductionInfo info)
	{
		if(info == null)
		{
			info = new TraceReductionInfo();
		}
		
		String[] args = new String[] { "-h", "localhost", "-p", port + "",
				"-k", "whatever", "-e", "DEFAULT", "-vdmsl", "-r", "vdm10",
				"-t", traceName, "-tracefolder",
				traceFolder.toURI().toASCIIString(),
				specfile.toURI().toASCIIString(), "-traceReduction", "{" + info.getSubset() + "," + info.getReductionType().toString() + "," + info.getSeed() + "}"};
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
						e.printStackTrace();
					} finally
					{
						try
						{
							conn.close();
						} catch (IOException e)
						{
							e.printStackTrace();
						}
						try
						{
							socket.close();
						} catch (IOException e)
						{
							e.printStackTrace();
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
