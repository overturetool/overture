package org.overture.codegen.merging;

public interface MergeCoordinator
{
	public void register(MergerObserver obs);

	public void unregister(MergerObserver obs);
}
