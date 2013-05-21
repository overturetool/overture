package org.overture.codegen.mergevisitor;

import org.overture.codegen.visitor.CodeGenContext;

public interface IContextManipulator
{
	public void manipulate(CodeGenContext context);
}
