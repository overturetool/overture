package org.overturetool.eclipse.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;



public 	class ProjectTreeNode implements IAdaptable {
	private List<ClassTreeNode> children;
	private TreeParent parent;
	private IProject project;

	public ProjectTreeNode(IProject project) {
		this.project = project;
		children = new ArrayList<ClassTreeNode>();
	}

	public void setParent(TreeParent parent) {
		this.parent = parent;
	}

	public TreeParent getParent() {
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

	public void addChild(ClassTreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(ClassTreeNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public List<ClassTreeNode> getChildren() {
		return children;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

}