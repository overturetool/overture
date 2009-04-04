/**
 * 
 */
package org.overturetool.potrans;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import junit.framework.TestCase;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.potrans.preparation.OvertureParserWrapper;

/**
 * @author miguel_ferreira
 * 
 */
public class VdmHolTranslatorTest extends TestCase {

	private final static String newLine = System.getProperty("line.separator");
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * @throws IOException
	 * @throws InvalidPreferencesFormatException
	 * @throws FileNotFoundException
	 */
	private void setUpPreferences() throws IOException,
			InvalidPreferencesFormatException, FileNotFoundException {
		Preferences.importPreferences(new BufferedInputStream(
				new FileInputStream("Settings.xml")));
		Preferences preferences = Preferences.userNodeForPackage(this
				.getClass());
		setModel = preferences.get("setModel", null);
	}

	public void testTranslateDocument() throws Exception {
		VdmHolTranslator translator = new VdmHolTranslator();
		IOmlDocument omlDocument = OvertureParserWrapper
				.getOmlDocument(setModel);
		String expected = "Define `inv_Element (inv_param:num)  = T`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inv_Element_def\"]);"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inv_Element_def\"]);"
				+ newLine
				+ "Define `inv_Set (inv_param:(num set))  = T`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inv_Set_def\"]);"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inv_Set_def\"]);"
				+ newLine
				+ "Define `inSet (inSet_parameter_1:(num set)) (inSet_parameter_2:num)"
				+ "  = (let s = inSet_parameter_1 and e = "
				+ "inSet_parameter_2 in (e  IN s ))`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inSet_def\"]);"
				+ newLine
				+ "Define `subSet (subSet_parameter_1:(num set)) (subSet_parameter_2:(num set))"
				+ "  = (let s1 = subSet_parameter_1 and s2 = subSet_parameter_2 in "
				+ "((s1  INTER s2 )  = s1 ))`;" + newLine
				+ "BasicProvers.export_rewrites([\"subSet_def\"]);" + newLine;

		HolDocument holDocument = translator.translateDocument(omlDocument);
		String actual = holDocument.print();

		assertEquals(expected, actual);
	}

}
