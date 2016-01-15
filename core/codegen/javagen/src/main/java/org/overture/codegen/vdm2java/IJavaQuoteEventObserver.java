package org.overture.codegen.vdm2java;

import java.util.List;

import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;

public interface IJavaQuoteEventObserver
{
	public void quoteClassesProduced(List<ADefaultClassDeclCG> quoteClasses);
}
