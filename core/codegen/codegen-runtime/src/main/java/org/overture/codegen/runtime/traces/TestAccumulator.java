package org.overture.codegen.runtime.traces;

import java.io.Serializable;

public interface TestAccumulator extends Serializable
{
	public void registerTest(TraceTest test);
}
