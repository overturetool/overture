/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.plugins.traces.views.treeView;

import org.eclipse.core.runtime.IAdaptable;
import org.overturetool.ct.utils.TraceTestResult;


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
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
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

