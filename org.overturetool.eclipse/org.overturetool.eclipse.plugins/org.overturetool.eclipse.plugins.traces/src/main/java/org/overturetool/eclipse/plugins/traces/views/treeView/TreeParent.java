package org.overturetool.eclipse.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

public class TreeParent implements IAdaptable,ITreeNode {
	private String name;
	private List<ITreeNode> children;

	public TreeParent(String name) {
		this.name = name;
		children = new ArrayList<ITreeNode>();
	}

	public String toString() {
		return getName();
	}

	public String getName() {
		return name;
	}

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

	public ITreeNode getParent()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChild(String name)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void setParent(ITreeNode parent)
	{
		// TODO Auto-generated method stub
		
	}
}