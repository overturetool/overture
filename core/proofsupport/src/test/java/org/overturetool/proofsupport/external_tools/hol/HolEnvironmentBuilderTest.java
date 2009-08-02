package org.overturetool.proofsupport.external_tools.hol;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.test.TestSettings;

public class HolEnvironmentBuilderTest extends TestCase {

	protected static final String MOSML_DIR = TestSettings.getMosmlDir();
	
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetEnvironment() {
		Map<String, String> expected = new HashMap<String,String>();
		expected.put(HolEnvironmentBuilder.MOSMLHOME, MOSML_DIR);
		expected.put(HolEnvironmentBuilder.DYLD_LIBRARY_PATH, MOSML_DIR + Utilities.FILE_SEPARATOR + "lib");
		expected.put(HolEnvironmentBuilder.PATH, MOSML_DIR + Utilities.FILE_SEPARATOR + "bin");
		
		HolEnvironmentBuilder holEnvBuilder = new HolEnvironmentBuilder(MOSML_DIR, "lib", "bin");
		Map<String, String> actual = holEnvBuilder.getEnvironment();
		
		assertTrue(expected.keySet().containsAll(actual.keySet()));
		assertTrue(actual.keySet().containsAll(expected.keySet()));
		for(String key : actual.keySet())
			assertEquals(expected.get(key), actual.get(key));
	}

}
