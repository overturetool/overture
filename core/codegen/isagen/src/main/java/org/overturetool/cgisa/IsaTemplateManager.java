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

import org.overture.cgisa.extast.declarations.AIsaClassDeclCG;
import org.overture.cgisa.extast.declarations.AMrFuncGroupDeclCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;

public class IsaTemplateManager extends TemplateManager
{

	private static final String FUNC_TEMPLATE = "Function";
	private static final String FORMAL_PARAM = "FormalParam";
	private static final String MUT_REC = "MutRec";
	private static final String ISA_CLASS = "IsaClass";

	public IsaTemplateManager(TemplateStructure templateStructure)
	{
		super(templateStructure);
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
		nodeTemplateFileNames.put(AIsaClassDeclCG.class, templateStructure.DECL_PATH
				+ ISA_CLASS);
		nodeTemplateFileNames.put(AClassDeclCG.class, templateStructure.DECL_PATH
				+ ISA_CLASS);
	}

}
