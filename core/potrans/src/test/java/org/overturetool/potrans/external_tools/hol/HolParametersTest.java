package org.overturetool.potrans.external_tools.hol;

import java.util.Map;

import junit.framework.TestCase;

public class HolParametersTest extends TestCase {

	protected final static String mosmlDir = "/Users/gentux/root/opt/mosml";
	protected final static String holDir = "/Users/gentux/root/opt/hol";
	protected final static HolParameters holParam = new HolParameters(mosmlDir, holDir);
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetMosmlBinaryPath() {
		String expected = mosmlDir + "/bin/mosml";
		String actual = holParam.getMosmlBinaryPath();
		
		assertEquals(expected, actual);
	}

	public void testGetUnquoteCommand() {
		String expected = holDir + "/bin/unquote";
		String actual = holParam.getUnquoteBinaryPath();
		
		assertEquals(expected, actual);
	}

	public void testGetHolDir() {
		String expected = holDir;
		String actual = holParam.getHolDir();
		
		assertEquals(expected, actual);
	}

	public void testGetHolEnv() {
		HolEnvironmentBuilder holEnvBuilder = new HolEnvironmentBuilder(mosmlDir, "lib", "bin");
		Map<String, String> expected = holEnvBuilder.getEnvironment();
		Map<String, String> actual = holParam.getHolEnv();
		
		assertTrue(expected.keySet().containsAll(actual.keySet()));
		assertTrue(actual.keySet().containsAll(expected.keySet()));
		for(String key : actual.keySet())
			assertEquals(expected.get(key), actual.get(key));
	}

}
