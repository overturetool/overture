package org.overturetool.proofsupport.external_tools.hol;

import java.util.HashMap;
import java.util.Map;

import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;

public class HolEnvironmentBuilderTest extends AutomaticProofSystemTestCase {
	
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetEnvironment() {
		Map<String, String> expected = new HashMap<String,String>();
		expected.put(HolEnvironmentBuilder.MOSMLHOME, mosmlDir);
		expected.put(HolEnvironmentBuilder.DYLD_LIBRARY_PATH, mosmlDir + Utilities.FILE_SEPARATOR + "lib");
		expected.put(HolEnvironmentBuilder.PATH, mosmlDir + Utilities.FILE_SEPARATOR + "bin");
		
		HolEnvironmentBuilder holEnvBuilder = new HolEnvironmentBuilder(mosmlDir, "lib", "bin");
		Map<String, String> actual = holEnvBuilder.getEnvironment();
		
		assertTrue(expected.keySet().containsAll(actual.keySet()));
		assertTrue(actual.keySet().containsAll(expected.keySet()));
		for(String key : actual.keySet())
			assertEquals(expected.get(key), actual.get(key));
	}

}
