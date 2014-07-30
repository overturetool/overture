package ctruntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ct.ctruntime.TraceRunnerMain;

public class CtTest
{

	private static final int port = 8891;
	private static final int timeout = 0;
	
	private static final String TRACE_OUTPUT_FOLDER = "trace-output/";
	
	//"target/trace-output/T1/DEFAULT-T1.xml".replace('/', File.separatorChar)))
	class Data
	{
		String message;
	}
	
	private TraceResultReader reader;
	
	public CtTest()
	{
		this.reader = new TraceResultReader();
	}

	@Test
	public void testSlT1() throws IOException
	{
		runTest("T1", "T1.vdmsl", TRACE_OUTPUT_FOLDER + "T1");
	}

	@Test
	public void testSlT2() throws IOException
	{
		runTest("T2", "T1.vdmsl", TRACE_OUTPUT_FOLDER +"T2");
	}
	
	@Test
	public void testSlT3() throws IOException
	{
		runTest("T3", "T1.vdmsl", TRACE_OUTPUT_FOLDER +"T3");
	}

	public void runTest(final String traceName, final String spec, String output)
			throws IOException
	{
		final ServerSocket socket = new ServerSocket(port);
		socket.setSoTimeout(timeout);
		final Data data = new Data();

		final File resultFile = new File(String.format("target/trace-output/%s/DEFAULT-%s.xml", traceName, traceName).replace('/', File.separatorChar));
		
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
							System.out.println(line);

							line = line.trim();
							if (!line.isEmpty())
							{
								data.message = line;
							}

							if (line.contains("status=\"completed\" progress=\"100\""))
							{
								final OutputStream out = conn.getOutputStream();
								out.write("exit\n".getBytes());
								out.flush();
								socket.close();
							}
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally
					{
						try
						{
							conn.close();
						} catch (IOException e)
						{
						}
						try
						{
							socket.close();
						} catch (IOException e)
						{
						}
					}

				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		});
		t.setDaemon(false);
		t.start();

		// String traceName = "T1";
		File traceFolder = new File(("target/" + output).replace('/', File.separatorChar));
		traceFolder.mkdirs();
		File specfile = new File(("src/test/resources/" + spec).replace('/', File.separatorChar));
		String[] args = new String[] { "-h", "localhost", "-p", port + "",
				"-k", "whatever", "-e", "DEFAULT", "-vdmsl", "-r", "vdm10",
				"-t", traceName, "-tracefolder",
				traceFolder.toURI().toASCIIString(),
				specfile.toURI().toASCIIString() };
		TraceRunnerMain.USE_SYSTEM_EXIT = false;
		TraceRunnerMain.main(args);

		final String message = data.message;
		System.out.println("Last message: " + message);
		
		try
		{
			t.join();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		outputResults(resultFile, traceName, spec);
		
		Assert.assertTrue("Test did not succed", message.contains("status=\"completed\" progress=\"100\""));

	}
	
	private void outputResults(File resultFile, String traceName, String spec)
	{
		try
		{
			List<TraceResult> results = reader.read(resultFile);
			
			for(TraceResult result : results)
			{
				System.out.println(result);
			}
		} catch (Exception e)
		{
			System.out.println("Problems reading result file!");
			e.printStackTrace();
		}
	}
}
