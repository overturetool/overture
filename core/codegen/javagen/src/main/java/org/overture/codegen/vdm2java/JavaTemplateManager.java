package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.expressions.ACardUnaryExpCG;
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG;
import org.overture.codegen.merging.TemplateManager;
import org.overture.codegen.merging.TemplateStructure;

public class JavaTemplateManager extends TemplateManager
{
	public JavaTemplateManager(TemplateStructure templateStructure)
	{
		super(templateStructure);
	}

	@Override
	protected void initNodeTemplateFileNames()
	{
		super.initNodeTemplateFileNames();

		nodeTemplateFileNames.put(ALenUnaryExpCG.class, templateStructure.UNARY_EXP_PATH + "Len_Card");

		nodeTemplateFileNames.put(ACardUnaryExpCG.class, templateStructure.UNARY_EXP_PATH + "Len_Card");
	}
}
