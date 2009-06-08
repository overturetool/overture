/**
 * 
 */
package org.overturetool.potrans.external_tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.overturetool.potrans.external_tools.ConsoleProcess;
import org.overturetool.potrans.external_tools.ConsoleProcessCommand;
import org.overturetool.potrans.external_tools.ConsoleProcessInputText;
import org.overturetool.potrans.external_tools.InputValidator;

import junit.framework.TestCase;

/**
 * @author miguel_ferreira
 * 
 */
public class CommandLineProcessTest extends TestCase {

	private static final String THIS_IS_A_TEST = "This is a test.";

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
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#executeBathProcess()}
	 * .
	 */
	public void testExecuteProcessAndWaitForItToFinish() throws Exception {
		String inputString = "This is a test";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				"echo", new String[] { inputString });
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);

		int exitValue = cmdLineProcess.executeBathProcess();

		assertEquals(0, exitValue);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#executeBathProcess()}
	 * .
	 */
	public void testExecuteProcessAndWaitForItToFinishInvalidCommand()
			throws Exception {
		String invalidCommand = "invalid_command";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				invalidCommand);
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);

		try {
			cmdLineProcess.executeBathProcess();
		} catch (ConsoleException e) {
			assertEquals(invalidCommand + ": not found", e.getCause().getMessage());
		} finally {
			cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#executeBathProcess()}
	 * .
	 */
	public void testExecuteProcessAndWaitForItToFinishEmptyCommand()
			throws Exception {
		String emptyCommand = "";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				emptyCommand);
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);

		try {
			cmdLineProcess.executeBathProcess();
		} catch (ConsoleException e) {
			assertEquals(emptyCommand + ": not found", e.getCause().getMessage());
		} finally {
			cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#executeProcess()}
	 * .
	 */
	public void testExecuteProcess() throws Exception {
		String waitCommand;
		if (System.getProperty("os.name").startsWith("Windows"))
			waitCommand = "PAUSE";
		else
			waitCommand = "read";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				waitCommand);
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);
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
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#executeProcess()}
	 * .
	 */
	public void testExecuteProcessInvalidCommand() throws Exception {
		String invalidCommand = "invalid_command";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				invalidCommand);
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);

		try {
			cmdLineProcess.executeProcess();
		} catch (ConsoleException e) {
			assertEquals(invalidCommand + ": not found", e.getCause().getMessage());
		} finally {
			if(!cmdLineProcess.isFinished())
				cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#executeProcess()}
	 * .
	 */
	public void testExecuteProcessEmptyCommand() throws Exception {
		String emptyCommand = "";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				emptyCommand);
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);

		try {
			cmdLineProcess.executeProcess();
		} catch (ConsoleException e) {
			assertEquals(emptyCommand + ": not found", e.getCause().getMessage());
		} finally {
			if(!cmdLineProcess.isFinished())
				cmdLineProcess.destroy();
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getProcessOutputString()}
	 * .
	 */
	public void testGetProcessOutput() throws Exception {
		String inputString = "This is a test";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				"echo", new String[] { inputString });
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);
		cmdLineProcess.executeBathProcess();

		String actual = cmdLineProcess.getProcessOutputString();

		assertEquals(inputString, actual.trim());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getProcessError()}
	 * .
	 */
	public void testGetProcessError() throws Exception {
		String invalidFlag = "-invalid_flag";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				vppdeExecutable, new String[] { invalidFlag });
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);
		cmdLineProcess.executeBathProcess();

		String actual = cmdLineProcess.getProcessError();

		assertTrue(settingsWarning, cmdLineProcess.getExitValue() != 0);
		assertTrue(settingsWarning, actual
				.contains("Usage: vppde [options] [specfiles]"));
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#setProcessInput(java.lang.String)}
	 * .
	 * 
	 * Miguel: This test will probably fail on Windows. If it does please
	 * contact me and supply the output from JUnit.
	 */
	public void testSetProcessInput() throws Exception {
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				vppdeExecutable);
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);
		cmdLineProcess.executeProcess();
		cmdLineProcess.setProcessInput(new ConsoleProcessInputText("quit" + newLine));
		cmdLineProcess.waitFor();

		assertEquals(settingsWarning, 0, cmdLineProcess.getExitValue());
	}
	
	public void testSetProcessInputOutputInterleaved() throws Exception {
		String expected = "codegen (cg) class [opt]  enable (ena) ident";
						
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				vppdeExecutable);
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);
		cmdLineProcess.executeProcess();
		cmdLineProcess.setProcessInput(new ConsoleProcessInputText("help" + newLine));
		cmdLineProcess.setProcessInput(new ConsoleProcessInputText("quit" + newLine));
		cmdLineProcess.waitFor();
		
		String actual = cmdLineProcess.getProcessOutputString();

		assertEquals(settingsWarning, 0, cmdLineProcess.getExitValue());
		assertTrue(settingsWarning, actual.contains(expected));
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getProcessOutputFromStream(java.io.InputStream)}
	 * .
	 */
	public void testGetProcessOutputFromStream() throws Exception {
		String expected = THIS_IS_A_TEST;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(expected
				.getBytes());

		String actual = new ConsoleProcess(new ConsoleProcessCommand(""))
				.getProcessOutputFromStream(inputStream);

		assertEquals(expected, actual);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getProcessOutputFromStream(java.io.InputStream)}
	 * .
	 */
	public void testGetProcessOutputFromStreamNullInputStream()
			throws Exception {
		ByteArrayInputStream inputStream = null;

		String actual = new ConsoleProcess(new ConsoleProcessCommand(""))
				.getProcessOutputFromStream(inputStream);

		assertEquals("", actual);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getTextFromStreamToStringBuffer(java.io.InputStream, java.lang.StringBuffer)}
	 * .
	 */
	public void testGetTextFromStreamToStringBuffer() throws Exception {
		String expected = THIS_IS_A_TEST;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(expected
				.getBytes());
		StringBuffer text = new StringBuffer();

		new ConsoleProcess(new ConsoleProcessCommand("")).getTextFromStreamToStringBuffer(inputStream, text);

		assertEquals(expected, text.toString());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getTextFromStreamToStringBuffer(java.io.InputStream, java.lang.StringBuffer)}
	 * .
	 */
	public void testGetTextFromStreamToStringBufferNullInputStream()
			throws Exception {
		ByteArrayInputStream inputStream = null;
		StringBuffer text = new StringBuffer();

		new ConsoleProcess(new ConsoleProcessCommand("")).getTextFromStreamToStringBuffer(inputStream, text);

		assertEquals("", text.toString());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getTextFromStreamToStringBuffer(java.io.InputStream, java.lang.StringBuffer)}
	 * .
	 */
	public void testGetTextFromStreamToStringBufferNullTextBuffer()
			throws Exception {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				THIS_IS_A_TEST.getBytes());
		StringBuffer text = null;

		new ConsoleProcess(new ConsoleProcessCommand("")).getTextFromStreamToStringBuffer(inputStream, text);

		assertNull(text);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getExitValue()}
	 * .
	 */
	public void testGetExitValue() throws Exception {
		String inputString = THIS_IS_A_TEST;
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				"echo", new String[] { inputString });
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);

		int actual = cmdLineProcess.executeBathProcess();

		assertEquals(cmdLineProcess.getExitValue(), actual);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#getExitValue()}
	 * .
	 */
	public void testGetExitValueProcessNotFinished() throws Exception {
		String waitCommand;
		if (System.getProperty("os.name").startsWith("Windows"))
			waitCommand = "PAUSE";
		else
			waitCommand = "read";
		ConsoleProcessCommand command = new ConsoleProcessCommand(
				waitCommand);
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);
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
	 * {@link org.overturetool.potrans.external_tools.ConsoleProcess#executeProcess()}
	 * .
	 */
	public void testExecuteEcho() throws Exception {
		ConsoleProcessCommand command = new ConsoleProcessCommand("echo");
		ConsoleProcess cmdLineProcess = new ConsoleProcess(command);
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
