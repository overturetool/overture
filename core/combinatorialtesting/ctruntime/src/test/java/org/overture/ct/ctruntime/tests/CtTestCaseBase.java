package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.ct.ctruntime.TraceRunnerMain;
import org.overture.ct.ctruntime.tests.util.CtTestHelper;
import org.overture.ct.ctruntime.tests.util.Data;
import org.overture.ct.ctruntime.tests.util.TraceResult;
import org.overture.ct.ctruntime.tests.util.TraceResultReader;
import org.overture.test.framework.Properties;
import org.overture.test.framework.TestResourcesResultTestCase4;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@SuppressWarnings("rawtypes")
public abstract class CtTestCaseBase extends TestResourcesResultTestCase4
{
	// The socket is used to communicate with the trace interpreter
	protected ServerSocket socket;
	protected static final int SOCKET_TIMEOUT = 0;
	public static final int PORT = 8889;

	// Used a fixed trace name for simplicity
	protected static final String TRACE_NAME = "T1";

	public static final String TRACE_OUTPUT_FOLDER = "target/trace-output/";

	// Test specifications are copied to a file with proper extension before they
	// are being parsed
	protected static final String SPEC_TEMP_FILE = TRACE_OUTPUT_FOLDER
			+ "tmp.vdmsl";

	// protected CtTestHelper testHelper;
	private String[] args;
	private File traceFolder;

	public CtTestCaseBase()
	{
		super();
	}

	public CtTestCaseBase(File file, File traceFolder, String[] args)
	{
		super(file);
		this.args = args;
		this.traceFolder = traceFolder;
	}

	@Before
	public void setUp() throws Exception
	{
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
	}

	@After
	public void tearDown() throws Exception
	{
		try
		{
			if (this.socket != null)
			{
				this.socket.close();
			}
		} catch (Exception e)
		{
		}
	}

	@Test
	public void test() throws Exception
	{
		if (file == null)
		{
			return;
		}

		File actualResultsFile = computeActualResults(TRACE_NAME);

		if (Properties.recordTestResults)
		{
			try
			{
				File resultFile = createResultFile(file.getAbsolutePath());
				resultFile.getParentFile().mkdirs();

				// Overwrite result file
				Path from = actualResultsFile.toPath();
				Path to = resultFile.toPath();
				Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);

			} catch (Exception e)
			{
				Assert.fail("The produced results could not be stored: "
						+ e.getMessage());
			}
		} else
		{
			File resultFile = getResultFile(file.getAbsolutePath());

			Assert.assertTrue("No result file found for test: " + file, resultFile.exists());

			TraceResultReader reader = new TraceResultReader();
			List<TraceResult> expectedResults = reader.read(resultFile);
			List<TraceResult> actualResults = reader.read(actualResultsFile);

			Assert.assertTrue(expectedResults.size() == actualResults.size());

			int size = expectedResults.size();

			for (int i = 0; i < size; i++)
			{
				TraceResult expected = expectedResults.get(i);
				TraceResult actual = actualResults.get(i);

				Assert.assertTrue("Actual results differs from expected results for test: "
						+ file
						+ "\nExpected: "
						+ expectedResults
						+ "\n\nActual: " + actualResults, expected.equals(actual));
			}
		}
	}

	protected File createResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	protected File getResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	public File computeActualResults(final String spec) throws IOException,
			XPathExpressionException, SAXException,
			ParserConfigurationException
	{
		socket = new ServerSocket(PORT);
		socket.setSoTimeout(SOCKET_TIMEOUT);
		final Data data = new Data();

		traceFolder.mkdirs();

		String traceName = "T1";

		final File actualOutputFile = new File(traceFolder,"DEFAULT-" + traceName + ".xml");

		CtTestHelper testHelper = new CtTestHelper();
		Thread t = testHelper.consCtClientThread(socket, data);
		t.setDaemon(false);
		t.start();

		TraceRunnerMain.USE_SYSTEM_EXIT = false;
		TraceRunnerMain.main(args);

		final String message = data.getMessage();

		Assert.assertTrue("Test did not succed", message.contains("status=\"completed\" progress=\"100\""));

		return actualOutputFile;
	}

	@Override
	public void encodeResult(Object result, Document doc, Element resultElement)
	{

	}

	@Override
	public Object decodeResult(Node node)
	{
		return null;
	}

	@Override
	protected boolean assertEqualResults(Object expected, Object actual,
			PrintWriter out)
	{
		return false;
	}
}
