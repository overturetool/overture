package org.overture.codegen.runtime.traces;

import java.io.Serializable;
import java.util.List;

public interface TestAccumulator extends Serializable
{
	public void registerTest(TraceTest test);

	public boolean hasNext();

	public TraceTest getNext();

	public List<TraceTest> getAllTests();
}
