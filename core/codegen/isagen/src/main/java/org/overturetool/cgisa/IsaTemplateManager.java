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

import org.overture.cgisa.extast.declarations.AMrFuncGroupDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTypeDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ANotImplementedExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.patterns.ATypeMultipleBindCG;
import org.overture.codegen.cgast.types.AMapMapTypeCG;
import org.overture.codegen.cgast.types.ASeqSeqTypeCG;
import org.overture.codegen.cgast.types.ASetSetTypeCG;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;

public class IsaTemplateManager extends TemplateManager
{

	private static final String MISC_EXP_PATH = "Misc";
	private static final String LITERAL_EXP_PATH = "Literal";
	private static final String BINDS_PATH = "Binds";

	private static final String FUNC_TEMPLATE = "Function";
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

	public IsaTemplateManager(TemplateStructure templateStructure,
			Class<?> classRef)
	{
		super(templateStructure, classRef);
		initIsaNodes();
	}

	private void initIsaNodes()
	{
		nodeTemplateFileNames.put(AFuncDeclCG.class, templateStructure.DECL_PATH
				+ FUNC_TEMPLATE);

		nodeTemplateFileNames.put(AFormalParamLocalParamCG.class, templateStructure.LOCAL_DECLS_PATH
				+ FORMAL_PARAM);

		nodeTemplateFileNames.put(AMrFuncGroupDeclCG.class, templateStructure.DECL_PATH
				+ MUT_REC);

		nodeTemplateFileNames.put(AModuleDeclCG.class, templateStructure.DECL_PATH
				+ MODULE_DECL);

		nodeTemplateFileNames.put(AApplyExpCG.class, templateStructure.EXP_PATH
				+ MISC_EXP_PATH + File.separatorChar + APPLY);

		nodeTemplateFileNames.put(AIdentifierVarExpCG.class, templateStructure.EXP_PATH
				+ MISC_EXP_PATH + File.separatorChar + VAR);

		nodeTemplateFileNames.put(ATernaryIfExpCG.class, templateStructure.EXP_PATH
				+ MISC_EXP_PATH + File.separatorChar + TERNARY);

		nodeTemplateFileNames.put(ABoolLiteralExpCG.class, templateStructure.EXP_PATH
				+ LITERAL_EXP_PATH + File.separatorChar + BOOL_LIT);

		nodeTemplateFileNames.put(ACharLiteralExpCG.class, templateStructure.EXP_PATH
				+ LITERAL_EXP_PATH + File.separatorChar + CHAR_LIT);

		nodeTemplateFileNames.put(AIntLiteralExpCG.class, templateStructure.EXP_PATH
				+ LITERAL_EXP_PATH + File.separatorChar + INT_LIT);

		nodeTemplateFileNames.put(ARealLiteralExpCG.class, templateStructure.EXP_PATH
				+ LITERAL_EXP_PATH + File.separatorChar + REAL_LIT);

		nodeTemplateFileNames.put(ATypeMultipleBindCG.class, templateStructure.makePath(BINDS_PATH)
				+ TYPE_MULTIPLE_BIND);
		
		nodeTemplateFileNames.put(ASetMultipleBindCG.class, templateStructure.makePath(BINDS_PATH)
				+ SET_MULT_BIND);

		nodeTemplateFileNames.put(AExplicitVarExpCG.class, templateStructure.EXP_PATH
				+ MISC_EXP_PATH + File.separatorChar + EXPLICIT_VAR);

		nodeTemplateFileNames.put(ANamedTypeDeclCG.class, templateStructure.DECL_PATH
				+ NAMED_TYPE);

		nodeTemplateFileNames.put(ASetSetTypeCG.class, templateStructure.TYPE_PATH
				+ SET_TYPE);

		nodeTemplateFileNames.put(ASeqSeqTypeCG.class, templateStructure.TYPE_PATH
				+ SEQ_TYPE);

		nodeTemplateFileNames.put(AMapMapTypeCG.class, templateStructure.TYPE_PATH
				+  MAP_TYPE);
		
		nodeTemplateFileNames.put(ANotImplementedExpCG.class, templateStructure.EXP_PATH
				+ MISC_EXP_PATH + File.separatorChar + NOT_IMPL_EXP);

	}
}
