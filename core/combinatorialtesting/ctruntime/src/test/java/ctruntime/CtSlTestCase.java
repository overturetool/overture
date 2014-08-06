package ctruntime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Assert;
import org.junit.Assume;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.ct.ctruntime.TraceRunnerMain;
import org.overture.test.framework.BaseTestCase;
import org.overture.test.framework.Properties;
import org.xml.sax.SAXException;

public class CtSlTestCase extends BaseTestCase
{
	public CtSlTestCase()
	{
		super();
	}
	
	public CtSlTestCase(File file)
	{
		super(file);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		try
		{
			this.thisSocket.close();
		} catch (Exception e)
		{
			// ignore
		}
	}
	
	@Override
	public void test() throws Exception
	{
		if(content == null)
		{
			return;
		}
		
		String filename = file.getAbsolutePath();
		
		File actualResultsFile = computeActualResults(TRACE_NAME, filename);

		if (Properties.recordTestResults)
		{
			try
			{
				File resultFile = createResultFile(filename);
				resultFile.getParentFile().mkdirs();

				Path from = actualResultsFile.toPath();
				Path to = resultFile.toPath();
				
				Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
				
			} catch (Exception e)
			{
				Assert.fail("The produced results could not be stored: "
						+ e.getMessage());
			}
		}
		else
		{
			File resultFile = getResultFile(filename);
			
			if (!resultFile.exists())
			{
				Assume.assumeTrue("no result file", false);
			}

			List<TraceResult> expectedResults = reader.read(resultFile);
			List<TraceResult> actualResults = reader.read(actualResultsFile);
			
			Assert.assertTrue(expectedResults.size() == actualResults.size());

			int size = expectedResults.size();
			
			for(int i = 0; i < size; i++)
			{
				TraceResult expected = expectedResults.get(i);
				TraceResult actual = actualResults.get(i);
				
				System.out.println("Comparing: " + expected + "\t" + actual);

				Assert.assertTrue(expected.equals(actual));
			}
		}
	}

	@Override
	public String getName()
	{
		return this.content;
	}

	protected void storeResult(File file, String result)
			throws FileNotFoundException
	{
		PrintStream out = new PrintStream(new FileOutputStream(file));
		out.print(result);
		out.close();
	}

	protected File createResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	protected File getResultFile(String filename)
	{
		return new File(filename + ".result");
	}
	
	private ServerSocket thisSocket;
	private final String TRACE_NAME = "T1";
	private static final int timeout = 0;
	
	class Data
	{
		String message;
	}
	
	public File computeActualResults(final String traceName, final String spec)
			throws IOException, XPathExpressionException, SAXException,
			ParserConfigurationException
	{
		
		File specfile = new File(spec.replace('/', File.separatorChar));
		
		Path specFilePath = specfile.toPath();
		File specFileWithExt = new File("target/tmp.vdmsl".replace('/', File.separatorChar));
		specFileWithExt.mkdirs();
		Path toPath = specFileWithExt.toPath();
		Files.copy(specFilePath, toPath, StandardCopyOption.REPLACE_EXISTING);
		
		final int port = 8889;
		thisSocket = new ServerSocket(port);
		thisSocket.setSoTimeout(timeout);
		final Data data = new Data();

		String traceOutputFolder = "target/trace-output/";
		File traceFolder = new File((traceOutputFolder + traceName).replace('/', File.separatorChar));
		traceFolder.getParentFile().mkdirs();
		
		String outputStrPath = String.format(traceOutputFolder + traceName + "/DEFAULT-" + traceName + ".xml").replace('/', File.separatorChar);
		final File actualOutputFile = new File(outputStrPath.replace('/', File.separatorChar));
		actualOutputFile.getParentFile().mkdirs();

		Thread t = getCtClient(thisSocket, data);
		t.setDaemon(false);
		t.start();

		String[] args = buildArgs(traceName, port, traceFolder, specFileWithExt);
		TraceRunnerMain.USE_SYSTEM_EXIT = false;
		TraceRunnerMain.main(args);

		final String message = data.message;
		System.out.println("Last message: " + message);

		//outputResults(resultFile, traceName, spec);

		Assert.assertTrue("Test did not succed", message.contains("status=\"completed\" progress=\"100\""));

		return actualOutputFile;
	}
	
	private Thread getCtClient(final ServerSocket socket, final Data data)
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
		return t;
	}
	

	private String[] buildArgs(final String traceName, final int port,
			File traceFolder, File specfile)
	{
		String[] args = new String[] { "-h", "localhost", "-p", port + "",
				"-k", "whatever", "-e", "DEFAULT", "-vdmsl", "-r", "vdm10",
				"-t", traceName, "-tracefolder",
				traceFolder.toURI().toASCIIString(),
				specfile.toURI().toASCIIString() };
		return args;
	}
	
	private TraceResultReader reader = new TraceResultReader();
	
//	private void outputResults(File resultFile, String traceName, String spec)
//			throws XPathExpressionException, SAXException, IOException,
//			ParserConfigurationException
//	{
//		if (!resultFile.exists())
//		{
//			Assume.assumeTrue("no result file", false);
//		}
//
//		List<TraceResult> results = reader.read(resultFile);
//
//		for (TraceResult result : results)
//		{
//			System.out.println(result);
//		}
//	}
}
