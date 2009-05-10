package org.overturetool.potrans;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import org.overturetool.ast.itf.IOmlDocument;
import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.potrans.external_tools.OvertureParserWrapper;

import jp.co.csk.vdm.toolbox.VDM.CGException;
import junit.framework.TestCase;

public class DocumentProverTest extends TestCase {

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

	public void testGetProofCounter() throws Exception {
		String po = "(forall s : Set & Set`pre_doNothing(s))";
		VdmHolTranslator translator = new VdmHolTranslator();
		IOmlDocument omlDocument = OvertureParserWrapper
				.getOmlDocument(setModel);
		IOmlExpression omlExpression = OvertureParserWrapper
				.getOmlExpression(po);
		HolDocument holDocument = translator.translateDocument(omlDocument);
		HashSet poSet = new HashSet();
		poSet.add(new ProofObligation(omlExpression, null));
		DocumentProver docProv = new DocumentProver(holDocument, poSet);

		String expected = "fun boolToInteger(true) = 1 | boolToInteger(false)=0;"
				+ newLine
				+ "Define `inv_Element (inv_param:num)  = T`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inv_Element_def\"]);"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inv_Element_def\"]);"
				+ newLine
				+ "Define `inv_Set (inv_Set_subj:(num set))  = (let s = " +
						"inv_Set_subj in ((\\x y . ~ (x = y)) s {}))`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inv_Set_def\"]);"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inv_Set_def\"]);"
				+ newLine
				+ "Define `inSet (inSet_parameter_1:(num set)) (inSet_parameter_2:num)" +
						"  = (let s = inSet_parameter_1 and e = inSet_parameter_2 in " +
						"(e  IN s ))`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"inSet_def\"]);"
				+ newLine
				+ "Define `subSet (subSet_parameter_1:(num set)) (subSet_parameter_2:" +
						"(num set))  = (let s1 = subSet_parameter_1 and s2 = " +
						"subSet_parameter_2 in ((s1  INTER s2 )  = s1 ))`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"subSet_def\"]);"
				+ newLine
				+ "Define `doNothing (doNothing_parameter_1:(num set))  = " +
						"(let s = doNothing_parameter_1 in s)`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"doNothing_def\"]);"
				+ newLine
				+ "Define `pre_doNothing (doNothing_parameter_1:(num set))  " +
						"= (let s = doNothing_parameter_1 in ((CARD s)  > 2 ))`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"pre_doNothing_def\"]);"
				+ newLine
				+ "Define `doSomething (doSomething_parameter_1:(num set))  " +
						"= (let s = doSomething_parameter_1 in (doNothing s))`;"
				+ newLine
				+ "BasicProvers.export_rewrites([\"doSomething_def\"]);"
				+ newLine
				+ "val total = 0;"
				+ newLine
				+ "val success = 0;"
				+ newLine
				+ "val success = success + boolToInteger(can TAC_PROOF(([]:(term list), " +
						"``(!  uni_0_var_1.((((inv_Set uni_0_var_1)  /\\ (?  " +
						"s.(s  = uni_0_var_1 )) )  /\\ T )  ==> " +
						"(let s = uni_0_var_1 in (pre_doNothing s)) ))``), " +
						"VDM_GENERIC_TAC));"
				+ newLine
				+ "val total = total + 1;"
				+ newLine
				+ "total;"
				+ newLine + "success;" + newLine;

		assertEquals(expected, docProv.getProofCounter().print());

	}

}
