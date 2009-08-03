package org.overturetool.proofsupport.external_tools;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.overturetool.proofsupport.external_tools.hol.HolParameters;
import org.overturetool.proofsupport.external_tools.hol.MosmlHolConsole;
import org.overturetool.proofsupport.external_tools.hol.UnquoteConsole;
import org.overturetool.proofsupport.test.TestSettings;

public class ConsolePipeTest extends TestCase {

	protected static final String TEST_MESSAGE = "This is a test!";
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testRunTwoCats() throws Exception {
		ArrayList<String> command = new ArrayList<String>();
		command.add(TestSettings.getCatProgram());
		Console input = new Console(command);
		Console output = new Console(command);
		Thread pipe = new Thread(new ConsolePipe(input, output), "PipeThread");
		pipe.start();
		
		input.writeLine(TEST_MESSAGE);
		
		// have to wait before calling destroy
		// otherwise will kill the stream
		Thread.sleep(1000);
		
		input.destroy();
		pipe.join();
		String actual = output.readLine();
		
		
		assertEquals(TEST_MESSAGE, actual);
	}
	
	public void testRunHol() throws Exception {
		HolParameters holParam = new HolParameters(TestSettings.getMosmlDir(), TestSettings.getHolDir());
		UnquoteConsole input = new UnquoteConsole(holParam.getUnquoteBinaryPath());
		MosmlHolConsole output = new MosmlHolConsole(holParam.buildMosmlHolCommand());
//		output.removeConsoleHeader();
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
