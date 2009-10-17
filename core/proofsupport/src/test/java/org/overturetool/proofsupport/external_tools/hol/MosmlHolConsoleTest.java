package org.overturetool.proofsupport.external_tools.hol;

import junit.framework.TestCase;

import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;
import org.overturetool.proofsupport.test.TestSettings;

public class MosmlHolConsoleTest extends AutomaticProofSystemTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testQuit() throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam
				.buildMosmlHolCommand());
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();

		assertEquals(0, returnValue);
	}

	public void testInputOutputQuit() throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam
				.buildMosmlHolCommand());
		mosml.writeLine("help;");
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();

		assertEquals(0, returnValue);
	}

	public void testRemoveConsoleHeader() throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam
				.buildMosmlHolCommand());
		mosml.writeLine("help;");
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();

		StringBuffer sb = new StringBuffer();
		String line = "";
		while ((line = mosml.readLine()) != null) {
			sb.append(line);
			if (line != null && !line.equals("- "))
				sb.append(Utilities.NEW_CHARACTER);
		}

		assertEquals(0, returnValue);
		assertEquals("> val it = fn : string -> unit\n- ", sb.toString());
	}

	public void testReadOutputOneLineBlock() throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam
				.buildMosmlHolCommand());

		mosml.writeLine("help;");
		String actual = mosml.readOutputBlock();
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();

		assertEquals(0, returnValue);
		assertEquals("> val it = fn : string -> unit", actual);
	}

	public void testReadOutputMultiLineBlock() throws Exception {
		String invalidId = "invalidId";
		String expected = "! Toplevel input:" + Utilities.LINE_SEPARATOR + "! "
				+ invalidId + ";" + Utilities.LINE_SEPARATOR + "! ^^^^^^^^^"
				+ Utilities.LINE_SEPARATOR + "! Unbound value identifier: "
				+ invalidId;

		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		MosmlHolConsole mosml = new MosmlHolConsole(holParam
				.buildMosmlHolCommand());

		mosml.writeLine(invalidId + ";");
		String actual = mosml.readOutputBlock();
		mosml.writeLine("quit();");
		int returnValue = mosml.waitFor();

		assertEquals(0, returnValue);
		assertEquals(expected, actual);
	}

	public void testQuitHol() throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		MosmlHolConsole console = new MosmlHolConsole(holParam
				.buildMosmlHolCommand());
		console.quitHol();

		assertTrue(console.hasTerminated());
	}

	public void testHolError() throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		MosmlHolConsole console = new MosmlHolConsole(holParam
				.buildMosmlHolCommand());
		console
				.writeLine("Hol_datatype `Path =  RootQuoteLiteral | ((char list) list) `;");
		try {
			console.readOutputBlock();
			fail("The HOL line used in the test contains an error and method should have thrown an exception.");
		} catch (HolInterpreterException e) {

		} finally{
			console.quitHol();
			assertTrue(console.hasTerminated());
		}

	}
}
