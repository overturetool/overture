package org.overturetool.potrans.external_tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.overturetool.potrans.external_tools.hol.HolParameters;
import org.overturetool.potrans.external_tools.hol.MosmlHolConsole;
import org.overturetool.potrans.external_tools.hol.UnquoteConsole;

import junit.framework.TestCase;

public class ConsolePipeTest extends TestCase {

	protected static final String TEST_MESSAGE = "This is a test!";
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRunTwoCats() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add("cat");
		Console input = new Console(command);
		Console output = new Console(command);
		Thread pipe = new Thread(new ConsolePipe(input, output), "PipeThread");
		pipe.start();
		
		input.writeLine(TEST_MESSAGE);
		System.err.println("Thread: " + Thread.currentThread().getName());
		output.waitForSomeOutput(5000);
		input.destroy();
		pipe.join();
		String actual = output.readLine();
		
		
		assertEquals(TEST_MESSAGE, actual);
	}
	
	public void testRunHol() throws Exception {
		HolParameters holParam = new HolParameters("/Users/gentux/root/opt/mosml", "/Users/gentux/root/opt/hol");
		UnquoteConsole input = new UnquoteConsole(holParam.getUnquoteBinaryPath());
		MosmlHolConsole output = new MosmlHolConsole(holParam.buildMosmlHolCommand());
		output.removeConsoleHeader();
		Thread pipe = new Thread(new ConsolePipe(input, output), "PipeThread");
		pipe.start();
		
		input.writeLine("help;");
		String actual = output.readOutputBlock();
		
		output.quitHol();
		input.destroy();
		pipe.join();
		
		
		assertEquals("> val it = fn : string -> unit", actual);
	}

}
