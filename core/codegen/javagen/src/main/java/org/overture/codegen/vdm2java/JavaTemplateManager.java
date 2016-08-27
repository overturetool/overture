package org.overture.codegen.vdm2java;

import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.expressions.ACardUnaryExpIR;
import org.overture.codegen.ir.expressions.ALenUnaryExpIR;
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

		setUserTemplatePath(getTemplateLoaderRef(), ALenUnaryExpIR.class, getTemplatePath(ACardUnaryExpIR.class));
		setUserTemplatePath(getTemplateLoaderRef(), ASystemClassDeclIR.class, getTemplatePath(ADefaultClassDeclIR.class));
	}
}
