/*
 * #%~
 * VDM to Isabelle Translation
 * %%
 * Copyright (C) 2008 - 2015 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overturetool.cgisa;

import java.io.File;

import org.overture.cgisa.extast.declarations.AMrFuncGroupDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AFuncDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.ANamedTypeDeclIR;
import org.overture.codegen.ir.declarations.AStateDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ABoolLiteralExpIR;
import org.overture.codegen.ir.expressions.ACharLiteralExpIR;
import org.overture.codegen.ir.expressions.AExplicitVarExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntDivNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.ALetDefExpIR;
import org.overture.codegen.ir.expressions.ANotImplementedExpIR;
import org.overture.codegen.ir.expressions.ARealLiteralExpIR;
import org.overture.codegen.ir.expressions.ATernaryIfExpIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.patterns.ATypeMultipleBindIR;
import org.overture.codegen.ir.types.AMapMapTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;

public class IsaTemplateManager extends TemplateManager
{

	private static final String MISC_EXP_FOLDER = "Misc";
	private static final String LITERAL_EXP_FOLDER = "Literal";
	private static final String BINDS_FOLDER = "Binds";

	private static final String FUNCTION = "Function";
	private static final String FORMAL_PARAM = "FormalParam";
	private static final String MUT_REC = "MutRec";
	private static final String MODULE_DECL = "Module";
	private static final String APPLY = "Apply";
	private static final String VAR = "Variable";
	private static final String TERNARY = "TernaryIf";
	private static final String BOOL_LIT = "BoolLiteral";
	private static final String CHAR_LIT = "CharLiteral";
	private static final String INT_LIT = "IntLiteral";
	private static final String REAL_LIT = "RealLiteral";
	private static final String TYPE_MULTIPLE_BIND = "TypeMultiple";
	private static final String EXPLICIT_VAR = "ExplicitVariable";
	private static final String NAMED_TYPE = "Name";
	private static final String SET_TYPE = "Set";
	private static final String SEQ_TYPE = "Seq";
	private static final String MAP_TYPE = "Map";
	private static final String NOT_IMPL_EXP = "NotImplemented";
	private static final String SET_MULT_BIND = "SetMultiple";
	private static final String LET_DEF_EXP = "LetDef";
	private static final String DIV_EXP = "Div";
	private static final String STATE_DECL = "State";

	public IsaTemplateManager(TemplateStructure templateStructure,
			Class<?> classRef)
	{
		super(templateStructure, classRef);
		initIsaNodes();
	}

	private void initIsaNodes()
	{
		nodeTemplateFileNames.put(AFuncDeclIR.class, templateStructure.DECL_PATH
				+ FUNCTION);

		nodeTemplateFileNames.put(AFormalParamLocalParamIR.class, templateStructure.LOCAL_DECLS_PATH
				+ FORMAL_PARAM);

		nodeTemplateFileNames.put(AMrFuncGroupDeclIR.class, templateStructure.DECL_PATH
				+ MUT_REC);

		nodeTemplateFileNames.put(AModuleDeclIR.class, templateStructure.DECL_PATH
				+ MODULE_DECL);

		nodeTemplateFileNames.put(AApplyExpIR.class, templateStructure.EXP_PATH
				+ MISC_EXP_FOLDER + File.separatorChar + APPLY);

		nodeTemplateFileNames.put(AIdentifierVarExpIR.class, templateStructure.EXP_PATH
				+ MISC_EXP_FOLDER + File.separatorChar + VAR);

		nodeTemplateFileNames.put(ATernaryIfExpIR.class, templateStructure.EXP_PATH
				+ MISC_EXP_FOLDER + File.separatorChar + TERNARY);

		nodeTemplateFileNames.put(ABoolLiteralExpIR.class, templateStructure.EXP_PATH
				+ LITERAL_EXP_FOLDER + File.separatorChar + BOOL_LIT);

		nodeTemplateFileNames.put(ACharLiteralExpIR.class, templateStructure.EXP_PATH
				+ LITERAL_EXP_FOLDER + File.separatorChar + CHAR_LIT);

		nodeTemplateFileNames.put(AIntLiteralExpIR.class, templateStructure.EXP_PATH
				+ LITERAL_EXP_FOLDER + File.separatorChar + INT_LIT);

		nodeTemplateFileNames.put(ARealLiteralExpIR.class, templateStructure.EXP_PATH
				+ LITERAL_EXP_FOLDER + File.separatorChar + REAL_LIT);

		nodeTemplateFileNames.put(ATypeMultipleBindIR.class, templateStructure.makePath(BINDS_FOLDER)
				+ TYPE_MULTIPLE_BIND);

		nodeTemplateFileNames.put(ASetMultipleBindIR.class, templateStructure.makePath(BINDS_FOLDER)
				+ SET_MULT_BIND);

		nodeTemplateFileNames.put(AExplicitVarExpIR.class, templateStructure.EXP_PATH
				+ MISC_EXP_FOLDER + File.separatorChar + EXPLICIT_VAR);

		nodeTemplateFileNames.put(ANamedTypeDeclIR.class, templateStructure.DECL_PATH
				+ NAMED_TYPE);

		nodeTemplateFileNames.put(ASetSetTypeIR.class, templateStructure.TYPE_PATH
				+ SET_TYPE);

		nodeTemplateFileNames.put(ASeqSeqTypeIR.class, templateStructure.TYPE_PATH
				+ SEQ_TYPE);

		nodeTemplateFileNames.put(AMapMapTypeIR.class, templateStructure.TYPE_PATH
				+ MAP_TYPE);

		nodeTemplateFileNames.put(ANotImplementedExpIR.class, templateStructure.EXP_PATH
				+ MISC_EXP_FOLDER + File.separatorChar + NOT_IMPL_EXP);

		nodeTemplateFileNames.put(ALetDefExpIR.class, templateStructure.EXP_PATH
				+ MISC_EXP_FOLDER + File.separatorChar + LET_DEF_EXP);

		nodeTemplateFileNames.put(AIntDivNumericBinaryExpIR.class, templateStructure.NUMERIC_BINARY_EXP_PATH
				+ File.separatorChar + DIV_EXP);

		nodeTemplateFileNames.put(AStateDeclIR.class, templateStructure.DECL_PATH
				+ File.separator + STATE_DECL);

	}
}
