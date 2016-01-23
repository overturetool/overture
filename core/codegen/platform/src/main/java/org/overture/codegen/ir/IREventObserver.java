package org.overture.codegen.ir;

import java.util.List;

import org.overture.codegen.cgast.PCG;

public interface IREventObserver
{
	public List<IRStatus<PCG>> initialIRConstructed(List<IRStatus<PCG>> ast, IRInfo info);
	public List<IRStatus<PCG>> finalIRConstructed(List<IRStatus<PCG>> ast, IRInfo info);
}
