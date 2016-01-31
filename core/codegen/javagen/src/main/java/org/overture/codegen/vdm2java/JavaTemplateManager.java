package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.ASystemClassDeclCG;
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
		
		setUserTemplatePath(getTemplateLoaderRef(), ALenUnaryExpCG.class, getTemplatePath(ACardUnaryExpCG.class));
		setUserTemplatePath(getTemplateLoaderRef(), ASystemClassDeclCG.class, getTemplatePath(ADefaultClassDeclCG.class));
	}
}
