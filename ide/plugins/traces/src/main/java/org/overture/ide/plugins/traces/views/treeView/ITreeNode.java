package org.overture.ide.plugins.traces.views.treeView;

import java.util.List;

public interface ITreeNode
{
	public ITreeNode getParent();

	public void setParent(ITreeNode parent);

	public void addChild(ITreeNode child);

	public List<ITreeNode> getChildren();

	public boolean hasChildren();

	public boolean hasChild(String name);
	
	public String getName();
}
