package org.overturetool.eclipse.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;


public class ClassTreeNode implements IAdaptable {
	private ProjectTreeNode parent;
	private String className;
	private List<TraceTreeNode> children;

	public ClassTreeNode(String className) {
		this.className = className;
		children = new ArrayList<TraceTreeNode>();
	}

	public void setParent(ProjectTreeNode parent) {
		this.parent = parent;
	}

	public ProjectTreeNode getParent() {
		return parent;
	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return className;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public void addChild(TraceTreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(TraceTreeNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public List<TraceTreeNode> getChildren() {
		return children;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
}
