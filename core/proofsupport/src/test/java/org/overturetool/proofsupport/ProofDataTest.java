package org.overturetool.proofsupport;

import java.util.ArrayList;

import org.overturetool.proofsupport.external_tools.Utilities;

import junit.framework.TestCase;

public class ProofDataTest extends TestCase {

	public void testGetProofCommand() {
		String theory = "Define `inv_Set (inv_Set_subj:(num set))  = (let s = inv_Set_subj in ((\\x y . ~ (x = y)) s {}))`;"
				+ Utilities.LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"inv_Set_def\"]);"
				+ Utilities.LINE_SEPARATOR
				+ "Define `pre_doNothing (doNothing_parameter_1:(num set))  = (let s = doNothing_parameter_1 in ((CARD s)  > 2 ))`;"
				+ Utilities.LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"pre_doNothing_def\"]);";
		String proofExpression = "(!  uni_0_var_1.((((inv_Set uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in (pre_doNothing s)) ))";
		ArrayList<String> holPos = new ArrayList<String>(1);
		holPos.add(proofExpression);
		ArrayList<String> vdmPos = new ArrayList<String>();
		vdmPos.add("Some VDM PO expression");
		Proof proof = new Proof(theory, holPos, vdmPos);

		String proofCommand = proof.getProofCommand(0);

		assertEquals(
				"can TAC_PROOF(([]:(term list), ``(!  uni_0_var_1.((((inv_Set uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in (pre_doNothing s)) ))``), VDM_GENERIC_TAC);",
				proofCommand);
	}

	public void testGetProofScript() {
		String theory = "Define `inv_Set (inv_Set_subj:(num set))  = (let s = inv_Set_subj in ((\\x y . ~ (x = y)) s {}))`;"
				+ Utilities.LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"inv_Set_def\"]);"
				+ Utilities.LINE_SEPARATOR
				+ "Define `pre_doNothing (doNothing_parameter_1:(num set))  = (let s = doNothing_parameter_1 in ((CARD s)  > 2 ))`;"
				+ Utilities.LINE_SEPARATOR
				+ "BasicProvers.export_rewrites([\"pre_doNothing_def\"]);";
		String proofExpression = "(!  uni_0_var_1.((((inv_Set uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in (pre_doNothing s)) ))";
		ArrayList<String> holPos = new ArrayList<String>(1);
		holPos.add(proofExpression);
		ArrayList<String> vdmPos = new ArrayList<String>();
		vdmPos.add("Some VDM PO expression");
		Proof proof = new Proof(theory, holPos, vdmPos);

		String proofScript = proof.getProofScript();

		assertEquals(
				theory
						+ Utilities.LINE_SEPARATOR
						+ "can TAC_PROOF(([]:(term list), ``(!  uni_0_var_1.((((inv_Set uni_0_var_1)  /\\ (?  s.(s  = uni_0_var_1 )) )  /\\ T )  ==> (let s = uni_0_var_1 in (pre_doNothing s)) ))``), VDM_GENERIC_TAC);"
						+ Utilities.LINE_SEPARATOR, proofScript);
	}

}
