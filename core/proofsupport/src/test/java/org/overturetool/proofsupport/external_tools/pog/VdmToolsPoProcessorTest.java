package org.overturetool.proofsupport.external_tools.pog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;

public class VdmToolsPoProcessorTest extends AutomaticProofSystemTestCase {

	public void testExtractPoExpression() throws Exception {
		String expected = "(forall i : int, l : seq of int &"
				+ Utilities.LINE_SEPARATOR + "not (true = (l = [])) =>"
				+ Utilities.LINE_SEPARATOR + " true = (i <= hd (l)) =>"
				+ Utilities.LINE_SEPARATOR + " l <> [])";
		ArrayList<String> po = new ArrayList<String>();
		po.add("Integrity property #1 :");
		po.add("In function DoSort InsertSorted, file: testinput/dosort.vpp l. 35 c. 16: non-empty sequence");
		po.add("--------------------------------------------------------------------------------------------");
		for (String line : expected.split(Utilities.LINE_SEPARATOR))
			po.add(line);

		VdmToolsPoProcessor pogProc = new VdmToolsPoProcessor();
		String actual = pogProc
				.extractPoExpression(po.toArray(new String[] {}));

		assertEquals(expected, actual.trim());
	}

	public void testExtractPosFromFileNoNewLine() throws Exception {
		VdmToolsPoProcessor pogProc = new VdmToolsPoProcessor();
		List<String[]> poList = pogProc
				.extractPosFromFile(testPogFileNoNewLine);

		assertEquals(5, poList.size());
		for (String[] po : poList) {
			assertNotNull(po);
			assertTrue(po.length > 0);
			for (String poLine : po) {
				assertNotNull(poLine);
				assertFalse(poLine.equals(""));
			}
		}
	}

	public void testExtractPosFromFileWithNewLine() throws Exception {
		VdmToolsPoProcessor pogProc = new VdmToolsPoProcessor();
		List<String[]> poList = pogProc
				.extractPosFromFile(testPogFileWithNewLine);

		assertEquals(5, poList.size());
		for (String[] po : poList) {
			assertNotNull(po);
			assertTrue(po.length > 0);
			for (String poLine : po) {
				assertNotNull(poLine);
				assertFalse(poLine.equals(""));
			}
		}
	}

	public void testReadPosNoNewLine() throws Exception {
		BufferedReader fin = new BufferedReader(new FileReader(
				testPogFileNoNewLine));
		VdmToolsPoProcessor pogProc = new VdmToolsPoProcessor();
		pogProc.readPos(fin);

		assertEquals(5, pogProc.poLines.size());
		for (String[] po : pogProc.poLines) {
			assertNotNull(po);
			assertTrue(po.length > 0);
			for (String poLine : po) {
				assertNotNull(poLine);
				assertFalse(poLine.equals(""));
			}
		}
	}

	public void testReadPosWithNewLine() throws Exception {
		BufferedReader fin = new BufferedReader(new FileReader(
				testPogFileWithNewLine));
		VdmToolsPoProcessor pogProc = new VdmToolsPoProcessor();
		pogProc.readPos(fin);

		assertEquals(5, pogProc.poLines.size());
		for (String[] po : pogProc.poLines) {
			assertNotNull(po);
			assertTrue(po.length > 0);
			for (String poLine : po) {
				assertNotNull(poLine);
				assertFalse(poLine.equals(""));
			}
		}
	}

	public void testInitBuffers() {
		VdmToolsPoProcessor pogProc = new VdmToolsPoProcessor();
		pogProc.lineBuffer = TEST_MESSAGE;
		pogProc.poBuffer.add(TEST_MESSAGE);

		pogProc.initBuffers();

		assertEquals("", pogProc.lineBuffer);
		assertEquals(0,pogProc.poBuffer.size());
	}

	public void testHandleLineSomeLine() {
		VdmToolsPoProcessor pogProc = new VdmToolsPoProcessor();
		pogProc.lineBuffer = TEST_MESSAGE;
		pogProc.handleLine();

		assertEquals(TEST_MESSAGE, pogProc.poBuffer.get(0));
		assertEquals(0, pogProc.poLines.size());
	}

	public void testHandleLineNoLine() {
		VdmToolsPoProcessor pogProc = new VdmToolsPoProcessor();
		pogProc.handleLine();

		assertEquals(0, pogProc.poBuffer.size());
		assertEquals(1, pogProc.poLines.size());
	}

}
