package org.overturetool.potrans.external_tools.hol;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class HolEnvironmentBuilderTest extends TestCase {

	protected final static String mosmlDir = "/Users/gentux/root/opt/mosml";
	
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetEnvironment() {
		Map<String, String> expected = new HashMap<String,String>();
		expected.put(HolEnvironmentBuilder.MOSMLHOME, mosmlDir);
		expected.put(HolEnvironmentBuilder.DYLD_LIBRARY_PATH, "${MOSMLHOME}/lib");
		expected.put(HolEnvironmentBuilder.PATH, "${MOSMLHOME}/bin");
		
		HolEnvironmentBuilder holEnvBuilder = new HolEnvironmentBuilder(mosmlDir, "lib", "bin");
		Map<String, String> actual = holEnvBuilder.getEnvironment();
		
		assertTrue(expected.keySet().containsAll(actual.keySet()));
		assertTrue(actual.keySet().containsAll(expected.keySet()));
		for(String key : actual.keySet())
			assertEquals(expected.get(key), actual.get(key));
	}

}
