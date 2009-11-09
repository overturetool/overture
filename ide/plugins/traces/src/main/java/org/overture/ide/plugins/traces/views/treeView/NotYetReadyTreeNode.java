package org.overture.ide.plugins.traces.views.treeView;

import org.eclipse.core.runtime.IAdaptable;
import org.overturetool.traces.utility.TraceTestResult;


public class NotYetReadyTreeNode extends TraceTestTreeNode implements IAdaptable {
//	private NamedTraceDefinition traceDefinition;
	private ITreeNode parent;
	

	public NotYetReadyTreeNode() {
		super(new TraceTestResult());
//		traceDefinition = traceDef;
		
	}

	@Override
	public ITreeNode getParent() {
		return parent;
	}

//	public NamedTraceDefinition GetTraceDefinition() {
//		return traceDefinition;
//	}

	

	@Override
	public String toString() {
		return "Please wait...";
	}

	
	@Override
	public String getName() {
		
			return toString();
		
	}

	@Override
	public void setParent(ITreeNode parent) {
		this.parent = parent;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	

	

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public boolean hasChild(String name) {
		
		return false;
	}

	

}

