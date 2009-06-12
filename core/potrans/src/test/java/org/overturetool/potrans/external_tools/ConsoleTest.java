package org.overturetool.potrans.external_tools;

import java.io.IOException;
import java.util.ArrayList;

import junit.framework.TestCase;

public class ConsoleTest extends TestCase {

	private static final String THIS_IS_A_TEST = "this is a test";

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testWriteReadLine() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add("cat");
		Console console = new Console(command);
		console.writeLine(THIS_IS_A_TEST);
		String actual = console.readLine();
		
		assertEquals(THIS_IS_A_TEST, actual);
	}
	
	public void testWriteReadLineMosml() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add("/Users/gentux/root/opt/mosml/bin/mosml");
		Console console = new Console(command);
		
		String newLine = System.getProperty("line.separator");
		console.writeLine("quit();");

		console.waitFor();
	}
	
	public void testWriteReadLineHol() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add("/Users/gentux/root/opt/hol/bin/hol");
		Console console = new Console(command);
				
		String newLine = System.getProperty("line.separator");
		String quit = "quit();";
		console.writeLine(quit);

        Thread.sleep(3000);

         
	}
	
	public void testWriteReadLineUnquote() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add("/Users/gentux/root/opt/hol/bin/unquote");
		Console console = new Console(command);
		
		String expected = "(Parse.Term [QUOTE \" (*#loc 1 4*)this is a test\"])";
		console.writeLine("``" + THIS_IS_A_TEST + "``");
		String actual = console.readLine();
		console.destroy();

		assertEquals(expected, actual);
	}
}
