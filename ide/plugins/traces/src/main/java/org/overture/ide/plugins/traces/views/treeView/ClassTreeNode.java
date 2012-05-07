///*******************************************************************************
// * Copyright (c) 2009, 2011 Overture Team and others.
// *
// * Overture is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Overture is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
// * 	
// * The Overture Tool web-site: http://overturetool.org/
// *******************************************************************************/
//package org.overture.ide.plugins.traces.views.treeView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.eclipse.core.runtime.IAdaptable;
//
//
//public class ClassTreeNode implements IAdaptable,ITreeNode {
//	private ITreeNode parent;
//	private String className;
//	private List<ITreeNode> children;
//
//	public ClassTreeNode(String className) {
//		this.className = className;
//		children = new ArrayList<ITreeNode>();
//	}
//
//	public void setParent(ITreeNode parent) {
//		this.parent = parent;
//	}
//
//	public ITreeNode getParent() {
//		return parent;
//	}
//
//	@Override
//	public String toString() {
//		return getName();
//	}
//
//	public String getName() {
//		return className;
//	}
//
//	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
//		return null;
//	}
//
//	public void addChild(ITreeNode child) {
//		children.add(child);
//		child.setParent(this);
//	}
//
//	public void removeChild(ITreeNode child) {
//		children.remove(child);
//		child.setParent(null);
//	}
//
//	public List<ITreeNode> getChildren() {
//		return children;
//	}
//
//	public boolean hasChildren() {
//		return children.size() > 0;
//	}
//
//
//
//	public boolean hasChild(String name)
//	{
//		// TODO Auto-generated method stub
//		return false;
//	}
//}
