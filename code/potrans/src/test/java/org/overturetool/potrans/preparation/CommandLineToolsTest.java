package org.overturetool.potrans.preparation;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * CommandLineToolsTest is the JUnit TestClass for the {@link CommandLineTools}
 * class.
 * 
 * @author miguel_ferreira
 *
 */
public class CommandLineToolsTest extends TestCase {
	

	
	/**
	 * Sets the initial test conditions by obtaining necessary values:
	 * <ul>
	 * <li>VDMTools command line binary path;
	 * <li>example VDM++ model path;
	 * </ul>
	 * for the execution of the test methods.
	 */
	protected void setUp() throws Exception {
		super.setUp();
	
	}
	
	
	public void testExecuteProcessValidCommand() throws Exception {
		String inputText = "This is a test";
		List<String> command = new ArrayList<String>(1);
		command.add("echo");
		command.add(inputText);
		
		String output = CommandLineTools.executeProcess(command);
		
		assertEquals(inputText, output.trim());
	}
	
	public void testExecuteProcessNullCmd() throws Exception {
		List<String> cmdText = null;

		try {
			CommandLineTools.executeProcess(cmdText);
		} catch(Exception e) {
			assertEquals("Given a null command the method should throw a NullPointerException from Rintime.",
					NullPointerException.class, e.getClass());
		}
	}
	
	public void testExecuteProcessEmptyCmd() throws Exception {
		List<String> command = new ArrayList<String>(1);
		command.add("");

		try {
			CommandLineTools.executeProcess(command);
		} catch(Exception e) {
			assertEquals("Given an empty command the method should throw a IllegalArgumentException from Rintime.",
					IllegalArgumentException.class, e.getClass());
			assertEquals("Empty command", e.getMessage().trim());
		}
	}

}
