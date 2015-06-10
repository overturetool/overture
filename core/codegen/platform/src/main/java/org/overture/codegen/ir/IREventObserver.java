package org.overture.codegen.ir;

import java.util.List;

import org.overture.codegen.cgast.INode;

public interface IREventObserver
{
	public List<IRStatus<INode>> initialIRConstructed(List<IRStatus<INode>> ast, IRInfo info);
	public List<IRStatus<INode>> finalIRConstructed(List<IRStatus<INode>> ast, IRInfo info);
}
