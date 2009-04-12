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
		String output = CommandLineTools.executeProcess("echo", new String[]{inputText});
		
		assertEquals(inputText, output.trim());
	}
	
	public void testExecuteProcessNullCmd() throws Exception {
		String inputText = "This is a test";
		try {
			CommandLineTools.executeProcess(null, new String[]{inputText});
		} catch(Exception e) {
			assertEquals("Given a null command the method should throw a NullPointerException in Runtime.",
					NullPointerException.class, e.getClass());
		}
	}
	
	public void testExecuteProcessEmptyCmd() throws Exception {
		String inputText = "This is a test";

		try {
			CommandLineTools.executeProcess("", new String[]{inputText});
		} catch(Exception e) {
			assertEquals("Given an empty command the method should throw a IllegalArgumentException in Runtime.",
					IllegalArgumentException.class, e.getClass());
			assertEquals("Empty command", e.getMessage().trim());
		}
	}
}
