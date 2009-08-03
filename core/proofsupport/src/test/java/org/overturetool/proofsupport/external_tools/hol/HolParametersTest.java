package org.overturetool.proofsupport.external_tools.hol;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.test.TestSettings;

public class HolParametersTest extends TestCase {

	private static final String THIS_IS_A_TEST = "this is a test";
	protected static final String HOL_DIR = TestSettings.getHolDir();
	protected static final String MOSML_DIR = TestSettings.getMosmlDir();
	protected final static HolParameters holParam = new HolParameters(
			MOSML_DIR, HOL_DIR);

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetMosmlBinaryPath() {
		String expected = MOSML_DIR + Utilities.FILE_SEPARATOR + "bin"
				+ Utilities.FILE_SEPARATOR + "mosml";
		String actual = holParam.getMosmlBinaryPath();

		assertEquals(expected, actual);
	}

	public void testGetUnquoteCommand() {
		String expected = HOL_DIR + Utilities.FILE_SEPARATOR + "bin"
				+ Utilities.FILE_SEPARATOR + "unquote";
		String actual = holParam.getUnquoteBinaryPath();

		assertEquals(expected, actual);
	}

	public void testGetHolDir() {
		String expected = HOL_DIR;
		String actual = holParam.getHolDir();

		assertEquals(expected, actual);
	}

	public void testBuildMosmlCommand() throws Exception {
		List<String> expected = new ArrayList<String>();
		expected.add(MOSML_DIR + Utilities.FILE_SEPARATOR + "bin" + Utilities.FILE_SEPARATOR + "mosml");
		expected.add("-quietdec");
		expected.add("-P");
		expected.add("full");
		expected.add("-I");
		expected.add(HOL_DIR + "/sigobj");
		expected.add(HOL_DIR + "/std.prelude");
		expected.add(HOL_DIR + "/tools/unquote-init.sml");
		expected.add(HOL_DIR + "/tools/end-init-boss.sml");

		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		List<String> actual = holParam.buildMosmlHolCommand();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i));
	}

	public void testBuildUnquoteCommand() throws Exception {
		List<String> expected = new ArrayList<String>();
		expected.add(HOL_DIR + Utilities.FILE_SEPARATOR + "bin" + Utilities.FILE_SEPARATOR + "unquote");

		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		List<String> actual = holParam.buildUnquoteCommand();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i));
	}

	public void testFormatCommandArgument() throws Exception {
		String[] expected = new String[8];
		expected[0] = "-quietdec";
		expected[1] = "-P";
		expected[2] = "full";
		expected[3] = "-I";
		expected[4] = HOL_DIR + "/sigobj";
		expected[5] = HOL_DIR + "/std.prelude";
		expected[6] = HOL_DIR + "/tools/unquote-init.sml";
		expected[7] = HOL_DIR + "/tools/end-init-boss.sml";

		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		String[] actual = holParam.formatCommandArguments();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}

	public void testSetHolDir() throws Exception {
		HolParameters holParam = new HolParameters(MOSML_DIR, HOL_DIR);
		holParam.setHolDir(THIS_IS_A_TEST);

		assertEquals(THIS_IS_A_TEST, holParam.getHolDir());
	}
}
