package org.overture.codegen.vdm2java;

import java.util.List;

import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;

public interface IJavaQuoteEventObserver
{
	public void quoteClassesProduced(List<ADefaultClassDeclIR> quoteClasses);
}
