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
package org.overture.ct.ctruntime.tests;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexLocation;
import org.overture.config.Settings;
import org.overture.ct.ctruntime.TraceRunnerMain;
import org.overture.ct.ctruntime.utils.CtHelper;
import org.overture.ct.ctruntime.utils.CtHelper.CtTestData;
import org.overture.ct.ctruntime.utils.Data;
import org.overture.ct.ctruntime.utils.TraceResult;
import org.overture.ct.ctruntime.utils.TraceResultReader;
import org.overture.test.framework.Properties;
import org.overture.test.framework.TestResourcesResultTestCase4;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

@SuppressWarnings("rawtypes")
public abstract class CtTestCaseBase extends TestResourcesResultTestCase4
{
	private static final String TESTS_CT_RUNTIME_PROPERTY_PREFIX = "tests.ctruntime.override.";

	// The socket is used to communicate with the trace interpreter
	protected ServerSocket socket;
	protected static final int SOCKET_TIMEOUT = 0;
	private static final int FROM_PORT = 10000;
	private static final int TO_PORT = 50000;

	// Used a fixed trace name for simplicity
	protected static final String TRACE_NAME = "T1";

	public static final String TRACE_OUTPUT_FOLDER = "target/trace-output/";

	private File traceFolder;
	private CtTestData testdata;
	
	

	public CtTestCaseBase()
	{
		super();
	}

	public CtTestCaseBase(File file, File traceFolder, CtTestData args)
	{
		super(file);

		this.testdata = args;
		this.traceFolder = traceFolder;
	}

	@Before
	public void internalSetup() throws Exception
	{
		setUp();
		
	}

	abstract public void setUp() throws Exception;

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

		try
		{
			configureResultGeneration();

			File actualResultsFile = computeActualResults(TRACE_NAME);

			if (Properties.recordTestResults)
			{
				try
				{
					File resultFile = createResultFile(file.getAbsolutePath());
					resultFile.getParentFile().mkdirs();

					// Overwrite result file
					FileUtils.copyFile(actualResultsFile, resultFile);

				} catch (Exception e)
				{
					Assert.fail("The produced results could not be stored: "
							+ e.getMessage());
				}
			} else
			{
				File resultFile = getResultFile(file.getAbsolutePath());

				TraceResultReader reader = new TraceResultReader();
				List<TraceResult> actualResults = reader.read(actualResultsFile);

				Assert.assertTrue("No result file found for test: " + file
						+ "\n\n" + actualResults, resultFile.exists());
				List<TraceResult> expectedResults = reader.read(resultFile);

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
		} finally
		{
			unconfigureResultGeneration();
		}

	}

	@Override
	protected File createResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename)
	{
		return new File(filename + ".result");
	}

	public File computeActualResults(final String spec) throws IOException,
			XPathExpressionException, SAXException,
			ParserConfigurationException
	{
		int port =findAvailablePort(FROM_PORT, TO_PORT);
		socket = new ServerSocket(port);
		socket.setSoTimeout(SOCKET_TIMEOUT);
		final Data data = new Data();

		traceFolder.mkdirs();

		String traceName = "T1";

		String actualOutputFileName = (Settings.dialect == Dialect.VDM_SL ? "DEFAULT-"
				: "Entry-")
				+ traceName + ".xml";

		final File actualOutputFile = new File(traceFolder, actualOutputFileName);

		CtHelper testHelper = new CtHelper();
		Thread t = testHelper.consCtClientThread(socket, data);
		t.setDaemon(false);
		t.start();

		TraceRunnerMain.USE_SYSTEM_EXIT = false;
		
		this.testdata.port = port;
		
		
		String[] args = testHelper.buildArgs(Settings.dialect, Settings.release, testdata);
		
		TraceRunnerMain.main(args);

		final String message = data.getMessage();

		Assert.assertTrue("Test did not succed. Are you sure that it contains "+ (Settings.dialect==Dialect.VDM_SL?"'DEFAULT`T1'" : "'Entry`T1'"), message.contains("status=\"completed\" progress=\"100\""));

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

	protected void configureResultGeneration()
	{
		LexLocation.absoluteToStringLocation = false;
		if (System.getProperty(TESTS_CT_RUNTIME_PROPERTY_PREFIX + "all") != null
				|| getPropertyId() != null
				&& System.getProperty(TESTS_CT_RUNTIME_PROPERTY_PREFIX
						+ getPropertyId()) != null)
		{
			Properties.recordTestResults = true;
		}

	}

	protected void unconfigureResultGeneration()
	{
		Properties.recordTestResults = false;
	}

	protected abstract String getPropertyId();
	
	
	
	public static int findAvailablePort(int fromPort, int toPort)
	{
		if (fromPort > toPort)
		{
			throw new IllegalArgumentException("startPortShouldBeLessThanOrEqualToEndPort");
		}

		int port = fromPort;
		ServerSocket socket = null;
		while (port <= toPort)
		{
			try
			{
				socket = new ServerSocket(port);
				return port;
			} catch (IOException e)
			{
				++port;
			} finally
			{
				if (socket != null)
				{
					try
					{
						socket.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		return -1;
	}
}
