package org.overture.codegen.ir;

public interface IREventCoordinator
{
	public void registerIrObs(IREventObserver obs);
	public void unregisterIrObs(IREventObserver obs);
}
