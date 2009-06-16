package org.overturetool.eclipse.plugins.traces.views.treeView;

import org.eclipse.core.runtime.IAdaptable;
import org.overturetool.traces.utility.TraceTestResult;


public class NotYetReadyTreeNode extends TraceTestTreeNode implements IAdaptable {
//	private NamedTraceDefinition traceDefinition;
	private ITreeNode parent;
	

	public NotYetReadyTreeNode() {
		super(new TraceTestResult());
//		traceDefinition = traceDef;
		
	}

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

	
	public String getName() {
		
			return toString();
		
	}

	public void setParent(ITreeNode parent) {
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	

	

	public boolean hasChildren() {
		return false;
	}

	public boolean hasChild(String name) {
		
		return false;
	}

	

}

