package org.overture.codegen.ir;

import java.util.List;

import org.overture.codegen.cgast.INode;

public interface IREventObserver
{
	public void initialIRConstructed(List<IRStatus<INode>> ast, IRInfo info);
	public void finalIRConstructed(List<IRStatus<INode>> ast, IRInfo info);
}
