package org.overturetool.proofsupport.external_tools.vdmholtranslator;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.ast.itf.IOmlExpression;
import org.overturetool.proofsupport.PreparationData;
import org.overturetool.proofsupport.TranslationPreProcessor;
import org.overturetool.proofsupport.external_tools.Utilities;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsPoProcessor;
import org.overturetool.proofsupport.external_tools.pog.VdmToolsWrapper;
import org.overturetool.proofsupport.test.AutomaticProofSystemTestCase;
import org.overturetool.proofsupport.test.TestSettings;

public class TranslatorWrapperTest extends AutomaticProofSystemTestCase {

	// TODO this method reveals a bug in the VDMTools POG generator:
	// -- the generated pog file will have two POs together (with no line
	// separating them),
	// public void testTranslateOmlToHol() throws Exception {
	// TranslationPreProcessor prep = new TranslationPreProcessor(new
	// VdmToolsWrapper(
	// "/Users/gentux/root/opt/vdmpp/bin/vppde"), new VdmToolsPogProcessor());
	// String modelFile = "testinput/stack.vpp";
	// List<String> contextFiles = new ArrayList<String>(1);
	// contextFiles.add("testinput/set.vpp");
	// PreparationData prepData = prep.prepareVdmFiles(modelFile, contextFiles);
	// Translator trans = new Translator();
	//
	// // TODO this expected variable i
	// String expected = "";
	//
	// String actual = trans.translateOmlToHol(prepData);
	//
	// System.err.println(actual);
	// // assertEquals(expected, actual);
	// }

	public void testTranslateOmlToHolNoContext() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(
				new VdmToolsWrapper(TestSettings.getVppdeBinary()),
				new VdmToolsPoProcessor());
		List<String> contextFiles = new ArrayList<String>(0);
		PreparationData prepData = prep.prepareVdmFiles(stackModel,
				contextFiles);
		TranslatorWrapper trans = new TranslatorWrapper();

		String expected = "fun boolToInteger(true) = 1 | boolToInteger(false)=0;"
				+ Utilities.NEW_CHARACTER
				+ "Define `inv_Stack (inv_param:(num list))  = T`;"
				+ Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"inv_Stack_def\"]);"
				+ Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"inv_Stack_def\"]);"
				+ Utilities.NEW_CHARACTER
				+ "Define `push (push_parameter_1:(num list)) (push_parameter_2:num)  = (let s = push_parameter_1 and n = push_parameter_2 in ([n]  ++ s ))`;"
				+ Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"push_def\"]);"
				+ Utilities.NEW_CHARACTER
				+ "Define `pop (pop_parameter_1:(num list))  = (let s = pop_parameter_1 in (TL s))`;"
				+ Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"pop_def\"]);"
				+ Utilities.NEW_CHARACTER
				+ "Define `pre_pop (pop_parameter_1:(num list))  = (let s = pop_parameter_1 in ((\\x y . ~ (x = y)) s []))`;"
				+ Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"pre_pop_def\"]);"
				+ Utilities.NEW_CHARACTER
				+ "Define `top (top_parameter_1:(num list))  = (let s = top_parameter_1 in (HD s))`;"
				+ Utilities.NEW_CHARACTER
				+ "BasicProvers.export_rewrites([\"top_def\"]);"
				+ Utilities.NEW_CHARACTER
				+ "val total = 0;"
				+ Utilities.NEW_CHARACTER
				+ "val success = 0;"
				+ Utilities.NEW_CHARACTER
				+ "val success = success + boolToInteger(can TAC_PROOF(([]:(term list), ``(!  uni_0_var_1.((((inv_Stack uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in ((\\x y . ~ (x = y)) s [])) ))``), VDM_GENERIC_TAC));"
				+ Utilities.NEW_CHARACTER
				+ "val total = total + 1;"
				+ Utilities.NEW_CHARACTER
				+ "val success = success + boolToInteger(can TAC_PROOF(([]:(term list), ``(!  uni_0_var_1.((((inv_Stack uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in (((\\x y . ~ (x = y)) s [])  ==> ((\\x y . ~ (x = y)) s []) )) ))``), VDM_GENERIC_TAC));"
				+ Utilities.NEW_CHARACTER
				+ "val total = total + 1;"
				+ Utilities.NEW_CHARACTER
				+ "total;"
				+ Utilities.NEW_CHARACTER
				+ "success;" + Utilities.NEW_CHARACTER;

		String actual = trans.translateModelAndPos(prepData);

		// TODO can't compare strings because output is not deterministic
		// as the POs can be printed in a different order in subsequent calls
		assertEquals(expected.length(), actual.length());
	}

	public void testDoModelTranslation() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(
				new VdmToolsWrapper(vppdeExecutable), new VdmToolsPoProcessor());
		String modelFile = stackModel;
		List<String> contextFiles = new ArrayList<String>(0);
		PreparationData prepData = prep
				.prepareVdmFiles(modelFile, contextFiles);

		TranslatorWrapper translator = new TranslatorWrapper();

		String holCode = translator.translateModel(prepData);

		assertNotNull(holCode);
		assertEquals(732, holCode.length());
	}
	
	public void testDoModelTranslationSet() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(
				new VdmToolsWrapper(vppdeExecutable), new VdmToolsPoProcessor());
		String modelFile = setModel;
		List<String> contextFiles = new ArrayList<String>(0);
		PreparationData prepData = prep
				.prepareVdmFiles(modelFile, contextFiles);

		TranslatorWrapper translator = new TranslatorWrapper();

		String holCode = translator.translateModel(prepData);

		assertNotNull(holCode);
		assertEquals(1210, holCode.length());
	}

	public void testTranslateExpression() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(
				new VdmToolsWrapper(vppdeExecutable), new VdmToolsPoProcessor());
		String vdmExpression = "forall i : int, l : seq of int &"
				+ "not (true = (l = [])) =>" + "true = (i <= hd (l)) =>"
				+ "l <> []";
		IOmlExpression omlExpression = prep.prepareVdmExpression(vdmExpression);
		TranslatorWrapper translator = new TranslatorWrapper();
		String holExpression = translator.translateExpression(omlExpression);

		assertNotNull(holExpression);
		assertEquals(310, holExpression.length());
		assertEquals(
				"(!  uni_0_var_2 uni_0_var_1.((((inv_num uni_0_var_1)  /\\ " +
				"(?  i.(i  = uni_0_var_1 )) )  /\\ (((inv_(num list) uni_0_var_2)  /\\ " +
				"(?  l.(l  = uni_0_var_2 )) )  /\\ T ) )  ==> " +
				"(let l = uni_0_var_2 in (let i = uni_0_var_1 in ((~ (T  = (l  = [] ) ))  ==> " +
				"((T  = (i  <= (HD l) ) )  ==> ((\\x y . ~ (x = y)) l []) ) ))) ))",
				holExpression);
	}
	
	public void testTranslateExpressionPo() throws Exception {
		TranslationPreProcessor prep = new TranslationPreProcessor(
				new VdmToolsWrapper(vppdeExecutable), new VdmToolsPoProcessor());
		String vdmExpression = "forall s : Set & Set`pre_doNothing(s)";
		IOmlExpression omlExpression = prep.prepareVdmExpression(vdmExpression);
		TranslatorWrapper translator = new TranslatorWrapper();
		String holExpression = translator.translateExpression(omlExpression);

		System.err.println(holExpression);
		
		assertNotNull(holExpression);
		assertEquals(132, holExpression.length());
		assertEquals(
				"(!  uni_0_var_1.((((inv_Set uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in (pre_doNothing s)) ))",
				holExpression);
	}
}
