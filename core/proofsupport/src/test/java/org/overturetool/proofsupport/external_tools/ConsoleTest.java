package org.overturetool.proofsupport.external_tools;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;
import org.overturetool.proofsupport.test.TestSettings;

public class ConsoleTest extends AutomaticProofSystemTestCase {

	

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWriteReadLine() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(TestSettings.getCatProgram());
		Console console = new Console(command);
		console.writeLine(TEST_MESSAGE);
		String actual = console.readLine();

		assertEquals(TEST_MESSAGE, actual);
	}

	public void testWriteReadLineMosml() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(mosmlDir + "/bin/mosml");
		Console console = new Console(command);

		console.writeLine("quit();");

		console.waitFor();
	}

//	public void testWriteReadLineHol() throws Exception {
//		ArrayList<String> command = new ArrayList<String>();
//		command.add(HOL_DIR + "/bin/" + TestSettings.getHolBin());
//		Console console = new Console(command);
//
//		String quit = "quit();";
//		console.writeLine(quit);
//
//		Thread.sleep(3000);
//	}

	public void testWriteReadLineUnquote() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(holDir + "/bin/unquote");
		Console console = new Console(command);

		String expected = "(Parse.Term [QUOTE \" (*#loc 1 4*)" + TEST_MESSAGE + "\"])";
		console.writeLine("``" + TEST_MESSAGE + "``");
		String actual = console.readLine();
		console.destroy();

		assertEquals(expected, actual);
	}

	public void testExitValueHasExited() throws Exception {
		Console console = new Console(TestSettings.getNopProgramCommand());
		Thread.sleep(3000);
		int actual = console.exitValue();

		assertEquals(0, actual);
	}

	public void testExitValueHasNotExited() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(TestSettings.getCatProgram());
		Console console = new Console(command);
		try {
			console.exitValue();
		} catch (Exception e) {
			assertEquals(IllegalThreadStateException.class, e.getClass());
		}
	}

	public void testHasTerminated() throws Exception {
		Console console = new Console(TestSettings.getNopProgramCommand());
		Thread.sleep(3000);

		assertTrue(console.hasTerminated());
	}

	public void testHasNotYetTerminated() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(TestSettings.getCatProgram());
		Console console = new Console(command);

		assertFalse(console.hasTerminated());
	}

	public void testReadErrorLineSomeError() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(TestSettings.getCatProgram());
		command.add(INVALID_FILE_NAME);
		Console console = new Console(command);
		String actual = console.readErrorLine();

		assertTrue(actual.contains(INVALID_FILE_NAME));
	}

	public void testCloseInput() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(TestSettings.getCatProgram());
		Console console = new Console(command);
		console.closeInput();

		try {
			console.writeLine();
		} catch (Exception e) {
			assertEquals(IllegalThreadStateException.class, e.getClass());
		}
	}
	
	public void testWriteAndReadLine() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(TestSettings.getCatProgram());
		Console console = new Console(command);
		
		String actual = console.writeAndReadLine(TEST_MESSAGE);
		
		assertEquals(TEST_MESSAGE, actual);
	}
	
	// TODO: check this test because it sometimes fails, sometimes doesn't
//	public void testWriteAndReadLines() throws Exception {
//		ArrayList<String> command = new ArrayList<String>();
//		command.add(TestSettings.getCatProgram());
//		Console console = new Console(command);
//		
//		console.writeLines(new String[]{THIS_IS_A_TEST, THIS_IS_A_TEST});
//		console.closeInput();
//		String actual = console.readAllLines();
//		
//		assertEquals(THIS_IS_A_TEST + Utilities.LINE_SEPARATOR + THIS_IS_A_TEST, actual.trim());
//	}
	
	public void testHasNoOutput() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(TestSettings.getCatProgram());
		Console console = new Console(command);

		assertFalse(console.hasOutput());
	}
	
	public void testBuildCommandList() throws Exception {
		List<String> expected = new ArrayList<String>();
		expected.add(TEST_MESSAGE);
		List<String> actual = Console.buildCommandList(TEST_MESSAGE);
		
		assertEquals(expected.size(), actual.size());
		for(int i = 0; i < expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i));
	}
	
	public void testPrintCommandList() throws Exception {
		List<String> command = new ArrayList<String>();
		command.add(TEST_MESSAGE);
		Console.printCommand(command);
		
		assertTrue(true);
	}
}

