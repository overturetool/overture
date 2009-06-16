package org.overturetool.eclipse.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;


public class ClassTreeNode implements IAdaptable,ITreeNode {
	private ITreeNode parent;
	private String className;
	private List<ITreeNode> children;

	public ClassTreeNode(String className) {
		this.className = className;
		children = new ArrayList<ITreeNode>();
	}

	public void setParent(ITreeNode parent) {
		this.parent = parent;
	}

	public ITreeNode getParent() {
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

	public void addChild(ITreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(ITreeNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public List<ITreeNode> getChildren() {
		return children;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}



	public boolean hasChild(String name)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
