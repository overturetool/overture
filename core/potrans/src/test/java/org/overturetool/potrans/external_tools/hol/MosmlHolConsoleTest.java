package org.overturetool.potrans.external_tools.hol;

import junit.framework.TestCase;

import org.overturetool.potrans.external_tools.Utilities;
import org.overturetool.potrans.test.TestSettings;

public class MosmlHolConsoleTest extends TestCase {

	protected static final String HOL_DIR = TestSettings.getHolDir();
	protected static final String MOSML_DIR = TestSettings.getMosmlDir();

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testQuit() throws Exception {
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
		
		assertEquals(0, returnValue);
	}
	
	public void testInputOutputQuit() throws Exception {
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());
		mosml.writeLine("help;");
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();

		assertEquals(0, returnValue);
	}
	
	public void testRemoveConsoleHeader() throws Exception {
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());
		mosml.removeConsoleHeader();
		mosml.writeLine("help;");
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
		
		StringBuffer sb = new StringBuffer();
		String line = "";
		while((line = mosml.readLine()) != null) {
			sb.append(line);
			if(line != null && !line.equals("- "))
				sb.append(Utilities.UNIVERSAL_LINE_SEPARATOR);
		}
		
		assertEquals(0, returnValue);
		assertEquals("> val it = fn : string -> unit\n- ", sb.toString());
	}
	
	public void testReadOutputOneLineBlock() throws Exception {
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());
		mosml.removeConsoleHeader();
		mosml.writeLine("help;");
		String actual = mosml.readOutputBlock();
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
		
		assertEquals(0, returnValue);
		assertEquals("> val it = fn : string -> unit", actual);
	}
	
	public void testReadOutputMultiLineBlock() throws Exception {
		String invalidId = "invalidId";
		String expected = 
			"! Toplevel input:" + Utilities.LINE_SEPARATOR
			+ "! " + invalidId + ";" + Utilities.LINE_SEPARATOR
			+ "! ^^^^^^^^^" + Utilities.LINE_SEPARATOR
			+ "! Unbound value identifier: " + invalidId;
		
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam.buildMosmlHolCommand());		
		mosml.removeConsoleHeader();
		mosml.writeLine(invalidId + ";");
		String actual = mosml.readOutputBlock();
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();
			
		assertEquals(0, returnValue);
		assertEquals(expected, actual);
	}
	
	public void testQuitHol() throws Exception {
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		MosmlHolConsole console = new MosmlHolConsole(holParam.buildMosmlHolCommand(), holParam.getHolEnv());
		console.quitHol();
		
		assertTrue(console.hasTerminated());
	}
}
