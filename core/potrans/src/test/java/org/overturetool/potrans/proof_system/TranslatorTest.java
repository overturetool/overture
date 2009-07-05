package org.overturetool.potrans.proof_system;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.overturetool.potrans.external_tools.Utilities;
import org.overturetool.potrans.external_tools.VdmToolsPogProcessor;
import org.overturetool.potrans.external_tools.VdmToolsWrapper;
import org.overturetool.potrans.test.TestSettings;

public class TranslatorTest extends TestCase {

	private static TestSettings settings = null;
	private static String stackModel = null;

	protected void setUp() throws Exception {
		super.setUp();

		setUpTestValues();
	}

	private void setUpTestValues() throws Exception {
		settings = new TestSettings();
		stackModel = settings.get(TestSettings.STACK_MODEL);
	}

	// TODO this method reveals a bug in the VDMTools POG generator:
	//        -- the generated pog file will have two POs together (with no line separating them),
//	public void testTranslateOmlToHol() throws Exception {
//		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
//				"/Users/gentux/root/opt/vdmpp/bin/vppde"), new VdmToolsPogProcessor());
//		String modelFile = "testinput/stack.vpp";
//		List<String> contextFiles = new ArrayList<String>(1);
//		contextFiles.add("testinput/set.vpp");
//		PreparationData prepData = prep.prepareVdmFiles(modelFile, contextFiles);
//		Translator trans = new Translator();
//
//		// TODO this expected variable i
//		String expected = "";
//
//		String actual = trans.translateOmlToHol(prepData);
//
//		System.err.println(actual);
//		// assertEquals(expected, actual);
//	}

	public void testTranslateOmlToHolNoContext() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
				TestSettings.getVppdeBinary()), new VdmToolsPogProcessor());
		List<String> contextFiles = new ArrayList<String>(0);
		PreparationData prepData = prep.prepareVdmFiles(stackModel, contextFiles);
		Translator trans = new Translator();

		String expected = "fun boolToInteger(true) = 1 | boolToInteger(false)=0;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "Define `inv_Stack (inv_param:(num list))  = T`;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"inv_Stack_def\"]);" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"inv_Stack_def\"]);" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "Define `push (push_parameter_1:(num list)) (push_parameter_2:num)  = (let s = push_parameter_1 and n = push_parameter_2 in ([n]  ++ s ))`;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"push_def\"]);" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "Define `pop (pop_parameter_1:(num list))  = (let s = pop_parameter_1 in (TL s))`;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"pop_def\"]);" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "Define `pre_pop (pop_parameter_1:(num list))  = (let s = pop_parameter_1 in ((\\x y . ~ (x = y)) s []))`;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"pre_pop_def\"]);" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "Define `top (top_parameter_1:(num list))  = (let s = top_parameter_1 in (HD s))`;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"top_def\"]);" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "val total = 0;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "val success = 0;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "val success = success + boolToInteger(can TAC_PROOF(([]:(term list), ``(!  uni_0_var_1.((((inv_Stack uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in ((\\x y . ~ (x = y)) s [])) ))``), VDM_GENERIC_TAC));" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "val total = total + 1;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "val success = success + boolToInteger(can TAC_PROOF(([]:(term list), ``(!  uni_0_var_1.((((inv_Stack uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in (((\\x y . ~ (x = y)) s [])  ==> ((\\x y . ~ (x = y)) s []) )) ))``), VDM_GENERIC_TAC));" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "val total = total + 1;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "total;" + Utilities.UNIVERSAL_LINE_SEPARATOR
				+ "success;" + Utilities.UNIVERSAL_LINE_SEPARATOR;

		String actual = trans.translateOmlToHol(prepData);
		
		// TODO can't compare strings because output is not deterministic
		//      as the POs can be printed in a different order in subsequent calls
		assertEquals(expected.length(), actual.length());
	}
	
//	public void testTranslateFS() throws Exception {
//		TranslationPreProcessor prep = new TranslationPreProcessor(new VdmToolsWrapper(
//				"/Users/gentux/root/opt/vdmpp/bin/vppde"), new VdmToolsPogProcessor());
//		List<String> contextFiles = new ArrayList<String>(0);
//		PreparationData prepData = prep.prepareVdmFiles("/Users/gentux/sig/sbmf09/models/vdm/filesystem.vpp.mod", contextFiles);
//		Translator trans = new Translator();
//
//
//		String actual = trans.translateOmlToHol(prepData);
//
//		System.err.println(actual);
//	}
}
