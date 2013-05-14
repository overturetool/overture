package org.overture.codegen.nodes;

import org.overture.codegen.visitor.CodeGenContext;

public interface ICommitable
{
	public void commit(CodeGenContext context);
}
