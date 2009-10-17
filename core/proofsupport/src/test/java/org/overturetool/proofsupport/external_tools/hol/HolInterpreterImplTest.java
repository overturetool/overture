package org.overturetool.proofsupport.external_tools.hol;

import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;

public class HolInterpreterImplTest extends AutomaticProofSystemTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testInterpretLineHelp()  throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		HolInterpreterImpl hol = new HolInterpreterImpl(holParam);
		hol.start();
		
		String expected = "> val it = fn : string -> unit";
		String actual = hol.interpretLine("help;");
		hol.quit();
		
		assertEquals(expected, actual);
	}

	public void testQuit() throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		HolInterpreterImpl hol = new HolInterpreterImpl(holParam);
		hol.start();
		
		hol.quit();
		
		assertTrue(hol.unquote.hasTerminated());
		assertTrue(hol.mosml.hasTerminated());
		assertFalse(hol.pipe.isAlive());
	}

}
