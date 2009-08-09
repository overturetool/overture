package org.overturetool.proofsupport.external_tools.vdmholtranslator;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.overturetool.proofsupport.PreparationData;
import org.overturetool.proofsupport.TranslationPreProcessor;
import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsPogProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsWrapper;
import org.overturetool.proofsupport.test.TestSettings;

public class TranslatorWrapperTest extends TestCase {

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
		TranslatorWrapper trans = new TranslatorWrapper();

		String expected = "fun boolToInteger(true) = 1 | boolToInteger(false)=0;" + Utilities.NEW_CHARACTER
				+ "Define `inv_Stack (inv_param:(num list))  = T`;" + Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"inv_Stack_def\"]);" + Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"inv_Stack_def\"]);" + Utilities.NEW_CHARACTER
				+ "Define `push (push_parameter_1:(num list)) (push_parameter_2:num)  = (let s = push_parameter_1 and n = push_parameter_2 in ([n]  ++ s ))`;" + Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"push_def\"]);" + Utilities.NEW_CHARACTER
				+ "Define `pop (pop_parameter_1:(num list))  = (let s = pop_parameter_1 in (TL s))`;" + Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"pop_def\"]);" + Utilities.NEW_CHARACTER
				+ "Define `pre_pop (pop_parameter_1:(num list))  = (let s = pop_parameter_1 in ((\\x y . ~ (x = y)) s []))`;" + Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"pre_pop_def\"]);" + Utilities.NEW_CHARACTER
				+ "Define `top (top_parameter_1:(num list))  = (let s = top_parameter_1 in (HD s))`;" + Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"top_def\"]);" + Utilities.NEW_CHARACTER
				+ "val total = 0;" + Utilities.NEW_CHARACTER
				+ "val success = 0;" + Utilities.NEW_CHARACTER
				+ "val success = success + boolToInteger(can TAC_PROOF(([]:(term list), ``(!  uni_0_var_1.((((inv_Stack uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in ((\\x y . ~ (x = y)) s [])) ))``), VDM_GENERIC_TAC));" + Utilities.NEW_CHARACTER
				+ "val total = total + 1;" + Utilities.NEW_CHARACTER
				+ "val success = success + boolToInteger(can TAC_PROOF(([]:(term list), ``(!  uni_0_var_1.((((inv_Stack uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in (((\\x y . ~ (x = y)) s [])  ==> ((\\x y . ~ (x = y)) s []) )) ))``), VDM_GENERIC_TAC));" + Utilities.NEW_CHARACTER
				+ "val total = total + 1;" + Utilities.NEW_CHARACTER
				+ "total;" + Utilities.NEW_CHARACTER
				+ "success;" + Utilities.NEW_CHARACTER;

		String actual = trans.translateOmlToHol(prepData);
		
		// TODO can't compare strings because output is not deterministic
		//      as the POs can be printed in a different order in subsequent calls
		assertEquals(expected.length(), actual.length());
	}
}
