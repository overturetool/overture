package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.expressions.ACardUnaryExpCG;
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG;
import org.overture.codegen.merging.TemplateManager;

public class JavaTemplateManager extends TemplateManager
{
	public JavaTemplateManager(String root)
	{
		super(root);
	}

	@Override
	protected void initNodeTemplateFileNames()
	{
		super.initNodeTemplateFileNames();

		reuseTemplate(ACardUnaryExpCG.class, ALenUnaryExpCG.class);
	}
}
