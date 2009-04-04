/**
 * 
 */
package org.overturetool.potrans.preparation;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.overturetool.ast.itf.IOmlDocument;

/**
 * @author miguel_ferreira
 * 
 */
public class OvertureParserWrapperTest extends TestCase {

	private static String settingsWarning = 
		  "If this test fails check that you have the correct vaules set in Settings.xml, "
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
				.userNodeForPackage(CommandLineTools.class);

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
	 * {@link org.overturetool.potrans.preparation.OvertureParserWrapper#getOmlDocument(java.lang.String)}
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
	 * {@link org.overturetool.potrans.preparation.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentEmptyFileName() throws Exception {
		String fileName = "";
		try {
			OvertureParserWrapper.getOmlDocument(fileName);
			fail("FileNotFoundException sohuld have been throuwn.");
		} catch (FileNotFoundException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentNullFileName() throws Exception {
		String fileName = null;
		try {
			OvertureParserWrapper.getOmlDocument(fileName);
			fail("NullPointerException sohuld have been throuwn.");
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.potrans.preparation.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentInvalidFileName() throws Exception {
		String fileName = "some_invalid_file";
		try {
			OvertureParserWrapper.getOmlDocument(fileName);
			fail("FileNotFoundException sohuld have been throuwn.");
		} catch (FileNotFoundException e) {
		}
	}

	// public void testGetOmlExpression() throws Exception {
	// String expression =
	// "(forall l : seq of int & "
	// + "(forall i,j in set inds (l) & "
	// + "(i > j => j in set inds (l)))";
	//		
	// IOmlExpression omlExpression =
	// OvertureParserWrapper.getOmlExpression(expression);
	// assertNotNull(omlExpression);
	// }
}
