package org.overture.ide.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;



public 	class ProjectTreeNode implements IAdaptable,ITreeNode {
	private List<ITreeNode> children;
	private ITreeNode parent;
	private IProject project;

	public ProjectTreeNode(IProject project) {
		this.project = project;
		children = new ArrayList<ITreeNode>();
	}

	public void setParent(ITreeNode parent) {
		this.parent = parent;
	}

	public ITreeNode getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public String getName() {
		return project.getName();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public void addChild(ITreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(ClassTreeNode child) {
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