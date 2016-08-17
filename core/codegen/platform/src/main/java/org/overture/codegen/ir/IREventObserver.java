package org.overture.codegen.ir;

import java.util.List;

public interface IREventObserver
{
	public List<IRStatus<PIR>> initialIRConstructed(List<IRStatus<PIR>> ast,
			IRInfo info);

	public List<IRStatus<PIR>> finalIRConstructed(List<IRStatus<PIR>> ast,
			IRInfo info);
}
