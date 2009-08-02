/**
 * 
 */
package org.overturetool.proofsupport.external_tools.omlparser;

import junit.framework.TestCase;

import org.overturetool.ast.imp.OmlBracketedExpression;
import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.proofsupport.test.TestSettings;

/**
 * @author miguel_ferreira
 * 
 */
public class OvertureParserWrapperTest extends TestCase {

	protected static TestSettings settings = null;
	protected static String testModel1 = null;


	protected void setUp() throws Exception {
		super.setUp();

		setUpPreferences();
	}


	private void setUpPreferences() throws Exception {
		settings = new TestSettings();
		testModel1 = settings.get(TestSettings.TEST_MODEL_1);
	}

	/**
	 * Test method for
	 * {@link org.overturetool.proofsupport.external_tools.omlparser.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocument() throws Exception {
		IOmlDocument omlDocument = new OvertureParserWrapper()
				.getOmlDocument(testModel1);

		assertNotNull(omlDocument);
		assertEquals(testModel1, omlDocument.getFilename()
				.trim());
	}

	/**
	 * Test method for
	 * {@link org.overturetool.proofsupport.external_tools.omlparser.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentEmptyFileName() throws Exception {
		String fileName = "";
		try {
			new OvertureParserWrapper().getOmlDocument(fileName);
			fail("OvertureParserException should have been thrown.");
		} catch (OvertureParserException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.proofsupport.external_tools.omlparser.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentNullFileName() throws Exception {
		String fileName = null;
		try {
			new OvertureParserWrapper().getOmlDocument(fileName);
			fail("NullPointerException should have been thrown.");
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Test method for
	 * {@link org.overturetool.proofsupport.external_tools.omlparser.OvertureParserWrapper#getOmlDocument(java.lang.String)}
	 * .
	 */
	public void testGetOmlDocumentInvalidFileName() throws Exception {
		String fileName = "some_invalid_file";
		try {
			new OvertureParserWrapper().getOmlDocument(fileName);
			fail("OvertureParserException should have been thrown.");
		} catch (OvertureParserException e) {
		}
	}

	public void testGetOmlExpression() throws Exception {
		String expression = "(forall l : seq of int & "
				+ "  (forall i,j in set inds (l) & "
				+ "    (i > j => j in set inds (l))))";

		IOmlExpression omlExpression = new OvertureParserWrapper()
				.getOmlExpression(expression);

		assertNotNull(omlExpression);
		assertEquals(OmlBracketedExpression.class, omlExpression.getClass());
	}

	public void testGetOmlExpressionEmptyExpression() throws Exception {
		String expression = "";

		try {
			new OvertureParserWrapper().getOmlExpression(expression);
			fail("ClassCastException should have been thrown.");
		} catch (ClassCastException e) {
		}
	}

	public void testGetOmlExpressionNullExpression() throws Exception {
		String expression = null;

		try {
			new OvertureParserWrapper().getOmlExpression(expression);
			fail("NullPointerException should have been thrown.");
		} catch (NullPointerException e) {
		}
	}

	public void testGetOmlExpressionInvalidExpression() throws Exception {
		String expression = "invalid expression";

		try {
			new OvertureParserWrapper().getOmlExpression(expression);
			fail("OvertureParserException should have been thrown.");
		} catch (OvertureParserException e) {
		}
	}
}
