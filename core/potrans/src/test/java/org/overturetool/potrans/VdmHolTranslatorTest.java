/**
 * 
 */
package org.overturetool.potrans;

import junit.framework.TestCase;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.potrans.test.TestSettings;

/**
 * @author miguel_ferreira
 * 
 */
public class VdmHolTranslatorTest extends TestCase {

	private final static String newLine = "\n";
	private static TestSettings settings = null;
	private static String setModel = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();

		setUpPreferences();
	}

	private void setUpPreferences() throws Exception {
		settings = new TestSettings();
		setModel = settings.get(TestSettings.SET_MODEL);
	}

//	public void testTranslateDocument() throws Exception {
//		VdmHolTranslator translator = new VdmHolTranslator();
//		IOmlDocument omlDocument = OvertureParserWrapper
//				.getOmlDocument(setModel);
//		String expected = "Define `inv_Element (inv_param:num)  = T`;"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"inv_Element_def\"]);"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"inv_Element_def\"]);"
//				+ newLine
//				+ "Define `inv_Set (inv_Set_subj:(num set))  = (let s = inv_Set_subj " +
//						"in ((\\x y . ~ (x = y)) s {}))`;"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"inv_Set_def\"]);"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"inv_Set_def\"]);"
//				+ newLine
//				+ "Define `inSet (inSet_parameter_1:(num set)) (inSet_parameter_2:num)  " +
//						"= (let s = inSet_parameter_1 and e = inSet_parameter_2 in (e  IN s ))`;"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"inSet_def\"]);"
//				+ newLine
//				+ "Define `subSet (subSet_parameter_1:(num set)) (subSet_parameter_2:(num set))" +
//						"  = (let s1 = subSet_parameter_1 and s2 = subSet_parameter_2 in ((s1  INTER s2 )  = s1 ))`;"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"subSet_def\"]);"
//				+ newLine
//				+ "Define `doNothing (doNothing_parameter_1:(num set))  " +
//						"= (let s = doNothing_parameter_1 in s)`;"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"doNothing_def\"]);"
//				+ newLine
//				+ "Define `pre_doNothing (doNothing_parameter_1:(num set))  " +
//						"= (let s = doNothing_parameter_1 in ((CARD s)  > 2 ))`;"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"pre_doNothing_def\"]);"
//				+ newLine
//				+ "Define `doSomething (doSomething_parameter_1:(num set))  " +
//						"= (let s = doSomething_parameter_1 in (doNothing s))`;"
//				+ newLine
//				+ "BasicProvers.export_rewrites([\"doSomething_def\"]);" + newLine;
//
//		HolDocument holDocument = translator.translateDocument(omlDocument);
//		String actual = holDocument.print();
//
//		assertEquals(expected, actual);
//	}
}
