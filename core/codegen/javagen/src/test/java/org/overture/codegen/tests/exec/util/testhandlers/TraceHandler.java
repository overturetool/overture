package org.overture.codegen.tests.exec.util.testhandlers;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.runtime.traces.TestAccumulator;
import org.overture.codegen.tests.exec.util.ExecutionResult;
import org.overture.config.Release;
import org.overture.ct.ctruntime.TraceRunnerMain;
import org.overture.ct.ctruntime.utils.CtHelper;
import org.overture.ct.ctruntime.utils.Data;
import org.overture.ct.ctruntime.utils.TraceReductionInfo;
import org.overture.ct.ctruntime.utils.TraceResult;
import org.overture.ct.ctruntime.utils.TraceResultReader;
import org.overture.ct.ctruntime.utils.TraceTest;
import org.xml.sax.SAXException;

public class TraceHandler extends ExecutableSpecTestHandler
{
	// The socket is used to communicate with the trace interpreter
	protected ServerSocket socket;
	protected static final int SOCKET_TIMEOUT = 0;

	public TraceHandler(Release release, Dialect dialect)
	{
		super(release, dialect);
	}

	public String getTraceName()
	{
		return "T1";
	}
	
	@Override
	public String getVdmEntry()
	{
		return getTraceName();
	}

	@Override
	public String getJavaEntry()
	{
		return "computeTests()";
	}
	
	public int getFreePort()
	{
		ServerSocket s = null;
		try
		{
			s = new ServerSocket(0);
			
			// returns the port the system selected
			return s.getLocalPort();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(s != null)
			{
				try
				{
					s.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return 8999;
	}

	public List<String> getMainClassMethods()
	{
		String computeTestsMethod = 
				" public static TestAccumulator computeTests()\n"
				+ " {\n"
				+ "    InMemoryTestAccumulator acc = new InMemoryTestAccumulator();\n"
				+ "    new Entry().Entry_T1_Run(acc);\n"
				+ "    return acc;\n"
				+ " }\n";

		List<String> methods = new LinkedList<String>();
		methods.add(computeTestsMethod);

		return methods;
	}

	@Override
	public ExecutionResult interpretVdm(File intputFile) throws Exception
	{
		File vdmTraceResultFile = computeVdmTraceResult(intputFile);

		List<TraceResult> testResult = new TraceResultReader().read(vdmTraceResultFile);

		if (testResult.size() != 1 || !testResult.get(0).traceName.equals(getTraceName()))
		{
			Logger.getLog().printErrorln("Expected a single trace named " + getTraceName() + "!. Got: "
					+ testResult);
			return null;
		}

		TraceResult t1 = testResult.get(0);
		
		StringBuilder sb = new StringBuilder();
		
		for(TraceTest t : t1.tests)
		{
			sb.append(t).append('\n');
		}
		
		return new ExecutionResult(sb.toString(), t1.tests);
	}

	@Override
	public ExecutionResult runJava(File folder)
	{
		ExecutionResult javaResult = super.runJava(folder);

		Object executionResult = javaResult.getExecutionResult();

		if(executionResult instanceof TestAccumulator)
		{
			TestAccumulator acc = (TestAccumulator) executionResult;

			return new ExecutionResult(javaResult.getStrRepresentation(), acc.getAllTests());
		}
		else
		{
			return new ExecutionResult(javaResult.getStrRepresentation(), javaResult.getExecutionResult().toString());
		}
	}

	public File computeVdmTraceResult(File specFile) throws IOException,
			XPathExpressionException, SAXException,
			ParserConfigurationException
	{
		// The client thread will close the socket
		int port = getFreePort();
		socket = new ServerSocket(port);
		socket.setSoTimeout(SOCKET_TIMEOUT);
		final Data data = new Data();

		File traceFolder = new File("target" + File.separatorChar + "cgtest"
				+ File.separatorChar + "traceoutput");

		traceFolder.mkdirs();

		final File actualOutputFile = new File(traceFolder, "Entry-"
				+ getVdmEntry() + ".xml");

		CtHelper testHelper = new CtHelper();
		Thread t = testHelper.consCtClientThread(socket, data);
		t.setDaemon(false);
		t.start();

		File destFile = new File(traceFolder, specFile.getName() + ".vdmpp");
		FileUtils.copyFile(specFile, destFile);

		TraceRunnerMain.USE_SYSTEM_EXIT = false;
		TraceReductionInfo info = new TraceReductionInfo();

		String[] buildArgs = new String[] {
				"-h",
				"localhost",
				"-p",
				port + "",
				"-k",
				"whatever",
				"-e",
				"Entry",
				"-vdmpp",
				"-r",
				"vdm10",
				"-t",
				getVdmEntry(),
				"-tracefolder",
				traceFolder.toURI().toASCIIString(),
				destFile.toURI().toASCIIString(),
				"-traceReduction",
				"{" + info.getSubset() + ","
						+ info.getReductionType().toString() + ","
						+ info.getSeed() + "}" };

		TraceRunnerMain.main(buildArgs);

		final String message = data.getMessage();

		Assert.assertTrue("Test did not succeed", message.contains("status=\"completed\" progress=\"100\""));

		return actualOutputFile;
	}
}
