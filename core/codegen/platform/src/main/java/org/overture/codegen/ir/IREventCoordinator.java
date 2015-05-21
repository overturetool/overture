package org.overture.codegen.ir;

public interface IREventCoordinator
{
	public void register(IREventObserver obs);
	public void unregister(IREventObserver obs);
}
