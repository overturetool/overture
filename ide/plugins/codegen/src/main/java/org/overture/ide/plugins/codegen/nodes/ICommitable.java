package org.overture.ide.plugins.codegen.nodes;

import org.overture.ide.plugins.codegen.visitor.CodeGenContext;

public interface ICommitable
{
	public void commit(CodeGenContext context);
}
