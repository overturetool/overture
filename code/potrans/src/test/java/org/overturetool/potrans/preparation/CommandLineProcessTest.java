/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * @author miguel_ferreira
 * 
 */
public class CommandLineProcessTest extends TestCase {

	private final static String newLine = System.getProperty("line.separator");

	private static final String VPPDE_PROPERTY_ERROR_MESSAGE = "You have to set the flag -DvppdeExecutable=<path to executable> for the JVM in"
			+ " the JUnit launch configuration.";

	private static String vppdeExecutable = System
			.getProperty("vppdeExecutable");;

	private static String settingsWarning = "If this test fails check that you set the property "
			+ "\"-DvppdeExecutable=</path/to/vdmtools/bin/>vppde\" in the JVM arguments.";

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		InputValidator.validateStringNotEmptyNorNull(vppdeExecutable,
				VPPDE_PROPERTY_ERROR_MESSAGE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#executeProcessAndWaitForItToFinish()}
	 * .
	 */
	public void testExecuteProcessAndWaitForItToFinish() throws Exception {
		String inputString = "This is a test";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { inputString });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);

		int exitValue = cmdLineProcess.executeProcessAndWaitForItToFinish();

		assertEquals(0, exitValue);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#executeProcessAndWaitForItToFinish()}
	 * .
	 */
	public void testExecuteProcessAndWaitForItToFinishInvalidCommand()
			throws Exception {
		String invalidCommand = "invalid_command";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { invalidCommand });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);

		try {
			cmdLineProcess.executeProcessAndWaitForItToFinish();
		} catch (IOException e) {
			assertEquals(invalidCommand + ": not found", e.getMessage());
		} finally {
			cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#executeProcessAndWaitForItToFinish()}
	 * .
	 */
	public void testExecuteProcessAndWaitForItToFinishEmptyCommand()
			throws Exception {
		String emptyCommand = "";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { emptyCommand });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);

		try {
			cmdLineProcess.executeProcessAndWaitForItToFinish();
		} catch (IOException e) {
			assertEquals(emptyCommand + ": not found", e.getMessage());
		} finally {
			cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#executeProcess()}
	 * .
	 */
	public void testExecuteProcess() throws Exception {
		String waitCommand;
		if (System.getProperty("os.name").startsWith("Windows"))
			waitCommand = "PAUSE";
		else
			waitCommand = "read";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { waitCommand });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);

		cmdLineProcess.executeProcess();

		try {
			cmdLineProcess.getExitValue();
		} catch (IllegalThreadStateException e) {
			assertEquals("process hasn't exited", e.getMessage());
		} finally {
			if(!cmdLineProcess.isFinished())
				cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#executeProcess()}
	 * .
	 */
	public void testExecuteProcessInvalidCommand() throws Exception {
		String invalidCommand = "invalid_command";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { invalidCommand });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);

		try {
			cmdLineProcess.executeProcess();
		} catch (IOException e) {
			assertEquals(invalidCommand + ": not found", e.getMessage());
		} finally {
			if(!cmdLineProcess.isFinished())
				cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#executeProcess()}
	 * .
	 */
	public void testExecuteProcessEmptyCommand() throws Exception {
		String emptyCommand = "";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { emptyCommand });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);

		try {
			cmdLineProcess.executeProcess();
		} catch (IOException e) {
			assertEquals(emptyCommand + ": not found", e.getMessage());
		} finally {
			if(!cmdLineProcess.isFinished())
				cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getProcessOutput()}
	 * .
	 */
	public void testGetProcessOutput() throws Exception {
		String inputString = "This is a test";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { inputString });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);
		cmdLineProcess.executeProcessAndWaitForItToFinish();

		String actual = cmdLineProcess.getProcessOutput();

		assertEquals(inputString, actual.trim());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getProcessError()}
	 * .
	 */
	public void testGetProcessError() throws Exception {
		String invalidFlag = "-invalid_flag";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				vppdeExecutable, new String[] { invalidFlag });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);
		cmdLineProcess.executeProcessAndWaitForItToFinish();

		String actual = cmdLineProcess.getProcessError();

		assertTrue(settingsWarning, cmdLineProcess.getExitValue() != 0);
		assertTrue(settingsWarning, actual
				.contains("Usage: vppde [options] [specfiles]"));
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#setProcessInput(java.lang.String)}
	 * .
	 * 
	 * Miguel: This test will probably fail on Windows. If it does please
	 * contact me and supply the output from JUnit.
	 */
	public void testSetProcessInput() throws Exception {
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				vppdeExecutable);
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);
		cmdLineProcess.executeProcess();

		cmdLineProcess.setProcessInput(new CommandLineProcessStringInput("quit" + newLine));
		cmdLineProcess.waitFor();

		assertEquals(settingsWarning, 0, cmdLineProcess.getExitValue());
	}
	
	public void testSetProcessInputOutputInterleaved() throws Exception {
		String expected = "The VDM++ Toolbox v8.1.1b - Fri 24-Oct-2008 08:59:25" + newLine
						+ "vpp> "
						+ "backtrace (bt)            init (i)                  tcov read file            " + newLine
						+ "break (b)                 dlclose (dlc)             tcov write file           " + newLine
						+ "classes                   disable (dis) ident       tcov reset                " + newLine
						+ "codegen (cg) class [opt]  enable (ena) ident        script file               " + newLine
						+ "cont (c)                  instvars class            selthread                 " + newLine
						+ "cquit                     javacg (jcg) class [opt]  set option                " + newLine
						+ "create (cr)               last                      singlestep (g)            " + newLine
						+ "curthread                 latex (l) opt ident       step (s)                  " + newLine
						+ "debug (d) expr            next (n)                  stepin (si)               " + newLine
						+ "delete (dl) ident,...     objects                   system (sys)              " + newLine
						+ "destroy obj               operations class          threads                   " + newLine
						+ "dir path,...              priorityfile (pf)         typecheck (tc) class opt  " + newLine
						+ "encode                    previous (pr)             types class               " + newLine
						+ "fscode                    print (p) expr,..         unset option              " + newLine
						+ "finish                    pwd                       values class              " + newLine
						+ "first (f)                 quit (q)                  version (v)               " + newLine
						+ "functions class           read (r) file             " + newLine
						+ "info (?) command          rtinfo ident              " + newLine
						+ "vpp> ";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				vppdeExecutable);
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);
		cmdLineProcess.executeProcess();
		cmdLineProcess.setProcessInput(new CommandLineProcessStringInput("help" + newLine));
		cmdLineProcess.setProcessInput(new CommandLineProcessStringInput("quit" + newLine));
		cmdLineProcess.waitFor();

		assertEquals(settingsWarning, 0, cmdLineProcess.getExitValue());
		assertEquals(settingsWarning, expected, cmdLineProcess.getProcessOutput());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getProcessOutputFromStream(java.io.InputStream)}
	 * .
	 */
	public void testGetProcessOutputFromStream() throws Exception {
		String expected = "This is a test.";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(expected
				.getBytes());

		String actual = CommandLineProcess
				.getProcessOutputFromStream(inputStream);

		assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getProcessOutputFromStream(java.io.InputStream)}
	 * .
	 */
	public void testGetProcessOutputFromStreamNullInputStream()
			throws Exception {
		ByteArrayInputStream inputStream = null;

		String actual = CommandLineProcess
				.getProcessOutputFromStream(inputStream);

		assertEquals("", actual);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getTextFromStreamToStringBuffer(java.io.InputStream, java.lang.StringBuffer)}
	 * .
	 */
	public void testGetTextFromStreamToStringBuffer() throws Exception {
		String expected = "This is a test.";
		ByteArrayInputStream inputStream = new ByteArrayInputStream(expected
				.getBytes());
		StringBuffer text = new StringBuffer();

		CommandLineProcess.getTextFromStreamToStringBuffer(inputStream, text);

		assertEquals(expected, text.toString());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getTextFromStreamToStringBuffer(java.io.InputStream, java.lang.StringBuffer)}
	 * .
	 */
	public void testGetTextFromStreamToStringBufferNullInputStream()
			throws Exception {
		ByteArrayInputStream inputStream = null;
		StringBuffer text = new StringBuffer();

		CommandLineProcess.getTextFromStreamToStringBuffer(inputStream, text);

		assertEquals("", text.toString());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getTextFromStreamToStringBuffer(java.io.InputStream, java.lang.StringBuffer)}
	 * .
	 */
	public void testGetTextFromStreamToStringBufferNullTextBuffer()
			throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				"This is a test.".getBytes());
		StringBuffer text = null;

		CommandLineProcess.getTextFromStreamToStringBuffer(inputStream, text);

		assertNull(text);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getExitValue()}
	 * .
	 */
	public void testGetExitValue() throws Exception {
		String inputString = "This is a test";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { inputString });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);

		int expected = cmdLineProcess.executeProcessAndWaitForItToFinish();

		assertEquals(expected, cmdLineProcess.getExitValue());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.CommandLineProcess#getExitValue()}
	 * .
	 */
	public void testGetExitValueProcessNotFinished() throws Exception {
		String waitCommand;
		if (System.getProperty("os.name").startsWith("Windows"))
			waitCommand = "PAUSE";
		else
			waitCommand = "read";
		CommandLineProcessCommand command = new CommandLineProcessCommand(
				"echo", new String[] { waitCommand });
		CommandLineProcess cmdLineProcess = new CommandLineProcess(command);
		cmdLineProcess.executeProcess();

		try {
			cmdLineProcess.getExitValue();
		} catch (IllegalThreadStateException e) {
			assertEquals("process hasn't exited", e.getMessage());
		} finally {
			if(!cmdLineProcess.isFinished())
				cmdLineProcess.destroy();
		}
	}
}
