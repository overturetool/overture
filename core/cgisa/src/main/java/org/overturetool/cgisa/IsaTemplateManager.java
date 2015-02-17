package org.overturetool.cgisa;

import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;


public class IsaTemplateManager extends TemplateManager
{

	private static final String FUNCTEMPLATE = "Function";

	public IsaTemplateManager(TemplateStructure templateStructure)
	{
		super(templateStructure);
		initIsaNodes();
	}

	private void initIsaNodes()
	{
		nodeTemplateFileNames.put(AFuncDeclCG.class,  templateStructure.DECL_PATH + FUNCTEMPLATE);
		
	}

	

}
