package org.overturetool.proofsupport.external_tools.hol;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;

public class HolParametersTest extends AutomaticProofSystemTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetMosmlBinaryPath() {
		String expected = mosmlDir + Utilities.FILE_SEPARATOR + "bin"
				+ Utilities.FILE_SEPARATOR + "mosml";
		String actual = holParam.getMosmlBinaryPath();

		assertEquals(expected, actual);
	}

	public void testGetUnquoteCommand() {
		String expected = holDir + Utilities.FILE_SEPARATOR + "bin"
				+ Utilities.FILE_SEPARATOR + "unquote";
		String actual = holParam.getUnquoteBinaryPath();

		assertEquals(expected, actual);
	}

	public void testGetHolDir() {
		String expected = holDir;
		String actual = holParam.getHolDir();

		assertEquals(expected, actual);
	}

	public void testBuildMosmlCommand() throws Exception {
		List<String> expected = new ArrayList<String>();
		expected.add(mosmlDir + Utilities.FILE_SEPARATOR + "bin" + Utilities.FILE_SEPARATOR + "mosml");
		expected.add("-quietdec");
		expected.add("-P");
		expected.add("full");
		expected.add("-I");
		expected.add(holDir + "/sigobj");
		expected.add(holDir + "/std.prelude");
		expected.add(holDir + "/tools/unquote-init.sml");
		expected.add(holDir + "/tools/end-init-boss.sml");

		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		List<String> actual = holParam.buildMosmlHolCommand();

		assertEquals(expected.size(), actual.size());
		for (int i = 0; i < expected.size(); i++)
			assertEquals(expected.get(i), actual.get(i));
	}

	public void testBuildUnquoteCommand() throws Exception {
		List<String> expected = new ArrayList<String>();
		expected.add(holDir + Utilities.FILE_SEPARATOR + "bin" + Utilities.FILE_SEPARATOR + "unquote");

		HolParameters holParam = new HolParameters(mosmlDir, holDir);
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
		expected[4] = holDir + "/sigobj";
		expected[5] = holDir + "/std.prelude";
		expected[6] = holDir + "/tools/unquote-init.sml";
		expected[7] = holDir + "/tools/end-init-boss.sml";

		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		String[] actual = holParam.formatCommandArguments();

		assertEquals(expected.length, actual.length);
		for (int i = 0; i < expected.length; i++)
			assertEquals(expected[i], actual[i]);
	}

	public void testSetHolDir() throws Exception {
		HolParameters holParam = new HolParameters(mosmlDir, holDir);
		holParam.setHolDir(TEST_MESSAGE);

		assertEquals(TEST_MESSAGE, holParam.getHolDir());
	}
}
