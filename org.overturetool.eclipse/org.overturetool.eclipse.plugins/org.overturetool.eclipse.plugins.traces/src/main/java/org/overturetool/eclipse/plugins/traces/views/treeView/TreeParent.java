package org.overturetool.eclipse.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

public class TreeParent implements IAdaptable {
	private String name;
	private List<ProjectTreeNode> children;

	public TreeParent(String name) {
		this.name = name;
		children = new ArrayList<ProjectTreeNode>();
	}

	public String toString() {
		return getName();
	}

	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public void addChild(ProjectTreeNode child) {
		children.add(child);
		child.setParent(this);
	}

	public void removeChild(ProjectTreeNode child) {
		children.remove(child);
		child.setParent(null);
	}

	public List<ProjectTreeNode> getChildren() {
		return children;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}
}