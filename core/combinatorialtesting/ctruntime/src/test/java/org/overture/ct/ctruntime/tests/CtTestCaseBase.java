package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
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
import org.overture.ct.ctruntime.tests.util.CtTestHelper;
import org.overture.ct.ctruntime.tests.util.Data;
import org.overture.ct.ctruntime.tests.util.TraceResult;
import org.overture.ct.ctruntime.tests.util.TraceResultReader;
import org.overture.test.framework.BaseTestCase;
import org.overture.test.framework.Properties;
import org.xml.sax.SAXException;

public abstract class CtTestCaseBase extends BaseTestCase
{
	//The socket is used to communicate with the trace interpreter
	protected ServerSocket thisSocket;
	protected static final int SOCKET_TIMEOUT = 0;
	protected static final int PORT = 8889;

	//Used a fixed trace name for simplicity
	protected static final String TRACE_NAME = "T1";
	
	protected static final String TRACE_OUTPUT_FOLDER = "target/trace-output/";
	
	//Test specifications are copied to a file with proper extension before they
	//are being parsed
	protected static final String SPEC_TEMP_FILE = TRACE_OUTPUT_FOLDER + "tmp.vdmsl";
	
	protected CtTestHelper testHelper;
	
	public CtTestCaseBase()
	{
		super();
	}
	
	public CtTestCaseBase(File file)
	{
		super(file);
	}
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		this.testHelper = new CtTestHelper();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}
	
	@Override
	protected void tearDown() throws Exception
	{
		this.thisSocket.close();
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

				//Overwrite result file
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
				Assume.assumeTrue("No result file", false);
			}

			TraceResultReader reader = new TraceResultReader();
			List<TraceResult> expectedResults = reader.read(resultFile);
			List<TraceResult> actualResults = reader.read(actualResultsFile);
			
			Assert.assertTrue(expectedResults.size() == actualResults.size());

			int size = expectedResults.size();
			
			for(int i = 0; i < size; i++)
			{
				TraceResult expected = expectedResults.get(i);
				TraceResult actual = actualResults.get(i);
				
				Assert.assertTrue(expected.equals(actual));
			}
		}
	}

	@Override
	public String getName()
	{
		return this.content;
	}

	protected File createResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	protected File getResultFile(String filename)
	{
		return new File(filename + ".result");
	}
	
	public File computeActualResults(final String traceName, final String spec)
			throws IOException, XPathExpressionException, SAXException,
			ParserConfigurationException
	{
		File specfile = new File(spec.replace('/', File.separatorChar));
		
		Path specFilePath = specfile.toPath();
		File specFileWithExt = new File(SPEC_TEMP_FILE.replace('/', File.separatorChar));
		specFileWithExt.mkdirs();
		Path toPath = specFileWithExt.toPath();
		Files.copy(specFilePath, toPath, StandardCopyOption.REPLACE_EXISTING);
		
		thisSocket = new ServerSocket(PORT);
		thisSocket.setSoTimeout(SOCKET_TIMEOUT);
		final Data data = new Data();

		File traceFolder = new File((TRACE_OUTPUT_FOLDER + traceName).replace('/', File.separatorChar));
		traceFolder.getParentFile().mkdirs();
		
		String outputStrPath = String.format(TRACE_OUTPUT_FOLDER + traceName + "/DEFAULT-" + traceName + ".xml").replace('/', File.separatorChar);
		final File actualOutputFile = new File(outputStrPath.replace('/', File.separatorChar));
		actualOutputFile.getParentFile().mkdirs();

		Thread t = testHelper.consCtClientThread(thisSocket, data);
		t.setDaemon(false);
		t.start();

		String[] args = getArgs(traceName, traceFolder, specFileWithExt);
		TraceRunnerMain.USE_SYSTEM_EXIT = false;
		TraceRunnerMain.main(args);

		final String message = data.getMessage();

		Assert.assertTrue("Test did not succed", message.contains("status=\"completed\" progress=\"100\""));

		return actualOutputFile;
	}
	
	abstract public String[] getArgs(String traceName, File traceFolder, File specFileWithExt);
}
