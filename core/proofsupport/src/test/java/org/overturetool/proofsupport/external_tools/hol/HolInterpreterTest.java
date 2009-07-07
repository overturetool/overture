package org.overturetool.proofsupport.external_tools.hol;

import org.overturetool.proofsupport.test.TestSettings;
import org.overturetool.proofsupport.external_tools.hol.HolInterpreter;
import org.overturetool.proofsupport.external_tools.hol.HolParameters;

import junit.framework.TestCase;

public class HolInterpreterTest extends TestCase {

	protected static final String HOL_DIR = TestSettings.getHolDir();
	protected static final String MOSML_DIR = TestSettings.getMosmlDir();

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testInterpretLineHelp()  throws Exception {
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		HolInterpreter hol = new HolInterpreter(holParam);
		hol.start();
		
		String expected = "> val it = fn : string -> unit";
		String actual = hol.interpretLine("help;");
		hol.quit();
		
		assertEquals(expected, actual);
	}

	public void testQuit() throws Exception {
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		HolInterpreter hol = new HolInterpreter(holParam);
		hol.start();
		
		hol.quit();
		
		assertTrue(hol.unquote.hasTerminated());
		assertTrue(hol.mosml.hasTerminated());
		assertFalse(hol.pipe.isAlive());
	}

}
