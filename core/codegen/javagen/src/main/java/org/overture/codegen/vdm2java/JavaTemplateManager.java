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
		
		String cardPath = getRelativePath(ACardUnaryExpCG.class);
		setUserDefinedPath(ALenUnaryExpCG.class, cardPath);
		
		String defClassPath = getRelativePath(ADefaultClassDeclCG.class);
		setUserDefinedPath(ASystemClassDeclCG.class, defClassPath);
	}
}
