package org.overture.codegen.merging;

import java.io.StringWriter;

import org.overture.codegen.cgast.INode;

public interface MergerObserver
{
	public void preMerging(INode node, StringWriter buffer);
	public void nodeMerged(INode node, StringWriter buffer);
}
