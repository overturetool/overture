package org.overturetool.potrans.external_tools;

import org.overturetool.potrans.external_tools.ConsoleException;
import org.overturetool.potrans.external_tools.ConsoleTools;

import junit.framework.TestCase;

/**
 * CommandLineToolsTest is the JUnit TestClass for the {@link ConsoleTools}
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
		String output = ConsoleTools.executeProcess("echo", new String[]{inputText});
		
		assertEquals(inputText, output.trim());
	}
	
	public void testExecuteProcessNullCmd() throws Exception {
		String inputText = "This is a test";
		try {
			ConsoleTools.executeProcess(null, new String[]{inputText});
		} catch(ConsoleException e) {
			assertNull(e.getCommand());
		}
	}
	
	public void testExecuteProcessEmptyCmd() throws Exception {
		String inputText = "This is a test";

		try {
			ConsoleTools.executeProcess("", new String[]{inputText});
		} catch(ConsoleException e) {
			assertEquals("", e.getCommand());
		}
	}
}
