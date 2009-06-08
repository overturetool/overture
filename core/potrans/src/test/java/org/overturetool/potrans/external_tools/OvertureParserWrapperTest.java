/**
 * 
 */
package org.overturetool.potrans.external_tools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.prefs.Preferences;

import jp.co.csk.vdm.toolbox.VDM.VDMRunTimeException;
import junit.framework.TestCase;

import org.overturetool.ast.imp.OmlBracketedExpression;
import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;

/**
 * @author miguel_ferreira
 * 
 */
public class OvertureParserWrapperTest extends TestCase {

	private static String settingsWarning = "If this test fails check that you have the correct vaules set in Settings.xml, "
			+ "namelly for the test VPP models. ";

	private static String testModel1 = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		Preferences.importPreferences(new BufferedInputStream(
				new FileInputStream("Settings.xml")));
		Preferences preferences = Preferences
				.userNodeForPackage(this.getClass());

		testModel1 = preferences.get("testModel1", null);

		// remove previously generated files
		removePreviousTestsData();
	}

	private void removePreviousTestsData() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocument() throws Exception {
		IOmlDocument omlDocument = OvertureParserWrapper
				.getOmlDocument(testModel1);

		assertNotNull(settingsWarning, omlDocument);
		assertEquals(settingsWarning, testModel1, omlDocument.getFilename()
				.trim());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentEmptyFileName() throws Exception {
		String fileName = "";
		try {
			OvertureParserWrapper.getOmlDocument(fileName);
			fail("FileNotFoundException should have been thrown.");
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentNullFileName() throws Exception {
		String fileName = null;
		try {
			OvertureParserWrapper.getOmlDocument(fileName);
			fail("NullPointerException should have been thrown.");
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.external_tools.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentInvalidFileName() throws Exception {
		String fileName = "some_invalid_file";
		try {
			OvertureParserWrapper.getOmlDocument(fileName);
			fail("FileNotFoundException should have been thrown.");
		} catch (FileNotFoundException e) {
		}
	}

	public void testGetOmlExpression() throws Exception {
		String expression = "(forall l : seq of int & "
				+ "  (forall i,j in set inds (l) & "
				+ "    (i > j => j in set inds (l))))";

		IOmlExpression omlExpression = OvertureParserWrapper
				.getOmlExpression(expression);

		assertNotNull(omlExpression);
		assertEquals(OmlBracketedExpression.class, omlExpression.getClass());
	}

	public void testGetOmlExpressionEmptyExpression() throws Exception {
		String expression = "";

		try {
			OvertureParserWrapper.getOmlExpression(expression);
			fail("ClassCastException should have been thrown.");
		} catch (ClassCastException e) {
		}
	}

	public void testGetOmlExpressionNullExpression() throws Exception {
		String expression = null;

		try {
			OvertureParserWrapper.getOmlExpression(expression);
			fail("NullPointerException should have been thrown.");
		} catch (NullPointerException e) {
		}
	}

	public void testGetOmlExpressionInvalidExpression() throws Exception {
		String expression = "invalid expression";

		try {
			OvertureParserWrapper.getOmlExpression(expression);
			fail("VDMRunTimeException should have been thrown.");
		} catch (VDMRunTimeException e) {
		}
	}
}
